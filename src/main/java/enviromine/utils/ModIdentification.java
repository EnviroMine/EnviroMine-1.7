package enviromine.utils;

import enviromine.core.EnviroMine;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import org.apache.logging.log4j.Level;

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
		}
		
		File file;
		String modName = "unknown";
		String fullPath = "";
		// Slight changes here so you can throw raw classes at this and still get the correct result as well as converting the string to just a file object and additional error handling
		try
		{
			// Remove class path and URL prefix...
			Class clazz = (obj instanceof Class ? (Class)obj : obj.getClass());
			
			if(clazz == null)
			{
				EnviroMine.logger.log(Level.ERROR, "ModID lookup failed for: NULL");
				return "unknown";
			}
			
			URL url = clazz.getResource("");
			if (url == null)
			{
				EnviroMine.logger.log(Level.ERROR, "ModID lookup failed for: "+clazz.getCanonicalName());
				return "unknown";
			}
			
			fullPath = url.toString();
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
			EnviroMine.logger.log(Level.WARN, "Unable to find matching ModID for file: " + file.getAbsolutePath());
		} else if (modName.equals("Forge") || modName.equals("FML") || modName.equals("mcp"))
		{
			modName = "minecraft";
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
