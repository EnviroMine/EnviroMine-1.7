package enviromine.trackers.properties;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.config.Configuration;

import enviromine.EnviroUtils;
import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class ArmorProperties
{
	public String name;
	public float nightTemp;
	public float shadeTemp;
	public float sunTemp;
	public float nightMult;
	public float shadeMult;
	public float sunMult;
	public float sanity;
	public float air;
	public boolean allowCamelPack;

	/** Armor properties:<br>0 ({@link String}) Name<br>1 ({@link Double}) Temp - Night<br>2 ({@link Double}) Temp - Shade<br>3 ({@link Double}) Temp - Sun<br>4 ({@link Double}) Temp multiplyer - Night<br>5 ({@link Double}) Temp multiplyer - Shade<br>6 ({@link Double}) Temp multiplyer - Sun<br>7 ({@link Double}) Sanity<br>8 ({@link Double}) Air<br>9 ({@link Boolean}) Allow Camel Pack */
	static String[] APName;
	
	public static String categoryName = "armor";
	
	public ArmorProperties(String name, float nightTemp, float shadeTemp, float sunTemp, float nightMult, float shadeMult, float sunMult, float sanity, float air, boolean allowCamelPack)
	{
		this.name = name;
		this.nightTemp = nightTemp;
		this.shadeTemp = shadeTemp;
		this.sunTemp = sunTemp;
		this.nightMult = nightMult;
		this.shadeMult = shadeMult;
		this.sunMult = sunMult;
		this.sanity = sanity;
		this.air = air;
		this.allowCamelPack = allowCamelPack;
	}
	
	/** Set Config Names for Armor properties:<br>0 ({@link String}) Name<br>1 ({@link Double}) Temp - Night<br>2 ({@link Double}) Temp - Shade<br>3 ({@link Double}) Temp - Sun<br>4 ({@link Double}) Temp multiplyer - Night<br>5 ({@link Double}) Temp multiplyer - Shade<br>6 ({@link Double}) Temp multiplyer - Sun<br>7 ({@link Double}) Sanity<br>8 ({@link Double}) Air<br>9 ({@link Boolean}) Allow Camel Pack */
	public static void setConfigNames()
	{
		APName = new String[10];
		APName[0] = "01.ID";
		APName[1] = "02.Temp Add - Night";
		APName[2] = "03.Temp Add - Shade";
		APName[3] = "04.Temp Add - Sun";
		APName[4] = "05.Temp Multiplier - Night";
		APName[5] = "06.Temp Multiplier - Shade";
		APName[6] = "07.Temp Multiplier - Sun";
		APName[7] = "08.Sanity";
		APName[8] = "09.Air";
		APName[9] = "10.Allow Camel Pack";
	}
	
	public static void LoadProperty(Configuration config, String catagory)
	{
		config.addCustomCategoryComment(catagory, "");
			
		String name = config.get(catagory, APName[0], "").getString();
		float nightTemp = (float)config.get(catagory, APName[1], 0.00).getDouble(0.00);
		float shadeTemp = (float)config.get(catagory, APName[2], 0.00).getDouble(0.00);
		float sunTemp = (float)config.get(catagory, APName[3], 0.00).getDouble(0.00);
		float nightMult = (float)config.get(catagory, APName[4], 1.00).getDouble(1.00);
		float shadeMult = (float)config.get(catagory, APName[5], 1.00).getDouble(1.00);
		float sunMult = (float)config.get(catagory, APName[6], 1.00).getDouble(1.00);
		float sanity = (float)config.get(catagory, APName[7], 0.00).getDouble(0.00);
		float air = (float)config.get(catagory, APName[8], 0.00).getDouble(0.00);
		
		Object tmp = Item.itemRegistry.getObject(name);
		boolean allowCamelPack = true;
		if (tmp instanceof ItemArmor && ((ItemArmor)tmp).armorType == 1) {
			allowCamelPack = config.get(catagory, APName[9], true).getBoolean(true);
		}
		
		ArmorProperties entry = new ArmorProperties(name, nightTemp, shadeTemp, sunTemp, nightMult, shadeMult, sunMult, sanity, air, allowCamelPack);
		EM_Settings.armorProperties.put(name, entry);
	}
	
	public static void SaveProperty(Configuration config, String catName, String name, double nightTemp, double shadeTemp, double sunTemp, double nightMult, double shadeMult, double sunMult, double sanity, double air)
	{
		config.get(catName, APName[0], name).getString();
		config.get(catName, APName[1], nightTemp).getDouble(nightTemp);
		config.get(catName, APName[2], shadeTemp).getDouble(shadeTemp);
		config.get(catName, APName[3], sunTemp).getDouble(sunTemp);
		config.get(catName, APName[4], nightMult).getDouble(nightMult);
		config.get(catName, APName[5], shadeMult).getDouble(shadeMult);
		config.get(catName, APName[6], sunMult).getDouble(sunMult);
		config.get(catName, APName[7], sanity).getDouble(sanity);
		config.get(catName, APName[8], air).getDouble(air);
	}
	
	public static void SaveDefaults(Configuration configFile)
	{
		SaveProperty(configFile, categoryName + ".helmetLeather", 	Item.itemRegistry.getNameForObject(Items.leather_helmet), 		1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
		SaveProperty(configFile, categoryName + ".plateLeather", 	Item.itemRegistry.getNameForObject(Items.leather_chestplate), 	1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
		SaveProperty(configFile, categoryName + ".legsLeather", 	Item.itemRegistry.getNameForObject(Items.leather_leggings), 	1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
		SaveProperty(configFile, categoryName + ".bootsLeather", 	Item.itemRegistry.getNameForObject(Items.leather_boots), 		1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
		
		SaveProperty(configFile, categoryName + ".helmetIron", 		Item.itemRegistry.getNameForObject(Items.iron_helmet), 		-0.5, 0.0, 2.5, 1.0, 1.0, 1.1, 0.0, 0.0);
		SaveProperty(configFile, categoryName + ".plateIron", 		Item.itemRegistry.getNameForObject(Items.iron_chestplate), 	-0.5, 0.0, 2.5, 1.0, 1.0, 1.1, 0.0, 0.0);
		SaveProperty(configFile, categoryName + ".legsIron", 		Item.itemRegistry.getNameForObject(Items.iron_leggings), 	-0.5, 0.0, 2.5, 1.0, 1.0, 1.1, 0.0, 0.0);
		SaveProperty(configFile, categoryName + ".bootsIron", 		Item.itemRegistry.getNameForObject(Items.iron_boots), 		-0.5, 0.0, 2.5, 1.0, 1.0, 1.1, 0.0, 0.0);
		
		SaveProperty(configFile, categoryName + ".helmetGold", 		Item.itemRegistry.getNameForObject(Items.golden_helmet), 		0.0, 0.0, 0.0, 1.0, 1.0, 1.2, 0.0, 0.0);
		SaveProperty(configFile, categoryName + ".plateGold", 		Item.itemRegistry.getNameForObject(Items.golden_chestplate), 	0.0, 0.0, 0.0, 1.0, 1.0, 1.2, 0.0, 0.0);
		SaveProperty(configFile, categoryName + ".legsGold", 		Item.itemRegistry.getNameForObject(Items.golden_leggings), 		0.0, 0.0, 0.0, 1.0, 1.0, 1.2, 0.0, 0.0);
		SaveProperty(configFile, categoryName + ".bootsGold", 		Item.itemRegistry.getNameForObject(Items.golden_boots), 		0.0, 0.0, 0.0, 1.0, 1.0, 1.2, 0.0, 0.0);
		
		SaveProperty(configFile, categoryName + ".helmetDiamond", 	Item.itemRegistry.getNameForObject(Items.diamond_helmet), 		0.0, 0.0, 0.0, 1.1, 1.0, 0.9, 0.0, 0.0);
		SaveProperty(configFile, categoryName + ".plateDiamond", 	Item.itemRegistry.getNameForObject(Items.diamond_chestplate), 	0.0, 0.0, 0.0, 1.1, 1.0, 0.9, 0.0, 0.0);
		SaveProperty(configFile, categoryName + ".legsDiamond", 	Item.itemRegistry.getNameForObject(Items.diamond_leggings), 	0.0, 0.0, 0.0, 1.1, 1.0, 0.9, 0.0, 0.0);
		SaveProperty(configFile, categoryName + ".bootsDiamond", 	Item.itemRegistry.getNameForObject(Items.diamond_boots), 		0.0, 0.0, 0.0, 1.1, 1.0, 0.9, 0.0, 0.0);

	}
	
	public static void SearchForModdedArmors()
	{
		//EnviroMine.logger.log(Level.INFO, "Searcing for mod armors...");
	
		
		Iterator itemList = Item.itemRegistry.iterator();
		Item theitem;
		//int armorCount = 0;
		
		while(itemList.hasNext())
		{
			theitem = (Item) itemList.next();
			String[] Names = SplitObjectName(Item.itemRegistry.getNameForObject(theitem));
			
			if(!Names[0].equalsIgnoreCase("minecraft")) // Ignore Minecraft Items
			{
				if(theitem instanceof ItemArmor)
				{
					
					DetectedArmorGen((ItemArmor)theitem, Names[0]);
					//armorCount += 1;
				}
			}

			
		}
		
		//EnviroMine.logger.log(Level.INFO, "Found " + armorCount + " mod armors");
	}
	

	private static void DetectedArmorGen(ItemArmor armor, String ModID)
	{
		String[] classpath = armor.getClass().getCanonicalName().toString().split("\\.");
		
		
		File armorFile = new File(EM_ConfigHandler.customPath + classpath[0] + ".cfg");
		if(!armorFile.exists())
		{
			try
			{
				armorFile.createNewFile();
			} catch(IOException e)
			{
				e.printStackTrace();
				return;
			}
		}
	
		Configuration config = new Configuration(armorFile, true);
		config.load();
		
		String catName = categoryName + "." + EnviroUtils.replaceULN(armor.getUnlocalizedName());
		
		config.addCustomCategoryComment(catName, "");
		config.get(catName, APName[0], Item.itemRegistry.getNameForObject(armor)).getString();
		config.get(catName, APName[1], 0.0D).getDouble(0.0D);
		config.get(catName, APName[2], 0.0D).getDouble(0.0D);
		config.get(catName, APName[3], 0.0D).getDouble(0.0D);
		config.get(catName, APName[4], 1.0D).getDouble(1.0D);
		config.get(catName, APName[5], 1.0D).getDouble(1.0D);
		config.get(catName, APName[6], 1.0D).getDouble(1.0D);
		config.get(catName, APName[7], 0.0D).getDouble(0.0D);
		config.get(catName, APName[8], 0.0D).getDouble(0.0D);
		
		config.save();
	}
	
	private static String[] SplitObjectName(String splitName)
	{
		String[] nameArr = splitName.split(":");
		return nameArr;
	}
}
