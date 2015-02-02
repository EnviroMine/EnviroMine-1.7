package enviromine.world;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.handlers.EM_PhysManager;
import enviromine.network.packet.PacketEnviroMine;

public class Earthquake
{
	public static ArrayList<Earthquake> pendingQuakes = new ArrayList<Earthquake>();
	public static ArrayList<ClientQuake> clientQuakes = new ArrayList<ClientQuake>();
	public static int lastTickDay = 0;
	public static int tickCount = 0;
	
	public World world;
	public int posX;
	public int posZ;
	
	public int length;
	public int width;
	public float angle;
	
	public int passY = 1;
	public int mode;
	
	ArrayList<int[]> ravineMask = new ArrayList<int[]>(); // 2D array containing x,z coordinates of blocks within the ravine
	
	public Earthquake(World world, int i, int k, int l, int w, int m)
	{
		this.posX = i;
		this.posZ = k;
		this.length = l;
		this.width = w;
		this.mode = m;
		
		if(world != null)
		{
			this.world = world;
			this.angle = MathHelper.clamp_float(world.rand.nextFloat() * 4F - 2F, -2F, 2F);
			this.markRavine(angle);
			pendingQuakes.add(this);
			
			if(!(this instanceof ClientQuake))
			{
				int size = length > width? length/2 : width/2;
				NBTTagCompound pData = new NBTTagCompound();
				pData.setInteger("id", 3);
				pData.setInteger("dimension", world.provider.dimensionId);
				pData.setInteger("posX", posX);
				pData.setInteger("posZ", posZ);
				pData.setInteger("length", length);
				pData.setInteger("width", width);
				pData.setFloat("angle", angle);
				pData.setFloat("action", 0);
				pData.setFloat("height", 1);
				EnviroMine.instance.network.sendToAllAround(new PacketEnviroMine(pData), new TargetPoint(world.provider.dimensionId, posX, passY, posZ, 128 + size));
			}
		}
	}
	
	public Earthquake(World world, int i, int k, int l, int w, int m, float a, boolean save)
	{
		this.posX = i;
		this.posZ = k;
		this.length = l;
		this.width = w;
		this.mode = m;
		
		if(world != null)
		{
			this.world = world;
			this.angle = MathHelper.clamp_float(a, -2F, 2F);
			this.markRavine(angle);
			
			if(save)
			{
				pendingQuakes.add(this);
				int size = length > width? length/2 : width/2;
				NBTTagCompound pData = new NBTTagCompound();
				pData.setInteger("id", 3);
				pData.setInteger("dimension", world.provider.dimensionId);
				pData.setInteger("posX", posX);
				pData.setInteger("posZ", posZ);
				pData.setInteger("length", length);
				pData.setInteger("width", width);
				pData.setFloat("angle", angle);
				pData.setFloat("action", 0);
				pData.setFloat("height", 1);
				EnviroMine.instance.network.sendToAllAround(new PacketEnviroMine(pData), new TargetPoint(world.provider.dimensionId, posX, passY, posZ, 128 + size));
			}
		}
	}
	
	public void markRavine(float angle)
	{
		ravineMask.clear();
		
		for(int i = -length / 2; i < length / 2; i++)
		{
			int fx = MathHelper.floor_float(Math.abs(angle) > 1F ? i * (angle > 0 ? angle - 1F : angle + 1F) : i);
			int fz = MathHelper.floor_float(Math.abs(angle) > 1F ? i : i * angle);
			int widthFactor = MathHelper.ceiling_double_int(Math.cos(i / (length / 3D)) * width);
			
			if(Math.abs(angle) <= 1F)
			{
				for(int z = fz - widthFactor / 2; z < fz + widthFactor / 2; z++)
				{
					this.ravineMask.add(new int[]{fx + posX, 1, z + posZ});
				}
			} else
			{
				for(int x = fx - widthFactor / 2; x < fx + widthFactor / 2; x++)
				{
					this.ravineMask.add(new int[]{x + posX, 1, fz + posZ});
				}
			}
		}
		
		if(this.mode >= 1 && this.mode <= 2)
		{
			this.reOrderFromCenter();
		}
	}
	
