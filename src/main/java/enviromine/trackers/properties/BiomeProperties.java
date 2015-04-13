package enviromine.trackers.properties;

import java.io.File;
import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;

import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.helpers.PropertyBase;
import enviromine.trackers.properties.helpers.SerialisableProperty;
import enviromine.utils.EnviroUtils;
import enviromine.utils.ModIdentification;


public class BiomeProperties implements SerialisableProperty, PropertyBase
{
	public static final BiomeProperties base = new BiomeProperties();
	static String[] BOName;
	
	public int id;
	public boolean biomeOveride;
	public String waterQuality;
	public float ambientTemp;
	public float tempRate;
	public float sanityRate;
	public float dehydrateRate;
	public float airRate;
	public String loadedFrom;
	
	public BiomeProperties(NBTTagCompound tags)
	{
		this.ReadFromNBT(tags);
	}
	
	public BiomeProperties()
	{
		// THIS CONSTRUCTOR IS FOR STATIC PURPOSES ONLY!
		
		if(base != null && base != this)
		{
			throw new IllegalStateException();
		}
	}

	public BiomeProperties(int id, boolean biomeOveride, String waterQuality, float ambientTemp, float tempRate, float sanityRate, float dehydrateRate, float airRate, String filename)
	{
		this.id = id;
		this.biomeOveride = biomeOveride;
		this.waterQuality = waterQuality;
		this.ambientTemp = ambientTemp;
		this.tempRate = tempRate;
		this.sanityRate = sanityRate;
		this.dehydrateRate = dehydrateRate;
		this.airRate = airRate;
		this.loadedFrom = filename;
	}
	/**
	 * <b>hasProperty(BiomeGenBase biome)</b><bR><br>
	 * Checks if Property contains custom properties.
	 * @param biome
	 * @return true if has custom properties
	 */
	public boolean hasProperty(BiomeGenBase biome)
	{
		return EM_Settings.biomeProperties.containsKey(biome.biomeID);
	}
	/** 
	 * 	<b>getProperty(BiomeGenBase biome)</b><bR><br>
	 * Gets Property.
	 * @param biome
	 * @return BiomeProperties
	 */
	public BiomeProperties getProperty(BiomeGenBase biome)
	{
		return EM_Settings.biomeProperties.get(biome.biomeID);
	}
	
