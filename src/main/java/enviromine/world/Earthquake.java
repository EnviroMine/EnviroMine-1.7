package enviromine.world;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.handlers.EM_PhysManager;
import enviromine.network.packet.PacketEnviroMine;
import java.util.ArrayList;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class Earthquake
{
	public static ArrayList<Earthquake> pendingQuakes = new ArrayList<Earthquake>();
	public static ArrayList<ClientQuake> clientQuakes = new ArrayList<ClientQuake>();
	public static int lastTickDay = 0;
	public static int tickCount = 0;
	
	World world;
	int posX;
	int posZ;
	
	int length;
	int width;
	float angle;
	
	int passY = 1;
	int mode;
	
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
				EnviroMine.instance.network.sendToAllAround(new PacketEnviroMine("ID:3,0," + world.provider.dimensionId + "," + posX + "," + posZ + "," + length + "," + width + "," + angle + ",1"), new TargetPoint(world.provider.dimensionId, posX, passY, posZ, 128 + size));
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
			
			for(int yy = y; yy >= 1; yy--)
			{
				if((world.getBlock(x, yy, z).getMaterial() == Material.lava && yy > 10) || world.getBlock(x, yy, z).getMaterial() == Material.water || world.getBlock(x, yy, z).getMaterial() == Material.rock || world.getBlock(x, yy, z).getMaterial() == Material.clay || world.getBlock(x, yy, z).getMaterial() == Material.sand || world.getBlock(x, yy, z).getMaterial() == Material.ground || world.getBlock(x, yy, z).getMaterial() == Material.grass || (yy <= 10 && world.getBlock(x, yy, z).getMaterial() == Material.air))
				{
					if(yy <= 10)
					{
						world.setBlock(x, yy, z, Blocks.flowing_lava);
						//System.out.println("Placed lava at (" + x + "," + yy + "," + z + ")");
						
						if(EM_Settings.enablePhysics && EM_Settings.quakePhysics)
						{
							EM_PhysManager.schedulePhysUpdate(world, x, yy, z, false, "Quake");
						}
						
						if(yy == y)
						{
							ravineMask.set(0, new int[]{x, y + EM_Settings.quakeSpeed, z});
							return true;
						}
					} else
					{
						world.setBlockToAir(x, yy, z);
						//System.out.println("Placed air at (" + x + "," + yy + "," + z + ")");
						
						if(EM_Settings.enablePhysics && EM_Settings.quakePhysics)
						{
							EM_PhysManager.schedulePhysUpdate(world, x, yy, z, false, "Quake");
						}
						
						if(yy == y)
						{
							ravineMask.set(0, new int[]{x, y + EM_Settings.quakeSpeed, z});
							return true;
						}
					}
				}
			}
			
			if(world.getTopSolidOrLiquidBlock(x, z) < 16 || world.canBlockSeeTheSky(x, y, z))
			{
				ravineMask.remove(0);
			} else
			{
				ravineMask.set(0, new int[]{x, y + EM_Settings.quakeSpeed, z});
			}

			int size = length > width? length/2 : width/2;
			EnviroMine.instance.network.sendToAllAround(new PacketEnviroMine("ID:3,1," + world.provider.dimensionId + "," + posX + "," + posZ + "," + length + "," + width + "," + angle + "," + passY), new TargetPoint(world.provider.dimensionId, posX, passY, posZ, 128 + size));
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
				
				if(y > passY)
				{
					continue;
				}
				
				for(int yy = y; yy >= 1; yy--)
				{
					if((world.getBlock(x, yy, z).getMaterial() == Material.lava && yy > 10) || world.getBlock(x, yy, z).getMaterial() == Material.water || world.getBlock(x, yy, z).getMaterial() == Material.rock || world.getBlock(x, yy, z).getMaterial() == Material.clay || world.getBlock(x, yy, z).getMaterial() == Material.sand || world.getBlock(x, yy, z).getMaterial() == Material.ground || world.getBlock(x, yy, z).getMaterial() == Material.grass || (yy <= 10 && world.getBlock(x, yy, z).getMaterial() == Material.air))
					{
						if(yy <= 10)
						{
							world.setBlock(x, yy, z, Blocks.flowing_lava);
							//System.out.println("Placed lava at (" + x + "," + yy + "," + z + ")");
							
							if(EM_Settings.enablePhysics && EM_Settings.quakePhysics)
							{
								EM_PhysManager.schedulePhysUpdate(world, x, yy, z, false, "Quake");
							}
							
							if(yy == y)
							{
								ravineMask.set(i, new int[]{x, y + EM_Settings.quakeSpeed, z});
								return true;
							}
						} else
						{
							world.setBlockToAir(x, yy, z);
							//System.out.println("Placed air at (" + x + "," + yy + "," + z + ")");
							
							if(EM_Settings.enablePhysics && EM_Settings.quakePhysics)
							{
								EM_PhysManager.schedulePhysUpdate(world, x, yy, z, false, "Quake");
							}
							
							if(yy == y)
							{
								ravineMask.set(i, new int[]{x, y + EM_Settings.quakeSpeed, z});
								return true;
							}
						}
					}
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
			EnviroMine.instance.network.sendToAllAround(new PacketEnviroMine("ID:3,1," + world.provider.dimensionId + "," + posX + "," + posZ + "," + length + "," + width + "," + angle + "," + passY), new TargetPoint(world.provider.dimensionId, posX, passY, posZ, 128 + size));
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
				EnviroMine.instance.network.sendToAllAround(new PacketEnviroMine("ID:3,2," + quake.world.provider.dimensionId + "," + quake.posX + "," + quake.posZ + "," + quake.length + "," + quake.width + "," + quake.angle + "," + quake.passY), new TargetPoint(quake.world.provider.dimensionId, quake.posX, quake.passY, quake.posZ, 128 + size));
				pendingQuakes.remove(i);
			}
		}
	}
	
	public static void TickDay(World world)
	{
		if(world.rand.nextInt(EM_Settings.quakeRarity) == 0 && world.playerEntities.size() > 0)
		{
			EntityPlayer player = (EntityPlayer)world.playerEntities.get(world.rand.nextInt(world.playerEntities.size()));
			
			int posX = MathHelper.floor_double(player.posX) + (world.rand.nextInt(4096) - 2048);
			int posZ = MathHelper.floor_double(player.posZ) + (world.rand.nextInt(4096) - 2048);
			
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
				EnviroMine.logger.log(Level.INFO, "Earthquake spawned at (" + posX + "," + posZ + ")");
			}
		}
	}
}
