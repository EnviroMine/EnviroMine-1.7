package enviromine_temp;

import net.minecraft.entity.EntityLivingBase;
import enviromine.core.api.PropertyTracker;
import enviromine.core.api.PropertyType;

public class PropertyTemperature extends PropertyType
{
	@Override
	public boolean isTrackable(EntityLivingBase entityLiving)
	{
		return true;
	}

	@Override
	public String getTrackerID()
	{
		return "ENVIRO_TEMP";
	}

	@Override
	public PropertyTracker getNewTracker(EntityLivingBase entityLiving)
	{
		return null;
	}
}