	public int getWaterQualityId()
	{
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

	@Override
	public NBTTagCompound WriteToNBT()
	{
		NBTTagCompound tags = new NBTTagCompound();
		tags.setInteger("id", this.id);
		tags.setBoolean("biomeOveride", this.biomeOveride);
		tags.setString("waterQuality", this.waterQuality);
		tags.setFloat("ambientTemp", this.ambientTemp);
		tags.setFloat("tempRate", this.tempRate);
		tags.setFloat("sanityRate", this.sanityRate);
		tags.setFloat("dehydrateRate", this.dehydrateRate);
		return tags;
	}

	@Override
	public void ReadFromNBT(NBTTagCompound tags)
	{
		this.id = tags.getInteger("id");
		this.biomeOveride = tags.getBoolean("biomeOveride");
		this.waterQuality = tags.getString("waterQuality");
		this.ambientTemp = tags.getFloat("ambientTemp");
		this.tempRate = tags.getFloat("tempRate");
		this.sanityRate = tags.getFloat("sanityRate");
		this.dehydrateRate = tags.getFloat("dehydrateRate");
	}

	@Override
	public String categoryName()
	{
		return "biomes";
	}

	@Override
	public String categoryDescription()
	{
		return "Manually change the environmental properties of each biome";
	}

	@Override
	public void LoadProperty(Configuration config, String category)
	{
		config.setCategoryComment(this.categoryName(), this.categoryDescription());
		int id = config.get(category, BOName[0], 0).getInt(0);
		boolean biomeOveride = config.get(category, BOName[1], false).getBoolean(false);
		String waterQ = config.get(category, BOName[2], "clean", "Water Quality: dirty, salty, cold, clean").getString();
		float ambTemp = (float)config.get(category, BOName[3], 25.00, "Biome temperature in celsius (Player body temp is offset by + 12C)").getDouble(25.00);
		float tempRate = (float)config.get(category, BOName[4], 0.0).getDouble(0.0);
		float sanRate = (float)config.get(category, BOName[5], 0.0).getDouble(0.0);
		float dehyRate = (float)config.get(category, BOName[6], 0.0).getDouble(0.0);
		float airRate = (float)config.get(category, BOName[7], 0.0).getDouble(0.0);
		String filename = config.getConfigFile().getName();
		
		BiomeProperties entry = new BiomeProperties(id, biomeOveride, waterQ, ambTemp, tempRate, sanRate, dehyRate, airRate, filename);
		
		if(EM_Settings.biomeProperties.containsKey(id) && !EM_ConfigHandler.loadedConfigs.contains(filename)) EnviroMine.logger.log(Level.ERROR, "CONFIG DUPLICATE: Biome ID "+id +" was already added from "+ EM_Settings.biomeProperties.get(id).loadedFrom.toUpperCase() +" and will be overriden by "+ filename.toUpperCase());
		EM_Settings.biomeProperties.put(id, entry);
	}

	@Override
	public void SaveProperty(Configuration config, String category)
	{
		config.get(category, BOName[0], this.id).getInt(0);
		config.get(category, BOName[1], this.biomeOveride).getBoolean(this.biomeOveride);
		config.get(category, BOName[2], this.waterQuality, "Water Quality: dirty, salty, cold, clean").getString();
		config.get(category, BOName[3], this.ambientTemp, "Biome temperature in celsius (Player body temp is offset by + 12C)").getDouble(this.ambientTemp);
		config.get(category, BOName[4], this.tempRate).getDouble(this.tempRate);
		config.get(category, BOName[5], this.sanityRate).getDouble(this.sanityRate);
		config.get(category, BOName[6], this.dehydrateRate).getDouble(this.dehydrateRate);
		config.get(category, BOName[7], this.airRate).getDouble(this.airRate);
	}

	@Override
	public void GenDefaults()
	{
		BiomeGenBase[] biomeArray = BiomeGenBase.getBiomeGenArray();
		
		for(int p = 0; p < biomeArray.length; p++)
		{
			BiomeGenBase biome = biomeArray[p];
			
			if(biome == null)
			{
				continue;
			}
			
			String modID = ModIdentification.idFromObject(biome);
			
			File file = new File(EM_ConfigHandler.customPath + EnviroUtils.SafeFilename(modID) + ".cfg");
			
			if(!file.exists())
			{
				try
				{
					file.createNewFile();
				} catch(Exception e)
				{
					EnviroMine.logger.log(Level.ERROR, "Failed to create file for biome '" + biome.biomeName + "'", e);
					continue;
				}
			}
			
			Configuration config = new Configuration(file, true);
			
			config.load();
			
			generateEmpty(config, biome);
			
			config.save();
		}
	}

	@Override
	public File GetDefaultFile()
	{
		return new File(EM_ConfigHandler.customPath + "Biomes.cfg");
	}

	@Override
	public void generateEmpty(Configuration config, Object obj)
	{
		if(obj == null || !(obj instanceof BiomeGenBase))
		{
			EnviroMine.logger.log(Level.ERROR, "Tried to register config with non biome object!", new Exception());
			return;
		}
		
		BiomeGenBase biome = (BiomeGenBase)obj;
		
		ArrayList<Type> typeList = new ArrayList<Type>();
		Type[] typeArray = BiomeDictionary.getTypesForBiome(biome);
		for(int i = 0; i < typeArray.length; i++)
		{
			typeList.add(typeArray[i]);
		}
		
		double air = typeList.contains(Type.NETHER)? -0.1D : 0D;
		double sanity = typeList.contains(Type.NETHER)? -0.1D : 0D;
		double water = typeList.contains(Type.NETHER) || typeList.contains(Type.DRY)? 0.05D : 0D;
		double temp = typeList.contains(Type.NETHER) || typeList.contains(Type.DRY)? 0.005D : 0D;
		
		String catName = this.categoryName() + "." + biome.biomeName;
		
		config.get(catName, BOName[0], biome.biomeID).getInt(biome.biomeID);
		config.get(catName, BOName[1], false).getBoolean(false);
		config.get(catName, BOName[2], EnviroUtils.getBiomeWater(biome), "Water Quality: dirty, salty, cold, clean").getString();
		config.get(catName, BOName[3], EnviroUtils.getBiomeTemp(biome), "Biome temperature in celsius (Player body temp is offset by + 12C)").getDouble(25.00);
		config.get(catName, BOName[4], temp).getDouble(temp);
		config.get(catName, BOName[5], sanity).getDouble(sanity);
		config.get(catName, BOName[6], water).getDouble(water);
		config.get(catName, BOName[7], air).getDouble(air);
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
		BOName = new String[8];
		BOName[0] = "01.Biome ID";
		BOName[1] = "02.Allow Config Override";
		BOName[2] = "03.Water Quality";
		BOName[3] = "04.Ambient Temperature";
		BOName[4] = "05.Temp Rate";
		BOName[5] = "06.Sanity Rate";
		BOName[6] = "07.Dehydrate Rate";
		BOName[7] = "08.Air Quality Rate";
	}
}
