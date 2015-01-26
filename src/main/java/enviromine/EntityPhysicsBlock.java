package enviromine;

import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.handlers.EM_PhysManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import org.apache.logging.log4j.Level;

public class EntityPhysicsBlock extends EntityFallingBlock implements IEntityAdditionalSpawnData
{
	
	public boolean isAnvil2 = true;
	public boolean isBreakingAnvil2;
	public int fallHurtMax2;
	public float fallHurtAmount2;
	public boolean isLandSlide = false;
	public boolean earthquake = false;
	
	public int fallTime = 0;
	
	public Block block;
	public int meta;
	
	public EntityPhysicsBlock(World world)
	{
		super(world);
		this.isAnvil2 = true;
		this.fallHurtMax2 = 40;
		this.fallHurtAmount2 = 2.0F;
		
		if(EM_Settings.entityFailsafe > 0 && !world.isRemote)
		{
			@SuppressWarnings("unchecked")
			List<EntityPhysicsBlock> entityList = this.worldObj.getEntitiesWithinAABB(EntityPhysicsBlock.class, this.boundingBox.expand(8F, 8F, 8F));
			
			if(entityList.size() >= 1024)
			{
				if(EM_Settings.entityFailsafe == 1)
				{
					EnviroMine.logger.log(Level.WARN, "Entity fail safe activated! Canceling new entities!");
					EnviroMine.logger.log(Level.WARN, "Location: " + this.posX + "," + this.posY + "," + this.posZ);
					EnviroMine.logger.log(Level.WARN, "No.: " + entityList.size());
					EM_PhysManager.physSchedule.clear();
					this.setDead();
					return;
				} else if(EM_Settings.entityFailsafe >= 2)
				{
					EnviroMine.logger.log(Level.ERROR, "Entity fail safe activated! Deleting excess entities!");
					EnviroMine.logger.log(Level.ERROR, "Location: " + this.posX + "," + this.posY + "," + this.posZ);
					EnviroMine.logger.log(Level.ERROR, "No.: " + entityList.size());
					Iterator<EntityPhysicsBlock> iterator = entityList.iterator();
					
					while(iterator.hasNext())
					{
						EntityPhysicsBlock oldPhysBlock = iterator.next();
						if(!oldPhysBlock.isDead)
						{
							oldPhysBlock.setDead();
						}
					}
					this.setDead();
					
					EM_PhysManager.physSchedule.clear();
					return;
				}
			}
		}
	}
	
	public EntityPhysicsBlock(World world, double x, double y, double z, Block block, int meta, boolean update)
	{
		super(world, x, y, z, flowerID(block), meta);
		this.isAnvil2 = true;
		this.fallHurtMax2 = 40;
		this.fallHurtAmount2 = 2.0F;
		this.block = block;
		this.meta = meta;
		
		if(EM_Settings.entityFailsafe > 0 && !world.isRemote)
		{
			@SuppressWarnings("unchecked")
			List<EntityPhysicsBlock> entityList = this.worldObj.getEntitiesWithinAABB(EntityPhysicsBlock.class, this.boundingBox.expand(8F, 8F, 8F));
			
			if(entityList.size() >= 512)
			{
				if(EM_Settings.entityFailsafe == 1)
				{
					EnviroMine.logger.log(Level.WARN, "Entity fail safe activated: Level 1");
					EnviroMine.logger.log(Level.WARN, "Location: " + this.posX + "," + this.posY + "," + this.posZ);
					EnviroMine.logger.log(Level.WARN, "No.: " + entityList.size());
					EM_PhysManager.physSchedule.clear();
					this.setDead();
					return;
				} else if(EM_Settings.entityFailsafe >= 2)
				{
					EnviroMine.logger.log(Level.ERROR, "Entity fail safe activated: Level 2");
					EnviroMine.logger.log(Level.ERROR, "Location: " + this.posX + "," + this.posY + "," + this.posZ);
					EnviroMine.logger.log(Level.ERROR, "No.: " + entityList.size());
					Iterator<EntityPhysicsBlock> iterator = entityList.iterator();
					
					while(iterator.hasNext())
					{
						EntityPhysicsBlock oldPhysBlock = iterator.next();
						if(!oldPhysBlock.isDead)
						{
							oldPhysBlock.setDead();
						}
					}
					this.setDead();
					
					EM_PhysManager.physSchedule.clear();
					return;
				}
			}
		}
		
		EM_PhysManager.usedSlidePositions.add("" + MathHelper.floor_double(this.posX) + "," + MathHelper.floor_double(this.posZ));
		
		if(update)
		{
			EM_PhysManager.schedulePhysUpdate(world, (int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z), false, earthquake? "Quake" : "Collapse");
		}
	}
	
	public static Block flowerID(Block block)
	{
		if(block instanceof BlockFlower)
		{
			return Blocks.air;
		} else
		{
			return block;
		}
	}
	
