package enviromine.core.api;

import java.util.HashMap;
import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
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
	 * @return An integer representing the interval in ticks between each update pass
	 */
	public int TickInterval()
	{
		return 20;
	}
	
	/**
	 * Requires client-server property sync (sync will use tracker NBT data)
	 */
	public boolean SyncClient()
	{
		return false;
	}
	
	public boolean doScan()
	{
		return false;
	}
	
	/**
	 * Scans the given location for blocks that could influence this property<br>
	 * <b>NOTES:</b><br>
	 * - This runs multiple times per tick and will loop back around when loopIdx reaches (<b><i>scanRange</i></b>*2)^3<br>
	 * - Relative position is NOT static between passes and should not be assumed to be so<br>
	 * - Averaging the values obtained from the scan loop is up to the module creator<br>
	 * - Ticks may be skipped if TPS drops too low
	 */
	public void scanLocation(PropertyTracker tracker, World world, int x, int y, int z, int loopIdx)
	{
	}
	
	/**
	 * How far does this property scan? (radius)<br>
	 * Keep low to prevent TPS latency
	 */
	public byte scanRange()
	{
		return 5;
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
