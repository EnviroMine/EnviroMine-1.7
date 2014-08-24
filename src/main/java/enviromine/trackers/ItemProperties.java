package enviromine.trackers;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import enviromine.core.EM_Settings;

public class ItemProperties
{
	public String name;
	public int meta;
	
	public boolean enableTemp;
	
	public float ambTemp;
	public float ambAir;
	public float ambSanity;
	
	public float effTemp;
	public float effAir;
	public float effSanity;
	public float effHydration;
	
	public float effTempCap;
	
	/** Item properties:<br>00 ({@link String}) Name <br>01 ({@link Int}) Damage <br>02 ({@link Boolean}) Enable Ambient Temperature <br>03 ({@link Double}) Ambient Temperature <br>04 ({@link Double}) Ambient Air Quality <br>05 ({@link Double}) Ambient Santity <br>06 ({@link Double}) Effect Temperature <br>07 ({@link Double}) Effect Air Quality <br>08 ({@link Double}) Effect Sanity <br>09 ({@link Double}) Effect Hydration <br>10 ({@link Double}) Effect Temperature Cap */
	static String[] IPName;
	
	public static String categoryName = "items";
	
	public ItemProperties(String name, int meta, boolean enableTemp, float ambTemp, float ambAir, float ambSanity, float effTemp, float effAir, float effSanity, float effHydration, float effTempCap)
	{
		this.name = name;
		this.meta = meta;
		this.enableTemp = enableTemp;
		
		this.ambTemp = ambTemp;
		this.ambAir = ambAir;
		this.ambSanity = ambSanity;
		
		this.effTemp = effTemp;
		this.effAir = effAir;
		this.effSanity = effSanity;
		this.effHydration = effHydration;
		
		this.effTempCap = effTempCap;
	}
	
	/**Set up config names for Item properties:<br>00 ({@link String}) Name <br>01 ({@link Int}) Damage <br>02 ({@link Boolean}) Enable Ambient Temperature <br>03 ({@link Double}) Ambient Temperature <br>04 ({@link Double}) Ambient Air Quality <br>05 ({@link Double}) Ambient Santity <br>06 ({@link Double}) Effect Temperature <br>07 ({@link Double}) Effect Air Quality <br>08 ({@link Double}) Effect Sanity <br>09 ({@link Double}) Effect Hydration <br>10 ({@link Double}) Effect Temperature Cap */
	public static void setConfigNames()
	{
		IPName = new String[11];
		IPName[0] = "01.Name";
		IPName[1] = "02.Damage";
		IPName[2] = "03.Enable Ambient Temperature";
		IPName[3] = "04.Ambient Temperature";
		IPName[4] = "05.Ambient Air Quality";
		IPName[5] = "06.Ambient Santity";
		IPName[6] = "07.Effect Temperature";
		IPName[7] = "08.Effect Air Quality";
		IPName[8] = "09.Effect Sanity";
		IPName[9] = "10.Effect Hydration";
		IPName[10] = "11.Effect Temperature Cap";
	}
	
	public static void LoadProperty(Configuration config, String category)
	{
		config.addCustomCategoryComment(category, "");
		
		String name = config.get(category, IPName[0], "").getString();
		int meta = config.get(category, IPName[1], 0).getInt(0);
		boolean enableTemp = config.get(category, IPName[2], false).getBoolean(false);
		float ambTemp = (float)config.get(category, IPName[3], 0.00).getDouble(0.00);
		float ambAir = (float)config.get(category, IPName[4], 0.00).getDouble(0.00);
		float ambSanity = (float)config.get(category, IPName[5], 0.00).getDouble(0.00);
		float effTemp = (float)config.get(category, IPName[6], 0.00).getDouble(0.00);
		float effAir = (float)config.get(category, IPName[7], 0.00).getDouble(0.00);
		float effSanity = (float)config.get(category, IPName[8], 0.00).getDouble(0.00);
		float effHydration = (float)config.get(category, IPName[9], 0.00).getDouble(0.00);
		float effTempCap = (float)config.get(category, IPName[10], 37.00).getDouble(37.00);
		
		ItemProperties entry = new ItemProperties(name, meta, enableTemp, ambTemp, ambAir, ambSanity, effTemp, effAir, effSanity, effHydration, effTempCap);
		
		if(meta < 0)
		{
			EM_Settings.itemProperties.put("" + name, entry);
		} else
		{
			EM_Settings.itemProperties.put("" + name + "," + meta, entry);
		}
	}
	
