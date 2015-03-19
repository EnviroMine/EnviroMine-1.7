package enviromine.utils;

import java.util.ArrayList;
import enviromine.client.gui.EM_GuiAuthWarn;
import enviromine.core.FunwayModAuthentication;

public abstract class LockedClass
{
	private static final ArrayList<Class<?>> LOCKED = new ArrayList<Class<?>>();
	
	public static final ArrayList<Class<?>> GetUnlocked()
	{
		// This may be a private static function but only the authentication system may actually use it
		if(!new Exception().getStackTrace()[1].getClassName().equals(FunwayModAuthentication.class.getName()))
		{
			throw new IllegalStateException("ENVIROMINE WAS TAMPERED WITH DURING AUTHENTICATION");
		}
		
		return LOCKED;
	}
	
	/*public static boolean IsLocked()
	{
		return LOCKED.size() <= 0;
	}*/
	
	{
		// Note that is isn't static. This is because if something like forge needs to scan the class we don't want this to trigger
		if(!LOCKED.contains(this.getClass()))
		{
			// MOD IS NOT AUTHENTICATED!
			//SecurityException exception = new SecurityException("UNAUTHORIZED USE OF MOD: " + EM_Settings.ModID.toUpperCase());
			//exception.setStackTrace(new StackTraceElement[]{}); // Empties the stack trace to hinder debugging hacks
			//throw exception;
			EM_GuiAuthWarn.shouldWarn = true;
		}
	}
}
