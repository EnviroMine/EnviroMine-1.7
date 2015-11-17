package enviromine_temp.properties;

import net.minecraft.entity.EntityLivingBase;
import enviromine.core.api.properties.PropertyTracker;
import enviromine.core.api.properties.PropertyType;

public class PropertyTemp extends PropertyType
{
	@Override
	public boolean isTrackable(EntityLivingBase entityLiving)
	{
		return true;
	}

	@Override
	public String getTrackerID()
	{
		return "BODY_TEMP";
	}

	@Override
	public PropertyTracker getNewTracker(EntityLivingBase entityLiving)
	{
		return new TrackerTemp(this, entityLiving);
	}
	
	@Override
	public boolean SyncClient()
	{
		return true;
	}
}
