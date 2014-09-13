package enviromine.trackers.properties;

import java.io.File;
import java.io.IOException;

import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;

import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;

public class DimensionProperties
{
	public int id;
	public boolean override;
	public boolean trackSanity;
	public boolean darkAffectSanity;
	public double sanityMultiplyer;
	public boolean trackAirQuality;
	public double airMulti;
	public boolean trackHydration;
	public double hydrationMulti;
	public boolean trackTemp;
	public double tempMulti;
	public boolean dayNightTemp;
	public boolean weatherAffectsTemp;
	public boolean mineshaftGen;
	public int sealevel;
	
	static String[] DMName;
	
	public static String categoryName = "dimensions";
	
	public DimensionProperties(int id, boolean override, boolean trackSanity, boolean darkAffectSanity, 	double sanityMultiplyer, boolean trackAirQuality, double airMulti, boolean trackHydration, double hydrationMulti, boolean trackTemp, double tempMulti, boolean dayNightTemp, boolean weatherAffectsTemp, boolean mineshaftGen, int sealevel)
	{
		this.id = id;
		this.override = override;
		this.trackSanity = trackSanity;
		this.darkAffectSanity = darkAffectSanity;
		this.sanityMultiplyer = sanityMultiplyer;
		this.trackAirQuality = trackAirQuality;
		this.airMulti = airMulti;
		this.trackHydration = trackHydration;
		this.hydrationMulti = hydrationMulti;
		this.trackTemp = trackTemp;
		this.tempMulti = tempMulti;
		this.dayNightTemp = dayNightTemp;
		this.weatherAffectsTemp = weatherAffectsTemp;
		this.mineshaftGen = mineshaftGen;
		this.sealevel = sealevel;
	}
	
	public static void setConfigNames()
	{
		DMName = new String[15];
		DMName[0] = "01.Dimension ID";
		DMName[1] = "02.Allow Config Override";
		DMName[2] = "03.Track Sanity";
		DMName[3] = "04.Dark Affects Sanity";
		DMName[4] = "05.Sanity Multiplier";
		DMName[5] = "06.Track Air Quility";
		DMName[6] = "07.Air Quility Multiplier";
		DMName[7] = "08.Track Hydration";
		DMName[8] = "09.Hydration Multiplier";
		DMName[9] = "10.Track Temperature";
		DMName[10] = "11.Temperature Multiplier";
		DMName[11] = "12.Day/Night Affects Temp";
		DMName[12] = "13.Weather Affects Temp";
		DMName[13] = "14.Generate Mineshafts";
		DMName[14] = "15.Where is Sea Level";
	}
	
