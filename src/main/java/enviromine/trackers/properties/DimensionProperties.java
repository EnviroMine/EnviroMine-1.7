package enviromine.trackers.properties;

import java.io.File;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;

import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.helpers.PropertyBase;
import enviromine.trackers.properties.helpers.SerialisableProperty;
import enviromine.utils.EnviroUtils;
import enviromine.utils.ModIdentification;

public class DimensionProperties implements SerialisableProperty, PropertyBase
{
	public static final DimensionProperties base = new DimensionProperties();
	static String[] DMName;
	
	public int id;
	public boolean override;
	public boolean trackSanity;
	public boolean darkAffectSanity;
	public float sanityMulti;
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
	public int mineDepth;
	public float tempRate;
	public float hydrationRate;
	public float sanityRate;
	public float airRate;
	public boolean physics;
	
	public DimensionProperties(NBTTagCompound tags)
	{
		this.ReadFromNBT(tags);
	}
	
	public DimensionProperties()
	{
		// THIS CONSTRUCTOR IS FOR STATIC PURPOSES ONLY!
		
		if(base != null && base != this)
		{
			throw new IllegalStateException();
		}
	}
	
	public DimensionProperties(int id, boolean override, boolean trackSanity, boolean darkAffectSanity, float sanityMulti, boolean trackAirQuality, float airMulti, boolean trackHydration, float hydrationMulti, boolean trackTemp, float tempMulti, boolean dayNightTemp, boolean weatherAffectsTemp, boolean mineshaftGen, int sealevel, int mineDepth, float tempRate, float hydrationRate, float sanityRate, float airRate, boolean physics)
	{
		this.id = id;
		this.override = override;
		this.trackSanity = trackSanity;
		this.darkAffectSanity = darkAffectSanity;
		this.sanityMulti = sanityMulti;
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
		this.mineDepth = mineDepth;
		this.tempRate = tempRate;
		this.hydrationRate = hydrationRate;
		this.sanityRate = sanityRate;
		this.airRate = airRate;
		this.physics = physics;
	}

	/**
	 * <b>hasProperty(BiomeGenBase biome)</b><bR><br>
	 * Checks if Property contains custom properties.
	 * @param biome
	 * @return true if has custom properties
	 */
	public boolean hasProperty(int dimensionId)
	{
		return EM_Settings.dimensionProperties.containsKey(dimensionId);
	}
	/** 
	 * 	<b>getProperty(BiomeGenBase biome)</b><bR><br>
	 * Gets Property.
	 * @param biome
	 * @return BiomeProperties
	 */
	public DimensionProperties getProperty(int dimensionId)
	{
		return EM_Settings.dimensionProperties.get(dimensionId);
	}
	
	@Override
	public NBTTagCompound WriteToNBT()
	{
		NBTTagCompound tags = new NBTTagCompound();
		tags.setInteger("id", this.id);
		tags.setBoolean("override", this.override);
		tags.setBoolean("trackSanity", this.trackSanity);
		tags.setBoolean("darkAffectSanity", this.darkAffectSanity);
		tags.setFloat("sanityMulti", this.sanityMulti);
		tags.setBoolean("trackAirQuality", this.trackAirQuality);
		tags.setFloat("airMulti", this.airMulti);
		tags.setBoolean("trackHydration", this.trackHydration);
		tags.setFloat("hydrationMulti", this.hydrationMulti);
		tags.setBoolean("trackTemp", this.trackTemp);
		tags.setFloat("tempMulti", this.tempMulti);
		tags.setBoolean("weatherAffectsTemp", this.weatherAffectsTemp);
		tags.setBoolean("mineshaftGen", this.mineshaftGen);
		tags.setInteger("sealevel", this.sealevel);
		tags.setInteger("mineDepth", this.mineDepth);
		tags.setFloat("tempRate", this.tempRate);
		tags.setFloat("hydrationRate", this.hydrationRate);
		tags.setFloat("sanityRate", this.sanityRate);
		tags.setFloat("airRate", this.airRate);
		tags.setBoolean("physics", this.physics);
		return tags;
	}

