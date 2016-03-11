package enviromine.core;

import java.io.File;
import java.util.ArrayList;
import net.minecraftforge.common.config.Configuration;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import enviromine.core.api.config.Attribute;
import enviromine.core.api.config.AttributeManager;
import enviromine.core.api.config.ConfigKey;
import enviromine.core.api.config.ConfigKeyManager;
import enviromine.core.api.config.ConfigRegistry;
import enviromine.core.api.helpers.JsonHelper;

public class ConfigLoader
{
	public static final File rootDir = new File("config/enviromine/");
	
	public static void LoadConfigs()
	{
		Configuration config = new Configuration(new File(rootDir, "EnviroMine.cfg"), true);
		
		config.load();
		
		Settings.profile = config.getString("Config Profile", Configuration.CATEGORY_GENERAL, "default", "The custom config profile directory to use");
		
		config.save();
		
		LoadCustomAttributes(getProfileDir(Settings.profile));
	}
	
	public static void LoadCustomAttributes(File rootDir)
	{
		ConfigRegistry.ResetAllAttributes();
		
		ArrayList<JsonObject> configList = getConfigList(rootDir);
		
		for(JsonObject config : configList)
		{
			for(ConfigKeyManager km : ConfigRegistry.getList_KM())
			{
				for(JsonElement subCat : JsonHelper.GetArray(config, km.CategoryName()))
				{
					if(subCat == null || !subCat.isJsonObject())
					{
						continue;
					}
					
					ConfigKey key = km.getKey(subCat.getAsJsonObject());
					
					for(AttributeManager am : ConfigRegistry.getList_AM(km))
					{
						Attribute att = am.getAttribute(key);
						att.loadFromConfig(JsonHelper.GetObject(subCat.getAsJsonObject(), am.getConfigID()));
					}
				}
			}
		}
	}
	
	public static File getProfileDir(String name)
	{
		File profDir = new File(rootDir, "/profiles/" + MakeFileSafe(name));
		
		if(!profDir.exists())
		{
			profDir.mkdirs();
		}
		
		return profDir;
	}
	
	public static ArrayList<JsonObject> getConfigList(File dir)
	{
		ArrayList<JsonObject> configs = new ArrayList<JsonObject>();
		File[] list = dir.listFiles();
		
		if(list == null)
		{
			return configs;
		}
		
		for(File f : list)
		{
			if(f.getName().endsWith(".json"))
			{
				configs.add(JsonHelper.ReadObjectFromFile(f));
			}
		}
		
		return configs;
	}
	
    public static final String[] reservedNames = new String[] {"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
    public static final char[] specialCharacters = new char[] {'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
	
	public static String MakeFileSafe(String text)
	{
		for(String reserved : reservedNames)
		{
			if(text.equalsIgnoreCase(reserved))
			{
				text = "_" + text + "_";
			}
		}
		
		for(char badChar : specialCharacters)
		{
			text = text.replace(badChar, '_');
		}
		
		return text;
	}
}