	/**
	 * Search thur Dimension List and add to Enviromine Config Files 
	 */
	public static void SearchForDimensions()
	{
		Integer[] DimensionIds = DimensionManager.getStaticDimensionIDs();
		

		EnviroMine.logger.log(Level.INFO, "Found " + DimensionIds.length + " Mod Dimension");
		
		for(int p = 0; p <= DimensionIds.length - 1 && DimensionIds[p] != null; p++)
		{
			WorldProvider dimension = WorldProvider.getProviderForDimension(DimensionIds[p]);
			
			String[] modname = dimension.getClass().getCanonicalName().toString().trim().toLowerCase().split("\\.");
			
			//System.out.println(modname[0]);
			if(modname[0].equalsIgnoreCase("net") && EM_Settings.useDefaultConfig == true)//If Vanilla
			{
				SaveConfig(dimension, "Defaults");
			}
			else
			{
				SaveConfig(dimension, modname[0]);
			}
		}
	}
	
	
	/**
	 * Save data to the Config files
	 * @param dimension
	 * @param ModID
	 */
	public static void SaveConfig(WorldProvider dimension, String ModID)
	{
		
	File dimensionFile = new File(EM_ConfigHandler.customPath + ModID + ".cfg");
		
		if(!dimensionFile.exists())
		{
			try
			{
				dimensionFile.createNewFile();
			} catch(IOException e)
			{
				e.printStackTrace();
				return;
			}
		}
		
		Configuration config = new Configuration(dimensionFile, true);
		config.load();
		
			String catName = categoryName + "."  + dimension.getDimensionName().toLowerCase().trim();
			config.addCustomCategoryComment(catName, "");
			

			if(dimension.getDimensionName().toLowerCase().trim() == "caves")
			{
				config.get(catName, DMName[0], dimension.dimensionId).getInt(dimension.dimensionId);
				config.get(catName, DMName[1], true).getBoolean(true);
				config.get(catName, DMName[2], true).getBoolean(true);
				config.get(catName, DMName[3], true).getBoolean(true);
				config.get(catName, DMName[5], true).getBoolean(true);
				config.get(catName, DMName[4], 1.0D).getDouble(1.0D);
				config.get(catName, DMName[7], true).getBoolean(true);
				config.get(catName, DMName[8], 1.0D).getDouble(1.0D);
				config.get(catName, DMName[9], true).getBoolean(true);
				config.get(catName, DMName[10], 1.0D).getDouble(1.0D);
				config.get(catName, DMName[11], true).getBoolean(true);
				config.get(catName, DMName[12], true).getBoolean(true);
				config.get(catName, DMName[13], false).getBoolean(false);
				config.get(catName, DMName[14], 65).getInt(65);
			} else
			{
				config.get(catName, DMName[0], dimension.dimensionId).getInt(dimension.dimensionId);
				config.get(catName, DMName[1], false).getBoolean(false);
				config.get(catName, DMName[2], true).getBoolean(true);
				config.get(catName, DMName[3], true).getBoolean(true);
				config.get(catName, DMName[5], true).getBoolean(true);
				config.get(catName, DMName[4], 1.0D).getDouble(1.0D);
				config.get(catName, DMName[7], true).getBoolean(true);
				config.get(catName, DMName[8], 1.0D).getDouble(1.0D);
				config.get(catName, DMName[9], true).getBoolean(true);
				config.get(catName, DMName[10], 1.0D).getDouble(1.0D);
				config.get(catName, DMName[11], true).getBoolean(true);
				config.get(catName, DMName[12], true).getBoolean(true);
				config.get(catName, DMName[13], false).getBoolean(false);
				config.get(catName, DMName[14], 65).getInt(65);
			}
		config.save();
		
	}
	
	/**
	 * Load Dimension Property into List
	 * @param config
	 * @param category
	 */
	public static void LoadProperty(Configuration config, String category)
	{
		config.addCustomCategoryComment(category, "");
		
		int id = config.get(category, DMName[0], 0).getInt(0);
		boolean override = config.get(category, DMName[1], false).getBoolean(false);
		boolean trackSanity = config.get(category, DMName[2], true).getBoolean(true);
		boolean darkAffectSanity = config.get(category, DMName[3], true).getBoolean(true);
		double sanityMulti = config.get(category, DMName[4], 1.0D).getDouble(1.0D);
		boolean trackAirQuality = config.get(category, DMName[5], true).getBoolean(true);
		double airMulti = config.get(category, DMName[6], 1.0D).getDouble(1.0D);
		boolean trackHydration = config.get(category, DMName[7], true).getBoolean(true);
		double hydrationMulti = config.get(category, DMName[8], 1.0D).getDouble(1.0D);
		boolean trackTemp = config.get(category, DMName[9], true).getBoolean(true);
		double tempMulti = config.get(category, DMName[10], 1.0D).getDouble(1.0D);
		boolean dayNightTemp = config.get(category, DMName[11], true).getBoolean(true);
		boolean weatherAffectsTemp = config.get(category, DMName[12], true).getBoolean(true);
		boolean mineshaftGen = config.get(category, DMName[13], true).getBoolean(true);
		int sealevel = config.get(category, DMName[14], 65).getInt(65);
		
		DimensionProperties entry = new DimensionProperties(id, override, trackSanity, darkAffectSanity, sanityMulti, trackAirQuality, airMulti, trackHydration, hydrationMulti, trackTemp, tempMulti, dayNightTemp, weatherAffectsTemp, mineshaftGen, sealevel);
		EM_Settings.dimensionProperties.put(id, entry);
	}
}
