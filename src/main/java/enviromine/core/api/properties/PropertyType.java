package enviromine.core.api.properties;

import java.util.HashMap;
import java.util.UUID;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.core.api.properties.PropertyRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.IExtendedEntityProperties;

public abstract class PropertyType
{
	public HashMap<UUID, PropertyTracker> persistMap = new HashMap<UUID, PropertyTracker>();
	
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
	
	/**
	 * NOTE: Do not re-instantiate a new renderer every time this is called.
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public PropertyRenderer getGuiRenderer()
	{
		return null;
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
