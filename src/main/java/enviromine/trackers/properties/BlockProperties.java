package enviromine.trackers.properties;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.helpers.SerialisableProperty;


public class BlockProperties implements SerialisableProperty
{
	public boolean hasPhys;
	
	public String name;
	public int meta;
	
	public int minFall;
	public int maxFall;
	public int supportDist;
	
	public String dropName;
	public int dropMeta;
	public int dropNum;
	
	public boolean enableTemp;
	
	public float temp;
	public float air;
	public float sanity;
	
	public boolean holdsOthers;
	public boolean slides;
	public boolean canHang;
	public boolean wetSlide;
	
	/** Block properties:<br>00 ({@link String}) Name<br>01 ({@link Int}) MetaID<br>02 ({@link String}) DropName<br>03 ({@link Int}) DropMetaID<br>04 ({@link Int}) DropNumber<br>05 ({@link Boolean}) EnableTemprature<br>06 ({@link Double}) Temprature<br>07 ({@link Double}) AirQuality<br>08 ({@link Double}) Sanity<br>09 ({@link String}) Stability<br>10 ({@link Boolean}) Slides<br>11 ({@link Boolean}) Slides when wet */ //Stablility slides slides when wet
	static String[] BPName;
	
	public static String categoryName = "blocks";
	
	public BlockProperties(NBTTagCompound tags)
	{
		this.ReadFromNBT(tags);
	}
	
	public BlockProperties(String name, int meta, boolean hasPhys, int minFall, int maxFall, int supportDist, String dropName, int dropMeta, int dropNum, boolean enableTemp, float temp, float air, float sanity, boolean holdOther, boolean slides, boolean canHang, boolean wetSlide)
	{
		this.name = name;
		this.meta = meta;
		this.hasPhys = hasPhys;
		this.minFall = minFall;
		this.maxFall = maxFall;
		this.supportDist = supportDist;
		this.dropName = dropName;
		this.dropMeta = dropMeta;
		this.dropNum = dropNum;
		this.enableTemp = enableTemp;
		this.temp = temp;
		this.air = air;
		this.sanity = sanity;
		this.holdsOthers = holdOther;
		this.slides = slides;
		this.canHang = canHang;
		this.wetSlide = wetSlide;
	}

	/** Set Config Names For Block properties:<br>00 ({@link String}) Name<br>01 ({@link Int}) MetaID<br>02 ({@link String}) DropName<br>03 ({@link Int}) DropMetaID<br>04 ({@link Int}) DropNumber<br>05 ({@link Boolean}) EnableTemprature<br>06 ({@link Double}) Temprature<br>07 ({@link Double}) AirQuality<br>08 ({@link Double}) Sanity<br>09 ({@link String}) Stability<br>10 ({@link Boolean}) Slides<br>11 ({@link Boolean}) Slides when wet */ //Stablility slides slides when wet
	public static void setConfigNames()
	{
		BPName = new String[12];
		BPName[0] = "01.Name";
		BPName[1] = "02.MetaID";
		BPName[2] = "03.DropName";
		BPName[3] = "04.DropMetaID";
		BPName[4] = "05.DropNumber";
		BPName[5] = "06.Enable Temperature";
		BPName[6] = "07.Temperature";
		BPName[7] = "08.Air Quality";
		BPName[8] = "09.Sanity";
		BPName[9] = "10.Stability";
		BPName[10] = "11.Slides";
		BPName[11] = "12.Slides When Wet";
	}
	