	@Override
	public void ReadFromNBT(NBTTagCompound tags)
	{
		this.id = tags.getInteger("id");
		this.override = tags.getBoolean("override");
		this.trackSanity = tags.getBoolean("trackSanity");
		this.darkAffectSanity = tags.getBoolean("darkAffectSanity");
		this.sanityMulti = tags.getFloat("sanityMulti");
		this.trackAirQuality = tags.getBoolean("trackAirQuality");
		this.airMulti = tags.getFloat("airMulti");
		this.trackHydration = tags.getBoolean("trackHydration");
		this.hydrationMulti = tags.getFloat("hydrationMulti");
		this.trackTemp = tags.getBoolean("trackTemp");
		this.tempMulti = tags.getFloat("tempMulti");
		this.weatherAffectsTemp = tags.getBoolean("weatherAffectsTemp");
		this.mineshaftGen = tags.getBoolean("mineshaftGen");
		this.sealevel = tags.getInteger("sealevel");
		this.mineDepth = tags.getInteger("mineDepth");
		this.tempRate = tags.getFloat("tempRate");
		this.hydrationRate = tags.getFloat("hydrationRate");
		this.sanityRate = tags.getFloat("sanityRate");
		this.airRate = tags.getFloat("airRate");
		this.physics = tags.getBoolean("physics");
	}

	@Override
	public String categoryName()
	{
		return "dimension";
	}

	@Override
	public String categoryDescription()
	{
		return "Customise dimension environments";
	}

	@Override
	public void LoadProperty(Configuration config, String category)
	{
		config.setCategoryComment(this.categoryName(), this.categoryDescription());
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
		int mineDepth = config.getInt(DMName[15], category, 12, -255, 255, "Use a negitive value to skip shaft entrance checks");
		float tempRate = (float)config.get(category, DMName[16], 0.0D).getDouble(0.0D);
		float hydrationRate = (float)config.get(category, DMName[17], 0.0D).getDouble(0.0D);
		float sanityRate = (float)config.get(category, DMName[18], 0.0D).getDouble(0.0D);
		float airRate = (float)config.get(category, DMName[19], 0.0D).getDouble(0.0D);
		boolean physics = config.get(category, DMName[20], true).getBoolean(true);
		
		DimensionProperties entry = new DimensionProperties(id, override, trackSanity, darkAffectSanity, sanityMulti, trackAirQuality, airMulti, trackHydration, hydrationMulti, trackTemp, tempMulti, dayNightTemp, weatherAffectsTemp, mineshaftGen, sealevel, mineDepth, tempRate, hydrationRate, sanityRate, airRate, physics);
		EM_Settings.dimensionProperties.put(id, entry);
	}

	@Override
	public void SaveProperty(Configuration config, String category)
	{
		config.get(category, DMName[0], id).getInt(id);
		config.get(category, DMName[1], override).getBoolean(override);
		config.get(category, DMName[2], trackSanity).getBoolean(trackSanity);
		config.get(category, DMName[3], darkAffectSanity).getBoolean(darkAffectSanity);
		config.get(category, DMName[4], sanityMulti).getDouble(sanityMulti);
		config.get(category, DMName[5], trackAirQuality).getBoolean(trackAirQuality);
		config.get(category, DMName[6], airMulti).getDouble(airMulti);
		config.get(category, DMName[7], trackHydration).getBoolean(trackHydration);
		config.get(category, DMName[8], hydrationMulti).getDouble(hydrationMulti);
		config.get(category, DMName[9], trackTemp).getBoolean(trackTemp);
		config.get(category, DMName[10], tempMulti).getDouble(tempMulti);
		config.get(category, DMName[11], dayNightTemp).getBoolean(dayNightTemp);
		config.get(category, DMName[12], weatherAffectsTemp).getBoolean(weatherAffectsTemp);
		config.get(category, DMName[13], mineshaftGen).getBoolean(mineshaftGen);
		config.get(category, DMName[14], sealevel).getInt(sealevel);
		config.getInt(DMName[15], category, mineDepth, -255, 255, "Use a negitive value to skip shaft entrance checks");
		config.get(category, DMName[16], 0.0D).getDouble(0.0D);
		config.get(category, DMName[17], 0.0D).getDouble(0.0D);
		config.get(category, DMName[18], 0.0D).getDouble(0.0D);
		config.get(category, DMName[19], 0.0D).getDouble(0.0D);
		config.get(category, DMName[20], physics).getBoolean(physics);
	}

