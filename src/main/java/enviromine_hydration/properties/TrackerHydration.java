package enviromine_hydration.properties;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import enviromine.core.api.properties.PropertyTracker;
import enviromine.core.api.properties.PropertyType;

public class TrackerHydration extends PropertyTracker
{
	public TrackerHydration(PropertyType type, EntityLivingBase entityLiving)
	{
		super(type, entityLiving);
	}
	
	@Override
	public void saveNBT(NBTTagCompound tags)
	{
	}
	
	@Override
	public void loadNBT(NBTTagCompound tags)
	{
	}
}
