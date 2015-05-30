package enviromine.core.api;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import enviromine.core.EnviroMine;

public class PropertyRegistry
{
	static HashMap<String, PropertyType> registry = new HashMap<String, PropertyType>();
	
	public static void RegisterProperty(PropertyType property, Object modInstance, String id)
	{
		ModContainer modContainer = modInstance == null? null : Loader.instance().getReversedModObjectList().get(modInstance);
		
		if(modContainer == null)
		{
			EnviroMine.logger.log(Level.ERROR, "Tried to register EnviroProperty with null or unknown mod instance!", new IllegalArgumentException());
			return;
		} else if(registry.containsKey(modContainer.getModId() + ":" + id))
		{
			EnviroMine.logger.log(Level.ERROR, "Cannot register duplicate property name '" + modContainer.getModId() + ":" + id + "'", new IllegalStateException());
			return;
		}
		
		registry.put(modContainer.getModId() + ":" + id, property);
	}
	
	public static String GetID(PropertyType property)
	{
		for(String key : registry.keySet())
		{
			if(registry.get(key) == property)
			{
				return key;
			}
		}
		
		return "";
	}
	
	public static PropertyType GetProperty(String id)
	{
		return registry.get(id);
	}
	
	public static ArrayList<PropertyType> getAllTypes()
	{
		ArrayList<PropertyType> typeList = new ArrayList<PropertyType>();
		
		typeList.addAll(registry.values());
		
		return typeList;
	}
}
