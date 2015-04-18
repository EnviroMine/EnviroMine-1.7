package enviromine.trackers.properties;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.entity.EntityList;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;
import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.helpers.PropertyBase;
import enviromine.utils.EnviroUtils;

public class CaveSpawnProperties implements PropertyBase
{
	public static final CaveSpawnProperties base = new CaveSpawnProperties();
	static String[] CGPNames;
	
	//Class<? extends EntityLiving> clazz;
	public int id;
	public int weight;
	public int minGroup;
	public int maxGroup;
	
	public CaveSpawnProperties()
	{
		// THIS CONSTRUCTOR IS FOR STATIC PURPOSES ONLY!
		
		if(base != null && base != this)
		{
			throw new IllegalStateException();
		}
	}
	
	public CaveSpawnProperties(int id, int weight, int minGroup, int maxGroup)
	{
		this.id = id;
		this.weight = weight;
		this.minGroup = minGroup;
		this.maxGroup = maxGroup;
	}

	@Override
	public String categoryName()
	{
		return "Cave Entities";
	}

	@Override
	public String categoryDescription()
	{
		return "Changes what entities can spawn in the cave dimension";
	}

	@Override
	public void LoadProperty(Configuration config, String category)
	{
		config.setCategoryComment(this.categoryName(), this.categoryDescription());
		int nID = config.get(category, CGPNames[0], 0).getInt(0);
		int nWeight = config.get(category, CGPNames[1], 100).getInt(100);
		int nMin = config.get(category, CGPNames[2], 4).getInt(4);
		int nMax = config.get(category, CGPNames[3], 4).getInt(4);
		
		CaveSpawnProperties entry = new CaveSpawnProperties(nID, nWeight, nMin, nMax);
		EM_Settings.caveSpawnProperties.put(nID, entry);
	}

	@Override
	public void SaveProperty(Configuration config, String category)
	{
		config.get(category, CGPNames[0], id);
		config.get(category, CGPNames[1], weight);
		config.get(category, CGPNames[2], minGroup);
		config.get(category, CGPNames[3], maxGroup);
	}

	@Override
	public void GenDefaults()
	{
		File file = GetDefaultFile();

		if(!file.exists())
		{
			try
			{
				file.createNewFile();
			} catch(Exception e)
			{
				EnviroMine.logger.log(Level.ERROR, "Failed to create file for Cave Ores", e);
				return;
			}
		}
		
		Configuration config = new Configuration(file, true);
		
		config.load();
		
		if(config.hasCategory(this.categoryName()))
		{
			config.save();
			return;
		}
				
		if(!config.hasCategory(this.categoryName() + ".Bat"))
		{
			String catName = this.categoryName() + ".Bat";
			
			config.get(catName, CGPNames[0], 65);
			config.get(catName, CGPNames[1], 100);
			config.get(catName, CGPNames[2], 4);
			config.get(catName, CGPNames[3], 8);
		}
		
		if(!config.hasCategory(this.categoryName() + ".Creeper"))
		{
			String catName = this.categoryName() + ".Creeper";
			
			config.get(catName, CGPNames[0], 50);
			config.get(catName, CGPNames[1], 1);
			config.get(catName, CGPNames[2], 1);
			config.get(catName, CGPNames[3], 1);
		}
		
		if(!config.hasCategory(this.categoryName() + ".Silverfish"))
		{
			String catName = this.categoryName() + ".Silverfish";
			
			config.get(catName, CGPNames[0], 50);
			config.get(catName, CGPNames[1], 100);
			config.get(catName, CGPNames[2], 1);
			config.get(catName, CGPNames[3], 4);
		}
		
		config.save();
	}

	@Override
	public File GetDefaultFile()
	{
		return new File(EM_ConfigHandler.loadedProfile + "CaveDimension.cfg");
	}
	
	@Override
	public void generateEmpty(Configuration config, Object obj)
	{
		if(obj == null || !(obj instanceof Integer))
		{
			EnviroMine.logger.log(Level.ERROR, "Tried to register config with non entity object!", new Exception());
			return;
		}
		
		int eID = (Integer)obj;
		
		String catName = this.categoryName() + "." + EnviroUtils.replaceULN(EntityList.getStringFromID(eID));
		
		config.get(catName, CGPNames[0], eID);
		config.get(catName, CGPNames[1], 100);
		config.get(catName, CGPNames[2], 4);
		config.get(catName, CGPNames[3], 4);
	}

	@Override
	public boolean useCustomConfigs()
	{
		return false;
	}
	
	@Override
	public void customLoad()
	{
		Configuration config;
		
		try
		{
			config = new Configuration(this.GetDefaultFile(), true);
			
		} catch(Exception e)
		{
			EnviroMine.logger.log(Level.ERROR, "Failed to load custom configuration for " + this.getClass().getSimpleName(), e);
			return;
		} 
		
		config.load();

		ArrayList<String> entries = EM_ConfigHandler.getSubCategories(config, this.categoryName());
		Iterator<String> iterator = entries.iterator();
		
		while(iterator.hasNext())
		{
			this.LoadProperty(config, iterator.next());
		}
		
		config.save();
	}
	
	/**
	 * Sets up all of the static information about this property type
	 */
	static
	{
		EM_Settings.caveSpawnProperties = new HashMap<Integer, CaveSpawnProperties>();
		
		CGPNames = new String[8];
		CGPNames[0] = "01.Entity ID";
		CGPNames[1] = "02.Spawn Weight";
		CGPNames[2] = "03.Min Group Size";
		CGPNames[3] = "04.Max Group Size";
	}
}
