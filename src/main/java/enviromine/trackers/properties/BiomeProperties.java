package enviromine.trackers.properties;

import enviromine.EnviroUtils;
import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;

import net.minecraft.world.biome.BiomeGenBase;

import java.io.File;
import java.io.IOException;

import net.minecraftforge.common.config.Configuration;


public class BiomeProperties
{
	public int id;
	public boolean biomeOveride;
	public String waterQuality;
	public double ambientTemp;
	public double tempRate;
	public double sanityRate;
	public double dehydrateRate;
	
	static String[] BOName;

	public static String categoryName = "biomes";

	public BiomeProperties(int id, boolean biomeOveride, String waterQuality, double ambientTemp, double tempRate, double sanityRate, double dehydrateRate)
	{
		this.id = id;
		this.biomeOveride = biomeOveride;
		this.waterQuality = waterQuality;
		this.ambientTemp = ambientTemp;
		this.tempRate = tempRate;
		this.sanityRate = sanityRate;
		this.dehydrateRate = dehydrateRate;
	}

	public int getWaterQualityId()
	{
		//System.out.println(this.waterQuality);

		if(this.waterQuality.trim().equalsIgnoreCase("dirty"))
		{
			return 1;
		} else if(this.waterQuality.trim().equalsIgnoreCase("salty"))
		{
			return 2;
		} else if(this.waterQuality.trim().equalsIgnoreCase("cold"))
		{
			return 3;
		} else if(this.waterQuality.trim().equalsIgnoreCase("clean"))
		{
			return 0;
		} else
		{
			return -1;
		}
	}

	public static void setConfigNames()
	{
		
		BOName = new String[7];
		BOName[0] = "01.Biome ID";
		BOName[1] = "02.Allow Config Override";
		BOName[2] = "03.Water Quality";
		BOName[3] = "04.Ambient Temperature";
		BOName[4] = "05.Temp Rate";
		BOName[5] = "06.Sanity Rate";
		BOName[6] = "07.Dehydrate Rate";
	}
	
	public static void LoadProperty(Configuration config, String category)
	{
		
		//System.out.println(category);
		
		//String catName = biomeCat + "." + category;
		config.addCustomCategoryComment(category, "");
		
		int id = config.get(category, BOName[0], 0, "Make sure if you change this id you also change it here.").getInt(0);
		boolean biomeOveride = config.get(category, BOName[1], false).getBoolean(false);
		String waterQ = config.get(category, BOName[2], "clean", "Water Quality: dirty, salt, cold, clean").getString();
		double ambTemp = config.get(category, BOName[3], 37.00, "In Celsius").getDouble(37.00);
		double tempRate = config.get(category, BOName[4], 0.0, "Rates Happen each Game tick").getDouble(0.0);
		double sanRate = config.get(category, BOName[5], 0.0).getDouble(0.0);
		double dehyRate = config.get(category, BOName[6], 0.0).getDouble(0.0);
		
		BiomeProperties entry = new BiomeProperties(id, biomeOveride, waterQ, ambTemp, tempRate, sanRate, dehyRate);
		
		EM_Settings.biomeProperties.put(id, entry);
		
	}
	
	public static void SearchForBiomes()
	{
		BiomeGenBase[] BiomeArray = BiomeGenBase.getBiomeGenArray();

		for (BiomeGenBase biomeGen : BiomeArray)
		{
			if (biomeGen == null)
			{
				continue;
			}

			BiomeSaveConfig(biomeGen, "Biomes");
		}
	}
	
	private static void BiomeSaveConfig(BiomeGenBase biomeArray, String ModID)
	{
		File biomesFile = new File(EM_ConfigHandler.customPath + ModID +".cfg");
		
		if(!biomesFile.exists())
		{
			try
			{
				biomesFile.createNewFile();
			} catch(IOException e)
			{
				e.printStackTrace();
				return;
			}
		}
		
		Configuration config = new Configuration(biomesFile, true);
		config.load();
		
		String catName = categoryName + "." + biomeArray.biomeName;
		config.addCustomCategoryComment(catName, "");
		
		config.get(catName, BOName[0], biomeArray.biomeID, "Make sure if you change this id you also change it here.").getInt(biomeArray.biomeID);
		config.get(catName, BOName[1], false).getBoolean(false);
		config.get(catName, BOName[2], EnviroUtils.getBiomeWater(biomeArray), "Water Quality: dirty, salt, cold, clean").getString();
		config.get(catName, BOName[3], EnviroUtils.getBiomeTemp(biomeArray), "In Celsius").getDouble(37.00);
		config.get(catName, BOName[4], 0.0, "Rates Happen each Game tick").getDouble(0.0);
		config.get(catName, BOName[5], 0.0).getDouble(0.0);
		config.get(catName, BOName[6], 0.0).getDouble(0.0);
		
		config.save();
	}
}
