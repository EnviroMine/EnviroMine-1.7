package enviromine.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import enviromine.core.EM_Settings;

public class TeleportHandler extends Teleporter
{
	private final WorldServer worldServerInstance;
	
	/** Stores successful portal placement locations for rapid lookup. */
	private final LongHashMap destinationCoordinateCache = new LongHashMap();
	
	/**
	 * A list of valid keys for the destinationCoordainteCache. These are based on the X & Z of the players initial
	 * location.
	 */
	private final List<Long> destinationCoordinateKeys = new ArrayList<Long>();
	
	static HashMap<WorldServer,TeleportHandler> instances = new HashMap<WorldServer,TeleportHandler>();
	private boolean recall = false;
	
	public static TeleportHandler GetInstance(WorldServer world)
	{
		return GetInstance(world, false);
	}
	
	public static TeleportHandler GetInstance(WorldServer world, boolean recall)
	{
		TeleportHandler tele = instances.get(world);
		
		if(tele != null)
		{
			tele.recall = recall;
			return tele;
		} else
		{
			tele = new TeleportHandler(world);
			tele.recall = recall;
			instances.put(world, tele);
			return tele;
		}
	}
	
	private TeleportHandler(WorldServer par1WorldServer)
	{
		super(par1WorldServer);
		this.worldServerInstance = par1WorldServer;
	}
	
