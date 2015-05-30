package enviromine.core.api;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public abstract class PropertyTracker implements IExtendedEntityProperties
{
	public EntityLivingBase entityLiving;
	public final PropertyType type;
	
	public PropertyTracker(PropertyType type, EntityLivingBase entityLiving)
	{
		this.type = type;
		this.entityLiving = entityLiving;
	}
	
	@Override
	public abstract void saveNBTData(NBTTagCompound compound);
	
	@Override
	public abstract void loadNBTData(NBTTagCompound compound);
	
	@Override
	public void init(Entity entity, World world)
	{
		if(entity instanceof EntityLivingBase)
		{
			if(entityLiving != entity)
			{
				this.onClone(entityLiving, entity);
			}
			
			this.entityLiving = (EntityLivingBase)entity;
		} else
		{
			this.entityLiving = null;
		}
	}
	
	public void onSpawn()
	{
		if(type.isPersistent() && entityLiving instanceof EntityPlayer && !entityLiving.worldObj.isRemote)
		{
			UUID id = entityLiving.getUniqueID();
			
			if(entityLiving.isEntityAlive() && type.persistMap.containsKey(id))
			{
				PropertyTracker tracker = type.persistMap.get(id);
				NBTTagCompound oldData = new NBTTagCompound();
				tracker.saveNBTData(oldData);
				loadNBTData(oldData);
				type.persistMap.remove(id);
			} else if(!entityLiving.isEntityAlive() && !type.persistMap.containsKey(id))
			{
				type.persistMap.put(id, this);
			}
		}
	}
	
	public void onClone(Entity oldEntity, Entity newEntity)
	{
	}
	
	public void onDeath()
	{
		if(!type.isPersistent())
		{
			this.Reset();
		}
	}
	
	public void Reset()
	{
	}
	
	public void onLivingUpdate()
	{
	}
}