	public static void LoadProperty(Configuration config, String category)
	{
		config.addCustomCategoryComment(category, "");
		
		String name = config.get(category, BPName[0], "").getString();
		int metaData = config.get(category, BPName[1], 0).getInt(0);
		String dropName = config.get(category, BPName[2], "").getString();
		int dropMeta = config.get(category, BPName[3], 0).getInt(0);
		int dropNum = config.get(category, BPName[4], 0).getInt(0);
		boolean enableTemp = config.get(category, BPName[5], false).getBoolean(false);
		float temperature = (float)config.get(category, BPName[6], 0.00).getDouble(0.00);
		float airQuality = (float)config.get(category, BPName[7], 0.00).getDouble(0.00);
		float sanity = (float)config.get(category, BPName[8], 0.00).getDouble(0.00);
		String stability = config.get(category, BPName[9], "loose").getString();
		boolean slides = config.get(category, BPName[10], false).getBoolean(false);
		boolean wetSlides = config.get(category, BPName[11], false).getBoolean(false);
		
		// 	Get Stability Options
		int minFall = 99;
		int maxFall = 99;
		int supportDist = 5;
		boolean holdOther = false;
		boolean canHang = true;
		boolean hasPhys = false;
		
		if(EM_Settings.stabilityTypes.containsKey(stability))
		{
			StabilityType stabType = EM_Settings.stabilityTypes.get(stability);
			
			minFall = stabType.minFall;
			maxFall = stabType.maxFall;
			supportDist = stabType.supportDist;
			hasPhys = stabType.enablePhysics;
			holdOther = stabType.holdOther;
			canHang = stabType.canHang;
		} else
		{
			EnviroMine.logger.log(Level.WARN, "Stability type '" + stability + "' not found.");
			minFall = 99;
			maxFall = 99;
			supportDist = 9;
			hasPhys = false;
			holdOther = false;
			canHang = true;
		}
		
		BlockProperties entry = new BlockProperties(name, metaData, hasPhys, minFall, maxFall, supportDist, dropName, dropMeta, dropNum, enableTemp, temperature, airQuality, sanity, holdOther, slides, canHang, wetSlides);
		
		if(metaData < 0)
		{
			EM_Settings.blockProperties.put("" + name, entry);
		} else
		{
			EM_Settings.blockProperties.put("" + name + "," + metaData, entry);
		}
	}
	
	public static void SaveProperty(Configuration config, String category, String name, int metaData, String dropName, int dropMeta, int dropNum, boolean enableTemp, double temperature, double airQuality, double sanity, String stability, boolean slides, boolean wetSlides)
	{
		config.get(category, BPName[0], name).getString();
		config.get(category, BPName[1], metaData).getInt(0);
		config.get(category, BPName[2], dropName).getString();
		config.get(category, BPName[3], dropMeta).getInt(0);
		config.get(category, BPName[4], dropNum).getInt(0);
		config.get(category, BPName[5], enableTemp).getBoolean(false);
		config.get(category, BPName[6], temperature).getDouble(0.00);
		config.get(category, BPName[7], airQuality).getDouble(0.00);
		config.get(category, BPName[8],sanity).getDouble(0.00);
		config.get(category, BPName[9], stability).getString();
		config.get(category, BPName[10], slides).getBoolean(false);
		config.get(category, BPName[11], wetSlides).getBoolean(false);
	}

	@Override
	public NBTTagCompound WriteToNBT()
	{
		NBTTagCompound tags = new NBTTagCompound();
		tags.setString("name", this.name);
		tags.setInteger("metaData", this.meta);
		tags.setString("dropName", this.dropName);
		tags.setInteger("dropMeta", this.dropMeta);
		tags.setInteger("dropNum", this.dropNum);
		tags.setBoolean("enableTemp", this.enableTemp);
		tags.setFloat("temp", this.temp);
		tags.setFloat("air", this.air);
		tags.setFloat("sanity", this.sanity);
		tags.setBoolean("holdsOthers", this.holdsOthers);
		tags.setBoolean("slides", this.slides);
		tags.setBoolean("canHang", this.canHang);
		tags.setBoolean("wetSlide", this.wetSlide);
		return tags;
	}

	@Override
	public void ReadFromNBT(NBTTagCompound tags)
	{
		this.name = tags.getString("name");
		this.meta = tags.getInteger("metaData");
		this.dropName = tags.getString("dropName");
		this.dropMeta = tags.getInteger("dropMeta");
		this.dropNum = tags.getInteger("dropNum");
		this.enableTemp = tags.getBoolean("enableTemp");
		this.temp = tags.getFloat("temp");
		this.air = tags.getFloat("air");
		this.sanity = tags.getFloat("sanity");
		this.holdsOthers = tags.getBoolean("holdsOthers");
		this.slides = tags.getBoolean("slides");
		this.canHang = tags.getBoolean("canHang");
		this.wetSlide = tags.getBoolean("wetSlide");
	}
}
