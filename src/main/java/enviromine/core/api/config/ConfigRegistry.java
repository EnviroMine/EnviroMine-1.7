package enviromine.core.api.config;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.logging.log4j.Level;
import enviromine.core.EnviroMine;
import enviromine.core.api.config.def.*;

public class ConfigRegistry
{
	static HashMap<ConfigKeyManager, ArrayList<AttributeManager>> attManagers = new HashMap<ConfigKeyManager, ArrayList<AttributeManager>>();
	
	public static final KeyManagerBlocks BLOCK = new KeyManagerBlocks();
	public static final KeyManagerEntities ENTITY = new KeyManagerEntities();
	public static final KeyManagerItems ITEM = new KeyManagerItems();
	
	public static boolean registerManager(ConfigKeyManager km, AttributeManager am)
	{
		if(km == null || am == null)
		{
			EnviroMine.logger.log(Level.ERROR, "Unabled to register attribute manager: Null Argument");
			return false;
		}
		
		if(attManagers.containsKey(km) && !attManagers.get(km).contains(am))
		{
			attManagers.get(km).add(am);
		} else if(!attManagers.containsKey(km))
		{
			if(getKeyManager(km.CategoryName()) != null)
			{
				EnviroMine.logger.log(Level.ERROR, "Unabled to register key manager: Category Name Conflict");
				return false;
			}
			
			ArrayList<AttributeManager> list = new ArrayList<AttributeManager>();
			list.add(am);
			attManagers.put(km, list);
		} else
		{
			EnviroMine.logger.log(Level.ERROR, "Unabled to register attribute manager: Duplicate Entry");
			return false;
		}
		
		return true;
	}
	
	public static void ResetAllAttributes()
	{
		for(ArrayList<AttributeManager> list : attManagers.values())
		{
			for(AttributeManager entry : list)
			{
				entry.ResetCache();
				entry.LoadDefaults();
			}
		}
	}
	
	public static ConfigKeyManager getKeyManager(String configID)
	{
		for(ConfigKeyManager entry : attManagers.keySet())
		{
			if(entry.CategoryName().equalsIgnoreCase(configID))
			{
				return entry;
			}
		}
		
		return null;
	}
	
	public static ArrayList<ConfigKeyManager> getList_KM()
	{
		return new ArrayList<ConfigKeyManager>(attManagers.keySet());
	}
	
	public static ArrayList<AttributeManager> getList_AM(ConfigKeyManager keyManager)
	{
		return new ArrayList<AttributeManager>(attManagers.get(keyManager));
	}
}
