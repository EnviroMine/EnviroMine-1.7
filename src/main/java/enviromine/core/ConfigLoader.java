package enviromine.core;

import java.io.File;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import enviromine.core.api.attributes.Attribute;
import enviromine.core.api.attributes.AttributeManager;
import enviromine.core.api.attributes.AttributeRegistry;
import enviromine.core.api.attributes.AttributeRegistry.Type;

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
		ResetAllAttributes();
		
		ArrayList<Configuration> configList = getConfigList(rootDir);
		
		for(Configuration config : configList)
		{
			config.load();
			
			ConfigCategory bCat = config.getCategory("Blocks");
			
			for(ConfigCategory subCat : bCat.getChildren())
			{
				String entryName = subCat.getQualifiedName();
				
				if(!config.hasKey(entryName, "BlockID"))
				{
					continue;
				}
				
				Block block = (Block)Block.blockRegistry.getObject(config.getString("BlockID", entryName, "", "Full block ID"));
				int[] metaList = config.get(entryName, "Metadata", new int[0], "Metadata list. Leave blank for wildcard").getIntList();
				metaList = metaList.length > 0? metaList : new int[]{-1};
				
				if(block == null)
				{
					continue;
				}
				
				for(AttributeManager manager : AttributeRegistry.getManagerList(Type.BLOCK))
				{
					for(int meta : metaList)
					{
						Attribute att = manager.getBlockAttribute(block, meta);
						att.loadFromConfig(config, entryName + Configuration.CATEGORY_SPLITTER + manager.getConfigID());
					}
				}
			}
			
			ConfigCategory iCat = config.getCategory("Items");
			
			for(ConfigCategory subCat : iCat.getChildren())
			{
				String entryName = subCat.getQualifiedName();
				
				if(!config.hasKey(entryName, "ItemID"))
				{
					continue;
				}
				
				Item item = (Item)Item.itemRegistry.getObject(config.getString("ItemID", entryName, "", "Full item ID"));
				
				if(item == null)
				{
					continue;
				}
				
				int[] dmgList = item.isDamageable()? new int[]{-1} : config.get(entryName, "Damage", new int[]{}, "Damage list. Leave blank for wildcard (doesn't apply to damageables)").getIntList();
				dmgList = dmgList.length > 0? dmgList : new int[]{-1};
				
				for(AttributeManager manager : AttributeRegistry.getManagerList(Type.ITEM))
				{
					for(int dmg : dmgList)
					{
						Attribute att = manager.getItemAttribute(item, dmg);
						att.loadFromConfig(config, entryName + Configuration.CATEGORY_SPLITTER + manager.getConfigID());
					}
				}
			}
			
			ConfigCategory eCat = config.getCategory("Entities");
			
			for(ConfigCategory subCat : eCat.getChildren())
			{
				String entryName = subCat.getQualifiedName();
				
				if(!config.hasKey(entryName, "EntityID"))
				{
					continue;
				}
				
				// Get entity instance without world?
				/*Entity entity = EntityList.createEntityByName(config.getString("EntityID", entryName, "", "Full entity ID name"), world);
				
				for(AttributeManager manager : AttributeRegistry.getManagerList(Type.ITEM))
				{
					Attribute att = manager.getEntityAttribute(entity);
					att.loadFromConfig(config, entryName + Configuration.CATEGORY_SPLITTER + manager.getConfigID());
				}*/
			}
			
			config.save();
		}
	}
	
	public static void ResetAllAttributes()
	{
		for(AttributeManager manager : AttributeRegistry.getManagerList(Type.BLOCK))
		{
			manager.ResetCache();
		}
		
		for(AttributeManager manager : AttributeRegistry.getManagerList(Type.ITEM))
		{
			manager.ResetCache();
		}
		
		for(AttributeManager manager : AttributeRegistry.getManagerList(Type.ENTITY))
		{
			manager.ResetCache();
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