	@Override
	public void GenDefaults()
	{
		Integer[] dimIDs = DimensionManager.getStaticDimensionIDs();
		
		for(int i = 0; i < dimIDs.length; i++)
		{
			WorldProvider dimension = WorldProvider.getProviderForDimension(dimIDs[i]);
			
			if(dimension == null)
			{
				continue;
			}
			
			String modID = ModIdentification.idFromObject(dimension);
			
			File file = new File(EM_ConfigHandler.customPath + EnviroUtils.SafeFilename(modID) + ".cfg");
			
			if(!file.exists())
			{
				try
				{
					file.createNewFile();
				} catch(Exception e)
				{
					EnviroMine.logger.log(Level.ERROR, "Failed to create file for dimension '" + dimension.getDimensionName() + "'", e);
					continue;
				}
			}
			
			Configuration config = new Configuration(file, true);
			
			config.load();
			
			String catName = this.categoryName() + "." + EnviroUtils.replaceULN(dimension.getDimensionName());
			
			if(dimension.dimensionId == EM_Settings.caveDimID)
			{
				config.get(catName, DMName[0], dimension.dimensionId).getInt(dimension.dimensionId);
				config.get(catName, DMName[1], true).getBoolean(true);
				config.get(catName, DMName[2], true).getBoolean(true);
				config.get(catName, DMName[3], true).getBoolean(true);
				config.get(catName, DMName[4], 1.0D).getDouble(1.0D);
				config.get(catName, DMName[5], true).getBoolean(true);
				config.get(catName, DMName[6], 1.0D).getDouble(1.0D);
				config.get(catName, DMName[7], true).getBoolean(true);
				config.get(catName, DMName[8], 1.0D).getDouble(1.0D);
				config.get(catName, DMName[9], true).getBoolean(true);
				config.get(catName, DMName[10], 1.0D).getDouble(1.0D);
				config.get(catName, DMName[11], true).getBoolean(true);
				config.get(catName, DMName[12], true).getBoolean(true);
				config.get(catName, DMName[13], true).getBoolean(true);
				config.get(catName, DMName[14], 340).getInt(340);
				config.getInt(DMName[15], catName, -192, -255, 255, "Use a negitive value to skip shaft entrance checks");
				config.get(catName, DMName[16], 0.0D).getDouble(0.0D);
				config.get(catName, DMName[17], 0.0D).getDouble(0.0D);
				config.get(catName, DMName[18], 0.0D).getDouble(0.0D);
				config.get(catName, DMName[19], 0.0D).getDouble(0.0D);
				config.get(catName, DMName[20], true).getBoolean(true);
			} else if(dimension.dimensionId == -1)
			{
				config.get(catName, DMName[0], dimension.dimensionId).getInt(dimension.dimensionId);
				config.get(catName, DMName[1], true).getBoolean(true);
				config.get(catName, DMName[2], true).getBoolean(true);
				config.get(catName, DMName[3], true).getBoolean(true);
				config.get(catName, DMName[4], 1.0D).getDouble(1.0D);
				config.get(catName, DMName[5], true).getBoolean(true);
				config.get(catName, DMName[6], 1.0D).getDouble(1.0D);
				config.get(catName, DMName[7], true).getBoolean(true);
				config.get(catName, DMName[8], 1.0D).getDouble(1.0D);
				config.get(catName, DMName[9], true).getBoolean(true);
				config.get(catName, DMName[10], 1.0D).getDouble(1.0D);
				config.get(catName, DMName[11], true).getBoolean(true);
				config.get(catName, DMName[12], true).getBoolean(true);
				config.get(catName, DMName[13], false).getBoolean(false);
				config.get(catName, DMName[14], 340).getInt(340);
				config.getInt(DMName[15], catName, -192, -255, 255, "Use a negitive value to skip shaft entrance checks");
				config.get(catName, DMName[16], 0.0D).getDouble(0.0D);
				config.get(catName, DMName[17], 0.0D).getDouble(0.0D);
				config.get(catName, DMName[18], 0.0D).getDouble(0.0D);
				config.get(catName, DMName[19], 0.0D).getDouble(0.0D);
				config.get(catName, DMName[20], true).getBoolean(true);
			} else if(EM_Settings.genConfigs || modID.equals("minecraft"))
			{
				this.generateEmpty(config, dimension);
			}
			
			config.save();
		}
	}