	public void reOrderFromCenter()
	{
		for(int i = 1; i < ravineMask.size(); i++)
		{
			int[] iEntry = ravineMask.get(i);
			double iDist = trigDist(iEntry[0], iEntry[2]);
			
			for(int j = i - 1; j >= 0; j--)
			{
				int[] jEntry = ravineMask.get(j);
				double jDist = trigDist(jEntry[0], jEntry[2]);
				
				if(jDist > iDist)
				{
					if(j == 0)
					{
						ravineMask.remove(i);
						ravineMask.add(j, iEntry);
					}
					continue;
				} else
				{
					if(j + 1 != i)
					{
						ravineMask.remove(i);
						ravineMask.add(j + 1, iEntry);
					}
					break;
				}
			}
		}
	}
	
	public double trigDist(double a, double b)
	{
		return (double)MathHelper.sqrt_double(Math.pow(a - posX, 2) + Math.pow(b - posZ, 2));
	}
	
	public boolean removeBlockCenter()
	{
		this.passY = 64;
		
		if(ravineMask.size() > 0)
		{
			int[] pos = this.ravineMask.get(0);
			
			int x = pos[0];
			int y = pos[1];
			int z = pos[2];
			
			boolean removed = false;
			
			for(int yy = y; yy >= 1; yy--)
			{
				if((world.getBlock(x, yy, z).getMaterial() == Material.lava && yy > 10) || world.getBlock(x, yy, z).getMaterial() == Material.water || world.getBlock(x, yy, z).getMaterial() == Material.rock || world.getBlock(x, yy, z).getMaterial() == Material.clay || world.getBlock(x, yy, z).getMaterial() == Material.sand || world.getBlock(x, yy, z).getMaterial() == Material.ground || world.getBlock(x, yy, z).getMaterial() == Material.grass || (yy <= 10 && world.getBlock(x, yy, z).getMaterial() == Material.air))
				{
					if(world.getBlock(x, yy, z).getBlockHardness(world, x, yy, z) < 0)
					{
						continue;
					}
					
					if(yy <= 10)
					{
						world.setBlock(x, yy, z, Blocks.flowing_lava);
						//System.out.println("Placed lava at (" + x + "," + yy + "," + z + ")");
						
						if(yy == y)
						{
							if(EM_Settings.enablePhysics && EM_Settings.quakePhysics)
							{
								EM_PhysManager.schedulePhysUpdate(world, x, yy, z, false, "Quake");
							}
							
							ravineMask.set(0, new int[]{x, y + EM_Settings.quakeSpeed, z});
							removed =  true;
						}
					} else
					{
						world.setBlockToAir(x, yy, z);
						//System.out.println("Placed air at (" + x + "," + yy + "," + z + ")");
						
						if(yy == y)
						{
							if(EM_Settings.enablePhysics && EM_Settings.quakePhysics)
							{
								EM_PhysManager.schedulePhysUpdate(world, x, yy, z, false, "Quake");
							}
							
							ravineMask.set(0, new int[]{x, y + EM_Settings.quakeSpeed, z});
							removed =  true;
						}
					}
				}
			}
			
			if(removed)
			{
				return true;
			}
			
			if(world.getTopSolidOrLiquidBlock(x, z) < 16 || world.canBlockSeeTheSky(x, y, z))
			{
				ravineMask.remove(0);
			} else
			{
				ravineMask.set(0, new int[]{x, y + EM_Settings.quakeSpeed, z});
			}

			int size = length > width? length/2 : width/2;
			NBTTagCompound pData = new NBTTagCompound();
			pData.setInteger("id", 3);
			pData.setInteger("dimension", world.provider.dimensionId);
			pData.setInteger("posX", posX);
			pData.setInteger("posZ", posZ);
			pData.setInteger("length", length);
			pData.setInteger("width", width);
			pData.setFloat("angle", angle);
			pData.setFloat("action", 1);
			pData.setFloat("height", passY);
			EnviroMine.instance.network.sendToAllAround(new PacketEnviroMine(pData), new TargetPoint(world.provider.dimensionId, posX, passY, posZ, 128 + size));
			return true;
		}
		return false;
	}
	
