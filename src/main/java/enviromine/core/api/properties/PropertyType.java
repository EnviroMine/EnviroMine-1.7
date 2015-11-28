package enviromine.core.api.properties;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.IExtendedEntityProperties;

public abstract class PropertyType
{
	/**
	 * Is this property persistent through deaths (does not apply to hard resets or non-player entities)
	 */
	public boolean isPersistent()
	{
		return false;
	}
	
	/**
	 * Requires client-server property sync (sync will use tracker NBT data)
	 */
	public boolean SyncClient()
	{
		return false;
	}
	
	public abstract boolean isTrackable(EntityLivingBase entityLiving);
	public abstract String getTrackerID();
	public abstract PropertyTracker getNewTracker(EntityLivingBase entityLiving);
	
	/**
	 * Shortcut method for getting the corresponding tracker for this type and entity
	 * @param entityLiving
	 * @return
	 */
	public final PropertyTracker getTracker(EntityLivingBase entityLiving)
	{
		if(entityLiving == null || !this.isTrackable(entityLiving))
		{
			return null;
		}
		
		IExtendedEntityProperties props = entityLiving.getExtendedProperties(this.getTrackerID());
		
		if(props instanceof PropertyTracker)
		{
			return (PropertyTracker)props;
		} else
		{
			return null;
		}
	}
}
