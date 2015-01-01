package enviromine.utils;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import enviromine.core.EnviroMine;

public class ModIdentification
{
	public static HashMap<String, String> modID_Name = new HashMap<String, String>();
	// Now uses full path (as a File object) instead of only the file name. This is to prevent identically named files in separate directories.
	public static HashMap<File, String> modSource_ID = new HashMap<File, String>();
	
	/**
	 * Good for UI purposes but not for config entries (use modID instead)
	 * @param obj
	 * @return
	 */
	public static String nameFromObject(Object obj)
	{
		String modID = idFromObject(obj);
		
		if(modID.equals("minecraft"))
		{
			return "Minecraft";
		} else if(modID_Name.containsKey(modID))
		{
			return modID_Name.get(modID);
		} else
		{
			return "Unknown";
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static String idFromObject(Object obj)
	{
		// Shortcut for known IDs
		if(obj instanceof ItemStack)
		{
			String tmpID = Item.itemRegistry.getNameForObject(((ItemStack)obj).getItem());
			return tmpID.isEmpty()? "unknown" : tmpID;
		} else if(obj instanceof Item)
		{
			String tmpID = Item.itemRegistry.getNameForObject(obj);
			return tmpID.isEmpty()? "unknown" : tmpID;
		} else if(obj instanceof Block)
		{
			String tmpID = Block.blockRegistry.getNameForObject(obj);
			return tmpID.isEmpty()? "unknown" : tmpID;
		} else if(obj instanceof Entity || (obj instanceof Class && Entity.class.isAssignableFrom((Class)obj)))
		{
			Class clazz;
			
			if(obj instanceof Entity)
			{
				clazz = obj.getClass();
			} else
			{
				clazz = (Class)obj;
			}
			
			EntityRegistration er = EntityRegistry.instance().lookupModSpawn((Class<? extends Entity>)clazz, true);
			
			if(er == null)
			{
				return "minecraft";
			}
			
			ModContainer mc =  er.getContainer();
			
			if(mc == null)
			{
				return "unknown";
			} else
			{
				return mc.getModId();
			}
		}
		
		// DANGER AREA: This is where non-standard objects are identified through their mod file location. May fail in some environments
		
		File file;
		String modName = "unknown";
		String fullPath = "";
		Class clazz = (obj instanceof Class ? (Class)obj : obj.getClass());
		
		// Slight changes here so you can throw raw classes at this and still get the correct result as well as converting the string to just a file object and additional error handling
		try
		{
			// Remove class path and URL prefix...
			
			if(clazz == null)
			{
				EnviroMine.logger.log(Level.ERROR, "ModID lookup failed for: NULL");
				return "unknown";
			}
			fullPath = clazz.getResource("").toString();
			int tmpIndex = fullPath.indexOf("file:/");
			fullPath = URLDecoder.decode(fullPath.substring(tmpIndex + "file:/".length()), "UTF-8");
			file = new File(fullPath);
		} catch(Exception e)
		{
			EnviroMine.logger.log(Level.INFO, "ModID lookup failed for: " + (obj instanceof Class? ((Class)obj).getName() : obj.getClass().getName()) + " {" + fullPath + "}", e);
			return modName;
		}
		
		for(File s : modSource_ID.keySet())
		{
			if(file.equals(s) || file.getAbsolutePath().startsWith(s.getAbsolutePath()))
			{
				modName = (String)modSource_ID.get(s);
				break;
			}
		}
		
		if(modName.equals("unknown"))
		{
			modName = OldIdentificationMethod(clazz);
		}
		
		if(modName.equals("unknown"))
		{
			EnviroMine.logger.log(Level.WARN, "Unable to find matching ModID for file: " + file.getAbsolutePath());
		} else if (modName.equals("Forge") || modName.equals("FML") || modName.equals("mcp"))
		{
			modName = "minecraft";
		}
		return modName;
	}
	
	/**
	 * Use the backup method for identifying the mod in the event the main one fails.
	 */
	private static String OldIdentificationMethod(Class<?> obj)
	{
		String modName = "unknown";
		String objPath = obj.getProtectionDomain().getCodeSource().getLocation().toString();
		
		try
		{
			objPath = URLDecoder.decode(objPath, "UTF-8");
		} catch (Exception e)
		{
			e.printStackTrace();
		}		
		
		for (File f: modSource_ID.keySet())
		{
			if (objPath.contains(f.getName()))
			{
				modName = modSource_ID.get(f);
				break;
			}
		}
		
		return modName;
	}
	
	static
	{
		for(ModContainer mod : Loader.instance().getModList())
		{
			modID_Name.put(mod.getModId(), mod.getName());
			modSource_ID.put(mod.getSource(), mod.getModId());
			
			EnviroMine.logger.log(Level.INFO, "Mapped mod: {" + mod.getSource().getAbsolutePath() + "," + mod.getName() + "," + mod.getModId() + "}");
		}
		
		// File names can be changed so these would not work in non-standard installations
		/*modSource_Name.put("1.6.2", "Minecraft");
		modSource_Name.put("1.6.3", "Minecraft");
		modSource_Name.put("1.6.4", "Minecraft");
		modSource_Name.put("1.7.2", "Minecraft");
		modSource_Name.put("1.7.10", "Minecraft");
		modSource_Name.put("Forge", "Minecraft");
		modSource_ID.put("1.6.2", "minecraft");
		modSource_ID.put("1.6.3", "minecraft");
		modSource_ID.put("1.6.4", "minecraft");
		modSource_ID.put("1.7.2", "minecraft");
		modSource_ID.put("1.7.10", "minecraft");
		modSource_ID.put("Forge", "minecraft");*/
	}
}