	@Override
	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	public boolean canBeCollidedWith()
	{
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void onUpdate()
	{
		if(this.block == null || this.block == Blocks.air)
		{
			this.setDead();
		} else
		{
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			++this.fallTime;
			this.motionY -= 0.03999999910593033D;
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.9800000190734863D;
			this.motionY *= 0.9800000190734863D;
			this.motionZ *= 0.9800000190734863D;
			
			if(!this.worldObj.isRemote)
			{
				int i = MathHelper.floor_double(this.posX);
				int j = MathHelper.floor_double(this.posY);
				int k = MathHelper.floor_double(this.posZ);
				
				if(this.fallTime == 1)
				{
					if(this.worldObj.getBlock(i, j, k) != this.block && !isLandSlide)
					{
						this.setDead();
						return;
					}
					
					@SuppressWarnings("unchecked")
					List<Entity> before = ((List<Entity>)this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(1, 1, 1)));
					this.worldObj.setBlockToAir(i, j, k);
					@SuppressWarnings("unchecked")
					List<Entity> after = ((List<Entity>)this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(1, 1, 1)));
					
					for (Entity e : before) {
						after.remove(e);
					}
					
					for (Entity e : after) {
						e.setDead();
					}
				}
				
				try
				{
			        AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(this.worldObj, i, j - 1, k);
			        if(axisalignedbb != null)
			        {
			        	List fallingBlocks = this.worldObj.getEntitiesWithinAABB(EntityPhysicsBlock.class, axisalignedbb);
			        	
			        	fallingBlocks.remove(this);
			        	
				        if(fallingBlocks.size() >= 1 && isLandSlide)
				        {
				        	this.motionY = 0;
				        	this.setPosition(i + 0.5D, j + 0.5D, k + 0.5D);
				        }
			        }
				} catch(NullPointerException e)
				{
				}
				
				if(earthquake)
				{
					for(int jj = j; j >= MathHelper.ceiling_double_int(motionY); j --)
					{
						if(jj <= 5)
						{
							this.setDead();
							return;
						} else if(this.worldObj.getBlock(i, jj, k).getMaterial() == Material.lava)
						{
							this.setDead();
							return;
						} else if(this.worldObj.getBlock(i, jj, k).getMaterial() == Material.water)
						{
							this.setDead();
							return;
						} else if(this.worldObj.getBlock(i, jj, k) == Blocks.obsidian || this.worldObj.getBlock(i, jj, k) == Blocks.cobblestone)
						{
							if(this.worldObj.getBlock(i, jj - 1, k).getMaterial() == Material.lava)
							{
								this.setDead();
								return;
							}
						}
					}
				}
				
				if(this.onGround)
				{
					this.motionX *= 0.699999988079071D;
					this.motionZ *= 0.699999988079071D;
					this.motionY *= -0.5D;
					
					if(this.worldObj.getBlock(i, j, k) != Blocks.piston_extension)
					{
						this.setDead();
						
						if(!this.worldObj.canPlaceEntityOnSide(Blocks.anvil, i, j, k, true, 1, (Entity)null, (ItemStack)null) && !EM_PhysManager.blockNotSolid(this.worldObj, i, j, k, false))
						{
							j += 1;
						}
						
						if((this.block == Blocks.snow_layer && this.worldObj.getBlock(i, j, k) == Blocks.snow_layer && this.worldObj.getBlockMetadata(i, j, k) < 15) || (!this.isBreakingAnvil2 && this.worldObj.canPlaceEntityOnSide(Blocks.anvil, i, j, k, true, 1, (Entity)null, (ItemStack)null) && !BlockFalling.func_149831_e(this.worldObj, i, j - 1, k) && this.worldObj.setBlock(i, j, k, this.block, this.meta, 3)))
						{
							if(this.block == Blocks.snow_layer && this.worldObj.getBlock(i, j, k) == Blocks.snow_layer && this.worldObj.getBlockMetadata(i, j, k) < 15)
							{
								if(this.worldObj.getBlockMetadata(i, j, k) >= 14)
								{
									this.worldObj.setBlock(i, j, k, Blocks.snow);
								} else
								{
									this.worldObj.setBlockMetadataWithNotify(i, j, k, this.worldObj.getBlockMetadata(i, j, k) + 1, 3);
								}
							} else if(meta != this.worldObj.getBlockMetadata(i, j, k))
							{
								this.worldObj.setBlockMetadataWithNotify(i, j, k, meta, 2);
							}
							
							EM_PhysManager.schedulePhysUpdate(this.worldObj, i, j, k, true, earthquake? "Quake" : "Collapse");
							
							/*if(block instanceof BlockFalling)
							{
								((BlockFalling)block).func_149828_a(this.worldObj, i, j, k, this.meta);
							}*/
							
							Block bSurface = null;
							if(this.worldObj.getBlock(i, j - 1, k) != Blocks.air && this.worldObj.getBlock(i, j - 1, k).getMaterial() != Material.air)
							{
								bSurface = this.worldObj.getBlock(i, j -1 , k);
								this.worldObj.playSoundEffect((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), bSurface.stepSound.func_150496_b(), (bSurface.stepSound.getVolume() + 1.0F) / 2.0F, bSurface.stepSound.getPitch() * 0.5F);
							}
							
							if(bSurface == null || bSurface != this.block)
							{
								this.worldObj.playSoundEffect((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), this.block.stepSound.func_150496_b(), (this.block.stepSound.getVolume() + 1.0F) / 2.0F, this.block.stepSound.getPitch() * 0.5F);
							}
							
							if(this.field_145810_d != null && block instanceof ITileEntityProvider)
							{
								TileEntity tileentity = this.worldObj.getTileEntity(i, j, k);
								
								if(tileentity != null)
								{
									NBTTagCompound nbttagcompound = new NBTTagCompound();
									tileentity.writeToNBT(nbttagcompound);
									Iterator iterator = this.field_145810_d.func_150296_c().iterator();

                                    while (iterator.hasNext())
                                    {
                                        String s = (String)iterator.next();
                                        NBTBase nbtbase = this.field_145810_d.getTag(s);

                                        if (!s.equals("x") && !s.equals("y") && !s.equals("z"))
                                        {
                                            nbttagcompound.setTag(s, nbtbase.copy());
                                        }
                                    }

                                    tileentity.readFromNBT(nbttagcompound);
                                    tileentity.markDirty();
								}
							}
						} else if(this.field_145813_c && !this.isBreakingAnvil2 && !earthquake)
						{
							this.entityDropItem(new ItemStack(this.block, 1, this.block.damageDropped(this.meta)), 0.0F);
						}
					}
				} else if(this.fallTime > 100 && !this.worldObj.isRemote && (j < 1 || j > 256) || this.fallTime > 600)
				{
					if(this.field_145813_c && !earthquake)
					{
						this.entityDropItem(new ItemStack(this.block, 1, this.block.damageDropped(this.meta)), 0.0F);
					}
					
					this.setDead();
				}
			}
		}
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	protected void fall(float par1)
	{
		if(this.isAnvil2)
		{
			int i = MathHelper.ceiling_float_int(par1 - 1.0F);
			
			if(isLandSlide)
			{
				i = 2;
			}
			
			if(i > 0)
			{
				ArrayList arraylist = new ArrayList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox));
				
				DamageSource damagesource;
				
				if(isLandSlide)
				{
					if(this.block.getMaterial() == Material.snow)
					{
						damagesource = EnviroDamageSource.avalanche;
					} else
					{
						damagesource = EnviroDamageSource.landslide;
					}
				} else
				{
					damagesource = this.block == Blocks.anvil ? DamageSource.anvil : DamageSource.fallingBlock;
				}
				
				Iterator iterator = arraylist.iterator();
				
				while(iterator.hasNext())
				{
					Entity entity = (Entity)iterator.next();
					entity.attackEntityFrom(damagesource, (float)Math.min(MathHelper.floor_float((float)i * this.fallHurtAmount2), this.fallHurtMax2));
				}
				
				if(this.block == Blocks.anvil && (double)this.rand.nextFloat() < 0.05000000074505806D + (double)i * 0.05D)
				{
					int j = this.meta >> 2;
					int k = this.meta & 3;
					++j;
					
					if(j > 2)
					{
						this.isBreakingAnvil2 = true;
					} else
					{
						this.meta = k | j << 2;
					}
				}
			}
		}
	}


    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
	@Override
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeEntityToNBT(par1NBTTagCompound);
    	par1NBTTagCompound.setByte("Tile", (byte)Block.getIdFromBlock(this.block));
    	par1NBTTagCompound.setInteger("TileID", Block.getIdFromBlock(this.block));
    	par1NBTTagCompound.setByte("Data", (byte)this.meta);
        par1NBTTagCompound.setBoolean("HurtEntities2", this.isAnvil2);
        par1NBTTagCompound.setFloat("FallHurtAmount2", this.fallHurtAmount2);
        par1NBTTagCompound.setInteger("FallHurtMax2", this.fallHurtMax2);
        par1NBTTagCompound.setBoolean("Landslide", this.isLandSlide);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readEntityFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("TileID", 99))
        {
            this.block = Block.getBlockById(par1NBTTagCompound.getInteger("TileID"));
        }
        else
        {
            this.block = Block.getBlockById(par1NBTTagCompound.getByte("Tile") & 255);
        }
        
        this.meta = par1NBTTagCompound.getByte("Data") & 255;
        
        if (par1NBTTagCompound.hasKey("HurtEntities2"))
        {
            this.isAnvil2 = par1NBTTagCompound.getBoolean("HurtEntities2");
            this.fallHurtAmount2 = par1NBTTagCompound.getFloat("FallHurtAmount2");
            this.fallHurtMax2 = par1NBTTagCompound.getInteger("FallHurtMax2");
        }
        else if (this.block == Blocks.anvil)
        {
            this.isAnvil2 = true;
        }
        this.isLandSlide = par1NBTTagCompound.getBoolean("Landslide");
    }

	@Override
	public void writeSpawnData(ByteBuf buffer)
	{
		NBTTagCompound tags = new NBTTagCompound();
		this.writeEntityToNBT(tags);
		
		ByteBufUtils.writeTag(buffer, tags);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData)
	{
		NBTTagCompound tags = ByteBufUtils.readTag(additionalData);
		this.readEntityFromNBT(tags);
	}
}
