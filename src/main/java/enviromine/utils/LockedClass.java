package enviromine.utils;

import java.util.ArrayList;
import enviromine.core.EM_Settings;

public abstract class LockedClass
{
	public static final ArrayList<Class<?>> LOCKED = new ArrayList<Class<?>>();
	
	{
		if(!LOCKED.contains(this.getClass()))
		{
			throw new SecurityException("UNAUTHORIZED USE OF MOD " + EM_Settings.ModID.toUpperCase());
		}
	}
}
