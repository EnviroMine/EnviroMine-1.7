package enviromine.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.PortalPosition;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleportHandler extends Teleporter
{
	private final WorldServer worldServerInstance;
	
	/** Stores successful portal placement locations for rapid lookup. */
	private final LongHashMap destinationCoordinateCache = new LongHashMap();
	
	/**
	 * A list of valid keys for the destinationCoordainteCache. These are based on the X & Z of the players initial
	 * location.
	 */
	private final List destinationCoordinateKeys = new ArrayList();
	
	public TeleportHandler(WorldServer par1WorldServer)
	{
		super(par1WorldServer);
		this.worldServerInstance = par1WorldServer;
	}
	
	/**
	 * Place an entity in a nearby portal, creating one if necessary.
	 */
	@Override
	public void placeInPortal(Entity par1Entity, double par2, double par4, double par6, float par8)
	{
		if(!this.placeInExistingPortal(par1Entity, par2, par4, par6, par8))
		{
			this.makePortal(par1Entity);
			this.placeInExistingPortal(par1Entity, par2, par4, par6, par8);
		} else
		{
			if(par1Entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)par1Entity;
				ItemStack itemTop = new ItemStack(ObjectHandler.elevatorTop, 1);
				ItemStack itemBot = new ItemStack(ObjectHandler.elevatorBottom, 1);
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
		}
	}
	
	/**
	 * Place an entity in a nearby portal which already exists.
	 */
	@Override
	public boolean placeInExistingPortal(Entity par1Entity, double par2, double par4, double par6, float par8)
	{
		short short1 = 0;
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
		
		boolean breakLoop = false;
		
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
			for (k1 = l - short1; k1 <= l + short1; ++k1)
			{
				double d5 = (double)k1 + 0.5D - par1Entity.posX;
				
				for (int l1 = i1 - short1; l1 <= i1 + short1; ++l1)
				{
					double d6 = (double)l1 + 0.5D - par1Entity.posZ;
					
					for (int i2 = this.worldServerInstance.getActualHeight() - 1; i2 >= 0; --i2)
					{
						if (this.worldServerInstance.getBlockId(k1, i2, l1) == ObjectHandler.elevatorBottom.blockID && this.worldServerInstance.getBlockId(k1, i2 + 1, l1) == ObjectHandler.elevatorTop.blockID)
						{
							breakLoop = true;
							while (this.worldServerInstance.getBlockId(k1, i2 - 1, l1) == ObjectHandler.elevatorBottom.blockID || this.worldServerInstance.getBlockId(k1, i2 - 1, l1) == ObjectHandler.elevatorTop.blockID)
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
						}
						
						if(breakLoop)
						{
							break;
						}
					}
					
					if(breakLoop)
					{
						break;
					}
				}
				
				if(breakLoop)
				{
					break;
				}
			}
		}
		
		if (d3 >= 0.0D)
		{
			if (flag)
			{
				this.destinationCoordinateCache.add(j1, new PortalPosition(this, i, j, k, this.worldServerInstance.getTotalWorldTime()));
				this.destinationCoordinateKeys.add(Long.valueOf(j1));
			}
			
			double d8 = (double)i + 0.5D;
			double d9 = (double)j + 0.5D;
			d4 = (double)k + 0.5D;
			
			par1Entity.motionX = par1Entity.motionY = par1Entity.motionZ = 0.0D;
			par1Entity.setLocationAndAngles(d8, d9, d4, par1Entity.rotationYaw, par1Entity.rotationPitch);
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
		int i = MathHelper.floor_double(par1Entity.posX);
		int j = 5;//MathHelper.floor_double(par1Entity.posY);
		int k = MathHelper.floor_double(par1Entity.posZ);
		boolean clearSpace = false;
		
		if(this.worldServerInstance.provider.dimensionId == -3)
		{
			for(int checkH = 120; checkH >= 32; checkH--)
			{
				if(this.worldServerInstance.isAirBlock(i, checkH, k) && this.worldServerInstance.isBlockNormalCube(i, checkH - 1, k))
				{
					j = checkH;
					break;
				}
				
				if(checkH <= 32)
				{
					j = 32;
					clearSpace = true;
					break;
				}
			}
		} else
		{
			for(int checkH = 9; checkH >= 5; checkH--)
			{
				if(this.worldServerInstance.isAirBlock(i, checkH, k) && this.worldServerInstance.isBlockNormalCube(i, checkH - 1, k))
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
							if(!this.worldServerInstance.isBlockNormalCube(x, y, z));
							{
								this.worldServerInstance.setBlock(x, y, z, Block.planks.blockID);
								
								if(x != i && z != k)
								{
									int supY = y - 1;
									
									while(!this.worldServerInstance.isBlockNormalCube(x, supY, z) && supY >= 0)
									{
										this.worldServerInstance.setBlock(x, supY, z, Block.fence.blockID);
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
		
		this.worldServerInstance.setBlock(i, j + 1, k, ObjectHandler.elevatorTop.blockID);
		this.worldServerInstance.setBlock(i, j, k, ObjectHandler.elevatorBottom.blockID);
		
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
			Iterator iterator = this.destinationCoordinateKeys.iterator();
			long j = par1 - 600L;
			
			while (iterator.hasNext())
			{
				Long olong = (Long)iterator.next();
				PortalPosition portalposition = (PortalPosition)this.destinationCoordinateCache.getValueByKey(olong.longValue());
				
				if (portalposition == null || portalposition.lastUpdateTime < j)
				{
					iterator.remove();
					this.destinationCoordinateCache.remove(olong.longValue());
				}
			}
		}
	}
}
