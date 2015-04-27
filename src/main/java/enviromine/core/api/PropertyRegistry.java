package enviromine.core.api;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.logging.log4j.Level;
import net.minecraft.entity.EntityLivingBase;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import enviromine.core.EnviroMine;

public class PropertyRegistry
{
	static HashMap<String, Class<? extends EnviroProperty>> registry = new HashMap<String, Class<? extends EnviroProperty>>();
	
	public static void RegisterProperty(Class<? extends EnviroProperty> property, Object modInstance, String id)
	{
		ModContainer modContainer = modInstance == null? null : Loader.instance().getReversedModObjectList().get(modInstance);
		
		if(modContainer == null)
		{
			throw new IllegalArgumentException("Tried to register EnviroProperty with null or unknown mod instance!");
		} else if(registry.containsKey(modContainer.getModId() + ":" + id))
		{
			throw new IllegalStateException("Cannot register duplicate property name '" + modContainer.getModId() + ":" + id + "'");
		}
		
		try
		{
			if(property.getConstructor() == null)
			{
				throw new NoSuchMethodException();
			}
		} catch(Exception e)
		{
			EnviroMine.logger.log(Level.ERROR, "EnviroProperty " + property.getSimpleName() + " is missing necessary constructor 'public " + property.getSimpleName() + "()'", e);
			return;
		}
		
		registry.put(modContainer.getModId() + ":" + id, property);
	}
	
	public static String GetID(Class<? extends EnviroProperty> property)
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
	
	public static Class<? extends EnviroProperty> GetProperty(String id)
	{
		return registry.get(id);
	}
	
	public static EnviroProperty[] InstatiateNewList(EntityLivingBase entityLiving)
	{
		ArrayList<EnviroProperty> propList = new ArrayList<EnviroProperty>();
		
		for(Class<? extends EnviroProperty> propClass : registry.values())
		{
			if(propClass == null)
			{
				continue;
			}
			
			try
			{
				propList.add(propClass.getConstructor(EntityLivingBase.class).newInstance(entityLiving));
			} catch(Exception e)
			{
				EnviroMine.logger.log(Level.ERROR, "Unable to instantiate property " + propClass.getSimpleName(), e);
				continue;
			}
		}
		
		return propList.toArray(new EnviroProperty[]{});
	}
}