	public boolean removeBlock()
	{
		if(mode >= 2)
		{
			return this.removeBlockCenter();
		}
		
		while(passY < world.getActualHeight())
		{
			for(int i = 0; i < ravineMask.size(); i++)
			{
				int[] pos = this.ravineMask.get(i);
				
				int x = pos[0];
				int y = pos[1];
				int z = pos[2];
				
				boolean removed = false;
				
				if(y > passY)
				{
					continue;
				}
				
				for(int yy = y; yy >= 1; yy--)
				{
					if((world.getBlock(x, yy, z).getMaterial() == Material.lava && yy > 10) || world.getBlock(x, yy, z).getMaterial() == Material.water || world.getBlock(x, yy, z).getMaterial() == Material.rock || world.getBlock(x, yy, z).getMaterial() == Material.clay || world.getBlock(x, yy, z).getMaterial() == Material.sand || world.getBlock(x, yy, z).getMaterial() == Material.ground || world.getBlock(x, yy, z).getMaterial() == Material.grass || (yy <= 10 && world.getBlock(x, yy, z).getMaterial() == Material.air))
					{
						if(world.getBlock(x, yy, z).getBlockHardness(world, x, yy, z) < 0)
						{
							continue;
						}
						
						if(yy <= 10)
						{
							world.setBlock(x, yy, z, Blocks.flowing_lava);
							//System.out.println("Placed lava at (" + x + "," + yy + "," + z + ")");
							
							if(yy == y)
							{
								if(EM_Settings.enablePhysics && EM_Settings.quakePhysics)
								{
									EM_PhysManager.schedulePhysUpdate(world, x, yy, z, false, "Quake");
								}
								
								ravineMask.set(i, new int[]{x, y + EM_Settings.quakeSpeed, z});
								removed =  true;
							}
						} else
						{
							world.setBlockToAir(x, yy, z);
							//System.out.println("Placed air at (" + x + "," + yy + "," + z + ")");
							
							if(yy == y)
							{
								if(EM_Settings.enablePhysics && EM_Settings.quakePhysics)
								{
									EM_PhysManager.schedulePhysUpdate(world, x, yy, z, false, "Quake");
								}
								
								ravineMask.set(i, new int[]{x, y + EM_Settings.quakeSpeed, z});
								removed =  true;
							}
						}
					}
				}
				
				if(removed)
				{
					return true;
				}
				
				Chunk chunk = world.getChunkFromBlockCoords(x, z);
				
				if((chunk != null && chunk.getSavedLightValue(EnumSkyBlock.Sky, x & 0xf, y, z & 0xf) >= 15) || world.getTopSolidOrLiquidBlock(x, z) < 16 || world.canBlockSeeTheSky(x, y, z))
				{
					ravineMask.remove(i);
				} else
				{
					ravineMask.set(i, new int[]{x, y + EM_Settings.quakeSpeed, z});
				}
			}
			
			passY += EM_Settings.quakeSpeed;
			int size = length > width? length/2 : width/2;
			NBTTagCompound pData = new NBTTagCompound();
			pData.setInteger("id", 3);
			pData.setInteger("dimension", world.provider.dimensionId);
			pData.setInteger("posX", posX);
			pData.setInteger("posZ", posZ);
			pData.setInteger("length", length);
			pData.setInteger("width", width);
			pData.setFloat("angle", angle);
			pData.setFloat("action", 1);
			pData.setFloat("height", passY);
			EnviroMine.instance.network.sendToAllAround(new PacketEnviroMine(pData), new TargetPoint(world.provider.dimensionId, posX, passY, posZ, 128 + size));
		}
		
		return false;
	}
	
	public void removeAll()
	{
		for(int y = 1; y < world.getActualHeight(); y++)
		{
			for(int i = 0; i < this.ravineMask.size(); i++)
			{
				int[] pos = this.ravineMask.get(i);
				
				int x = pos[0];
				int z = pos[2];
				
				if((world.getBlock(x, y, z).getMaterial() == Material.lava && y > 10) || world.getBlock(x, y, z).getMaterial() == Material.water || world.getBlock(x, y, z).getMaterial() == Material.rock || world.getBlock(x, y, z).getMaterial() == Material.clay || world.getBlock(x, y, z).getMaterial() == Material.sand || world.getBlock(x, y, z).getMaterial() == Material.ground || world.getBlock(x, y, z).getMaterial() == Material.grass || (y <= 10 && world.getBlock(x, y, z).getMaterial() == Material.air))
				{
					if(world.getBlock(x, y, z).getBlockHardness(world, x, y, z) < 0)
					{
						continue;
					}
					
					if(y <= 10)
					{
						world.setBlock(x, y, z, Blocks.flowing_lava);
					} else
					{
						world.setBlockToAir(x, y, z);
					}
				}
			}
		}
		
		this.ravineMask.clear();
	}
	