	public static void SaveProperty(Configuration config, String catName, String name, int meta, boolean enableAmbTemp, double ambTemp, double ambAir, double ambSanity, double effTemp, double effAir, double effSanity, double effHydration, double tempCap)
	{
		config.get(catName, IPName[0], name).getString();
		config.get(catName, IPName[1], meta).getInt(meta);
		config.get(catName, IPName[2], enableAmbTemp).getBoolean(enableAmbTemp);
		config.get(catName, IPName[3], ambTemp).getDouble(ambTemp);
		config.get(catName, IPName[4], ambAir).getDouble(ambAir);
		config.get(catName, IPName[5], ambSanity).getDouble(ambSanity);
		config.get(catName, IPName[6], effTemp).getDouble(effTemp);
		config.get(catName, IPName[7], effTemp).getDouble(effTemp);
		config.get(catName, IPName[8], effSanity).getDouble(effSanity);
		config.get(catName, IPName[9], effHydration).getDouble(effHydration);
		config.get(catName, IPName[10], tempCap).getDouble(tempCap);
	}
	
	public static void SaveDefaults(Configuration configFile)
	{
		SaveProperty(configFile, categoryName + ".potions", 	Item.itemRegistry.getNameForObject(Items.potionitem), 	-1, false, 0.0, 0.0, 0.0, -0.05, 0.0, 0.0, 25.0, 37.05);
		SaveProperty(configFile, categoryName + ".melon", 		Item.itemRegistry.getNameForObject(Items.melon), 		-1, false, 0.0, 0.0, 0.0, -0.01, 0.0, 0.0, 5.0, 37.01);
		SaveProperty(configFile, categoryName + ".carrot", 		Item.itemRegistry.getNameForObject(Items.carrot), 		-1, false, 0.0, 0.0, 0.0, -0.01, 0.0, 0.0, 5.0, 37.01);
		SaveProperty(configFile, categoryName + ".goldCarrot", 	Item.itemRegistry.getNameForObject(Items.golden_apple), -1, false, 0.0, 0.0, 0.0, -0.01, 0.0, 0.0, 5.0, 37.01);
		SaveProperty(configFile, categoryName + ".redApple", 	Item.itemRegistry.getNameForObject(Items.apple), 		-1, false, 0.0, 0.0, 0.0, -0.01, 0.0, 0.0, 5.0, 37.01);
		
		SaveProperty(configFile, categoryName + ".bucketLava", 	Item.itemRegistry.getNameForObject(Items.lava_bucket), 		-1, true, 100.0, -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 37.0);
		SaveProperty(configFile, categoryName + ".redFlower", 	Block.blockRegistry.getNameForObject(Blocks.red_flower), 	-1, false, 0.0, 0.01, 0.1, 0.0, 0.0, 0.0, 0.0, 37.0);
		SaveProperty(configFile, categoryName + ".yellowFlower",Block.blockRegistry.getNameForObject(Blocks.yellow_flower), -1, false, 0.0, 0.01, 0.1, 0.0, 0.0, 0.0, 0.0, 37.0);
		SaveProperty(configFile, categoryName + ".leaves", 		Block.blockRegistry.getNameForObject(Blocks.leaves), 		-1, false, 0.0, 0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 37.0);
		SaveProperty(configFile, categoryName + ".snowBlock", 	Block.blockRegistry.getNameForObject(Blocks.snow), 			-1, true, -0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 37.0);
		SaveProperty(configFile, categoryName + ".ice", 		Block.blockRegistry.getNameForObject(Blocks.ice), 			-1, true, -0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 37.0);
		SaveProperty(configFile, categoryName + ".snowLayer",	Block.blockRegistry.getNameForObject(Blocks.snow_layer), 	-1, true, -0.05, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 37.0);
		SaveProperty(configFile, categoryName + ".netherrack", 	Block.blockRegistry.getNameForObject(Blocks.netherrack), 	-1, true, 50.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 37.0);
		SaveProperty(configFile, categoryName + ".soulSand", 	Block.blockRegistry.getNameForObject(Blocks.soul_sand), 	-1, false, 0.0, 0.0, -0.5, 0.0, 0.0, 0.0, 0.0, 37.0);
		SaveProperty(configFile, categoryName + ".skull", 		Item.itemRegistry.getNameForObject(Items.skull), 			-1, false, 0.0, 0.0, -0.1, 0.0, 0.0, 0.0, 0.0, 37.0);
		SaveProperty(configFile, categoryName + ".web", 		Block.blockRegistry.getNameForObject(Blocks.web), 			-1, false, 0.0, 0.0, -0.01, 0.0, 0.0, 0.0, 0.0, 37.0);
		SaveProperty(configFile, categoryName + ".11", 			Item.itemRegistry.getNameForObject(Items.record_11), 		-1, false, 0.0, 0.0, -1, 0.0, 0.0, 0.0, 0.0, 37.0);
	}
}
