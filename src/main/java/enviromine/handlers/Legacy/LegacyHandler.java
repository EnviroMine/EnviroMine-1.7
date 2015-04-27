package enviromine.handlers.Legacy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class LegacyHandler 
{
	public final static HashMap<String, Object> LegacyList = new HashMap<String, Object>();
	
	public LegacyHandler()
	{

	}
	/** Add Registered Legacys here
	 * 
	 */
	public static void preInit()
	{
		RegisterLegacy("ConfigHandlerLegacy", new ConfigLegacy());
	}
	
	public static void init()
	{				
		Iterator it = LegacyList.entrySet().iterator();
		
		while(it.hasNext())
		{
			Map.Entry pair = (Map.Entry)it.next();
			
			LegacyHandler legacyFile = (LegacyHandler) pair.getValue();
		       			
			if(legacyFile.initCheck())
			{
				legacyFile.runLegacy();
			}
			
		}	
	}
	
	public static void postInit()
	{
		
	}
	
	public static void RegisterLegacy(String key, LegacyHandler legacyFile)
	{
		if(!LegacyList.containsValue(legacyFile))
		{
			LegacyList.put(key, legacyFile);
		}
	}
	
	public static LegacyHandler getByKey(String key)
	{
		return (LegacyHandler) LegacyList.get(key);
	}
	
	public abstract boolean initCheck();
	
	//public abstract boolean runOrder();
	
	public abstract void runLegacy();
	
	public abstract boolean didRun();
	
}
