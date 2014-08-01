package enviromine.world.features.mineshaft;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.world.features.WorldFeatureGenerator;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class MineshaftBuilder
{
	/* All mines are designed in a 1024x1024 block grid where 9 grid positions (3x3 centered on last generated chunk)
	 * are scanned for valid mine placements. No mine should generated more than 1024 in length or width.
	 * 
	 * Use 'new Random(world.rand.getSeed()).setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L)' to ensure every time a seed is used
	 * the same mine is generated in the same place.
	 * 
	 * A chunk is a valid placement for a mine if(new Random(world.rand.getSeed()).setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L).nextInt(100) == 0)
	 * and the chunk's ground level is above Y:48. Any mines above Y:80 will require a valid drift mine entrance.
	 * Mine's can only generate on every 8th chunk. This is to keep scanning to a minimum and the mine's spread out.
	 * The chunk generate event can then trigger any valid segments inside the HashMap to be built.
	 * 
	 * Builders are not required to be saved to file, only the grid positions and the number of outstanding builders in each need to be saved.
	 * Builders can be reloaded from the saved grid positions that contain pending builders in the same way they were created.
	 * When loading a builder from file, any segments that lie completely on existent chunks are assumed to be built already and
	 * are not re-added to the pending segment HashMap
	 * 
	 * Shaft entrances that spawn under areas of land too high above sea level will instead tunnel in a random horizontal direction until they reach a surface opening.
	 * Stairs may be necessary in the horizontal tunnel to reach the surface if the tunnel become's too long trying to reach the surface.
	 * When generating the horizontal tunnel it will be necessary to pre-generate terrain around the entrance to determine the viability of a drift tunnel.
	 * Use the biome's minimum terrain height in order to determine whether a drift tunnel should be made or simply continue drilling to the surface.
	 * (Alternative method for generating drift mines, doesn't require additional chunk loading) If the origin chunk is detected to be on the side of a cliff
	 * (across 3 blocks Y differs by more than 5 blocks) then the entrance should automatically drift mine into the side of said cliff.
	 * If the cliff side is above sea level then immediately build downward stair segments until it is under sea level before building the main shaft.
	 */
	
	public static HashMap<String, Integer> scannedGrids = new HashMap<String, Integer>();
	public static ArrayList<MineshaftBuilder> pendingBuilders = new ArrayList<MineshaftBuilder>();
	public HashMap<String, ArrayList<MineSegment>> segmentMap = new HashMap<String, ArrayList<MineSegment>>();
	
	World world;
	Random rand;
	int origX = 0;
	int origZ = 0;
	int origY = 0;
	int rot = 0;
	
	int decayAmount;
	WeightedRandomChestContent[] loot;
	
	public MineshaftBuilder(World world, int originX, int originZ, int dir)
	{
		this.world = world;
		this.rand = world.rand;
		this.origX = originX;
		this.origZ = originZ;
		this.origY = world.getChunkFromBlockCoords(originX, originZ).getHeightValue(originX%16, originZ%16);
		this.rot = dir%4;
		
		this.setupLoot();
	}
	
	public MineshaftBuilder(World world, int originX, int originY, int originZ, int dir)
	{
		this.world = world;
		this.rand = world.rand;
		this.origX = originX;
		this.origZ = originZ;
		this.origY = originY;
		this.rot = dir%4;
		
		this.setupLoot();
	}
	
	public void setupLoot()
	{
		ArrayList<WeightedRandomChestContent> initLoot = new ArrayList<WeightedRandomChestContent>();
		
		initLoot.add(new WeightedRandomChestContent(Item.ingotIron.itemID, 0, 1, 5, 5));
		initLoot.add(new WeightedRandomChestContent(Item.ingotGold.itemID, 0, 1, 3, 5));
		initLoot.add(new WeightedRandomChestContent(Item.redstone.itemID, 0, 4, 9, 5));
		initLoot.add(new WeightedRandomChestContent(Item.diamond.itemID, 0, 2, 3, 3));
		initLoot.add(new WeightedRandomChestContent(Item.coal.itemID, 0, 3, 8, 10));

		initLoot.add(new WeightedRandomChestContent(Block.oreIron.blockID, 0, 1, 5, 5));
		initLoot.add(new WeightedRandomChestContent(Block.oreGold.blockID, 0, 1, 3, 5));
		
		initLoot.add(new WeightedRandomChestContent(Item.pickaxeIron.itemID, 0, 1, 1, 1));
		initLoot.add(new WeightedRandomChestContent(Item.shovelIron.itemID, 0, 1, 1, 1));
		
		initLoot.add(new WeightedRandomChestContent(Block.wood.blockID, 0, 2, 4, 3));
		initLoot.add(new WeightedRandomChestContent(Block.planks.blockID, 0, 2, 4, 3));
		initLoot.add(new WeightedRandomChestContent(Block.fence.blockID, 0, 2, 2, 3));
		
		this.loot = initLoot.toArray(new WeightedRandomChestContent[initLoot.size()]);
	}
	
	public static void scanGrids(World world, int chunkX, int chunkZ, Random random)
	{
		//This is here to disabled the scanning event on generated chunks when the new mines are calculating their starting depth from the terrain height.
		//Pre-existing mine segments will however still be generated
		WorldFeatureGenerator.disableMineScan = true;
		
		int gridX = chunkX/64;
		int gridZ = chunkZ/64;
		
		for(int i = gridX - 1; i <= gridX + 1; i++)
		{
			for(int k = gridZ - 1; k <= gridZ + 1; k++)
			{
				if(scannedGrids.containsKey("" + i + "," + k + "," + world.provider.dimensionId))
				{
					continue;
				} else
				{
					int foundBuilders = 0;
					
					for(int ii = i*64; ii <= (i*64) + 64; ii += 8)
					{
						for(int kk = k*64; kk <= (k*64) + 64; kk += 8)
						{
							if(random.nextInt(100) == 0)
							{
								int rotation = 0;
								
								MineshaftBuilder tmpBuilder = new MineshaftBuilder(world, ii*16, kk*16, rotation);
								tmpBuilder.decayAmount = random.nextInt(10);
								tmpBuilder.setRandom(random);
								if(world.provider.dimensionId == 0)
								{
									if(tmpBuilder.startOldGridDesign())
									{
										pendingBuilders.add(tmpBuilder);
										foundBuilders += 1;
									}
								} else if(world.provider.dimensionId == EM_Settings.caveDimID)
								{
									if(tmpBuilder.startCaveDesign())
									{
										pendingBuilders.add(tmpBuilder);
										foundBuilders += 1;
									}
								}
							}
						}
					}
					
					scannedGrids.put("" + i + "," + k + "," + world.provider.dimensionId, foundBuilders);
				}
			}
		}
		
		WorldFeatureGenerator.disableMineScan = false;
	}
	
	public MineshaftBuilder setRandom(Random newRand)
	{
		this.rand = newRand;
		return this;
	}
	
	public boolean checkAndBuildSegments(int chunkX, int chunkZ)
	{
		ArrayList<MineSegment> chunkSegments = segmentMap.get("" + chunkX + "," + chunkZ);
		
		if(chunkSegments != null)
		{
			for(int i = chunkSegments.size() - 1; i >= 0; i--)
			{
				MineSegment segment = chunkSegments.get(i);
				
				if(segment.allChunksLoaded())
				{
					if(segment.canBuild())
					{
						segment.build();
					}
					chunkSegments.remove(i);
				}
			}
			
			if(chunkSegments.size() > 0)
			{
				segmentMap.put("" + chunkX + "," + chunkZ, chunkSegments);
			} else
			{
				segmentMap.remove(segmentMap.get("" + chunkX + "," + chunkZ));
			}
		}
		
		return this.segmentMap.size() <= 0;
	}
	
	public boolean startOldGridDesign()
	{
		//Old grid design is made here. Each segment must call 'linkChunksToBuilder()' to ensure it will be built when the chunks are loaded
		//DO NOT call .build() in this function, it may cause additional chunks to be force loaded.
		//Any segment that already has all chunks loaded should not be added to the segment map.
		
		if(!this.world.isBlockNormalCube(origX, origY - 1, origX) || origY < 48)
		{
			return false;
		}
		
		int mineDepth = 32 - (this.rand.nextInt(5)*4);
		int chunkY = origY;
		
		if(chunkY%4 != 0)
		{
			chunkY -= 3;
			MineSegment segment = new MineSegmentShaft(this.world, this.xOffset(0, 0), chunkY, this.zOffset(0, 0), this.rot, this, false);
			segment.setDecay(decayAmount);
			segment.linkChunksToBuilder();
			chunkY -= chunkY%4;
		}
		
		while(chunkY >= mineDepth)
		{
			MineSegment segment = null;
			
			if(chunkY == mineDepth)
			{
				segment = new MineSegmentShaft(this.world, this.xOffset(0, 0), chunkY, this.zOffset(0, 0), this.rot, this, true);
				segment.setDecay(decayAmount);
				segment.linkChunksToBuilder();
				break;
				
			} else
			{
				segment = new MineSegmentShaft(this.world, this.xOffset(0, 0), chunkY, this.zOffset(0, 0), this.rot, this, false);
				segment.setDecay(decayAmount);
				segment.linkChunksToBuilder();
				chunkY -= 4;
			}
		}
		
		this.DesignGridMine(3 + this.rand.nextInt(7), this.rand.nextInt(64) + 64, this.rand.nextInt(64) + 64, chunkY);
		
		if(this.segmentMap.size() > 0)
		{
			EnviroMine.logger.log(Level.INFO, "New mine at " + this.origX + "," + this.origY + "," + this.origZ + " with rotation " + this.rot + " in dimension " + world.provider.dimensionId);
			return true;
		} else
		{
			return false;
		}
	}
	
	public boolean startCaveDesign()
	{
		//Cave design is made here. Each segment must call 'linkChunksToBuilder()' to ensure it will be built when the chunks are loaded
		//DO NOT call .build() in this function, it may cause additional chunks to be force loaded.
		//Any segment that already has all chunks loaded should not be added to the segment map.
		
		return true;
	}
	
	public boolean startRandomDesign()
	{
		//Random design is made here. Each segment must call 'linkChunksToBuilder()' to ensure it will be built when the chunks are loaded
		//DO NOT call .build() in this function, it may cause additional chunks to be force loaded.
		//Any segment that already has all chunks loaded should not be added to the segment map.
		
		return true;
	}
	
	public void BuildCombMine(int interval)
	{
		int length = rand.nextInt(128) + 128;
		
		for(int i = 0; i <= length; i++)
		{
			new MineSegmentNormal(this.world, this.xOffset(i * 4, 0), this.yOffset(0), this.zOffset(i * 4, 0), this.rot, this, true).build();
			
			if(i%(interval + 1) == 0)
			{
				int lWidth = rand.nextInt(8) + 8;
				
				for(int il = 1; il <= lWidth; il++)
				{
					new MineSegmentNormal(this.world, this.xOffset(i * 4, il * 4), this.yOffset(0), this.zOffset(i * 4, il * 4), this.rot, this, true).build();
				}
			}
		}
	}
	
	public void BuildFeatherMine(int interval)
	{
		int length = rand.nextInt(16) + 16;
		
		for(int i = 0; i <= length; i++)
		{
			new MineSegmentNormal(this.world, this.xOffset(i * 4, 0), this.yOffset(0), this.zOffset(i * 4, 0), this.rot, this, true).build();
			
			if(i%(interval + 1) == 0)
			{
				int lWidth = rand.nextInt(4) + 4;
				int rWidth = (rand.nextInt(4) + 4) * -1;
				
				for(int il = 1; il <= lWidth; il++)
				{
					new MineSegmentNormal(this.world, this.xOffset(i * 4, il * 4), this.yOffset(0), this.zOffset(i * 4, il * 4), this.rot, this, true).build();
				}
				
				for(int ir = -1; ir >= rWidth; ir--)
				{
					new MineSegmentNormal(this.world, this.xOffset(i * 4, ir * 4), this.yOffset(0), this.zOffset(i * 4, ir * 4), this.rot, this, true).build();
				}
			}
		}
	}
	
	public void DesignGridMine(int gridSize, int gridL, int gridW, int mineDepth)
	{
		if(gridSize < 0)
		{
			return;
		}
		
		int skew = 0;
		
		for(int i = -((gridL/2) - (gridL/2)%(gridSize+1)); i < gridL/2; i += gridSize + 1)
		{
			for(int j = -(gridW/2); j < gridW/2; j++)
			{
				if(i == 0 && j == 0)
				{
					continue;
				}
				
				if(i == 0)
				{
					skew = 0;
				} else if(j%(gridSize+1) == 0)
				{
					skew = rand.nextInt(3) - 1;
				}
				
				MineSegment segment = new MineSegmentNormal(this.world, this.xOffset(i * 4 + skew * 4, j * 4), mineDepth, this.zOffset(i * 4 + skew * 4, j * 4), this.rot, this, true);
				segment.setDecay(decayAmount);
				segment.linkChunksToBuilder();
			}
		}
		
		for(int i = -((gridW/2) - (gridW/2)%(gridSize+1)); i < gridW/2; i += gridSize + 1)
		{
			for(int j = -(gridL/2); j < gridL/2; j++)
			{
				if(i == 0 && j == 0)
				{
					continue;
				}
				
				if(i == 0)
				{
					skew = 0;
				} else if(j%(gridSize+1) == 0)
				{
					skew = rand.nextInt(3) - 1;
				}
				
				MineSegment segment = new MineSegmentNormal(this.world, this.xOffset(j * 4 + skew * 4, i * 4), mineDepth, this.zOffset(j * 4 + skew * 4, i * 4), this.rot, this, true);
				segment.setDecay(decayAmount);
				segment.linkChunksToBuilder();
			}
		}
	}
	
	public MineSegment GetSegmentFromID(SegmentType type)
	{
		return null;
	}
	
	public int xOffset(int x, int z)
	{
		return xOffset(origX, rot, x, z);
	}
	
	public int yOffset(int y)
	{
		return yOffset(origY, y);
	}
	
	public int zOffset(int x, int z)
	{
		return zOffset(origZ, rot, x, z);
	}
	
	public static int xOffset(int pointX, int rotation, int x, int z)
	{
		switch(rotation)
		{
			case 0:
			{
				return x + pointX;
			}
			
			case 1:
			{
				return z + pointX;
			}
			
			case 2:
			{
				return -x + pointX;
			}
			
			case 3:
			{
				return -z + pointX;
			}
			
			default:
			{
				return x + pointX;
			}
		}
	}
	
	public static int yOffset(int pointY, int y)
	{
		return y + pointY;
	}
	
	public static int zOffset(int pointZ, int rotation, int x, int z)
	{
		switch(rotation)
		{
			case 0:
			{
				return z + pointZ;
			}
			
			case 1:
			{
				return -x + pointZ;
			}
			
			case 2:
			{
				return -z + pointZ;
			}
			
			case 3:
			{
				return x + pointZ;
			}
			
			default:
			{
				return z + pointZ;
			}
		}
	}
	
	public static void saveBuilders(File file)
	{
		EnviroMine.logger.log(Level.INFO, "Saving Mineshaft Builders...");
		try
		{
			if(!file.exists())
			{
				file.createNewFile();
			}
			
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			
			HashMap<String, Integer> savedGrids = new HashMap<String, Integer>();
			
			Iterator<String> iterator = scannedGrids.keySet().iterator();
			
			while(iterator.hasNext())
			{
				String key = iterator.next();
				Integer value = scannedGrids.get(key);
				if(value <= 0)
				{
					savedGrids.put(key, value);
				}
			}
			
			oos.writeObject(savedGrids);
			
			oos.close();
			bos.close();
			fos.close();
		} catch(FileNotFoundException e)
		{
			EnviroMine.logger.log(Level.WARNING, "Failed to save Mineshaft Builders: FileNotFoundException");
			e.printStackTrace();
		} catch(IOException e)
		{
			EnviroMine.logger.log(Level.WARNING, "Failed to save Mineshaft Builders: IOException!");
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void loadBuilders(File file)
	{
		EnviroMine.logger.log(Level.INFO, "Loading Mineshaft Builders...");
		
		if(!file.exists())
		{
			return;
		} else
		{
			try
			{
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				ObjectInputStream ois = new ObjectInputStream(bis);
				
				HashMap<String, Integer> loadedGrids = (HashMap<String, Integer>)ois.readObject();
				
				Iterator<String> iterator = loadedGrids.keySet().iterator();
				
				while(iterator.hasNext())
				{
					String key = iterator.next();
					Integer value = loadedGrids.get(key);
					if(value <= 0)
					{
						scannedGrids.put(key, value);
					}
				}
				
				ois.close();
				bis.close();
				fis.close();
			} catch(FileNotFoundException e)
			{
				EnviroMine.logger.log(Level.WARNING, "Failed to load Mineshaft Builders: FileNotFoundException");
				e.printStackTrace();
			} catch(IOException e)
			{
				EnviroMine.logger.log(Level.WARNING, "Failed to load Mineshaft Builders: IOException!");
				e.printStackTrace();
			} catch(ClassCastException e)
			{
				EnviroMine.logger.log(Level.WARNING, "Failed to load Mineshaft Builders: ClassCastException! (file format error)");
				e.printStackTrace();
			} catch(ClassNotFoundException e)
			{
				EnviroMine.logger.log(Level.WARNING, "Failed to load Mineshaft Builders: ClassNotFoundException! (file format error)");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Clears all builders and scanned segments from temporary memory.
	 * WARNING: Only call this after the builders have been saved!
	 */
	public static void clearBuilders()
	{
		scannedGrids = new HashMap<String, Integer>();
		pendingBuilders = new ArrayList<MineshaftBuilder>();
	}
	
	public enum SegmentType
	{
		NORMAL,
		SHAFT,
		SHAFT_PLATFORM,
		STAIR_UP,
		STAIR_DOWN,
		STOPE,
	}
}
