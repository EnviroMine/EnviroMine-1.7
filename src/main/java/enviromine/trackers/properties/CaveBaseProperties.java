package enviromine.trackers.properties;

import java.io.File;
import org.apache.logging.log4j.Level;
import net.minecraftforge.common.config.Configuration;
import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.helpers.PropertyBase;

public class CaveBaseProperties implements PropertyBase
{
	public static final CaveBaseProperties base = new CaveBaseProperties();
	static String[] CBName;
	
	public CaveBaseProperties()
	{
		// THIS CONSTRUCTOR IS FOR STATIC PURPOSES ONLY!
		
		if(base != null && base != this)
		{
			throw new IllegalStateException();
		}
	}

	@Override
	public String categoryName()
	{
		return "Main";
	}

	@Override
	public String categoryDescription()
	{
		return "The main options for the cave dimension";
	}

	@Override
	public void LoadProperty(Configuration config, String category)
	{
	}

	@Override
	public void SaveProperty(Configuration config, String category)
	{
	}

	@Override
	public void GenDefaults()
	{
		File file = this.GetDefaultFile();

		if(!file.exists())
		{
			try
			{
				file.createNewFile();
			} catch(Exception e)
			{
				EnviroMine.logger.log(Level.ERROR, "Failed to create file for StabilityTypes", e);
				return;
			}
		}
		
		Configuration config = new Configuration(file, true);
		String catName = this.categoryName();
		
		config.load();
		
		config.get(catName, CBName[0], -2).getInt(-2);
		config.get(catName, CBName[1], true).getBoolean(true);
		config.get(catName, CBName[2], false, "Makes the dimension more volcanic").getBoolean(false);
		config.get(catName, CBName[3], 30).getInt(30);
		config.get(catName, CBName[4], 7).getInt(7);
		config.get(catName, CBName[5], 8).getInt(8);
		config.get(catName, CBName[6], 23).getInt(23);
		config.get(catName, CBName[7], false).getBoolean(false);
		config.get(catName, CBName[8], 10).getInt(10);
		config.getInt(CBName[9], catName, 32, 0, 255, "Height at which water/lava generates");
		config.get(catName, CBName[10], true).getBoolean(true);
		config.get(catName, CBName[11], false).getBoolean(false);
		
		config.save();
	}

	@Override
	public File GetDefaultFile()
	{
		return new File(EM_ConfigHandler.configPath + "CaveDimension.cfg");
	}

	@Override
	public void generateEmpty(Configuration config, Object obj)
	{
	}

	@Override
	public boolean useCustomConfigs()
	{
		return false;
	}

	@Override
	public void customLoad()
	{
		File file = this.GetDefaultFile();

		if(!file.exists())
		{
			try
			{
				file.createNewFile();
			} catch(Exception e)
			{
				EnviroMine.logger.log(Level.ERROR, "Failed to create file for StabilityTypes", e);
				return;
			}
		}
		
		Configuration config = new Configuration(file, true);
		String catName = this.categoryName();
		
		config.load();
		
		EM_Settings.caveDimID = config.get(catName, CBName[0], -2).getInt(-2);
		EM_Settings.caveOreEvent = config.get(catName, CBName[1], true).getBoolean(true);
		EM_Settings.caveLava = config.get(catName, CBName[2], false, "Makes the dimension more volcanic").getBoolean(false);
		EM_Settings.caveRavineRarity = config.get(catName, CBName[3], 30).getInt(30);
		EM_Settings.caveTunnelRarity = config.get(catName, CBName[4], 7).getInt(7);
		EM_Settings.caveDungeons = config.get(catName, CBName[5], 8).getInt(8);
		EM_Settings.caveBiomeID = config.get(catName, CBName[6], 23).getInt(23);
		EM_Settings.disableCaves = config.get(catName, CBName[7], false).getBoolean(false);
		EM_Settings.limitElevatorY = config.get(catName, CBName[8], 10).getInt(10);
		EM_Settings.caveLiquidY = config.getInt(CBName[9], catName, 32, 0, 255, "Height at which water/lava generates");
		EM_Settings.caveFlood = config.get(catName, CBName[10], true).getBoolean(true);
		EM_Settings.caveRespawn = config.get(catName, CBName[11], false).getBoolean(false);
		
		config.save();
	}
	
	static
	{
		CBName = new String[12];
		CBName[0] = "Dimension ID";
		CBName[1] = "Fire OreGen event";
		CBName[2] = "Lava instead of Water";
		CBName[3] = "Ravine Rarity";
		CBName[4] = "Small Cave Rarity";
		CBName[5] = "Dungeons";
		CBName[6] = "Cave Biome ID";
		CBName[7] = "Disable Elevator Access";
		CBName[8] = "Elevator Height Limit";
		CBName[9] = "Water/Lava Height";
		CBName[10] = "Flood Side Caves";
		CBName[11] = "Can Respawn in Caves";
	}
}
