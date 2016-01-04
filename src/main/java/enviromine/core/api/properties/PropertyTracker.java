package enviromine.core.api.properties;

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
	public final void saveNBTData(NBTTagCompound compound) // Forwards main tracker tag onto a property specific tag
	{
		NBTTagCompound propTags = new NBTTagCompound();
		this.saveNBT(propTags);
		compound.setTag("ENVIROMINE_" + type.getTrackerID(), propTags);
	}
	
	@Override
	public final void loadNBTData(NBTTagCompound compound) // Forwards main tracker tag onto a property specific tag
	{
		this.loadNBT(compound.getCompoundTag("ENVIROMINE_" + type.getTrackerID()));
	}
	
	public abstract void saveNBT(NBTTagCompound tags);
	
	public abstract void loadNBT(NBTTagCompound tags);
	
	@Override
	public void init(Entity entity, World world)
	{
		if(entity instanceof EntityLivingBase)
		{
			this.entityLiving = (EntityLivingBase)entity;
		} else
		{
			this.entityLiving = null;
		}
	}
	
	public abstract void Reset();
	
	public void onSpawn()
	{
	}
	
	public void onClone(EntityPlayer oldPlayer, EntityPlayer newPlayer)
	{
	}
	
	public void onDeath()
	{
	}
	
	public void onLivingUpdate()
	{
	}
}