	public static void updateEarthquakes()
	{
		if(!EM_Settings.enableQuakes)
		{
			pendingQuakes.clear();
			return;
		}
		
		if(tickCount >= EM_Settings.quakeDelay * pendingQuakes.size())
		{
			tickCount = 0;
		} else
		{
			tickCount++;
			return;
		}
		
		for(int i = pendingQuakes.size() - 1; i >= 0; i--)
		{
			Earthquake quake = pendingQuakes.get(i);
			
			if(quake.world.isRemote)
			{
				pendingQuakes.remove(i);
				continue;
			}
			
			//quake.removeAll();
			if(!quake.removeBlock() || quake.ravineMask.size() <= 0)
			{
				int size = quake.length > quake.width? quake.length/2 : quake.width/2;
				NBTTagCompound pData = new NBTTagCompound();
				pData.setInteger("id", 3);
				pData.setInteger("dimension", quake.world.provider.dimensionId);
				pData.setInteger("posX", quake.posX);
				pData.setInteger("posZ", quake.posZ);
				pData.setInteger("length", quake.length);
				pData.setInteger("width", quake.width);
				pData.setFloat("angle", quake.angle);
				pData.setFloat("action", 2);
				pData.setFloat("height", quake.passY);
				EnviroMine.instance.network.sendToAllAround(new PacketEnviroMine(pData), new TargetPoint(quake.world.provider.dimensionId, quake.posX, quake.passY, quake.posZ, 128 + size));
				pendingQuakes.remove(i);
			}
		}
	}
	
	public static void TickDay(World world)
	{
		if(world.rand.nextInt(2) == 0 && world.playerEntities.size() > 0)
		{
			Entity player = (Entity)world.playerEntities.get(world.rand.nextInt(world.playerEntities.size()));
			
			int posX = MathHelper.floor_double(player.posX) + (world.rand.nextInt(1024) - 512);
			int posZ = MathHelper.floor_double(player.posZ) + (world.rand.nextInt(1024) - 512);
			
			 // Chunk check can be disabled but may cause a large amount of chunks to be generated where the earthquake passes through
			if(world.getChunkProvider().chunkExists(posX >> 4, posZ >> 4))
			{
				int mode = 0;
				
				if(EM_Settings.quakeMode <= -1)
				{
					mode = world.rand.nextInt(4);
				} else
				{
					mode = EM_Settings.quakeMode;
				}
				
				new Earthquake(world, posX, posZ, 32 + world.rand.nextInt(128-32), 4 + world.rand.nextInt(32-4), mode);
				EnviroMine.logger.log(Level.INFO, "Earthquake at (" + posX + "," + posZ + ") with type " + mode);
			}
		}
	}

	public static void saveQuakes(File file)
	{
		try
		{
			if(!file.exists())
			{
				file.createNewFile();
			}
			
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			
			ArrayList<float[]> savedQuakes = new ArrayList<float[]>();
			
			for(int i = 0; i < pendingQuakes.size(); i++)
			{
				Earthquake quake = pendingQuakes.get(i);
				float[] entry = new float[7];
				entry[0] = quake.world.provider.dimensionId;
				entry[1] = quake.posX;
				entry[2] = quake.posZ;
				entry[3] = quake.length;
				entry[4] = quake.width;
				entry[5] = quake.mode;
				entry[6] = quake.angle;
				//entry[7] = quake.passY;
				
				savedQuakes.add(entry);
			}
			
			oos.writeObject(savedQuakes);
			
			oos.close();
			bos.close();
			fos.close();
		} catch(Exception e)
		{
			EnviroMine.logger.log(Level.ERROR, "Failed to save Earthquakes", e);
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void loadQuakes(File file)
	{
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
				
				ArrayList<float[]> loadedQuakes = (ArrayList<float[]>)ois.readObject();
				
				for(int i = 0; i < loadedQuakes.size(); i++)
				{
					float[] qData = loadedQuakes.get(i);
					
					int d = (int)qData[0];
					World world = MinecraftServer.getServer().worldServerForDimension(d);
					int x = (int)qData[1];
					int y = (int)qData[2];
					int l = (int)qData[3];
					int w = (int)qData[4];
					int m = (int)qData[5];
					float a = qData[6];
					
					new Earthquake(world, x, y, l, w, m, a, true);
				}
				
				ois.close();
				bis.close();
				fis.close();
			} catch(Exception e)
			{
				EnviroMine.logger.log(Level.ERROR, "Failed to load Earthquakes", e);
				e.printStackTrace();
			}
		}
	}
	
	public static void Reset()
	{
		pendingQuakes.clear();
		clientQuakes.clear();
		lastTickDay = 0;
	}
}
