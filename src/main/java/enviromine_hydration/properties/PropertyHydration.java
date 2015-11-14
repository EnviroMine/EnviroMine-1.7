package enviromine_hydration.properties;

import net.minecraft.entity.EntityLivingBase;
import enviromine.core.api.properties.PropertyRenderer;
import enviromine.core.api.properties.PropertyTracker;
import enviromine.core.api.properties.PropertyType;

public class PropertyHydration extends PropertyType
{
	RendererHydration renderer = new RendererHydration();
	
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
	
	@Override
	public PropertyRenderer getGuiRenderer()
	{
		return renderer;
	}
}
