package enviromine.core.api;

import enviromine.core.managers.PropertyManager.PropertyTracker;
import net.minecraft.nbt.NBTTagCompound;

public abstract class EnviroProperty
{
	/**
	 * Is this property persistent through deaths (does not apply to hard resets)
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
	
	public abstract boolean RequiresSync();
	
	public abstract void Update(PropertyTracker tracker);
	
	public abstract void Reset();
	
	public abstract NBTTagCompound writeToNBT(NBTTagCompound tag);
	
	public abstract void readFromNBT(NBTTagCompound tag);
}
