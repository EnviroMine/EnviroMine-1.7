package enviromine.trackers.properties;

import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;

import java.io.File;
import java.io.IOException;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

public class DimensionProperties implements SerialisableProperty
{
	public int id;
	public boolean override;
	public boolean trackSanity;
	public boolean darkAffectSanity;
	public float sanityMultiplyer;
	public boolean trackAirQuality;
	public float airMulti;
	public boolean trackHydration;
	public float hydrationMulti;
	public boolean trackTemp;
	public float tempMulti;
	public boolean dayNightTemp;
	public boolean weatherAffectsTemp;
	public boolean mineshaftGen;
	public int sealevel;
	
	static String[] DMName;
	
	public static String categoryName = "dimensions";
	
	public DimensionProperties(NBTTagCompound tags)
	{
		this.ReadFromNBT(tags);
	}
	
	public DimensionProperties(int id, boolean override, boolean trackSanity, boolean darkAffectSanity, float sanityMultiplyer, boolean trackAirQuality, float airMulti, boolean trackHydration, float hydrationMulti, boolean trackTemp, float tempMulti, boolean dayNightTemp, boolean weatherAffectsTemp, boolean mineshaftGen, int sealevel)
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
			
			String canonicalName = dimension.getClass().getCanonicalName();
			if (canonicalName == null)
			{
				SaveConfig(dimension, "Defaults");
			} else {
				String[] modname = canonicalName.trim().toLowerCase().split("\\.");
				
				//System.out.println(modname[0]);
				if(modname[0].equalsIgnoreCase("net") && EM_Settings.useDefaultConfig)//If Vanilla
				{
					SaveConfig(dimension, "Defaults");
				}
				else
				{
					SaveConfig(dimension, modname[0]);
				}
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
		float sanityMulti = (float)config.get(category, DMName[4], 1.0D).getDouble(1.0D);
		boolean trackAirQuality = config.get(category, DMName[5], true).getBoolean(true);
		float airMulti = (float)config.get(category, DMName[6], 1.0D).getDouble(1.0D);
		boolean trackHydration = config.get(category, DMName[7], true).getBoolean(true);
		float hydrationMulti = (float)config.get(category, DMName[8], 1.0D).getDouble(1.0D);
		boolean trackTemp = config.get(category, DMName[9], true).getBoolean(true);
		float tempMulti = (float)config.get(category, DMName[10], 1.0D).getDouble(1.0D);
		boolean dayNightTemp = config.get(category, DMName[11], true).getBoolean(true);
		boolean weatherAffectsTemp = config.get(category, DMName[12], true).getBoolean(true);
		boolean mineshaftGen = config.get(category, DMName[13], true).getBoolean(true);
		int sealevel = config.get(category, DMName[14], 65).getInt(65);
		
		DimensionProperties entry = new DimensionProperties(id, override, trackSanity, darkAffectSanity, sanityMulti, trackAirQuality, airMulti, trackHydration, hydrationMulti, trackTemp, tempMulti, dayNightTemp, weatherAffectsTemp, mineshaftGen, sealevel);
		EM_Settings.dimensionProperties.put(id, entry);
	}

	@Override
	public NBTTagCompound WriteToNBT()
	{
		NBTTagCompound tags = new NBTTagCompound();
		tags.setInteger("id", this.id);
		tags.setBoolean("override", this.override);
		tags.setBoolean("trackSanity", this.trackSanity);
		tags.setBoolean("darkAffectSanity", this.darkAffectSanity);
		tags.setFloat("sanityMultiplyer", this.sanityMultiplyer);
		tags.setBoolean("trackAirQuality", this.trackAirQuality);
		tags.setFloat("airMulti", this.airMulti);
		tags.setBoolean("trackHydration", this.trackHydration);
		tags.setFloat("hydrationMulti", this.hydrationMulti);
		tags.setBoolean("trackTemp", this.trackTemp);
		tags.setFloat("tempMulti", this.tempMulti);
		tags.setBoolean("weatherAffectsTemp", this.weatherAffectsTemp);
		tags.setBoolean("mineshaftGen", this.mineshaftGen);
		tags.setInteger("sealevel", this.sealevel);
		return tags;
	}

	@Override
	public void ReadFromNBT(NBTTagCompound tags)
	{
		this.id = tags.getInteger("id");
		this.override = tags.getBoolean("override");
		this.trackSanity = tags.getBoolean("trackSanity");
		this.darkAffectSanity = tags.getBoolean("darkAffectSanity");
		this.sanityMultiplyer = tags.getFloat("sanityMultiplyer");
		this.trackAirQuality = tags.getBoolean("trackAirQuality");
		this.airMulti = tags.getFloat("airMulti");
		this.trackHydration = tags.getBoolean("trackHydration");
		this.hydrationMulti = tags.getFloat("hydrationMulti");
		this.trackTemp = tags.getBoolean("trackTemp");
		this.tempMulti = tags.getFloat("tempMulti");
		this.weatherAffectsTemp = tags.getBoolean("weatherAffectsTemp");
		this.mineshaftGen = tags.getBoolean("mineshaftGen");
		this.sealevel = tags.getInteger("sealevel");
	}
}