	/**
	 * Place an entity in a nearby portal, creating one if necessary.
	 */
	@Override
	public void placeInPortal(Entity entity, double par2, double par4, double par6, float par8)
	{
		if(!this.placeInExistingPortal(entity, par2, par4, par6, par8))
		{
			this.makePortal(entity);
			this.placeInExistingPortal(entity, par2, par4, par6, par8);
			if(entity instanceof EntityPlayer && EM_Settings.caveRespawn)
			{
				EntityPlayer player = (EntityPlayer)entity;
	            ChunkCoordinates chunkcoordinates = player.getPlayerCoordinates();
	            player.setSpawnChunk(chunkcoordinates, true);
			}
		} else
		{
            int i = MathHelper.floor_double(entity.posX);
            int j = MathHelper.floor_double(entity.posY);
            int k = MathHelper.floor_double(entity.posZ);
            
			if(entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)entity;
				
				if(!recall)
				{
					ItemStack itemTop = new ItemStack(ObjectHandler.elevator, 1, 0);
					ItemStack itemBot = new ItemStack(ObjectHandler.elevator, 1, 1);
					
					if(!player.inventory.addItemStackToInventory(itemTop))
					{
						EntityItem entityitem = new EntityItem(this.worldServerInstance, player.posX, player.posY, player.posZ, itemTop);
						this.worldServerInstance.spawnEntityInWorld(entityitem);
					}
					
					if(!player.inventory.addItemStackToInventory(itemBot))
					{
						EntityItem entityitem = new EntityItem(this.worldServerInstance, player.posX, player.posY, player.posZ, itemBot);
						this.worldServerInstance.spawnEntityInWorld(entityitem);
					}
				}
				
				if(EM_Settings.caveRespawn)
				{
		            ChunkCoordinates chunkcoordinates = player.getPlayerCoordinates();
		            player.setSpawnChunk(chunkcoordinates, true);
				}
			}
			
			entity.setLocationAndAngles((double)i, (double)j, (double)k, entity.rotationYaw, 0.0F);
			entity.motionX = entity.motionY = entity.motionZ = 0.0D;
		}
	}
	
	/**
	 * Place an entity in a nearby portal which already exists.
	 */
	@Override
	public boolean placeInExistingPortal(Entity par1Entity, double par2, double par4, double par6, float par8)
	{
		short short1 = 1;
		double d3 = -1.0D;
		int i = 0;
		int j = 0;
		int k = 0;
		int l = MathHelper.floor_double(par1Entity.posX);
		int i1 = MathHelper.floor_double(par1Entity.posZ);
		long j1 = ChunkCoordIntPair.chunkXZ2Int(l, i1);
		boolean flag = true;
		double d4;
		int k1;
		
		if (this.destinationCoordinateCache.containsItem(j1))
		{
			PortalPosition portalposition = (PortalPosition)this.destinationCoordinateCache.getValueByKey(j1);
			d3 = 0.0D;
			i = portalposition.posX;
			j = portalposition.posY;
			k = portalposition.posZ;
			portalposition.lastUpdateTime = this.worldServerInstance.getTotalWorldTime();
			flag = false;
		}
		else
		{
			brkAll:
			for (k1 = l - short1; k1 <= l + short1; ++k1)
			{
				double d5 = (double)k1 + 0.5D - par1Entity.posX;
				
				for (int l1 = i1 - short1; l1 <= i1 + short1; ++l1)
				{
					double d6 = (double)l1 + 0.5D - par1Entity.posZ;
					
					for (int i2 = 255 - 1; i2 >= 0; --i2)
					{
						if (this.worldServerInstance.getBlock(k1, i2, l1) == ObjectHandler.elevator && this.worldServerInstance.getBlock(k1, i2 + 1, l1) == ObjectHandler.elevator)
						{
							if(this.worldServerInstance.getBlockMetadata(k1, i2, l1) == 1 && this.worldServerInstance.getBlockMetadata(k1, i2 + 1, l1) == 0)
							{
								// Normal elevator
								//recall = false;
							} else if(this.worldServerInstance.getBlockMetadata(k1, i2, l1) == 1 && this.worldServerInstance.getBlockMetadata(k1, i2 + 1, l1) == 0)
							{
								// Recall
								recall = true;
							} else
							{
								continue;
							}
							while (this.worldServerInstance.getBlock(k1, i2 - 1, l1) == ObjectHandler.elevator)
							{
								--i2;
							}
							
							d4 = (double)i2 + 0.5D - par1Entity.posY;
							double d7 = d5 * d5 + d4 * d4 + d6 * d6;
							
							if (d3 < 0.0D || d7 < d3)
							{
								d3 = d7;
								i = k1;
								j = i2;
								k = l1;
							}
							
							break brkAll;
						}
					}
				}
			}
		}
		
		if (d3 >= 0.0D)
		{
			if (flag)
			{
				this.destinationCoordinateCache.add(j1, new PortalPosition(i, j, k, this.worldServerInstance.getTotalWorldTime()));
				this.destinationCoordinateKeys.add(Long.valueOf(j1));
			}
			
			double d8 = (double)i + 0.5D;
			double d9 = (double)j + 0.5D;
			d4 = (double)k + 0.5D;
			
			par1Entity.motionX = par1Entity.motionY = par1Entity.motionZ = 0.0D;
			par1Entity.setLocationAndAngles(d8, d9 + 0.1D, d4, par1Entity.rotationYaw, par1Entity.rotationPitch);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public boolean makePortal(Entity par1Entity)
	{
		int i = (int)(MathHelper.floor_double(par1Entity.posX) + Math.signum(par1Entity.posX < 0? par1Entity.posX : 0));
		int j = 5;//MathHelper.floor_double(par1Entity.posY);
		int k = (int)(MathHelper.floor_double(par1Entity.posZ) + Math.signum(par1Entity.posZ < 0? par1Entity.posZ : 0));
		boolean clearSpace = false;
		
		if(this.worldServerInstance.provider.dimensionId == EM_Settings.caveDimID)
		{
			for(int checkH = 120; checkH >= 32; checkH--)
			{
				if(this.worldServerInstance.isAirBlock(i, checkH, k) && this.worldServerInstance.isAirBlock(i, checkH + 1, k) && this.worldServerInstance.getBlock(i, checkH - 1, k).isNormalCube())
				{
					j = checkH;
					break;
				}
				
				if(checkH <= 32)
				{
					j = EM_Settings.caveLiquidY;
					clearSpace = true;
					break;
				}
			}
		} else
		{
			for(int checkH = 9; checkH >= 5; checkH--)
			{
				if(this.worldServerInstance.isAirBlock(i, checkH, k) && this.worldServerInstance.isAirBlock(i, checkH + 1, k) && this.worldServerInstance.getBlock(i, checkH - 1, k).isNormalCube())
				{
					j = checkH;
					break;
				}
				
				if(checkH <= 5)
				{
					j = 5;
					clearSpace = true;
					break;
				}
			}
		}
		
		if(clearSpace)
		{
			for(int x = i - 1; x <= i + 1; x++)
			{
				for(int y = j - 1; y <= j + 2; y++)
				{
					for(int z = k - 1; z <= k + 1; z++)
					{
						if(y == j - 1)
						{
							if(!this.worldServerInstance.getBlock(x, y, z).isOpaqueCube());
							{
								this.worldServerInstance.setBlock(x, y, z, Blocks.cobblestone);
								
								if(x != i && z != k)
								{
									int supY = y - 1;
									
									while(!this.worldServerInstance.getBlock(x, supY, z).isOpaqueCube() && supY >= 0)
									{
										this.worldServerInstance.setBlock(x, supY, z, Blocks.cobblestone_wall);
										supY -= 1;
									}
								}
							}
						} else
						{
							this.worldServerInstance.setBlockToAir(x, y, z);
						}
					}
				}
			}
		}
		
		this.worldServerInstance.setBlock(i, j + 1, k, ObjectHandler.elevator, 0, 2);
		this.worldServerInstance.setBlock(i, j, k, ObjectHandler.elevator, 1, 2);
		
		return true;
	}
	
	/**
	 * called periodically to remove out-of-date portal locations from the cache list. Argument par1 is a
	 * WorldServer.getTotalWorldTime() value.
	 */
	@Override
	public void removeStalePortalLocations(long par1)
	{
		if (par1 % 100L == 0L)
		{
			Iterator<Long> iterator = this.destinationCoordinateKeys.iterator();
			long j = par1 - 600L;
			
			while (iterator.hasNext())
			{
				Long olong = iterator.next();
				PortalPosition portalposition = (PortalPosition)this.destinationCoordinateCache.getValueByKey(olong.longValue());
				
				if (portalposition == null || portalposition.lastUpdateTime < j)
				{
					iterator.remove();
					this.destinationCoordinateCache.remove(olong.longValue());
				}
			}
		}
	}
	
	public static boolean RecallElevator(int x, int y, int z, boolean invert)
	{
		WorldServer caveWorld = MinecraftServer.getServer().worldServerForDimension(EM_Settings.caveDimID);
		WorldServer overWorld = MinecraftServer.getServer().worldServerForDimension(0);
		
		int i = 0;
		int j = 0;
		int k = 0;
		short short1 = 1;
		boolean found = false;
		
		WorldServer checkWorld = invert? overWorld : caveWorld;
		WorldServer recallWorld = invert? caveWorld : overWorld;
		
		brkAll:
		for (i = x - short1; i <= x + short1; ++i)
		{
			for (k = z - short1; k <= z + short1; ++k)
			{
				for (j = 255 - 1; j >= 0; --j)
				{
					if (checkWorld.getBlock(i, j, k) == ObjectHandler.elevator && checkWorld.getBlockMetadata(i, j, k) == 1 && checkWorld.getBlock(i, j + 1, k) == ObjectHandler.elevator && checkWorld.getBlockMetadata(i, j + 1, k) == 0)
					{
						while (checkWorld.getBlock(i, j - 1, k) == ObjectHandler.elevator)
						{
							--j;
						} 
						
						found = true;
						break brkAll;
					}
				}
			}
		}
		
		if(found)
		{
			// Set recall blocks
			checkWorld.setBlock(i, j, k, ObjectHandler.elevator, 3, 2);
			checkWorld.setBlock(i, j + 1, k, ObjectHandler.elevator, 2, 2);
			
			// Set elevator blocks
			recallWorld.setBlock(x, y, z, ObjectHandler.elevator, 1, 2);
			recallWorld.setBlock(x, y + 1, z, ObjectHandler.elevator, 0, 2);
		}
		
		return found;
	}
}