	@Override
	public File GetDefaultFile()
	{
		return new File(EM_ConfigHandler.customPath + "Dimensions.cfg");
	}

	@Override
	public void generateEmpty(Configuration config, Object obj)
	{
		if(obj == null || !(obj instanceof WorldProvider))
		{
			EnviroMine.logger.log(Level.ERROR, "Tried to register config with non WorldProvider object!", new Exception());
			return;
		}
		
		WorldProvider dimension = (WorldProvider)obj;
		
		String catName = this.categoryName() + "." + EnviroUtils.replaceULN(dimension.getDimensionName());
		
		config.get(catName, DMName[0], dimension.dimensionId).getInt(dimension.dimensionId);
		config.get(catName, DMName[1], true).getBoolean(true);
		config.get(catName, DMName[2], true).getBoolean(true);
		config.get(catName, DMName[3], true).getBoolean(true);
		config.get(catName, DMName[4], 1.0D).getDouble(1.0D);
		config.get(catName, DMName[5], true).getBoolean(true);
		config.get(catName, DMName[6], 1.0D).getDouble(1.0D);
		config.get(catName, DMName[7], true).getBoolean(true);
		config.get(catName, DMName[8], 1.0D).getDouble(1.0D);
		config.get(catName, DMName[9], true).getBoolean(true);
		config.get(catName, DMName[10], 1.0D).getDouble(1.0D);
		config.get(catName, DMName[11], !dimension.hasNoSky).getBoolean(!dimension.hasNoSky);
		config.get(catName, DMName[12], !dimension.hasNoSky).getBoolean(!dimension.hasNoSky);
		config.get(catName, DMName[13], !dimension.hasNoSky).getBoolean(!dimension.hasNoSky);
		config.get(catName, DMName[14], dimension.hasNoSky? 340 : 64).getInt(dimension.hasNoSky? 340 : 64);
		config.getInt(DMName[15], catName, 12, -255, 255, "Use a negitive value to skip shaft entrance checks");
		config.get(catName, DMName[16], 0.0D).getDouble(0.0D);
		config.get(catName, DMName[17], 0.0D).getDouble(0.0D);
		config.get(catName, DMName[18], 0.0D).getDouble(0.0D);
		config.get(catName, DMName[19], 0.0D).getDouble(0.0D);
		config.get(catName, DMName[20], true).getBoolean(true);
	}

	@Override
	public boolean useCustomConfigs()
	{
		return true;
	}

	@Override
	public void customLoad()
	{
	}
	
	static
	{
		DMName = new String[21];
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
		DMName[15] = "16.Mine Y height";
		DMName[16] = "17.Base Temperature Rate";
		DMName[17] = "18.Base Hydration Rate";
		DMName[18] = "19.Base Sanity Rate";
		DMName[19] = "20.Base Air Quality Rate";
		DMName[20] = "20.Enable Physics";
	}
}
