package enviromine_hydration.properties;

import net.minecraft.entity.EntityLivingBase;
import enviromine.core.api.properties.PropertyTracker;
import enviromine.core.api.properties.PropertyType;

public class PropertyHydration extends PropertyType
{
	@Override
	public boolean isTrackable(EntityLivingBase entityLiving)
	{
		return true;
	}
	
	@Override
	public String getTrackerID()
	{
		return "HYDRATION";
	}
	
	@Override
	public PropertyTracker getNewTracker(EntityLivingBase entityLiving)
	{
		return new TrackerHydration(this, entityLiving);
	}
	
	@Override
	public boolean SyncClient()
	{
		return true;
	}
}
