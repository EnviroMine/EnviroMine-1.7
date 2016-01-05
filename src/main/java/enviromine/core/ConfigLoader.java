package enviromine.core;

import java.io.File;
import java.util.ArrayList;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import enviromine.core.api.config.Attribute;
import enviromine.core.api.config.AttributeManager;
import enviromine.core.api.config.ConfigKey;
import enviromine.core.api.config.ConfigKeyManager;
import enviromine.core.api.config.ConfigRegistry;

public class ConfigLoader
{
	public static final File rootDir = new File("config/enviromine/");
	
	public static void LoadConfigs()
	{
		Configuration config = new Configuration(new File(rootDir, "EnviroMine.cfg"), true);
		
		config.load();
		
		config.getString("Config Profile", Configuration.CATEGORY_GENERAL, "default", "The custom config profile directory to use");
		
		config.save();
		
		LoadCustomAttributes(getProfileDir(Settings.profile));
	}
	
	public static void LoadCustomAttributes(File rootDir)
	{
		ConfigRegistry.ResetAllAttributes();
		
		ArrayList<Configuration> configList = getConfigList(rootDir);
		
		for(Configuration config : configList)
		{
			config.load();
			
			for(ConfigKeyManager km : ConfigRegistry.getList_KM())
			{
				for(ConfigCategory subCat : config.getCategory(km.CategoryName()).getChildren())
				{
					ConfigKey key = km.getKey(config, subCat);
					
					for(AttributeManager am : ConfigRegistry.getList_AM(km))
					{
						Attribute att = am.getAttribute(key);
						att.loadFromConfig(config, subCat + Configuration.CATEGORY_SPLITTER + am.getConfigID());
					}
				}
			}
			
			config.save();
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
	
	public static ArrayList<Configuration> getConfigList(File dir)
	{
		ArrayList<Configuration> configs = new ArrayList<Configuration>();
		File[] list = dir.listFiles();
		
		if(list == null)
		{
			return configs;
		}
		
		for(File f : list)
		{
			if(f.getName().endsWith(".cfg"))
			{
				configs.add(new Configuration(f, true));
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
