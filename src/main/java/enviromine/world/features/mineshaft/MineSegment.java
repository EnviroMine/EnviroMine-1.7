package enviromine.world.features.mineshaft;

import java.util.ArrayList;
import java.util.logging.Level;
import enviromine.core.EnviroMine;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public abstract class MineSegment
{
	// Rotation = 90 * value (top-down clockwise direction)(+X = forward, +Z = right)
	int rot = 0;
	World world;
	int posX = 0;
	int posY = 0;
	int posZ = 0;
	
	int minX = 0;
	int minZ = 0;
	int minY = 0;
	int maxX = 0;
	int maxY = 0;
	int maxZ = 0;
	
	int chunkMinX = 0;
	int chunkMaxX = 0;
	int chunkMinZ = 0;
	int chunkMaxZ = 0;
	
	int decay = 0;
	
	MineshaftBuilder builder;
	
	public MineSegment(World world, int x, int y, int z, int rotation, MineshaftBuilder builder)
	{
		this.world = world;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.rot = rotation%4;
		this.builder = builder;
	}
	
	public abstract boolean build();
	
	void setBlockBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
	{
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
	
	void setChunkBounds(int minX, int minZ, int maxX, int maxZ)
	{
		this.chunkMinX = minX;
		this.chunkMinZ = minZ;
		
		this.chunkMaxX = maxX;
		this.chunkMaxZ = maxZ;
	}
	
	public void setDecay(int decay)
	{
		this.decay = decay;
	}
	
	public int[] getExitPoint(int rotation)
	{
		return new int[]{posX,posY,posZ};
	}
	
	public int[] getExitPoint(int rotation, int yDir)
	{
		int[] point = getExitPoint(rotation);
		return point;
	}
	
	public boolean canBuild()
	{
		if(posY + maxY > 255)
		{
			return false;
		} else if(posY - minY < 0)
		{
			return false;
		}
		return true;
	}
	
	public ArrayList<String> getRequiredChunks()
	{
		ArrayList<String> chunks = new ArrayList<String>();
		
		int xOffMin = this.xOffset(chunkMinX, chunkMinZ)/16;
		int zOffMin = this.zOffset(chunkMinX, chunkMinX)/16;
		int xOffMax = this.xOffset(chunkMaxX, chunkMaxZ)/16;
		int zOffMax = this.zOffset(chunkMaxX, chunkMaxZ)/16;
		
		if(xOffMin > xOffMax)
		{
			int tmp = xOffMin;
			xOffMin = xOffMax;
			xOffMax = tmp;
		}
		
		if(zOffMin > zOffMax)
		{
			int tmp = zOffMin;
			zOffMin = zOffMax;
			zOffMax = tmp;
		}
		
		for(int i = xOffMin; i <= xOffMax; i++)
		{
			for(int k = zOffMin; k <= zOffMax; k++)
			{
				chunks.add("" + i + "," + k);
			}
		}
		
		return chunks;
	}
	
	public void linkChunksToBuilder()
	{
		ArrayList<String> chunks = this.getRequiredChunks();
		
		if(chunks.size() <= 0)
		{
			EnviroMine.logger.log(Level.WARNING, "ERROR: MineSegment is registering 0 chunks! It will not generate!");
		}
		
		for(int i = 0; i < chunks.size(); i++)
		{
			if(this.builder.segmentMap.containsKey(chunks.get(i)))
			{
				ArrayList<MineSegment> tempList = this.builder.segmentMap.get(chunks.get(i));
				tempList.add(this);
				this.builder.segmentMap.put(chunks.get(i), tempList);
			} else
			{
				ArrayList<MineSegment> tempList = new ArrayList<MineSegment>();
				tempList.add(this);
				this.builder.segmentMap.put(chunks.get(i), tempList);
			}
		}
	}
	
	public boolean allChunksLoaded()
	{
		int xOffMin = this.xOffset(chunkMinX, chunkMinZ)/16;
		int zOffMin = this.zOffset(chunkMinX, chunkMinX)/16;
		int xOffMax = this.xOffset(chunkMaxX, chunkMaxZ)/16;
		int zOffMax = this.zOffset(chunkMaxX, chunkMaxZ)/16;
		
		if(xOffMin > xOffMax)
		{
			int tmp = xOffMin;
			xOffMin = xOffMax;
			xOffMax = tmp;
		}
		
		if(zOffMin > zOffMax)
		{
			int tmp = zOffMin;
			zOffMin = zOffMax;
			zOffMax = tmp;
		}
		
		for(int i = xOffMin; i <= xOffMax; i++)
		{
			for(int k = zOffMin; k <= zOffMax; k++)
			{
				if(!this.world.getChunkProvider().chunkExists(i, k))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	public void fillArea(int i1, int j1, int k1, int i2, int j2, int k2, int id, int meta)
	{
		for(int i = i1; i <= i2; i++)
		{
			for(int j = j1; j <= j2; j++)
			{
				for(int k = k1; k <= k2; k++)
				{
					this.setBlock(i, j, k, id, meta);
				}
			}
		}
	}
	
	public void fillAndRotate(int i1, int j1, int k1, int i2, int j2, int k2, int id, int meta)
	{
		this.fillArea(i1, j1, k1, i2, j2, k2, id, this.rotateMeta(meta));
	}
	
	public void setBlock(int x, int y, int z, int id, int meta)
	{
		if(x > maxX || x < minX || y > maxY || y < minY || z > maxZ || z < minZ)
		{
			EnviroMine.logger.log(Level.WARNING, this.getClass().getSimpleName() + " tried to place block out of bounds!");
			return;
		}
		
		if(decay == 0 || builder.rand.nextInt(50) > decay)
		{
			this.world.setBlock(this.xOffset(x, z), this.yOffset(y), this.zOffset(x, z), id, meta, 2);
		} else
		{
			if(id != 0 && Block.blocksList[id].blockMaterial == Material.wood && builder.rand.nextBoolean())
			{
				this.world.setBlock(this.xOffset(x, z), this.yOffset(y), this.zOffset(x, z), Block.vine.blockID, 0, 2);
			} else
			{
				this.world.setBlockToAir(this.xOffset(x, z), this.yOffset(y), this.zOffset(x, z));
			}
		}
	}
	
	public void setBlockAndRotate(int x, int y, int z, int id, int meta)
	{
		this.setBlock(x, y, z, id, this.rotateMeta(meta));
	}
	
	public int getBlockID(int x, int y, int z)
	{
		return this.world.getBlockId(this.xOffset(x, z), this.yOffset(y), this.zOffset(x, z));
	}
	
	public int getBlockMeta(int x, int y, int z)
	{
		return this.world.getBlockMetadata(this.xOffset(x, z), this.yOffset(y), this.zOffset(x, z));
	}
	
	public void addLootChest(int x, int y, int z, int itemCount)
	{
		int i = this.xOffset(x, z);
		int j = this.yOffset(y);
		int k = this.zOffset(x, z);
		
		if(world.getChunkFromBlockCoords(i, k) == null)
		{
			return;
		}
		
		world.setBlock(i, j, k, Block.chest.blockID);
		TileEntityChest chestTile = (TileEntityChest)world.getBlockTileEntity(i, j, k);
		
		if(chestTile != null)
		{
			WeightedRandomChestContent.generateChestContents(builder.rand, builder.loot, chestTile, itemCount);
		}
	}
	
	public int rotateMeta(int meta)
	{
		if(this.rot == 0)
		{
			return meta;
		} else if(this.rot == 1)
		{
			if(meta == 2)
			{
				return 4;
			} else if(meta == 3)
			{
				return 5;
			} else if(meta == 4)
			{
				return 3;
			} else if(meta == 5)
			{
				return 2;
			} else
			{
				return meta;
			}
		} else if(this.rot == 2)
		{
			if(meta == 2)
			{
				return 3;
			} else if(meta == 3)
			{
				return 2;
			} else if(meta == 4)
			{
				return 5;
			} else if(meta == 5)
			{
				return 4;
			} else
			{
				return meta;
			}
		} else if(this.rot == 3)
		{
			if(meta == 2)
			{
				return 5;
			} else if(meta == 3)
			{
				return 4;
			} else if(meta == 4)
			{
				return 2;
			} else if(meta == 5)
			{
				return 3;
			} else
			{
				return meta;
			}
		}
		{
			return meta;
		}
	}
	
	public int xOffset(int x, int z)
	{
		return this.xOffset(posX, rot, x, z);
	}
	
	public int yOffset(int y)
	{
		return this.yOffset(posY, y);
	}
	
	public int zOffset(int x, int z)
	{
		return this.zOffset(posZ, rot, x, z);
	}
	
	public int xOffset(int pointX, int rotation, int x, int z)
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
				return -x + pointX + maxX;
			}
			
			case 3:
			{
				return -z + pointX + maxX;
			}
			
			default:
			{
				return x + pointX;
			}
		}
	}
	
	public int yOffset(int pointY, int y)
	{
		return y + pointY;
	}
	
	public int zOffset(int pointZ, int rotation, int x, int z)
	{
		switch(rotation)
		{
			case 0:
			{
				return z + pointZ;
			}
			
			case 1:
			{
				return -x + pointZ + maxZ;
			}
			
			case 2:
			{
				return -z + pointZ + maxZ;
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
}
