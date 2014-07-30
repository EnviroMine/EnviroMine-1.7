package enviromine.core;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import cpw.mods.fml.common.registry.EntityRegistry;

import enviromine.handlers.keybinds.AddRemoveCustom;
import enviromine.trackers.ArmorProperties;
import enviromine.trackers.BlockProperties;
import enviromine.trackers.EntityProperties;
import enviromine.trackers.ItemProperties;
import enviromine.trackers.StabilityType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;

public class EM_ConfigHandler
{
	// Dirs for Custom Files
	public static String configPath = "config/enviromine/";
	static String customPath = configPath + "CustomProperties/";
	
	// Categories for Custom Objects
	static String armorCat = "armor";
	static String blockCat = "blocks";
	static String entityCat = "entity";
	static String itemsCat = "items";
	
	// Arrays for property names
	static String[] APName;
	static String[] BPName;
	static String[] EPName;
	static String[] IPName;
	static String[] SPName;
	
	public static int initConfig()
	{
		// Load in property names into arrays
		setPropertyNames();
		
		// Check for Data Directory 
		CheckDir(new File(customPath));
		
		EnviroMine.logger.log(Level.INFO, "Loading configs...");
		
		File stabConfigFile = new File(configPath + "StabilityTypes.cfg");
		loadStabilityTypes(stabConfigFile);
		
		// load defaults
		if(EM_Settings.useDefaultConfig)
		{
			loadDefaultArmorProperties();
		}
		
		// Now load Files from "Custom Objects"
		File[] customFiles = GetFileList(customPath);
		for(int i = 0; i < customFiles.length; i++)
		{
			LoadCustomObjects(customFiles[i]);
		}
		
		// Load Main Config File And this will go though changes
		File configFile = new File(configPath + "EnviroMine.cfg");
		loadGeneralConfig(configFile);
		
		int Total = EM_Settings.armorProperties.size() + EM_Settings.blockProperties.size() + EM_Settings.livingProperties.size()+ EM_Settings.itemProperties.size();
		
		return Total;
	}
	
	private static void setPropertyNames()
	{
		APName = new String[9];
		APName[0] = "01.ID";
		APName[1] = "02.Temp Add - Night";
		APName[2] = "03.Temp Add - Shade";
		APName[3] = "04.Temp Add - Sun";
		APName[4] = "05.Temp Multiplier - Night";
		APName[5] = "06.Temp Multiplier - Shade";
		APName[6] = "07.Temp Multiplier - Sun";
		APName[7] = "08.Sanity";
		APName[8] = "09.Air";
		
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
		
		EPName = new String[15];
		EPName[0] = "01.Entity ID";
		EPName[1] = "02.Enable EnviroTracker";
		EPName[2] = "03.Enable Dehydration";
		EPName[3] = "04.Enable BodyTemp";
		EPName[4] = "05.Enable Air Quality";
		EPName[5] = "06.Immune To Frost";
		EPName[6] = "07.Immune To Heat";
		EPName[7] = "08.Ambient Sanity";
		EPName[8] = "09.Hit Sanity";
		EPName[9] = "10.Ambient Temperature";
		EPName[10] = "11.Hit Temperature";
		EPName[11] = "12.Ambient Air";
		EPName[12] = "13.Hit Air";
		EPName[13] = "14.Ambient Hydration";
		EPName[14] = "15.Hit Hydration";
		
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
		
		SPName = new String[6];
		SPName[0] = "01.Enable Physics";
		SPName[1] = "02.Max Support Distance";
		SPName[2] = "03.Min Missing Blocks To Fall";
		SPName[3] = "04.Max Missing Blocks To Fall";
		SPName[4] = "05.Can Hang";
		SPName[5] = "06.Holds Others Up";
	}

	public static void loadGeneralConfig(File file)
	{
		Configuration config;
		try
		{
			config = new Configuration(file, true);
		} catch(NullPointerException e)
		{
			e.printStackTrace();
			EnviroMine.logger.log(Level.WARN, "FAILED TO LOAD MAIN CONFIG!\nBACKUP SETTINGS ARE NOW IN EFFECT!");
			return;
		} catch(StringIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			EnviroMine.logger.log(Level.WARN, "FAILED TO LOAD MAIN CONFIG!\nBACKUP SETTINGS ARE NOW IN EFFECT!");
			return;
		}
		
		config.load();
		
		//World Generation
		
		EM_Settings.shaftGen = config.get("World Generation", "Enable Village MineShafts", true, "Generates mineshafts in villages").getBoolean(true);
		
		//General Settings
		EM_Settings.enablePhysics = config.get(Configuration.CATEGORY_GENERAL, "Enable Physics", true, "Turn physics On/Off").getBoolean(true);
		EM_Settings.enableLandslide = config.get(Configuration.CATEGORY_GENERAL, "Enable Physics Landslide", true).getBoolean(true);
		EM_Settings.enableSanity = config.get(Configuration.CATEGORY_GENERAL, "Allow Sanity", true).getBoolean(true);
		EM_Settings.enableHydrate = config.get(Configuration.CATEGORY_GENERAL, "Allow Hydration", true).getBoolean(true);
		EM_Settings.enableBodyTemp = config.get(Configuration.CATEGORY_GENERAL, "Allow Body Temperature", true).getBoolean(true);
		EM_Settings.enableAirQ = config.get(Configuration.CATEGORY_GENERAL, "Allow Air Quality", true, "True/False to turn Enviromine Trackers for Sanity, Air Quality, Hydration, and Body Temperature.").getBoolean(true);
		EM_Settings.trackNonPlayer = config.get(Configuration.CATEGORY_GENERAL, "Track NonPlayer entitys", false, "Track enviromine properties on Non-player entites(mobs & animals)").getBoolean(false);
		EM_Settings.updateCheck = config.get(Configuration.CATEGORY_GENERAL, "Check For Updates", true).getBoolean(true);
		EM_Settings.physBlockID = config.get(Configuration.CATEGORY_GENERAL, "EntityPhysicsBlock ID", EntityRegistry.findGlobalUniqueEntityId()).getInt(EntityRegistry.findGlobalUniqueEntityId());
		EM_Settings.villageAssist = config.get(Configuration.CATEGORY_GENERAL, "Enable villager assistance", true).getBoolean(true);
		
		// Physics Settings
		String PhySetCat = "Physics";
		int minPhysInterval = 5;
		EM_Settings.spreadIce = config.get(PhySetCat, "Large Ice Cracking", false, "Setting Large Ice Cracking to true can cause Massive Lag").getBoolean(false);
		EM_Settings.updateCap = config.get(PhySetCat, "Consecutive Physics Update Cap", 128 , "This will change maximum number of blocks that can be updated with physics at a time. - 1 = Unlimited").getInt(128);
		EM_Settings.physInterval = getConfigIntWithMinInt(config.get(PhySetCat, "Physics Interval", minPhysInterval , "The number of ticks between physics update passes (must be "+minPhysInterval+" or more)"), minPhysInterval);
		EM_Settings.stoneCracks = config.get(PhySetCat, "Stone Cracks Before Falling", true).getBoolean(true);
		EM_Settings.defaultStability = config.get(PhySetCat, "Default Stability Type (BlockIDs > 175)", "loose").getString();
		EM_Settings.worldDelay = config.get(PhySetCat, "World Start Delay", 1000, "How long after world start until the physics system kicks in (DO NOT SET TOO LOW)").getInt(1000);
		EM_Settings.chunkDelay = config.get(PhySetCat, "Chunk Physics Delay", 500, "How long until individual chunk's physics starts after loading (DO NOT SET TOO LOW)").getInt(500);
		EM_Settings.entityFailsafe = config.get(PhySetCat, "Physics entity fail safe level", 1, "0 = No action, 1 = Limit to < 100 per 8x8 block area, 2 = Delete excessive entities & Dump physics (EMERGENCY ONLY)").getInt(1);
		
		// Gui settings
		String GuiSetCat = "GUI Settings";
		EM_Settings.sweatParticals = config.get(GuiSetCat, "Show Sweat Particales", true).getBoolean(true);
		EM_Settings.insaneParticals = config.get(GuiSetCat, "Show Insanity Particles", true, "Show/Hide Particales").getBoolean(true);
		EM_Settings.useFarenheit = config.get(GuiSetCat, "Use Farenheit instead of Celsius", false, "Will display either Farenhit or Celcius on GUI").getBoolean(false);
		EM_Settings.heatBarPos = config.get(GuiSetCat, "Position Heat Bat", "Bottom_Left").getString();
		EM_Settings.waterBarPos = config.get(GuiSetCat, "Position Thirst Bar", "Bottom_Left").getString();
		EM_Settings.sanityBarPos = config.get(GuiSetCat, "Position Sanity Bar", "Bottom_Right").getString();
		EM_Settings.oxygenBarPos = config.get(GuiSetCat, "Position Air Quality Bar", "Bottom_Right", "Change position of Enviro Bars. Options: Bottom_Left, Bottom_Right, Bottom_Center_Left, Bottom_Center_Right, Top_Left, Top_Right, Top_Center, Middle_Left, Middle_Right, Custom_#,# (Custom_X(0-100),Y(0-100))").getString();
		EM_Settings.minimalHud = config.get(GuiSetCat, "Minimalistic Bars", false, "WARNING: This option will hide the ambient air temperature! It will also override icons and text to true.").getBoolean(false);
		
		EM_Settings.ShowDebug = config.get(GuiSetCat, "Show Gui Debugging Info", false, "Show Hide Gui Text Display and Icons").getBoolean(false);
		EM_Settings.ShowText = config.get(GuiSetCat, "Show Gui Status Text", true).getBoolean(true);
		EM_Settings.ShowGuiIcons = config.get(GuiSetCat, "Show Gui Icons", true).getBoolean(true);
		
		// Config Item ID's
		/*String ItemSetCat = "item";
		EM_Settings.dirtBottleID = config.get(ItemSetCat, "Dirty Water Bottle", 5001).getInt(5001);
		EM_Settings.saltBottleID = config.get(ItemSetCat, "Salt Water Bottle", 5002).getInt(5002);
		EM_Settings.coldBottleID = config.get(ItemSetCat, "Cold Water Bottle", 5003).getInt(5003);
		EM_Settings.camelPackID = config.get(ItemSetCat, "Camel Pack", 5004).getInt(5004);*/
		
		// Potion ID's
		EM_Settings.hypothermiaPotionID = -1;
		EM_Settings.heatstrokePotionID = -1;
		EM_Settings.frostBitePotionID = -1;
		EM_Settings.dehydratePotionID = -1;
		EM_Settings.insanityPotionID = -1;
		
		EM_Settings.hypothermiaPotionID = config.get("Potions", "Hypothermia", nextAvailPotion(27)).getInt(nextAvailPotion(27));
		EM_Settings.heatstrokePotionID = config.get("Potions", "Heat Stroke", nextAvailPotion(28)).getInt(nextAvailPotion(28));
		EM_Settings.frostBitePotionID = config.get("Potions", "Frostbite", nextAvailPotion(29)).getInt(nextAvailPotion(29));
		EM_Settings.dehydratePotionID = config.get("Potions", "Dehydration", nextAvailPotion(30)).getInt(nextAvailPotion(30));
		EM_Settings.insanityPotionID = config.get("Potions", "Insanity", nextAvailPotion(31)).getInt(nextAvailPotion(31));
		
		// Multipliers ID's
		EM_Settings.tempMult = config.get("Speed Multipliers", "BodyTemp", 1.0D).getDouble(1.0D);
		EM_Settings.hydrationMult = config.get("Speed Multipliers", "Hydration", 1.0D).getDouble(1.0D);
		EM_Settings.airMult = config.get("Speed Multipliers", "AirQuality", 1.0D).getDouble(1.0D);
		EM_Settings.sanityMult = config.get("Speed Multipliers", "Sanity", 1.0D).getDouble(1.0D);
		
		EM_Settings.tempMult 		= EM_Settings.tempMult 		< 0? 	0F : EM_Settings.tempMult;
		EM_Settings.hydrationMult 	= EM_Settings.hydrationMult < 0? 	0F : EM_Settings.hydrationMult;
		EM_Settings.airMult 		= EM_Settings.airMult 		< 0? 	0F : EM_Settings.airMult;
		EM_Settings.sanityMult 		= EM_Settings.sanityMult 	< 0? 	0F : EM_Settings.sanityMult;
		
		// Config Options
		String ConSetCat = "Config";
		EM_Settings.genArmorConfigs = config.get(ConSetCat, "Generate Armor Configs", true, "Will attempt to find and generate blank configs for any custom armors loaded before EnviroMine.").getBoolean(true);
		EM_Settings.useDefaultConfig = config.get(ConSetCat, "Generate Defaults", true).getBoolean(true);
		
		config.save();
	}
	
	private static int getConfigIntWithMinInt(Property prop, int min)
	{
		if (prop.getInt(min) >= min) {
			return prop.getInt(min);
		} else {
			prop.set(min);
			return min;
		}
	}
	
	static int nextAvailPotion(int startID)
	{
		for(int i = startID; i > 0; i++)
		{
			if(i == EM_Settings.hypothermiaPotionID || i == EM_Settings.heatstrokePotionID || i == EM_Settings.frostBitePotionID || i == EM_Settings.dehydratePotionID || i == EM_Settings.insanityPotionID)
			{
				continue;
			} else if(i >= Potion.potionTypes.length)
			{
				return i;
			} else if(Potion.potionTypes[i] == null)
			{
				return i;
			}
		}
		
		return startID;
	}

	//#######################################
	//#          Get File List              #                 
	//#This Grabs Directory List for Custom #
	//#######################################
	public static File[] GetFileList(String path)
	{
		
		// Will be used Auto Load Custom Objects from ??? Dir 
		File f = new File(path);
		File[] list = f.listFiles();
		
		return list;
	}
	
	private static boolean isCFGFile(String fileName)
	{
		//Matcher
		String patternString = "(.*\\.cfg$)";
		
		Pattern pattern;
		Matcher matcher;
		// Make Sure its a .cfg File
		pattern = Pattern.compile(patternString);
		matcher = pattern.matcher(fileName);
		
		return matcher.matches();
	}
	
	//###################################
	//#           Check Dir             #                 
	//#  Checks for, or makes Directory #
	//###################################	
	public static void CheckDir(File Dir)
	{
		boolean dirFlag = false;
		
		// create File object
		
		if(Dir.exists())
		{
			return;
		}
		
		try
		{
			dirFlag = Dir.mkdirs();
		} catch(SecurityException Se)
		{
			EnviroMine.logger.log(Level.WARN, "Error while creating config directory:\n" + Se);
		}
		
		if(!dirFlag)
		{
			EnviroMine.logger.log(Level.WARN, "Failed to create config directory!");
		}
	}
	
	//####################################
	//#   Load Custom Objects            #
	//# Used to Load Custom Blocks,Armor #                              
	//#   Entitys, & Items from Mods     #
	//####################################
	public static void LoadCustomObjects(File customFiles)
	{
		boolean datFile = isCFGFile(customFiles.getName());
		
		// Check to make sure this is a Data File Before Editing
		if(datFile == true)
		{
			Configuration config;
			try
			{
				config = new Configuration(customFiles, true);
				
				EnviroMine.logger.log(Level.INFO, "Loading Config File: " + customFiles.getAbsolutePath());

			} catch(NullPointerException e)
			{
				e.printStackTrace();
				EnviroMine.logger.log(Level.WARN, "FAILED TO LOAD CUSTOM CONFIG: " + customFiles.getName() + "\nNEW SETTINGS WILL BE IGNORED!");
				return;
			} catch(StringIndexOutOfBoundsException e)
			{
				e.printStackTrace();
				EnviroMine.logger.log(Level.WARN, "FAILED TO LOAD CUSTOM CONFIG: " + customFiles.getName() + "\nNEW SETTINGS WILL BE IGNORED!");
				return;
			}
			
			config.load();
			
			// Load Default Categories
			config.addCustomCategoryComment(armorCat, "Custom armor properties");
			config.addCustomCategoryComment(blockCat, "Custom block properties");
			config.addCustomCategoryComment(entityCat, "Custom entity properties");
			config.addCustomCategoryComment(itemsCat, "Custom item properties");
			
			// 	Grab all Categories in File
			List<String> catagory = new ArrayList<String>();
			Set<String> nameList = config.getCategoryNames();
			Iterator<String> nameListData = nameList.iterator();
			
			// add Categories to a List 
			while(nameListData.hasNext())
			{
				catagory.add(nameListData.next());
			}
			
			// Now Read/Save Each Category And Add into Proper Hash Maps
			
			for(int x = 0; x <= (catagory.size() - 1); x++)
			{
				String CurCat = catagory.get(x);
				
				if(!((String)CurCat).isEmpty() && ((String)CurCat).contains(Configuration.CATEGORY_SPLITTER))
				{
					String parent = CurCat.split("\\" + Configuration.CATEGORY_SPLITTER)[0];
					
					if(parent.equals(blockCat))
					{
						LoadBlockProperty(config, catagory.get(x));
					} else if(parent.equals(armorCat))
					{
						LoadArmorProperty(config, catagory.get(x));
					} else if(parent.equals(itemsCat))
					{
						LoadItemProperty(config, catagory.get(x));
					} else if(parent.equals(entityCat))
					{
						LoadLivingProperty(config, catagory.get(x));
					} else
					{
						EnviroMine.logger.log(Level.WARN, "Failed to load object " + CurCat);
					}
					
				}
			}
			
			config.save();
		}
	}
	
	private static void LoadItemProperty(Configuration config, String category)
	{
		
		config.addCustomCategoryComment(category, "");
		int id = 					config.get(category, IPName[0], 0).getInt(0);
		int meta = 					config.get(category, IPName[1], 0).getInt(0);
		boolean enableTemp = 		config.get(category, IPName[2], false).getBoolean(false);
		float ambTemp = (float)		config.get(category, IPName[3], 0.00).getDouble(0.00);
		float ambAir = (float)		config.get(category, IPName[4], 0.00).getDouble(0.00);
		float ambSanity = (float)	config.get(category, IPName[5], 0.00).getDouble(0.00);
		float effTemp = (float)		config.get(category, IPName[6], 0.00).getDouble(0.00);
		float effAir = (float)		config.get(category, IPName[7], 0.00).getDouble(0.00);
		float effSanity = (float)	config.get(category, IPName[8], 0.00).getDouble(0.00);
		float effHydration = (float)config.get(category, IPName[9], 0.00).getDouble(0.00);
		float effTempCap = (float)	config.get(category, IPName[10], 37.00).getDouble(37.00);
		
		ItemProperties entry = new ItemProperties(id, meta, enableTemp, ambTemp, ambAir, ambSanity, effTemp, effAir, effSanity, effHydration, effTempCap);
		
		if(meta < 0)
		{
			EM_Settings.itemProperties.put("" + id, entry);
		} else
		{
			EM_Settings.itemProperties.put("" + id + "," + meta, entry);
		}
	}
	
	private static void LoadBlockProperty(Configuration config, String category)
	{
		config.addCustomCategoryComment(category, "");
		
		String name = 				config.get(category, BPName[0], "").getString();
		int metaData = 				config.get(category, BPName[1], 0).getInt(0);
		String dropName = 			config.get(category, BPName[2], "").getString();
		int dropMeta = 				config.get(category, BPName[3], 0).getInt(0);
		int dropNum = 				config.get(category, BPName[4], 0).getInt(0);
		boolean enableTemp = 		config.get(category, BPName[5], false).getBoolean(false);
		float temperature = (float)	config.get(category, BPName[6], 0.00).getDouble(0.00);
		float airQuality = (float)	config.get(category, BPName[7], 0.00).getDouble(0.00);
		float sanity = (float)		config.get(category, BPName[8], 0.00).getDouble(0.00);
		String stability = 			config.get(category, BPName[9], "loose").getString();
		boolean slides = 			config.get(category, BPName[10], false).getBoolean(false);
		boolean wetSlides = 		config.get(category, BPName[11], false).getBoolean(false);
		
		// Get Stability Options
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
			EnviroMine.logger.log(Level.WARN,"Stability type '" + stability + "' not found.");
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
	
	private static void LoadArmorProperty(Configuration config, String catagory)
	{
		config.addCustomCategoryComment(catagory, "");
		int id = 					config.get(catagory, APName[0], 0).getInt(0);
		float nightTemp = (float)	config.get(catagory, APName[1], 0.00).getDouble(0.00);
		float shadeTemp = (float)	config.get(catagory, APName[2], 0.00).getDouble(0.00);
		float sunTemp = (float)		config.get(catagory, APName[3], 0.00).getDouble(0.00);
		float nightMult = (float)	config.get(catagory, APName[4], 1.00).getDouble(1.00);
		float shadeMult = (float)	config.get(catagory, APName[5], 1.00).getDouble(1.00);
		float sunMult = (float)		config.get(catagory, APName[6], 1.00).getDouble(1.00);
		float sanity = (float)		config.get(catagory, APName[7], 0.00).getDouble(0.00);
		float air = (float)			config.get(catagory, APName[8], 0.00).getDouble(0.00);
		
		ArmorProperties entry = new ArmorProperties(id, nightTemp, shadeTemp, sunTemp, nightMult, shadeMult, sunMult, sanity, air);
		EM_Settings.armorProperties.put(id, entry);
	}
	
	private static void LoadLivingProperty(Configuration config, String catagory)
	{
		config.addCustomCategoryComment(catagory, "");
		int id = 			config.get(catagory, EPName[0], 0).getInt(0);
		boolean track = 		config.get(catagory, EPName[1], true).getBoolean(true);
		boolean dehydration = 	config.get(catagory, EPName[2], true).getBoolean(true);
		boolean bodyTemp = 		config.get(catagory, EPName[3], true).getBoolean(true);
		boolean airQ = 			config.get(catagory, EPName[4], true).getBoolean(true);
		boolean immuneToFrost = config.get(catagory, EPName[5], false).getBoolean(false);
		boolean immuneToHeat = 	config.get(catagory, EPName[6], false).getBoolean(false);
		float aSanity = (float)	config.get(catagory, EPName[7], 0.0D).getDouble(0.0D);
		float hSanity = (float)	config.get(catagory, EPName[8], 0.0D).getDouble(0.0D);
		float aTemp = (float)	config.get(catagory, EPName[9], 37.0D, "Overridden by body temp").getDouble(37.0D);
		float hTemp = (float)	config.get(catagory, EPName[10], 0.0D).getDouble(0.0D);
		float aAir = (float)	config.get(catagory, EPName[11], 0.0D).getDouble(0.0D);
		float hAir = (float)	config.get(catagory, EPName[12], 0.0D).getDouble(0.0D);
		float aHyd = (float)	config.get(catagory, EPName[13], 0.0D).getDouble(0.0D);
		float hHyd = (float)	config.get(catagory, EPName[14], 0.0D).getDouble(0.0D);
		
		EntityProperties entry = new EntityProperties(id, track, dehydration, bodyTemp, airQ, immuneToFrost, immuneToHeat, aSanity, hSanity, aTemp, hTemp, aAir, hAir, aHyd, hHyd);
		EM_Settings.livingProperties.put(id, entry);
	}
	
	// RIGHT NOW I AM JUST LOADING DEFAULT ARMOR INTO HASH MAPS
	// IF SOME CUSTOMIZED ARMOR>> THAN IT OVERIDES THIS FUNCTION
	public static void loadDefaultArmorProperties()
	{
		File customFile = new File(customPath + "Defaults.cfg");
		
		Configuration custom;
		try	
		{
			custom = new Configuration(customFile, true);
		} 
		catch(NullPointerException e)
		{
			e.printStackTrace();
			EnviroMine.logger.log(Level.WARN, "FAILED TO LOAD DEFAULTS!");
			return;
		} 
		catch(StringIndexOutOfBoundsException e)	
		{
			e.printStackTrace();
			EnviroMine.logger.log(Level.WARN, "FAILED TO LOAD DEFAULTS!");
			return;
		}
		EnviroMine.logger.log(Level.INFO, "Loading Default Config: " + customFile.getAbsolutePath());
		
		custom.load();
		
		// Load Default Categories
		custom.addCustomCategoryComment(armorCat, "Custom armor properties");
		custom.addCustomCategoryComment(blockCat, "Custom block properties");
		custom.addCustomCategoryComment(entityCat, "Custom entity properties");
		custom.addCustomCategoryComment(itemsCat, "Custom item properties");
		
		ArmorDefaultSave(custom, armorCat + ".helmetLeather", 	Item.itemRegistry.getNameForObject(Items.leather_helmet), 		1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
		ArmorDefaultSave(custom, armorCat + ".plateLeather", 	Item.itemRegistry.getNameForObject(Items.leather_chestplate), 	1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
		ArmorDefaultSave(custom, armorCat + ".legsLeather", 	Item.itemRegistry.getNameForObject(Items.leather_leggings), 	1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
		ArmorDefaultSave(custom, armorCat + ".bootsLeather", 	Item.itemRegistry.getNameForObject(Items.leather_boots), 		1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
		
		ArmorDefaultSave(custom, armorCat + ".helmetIron", 		Item.itemRegistry.getNameForObject(Items.iron_helmet), 		-0.5, 0.0, 2.5, 1.0, 1.0, 1.1, 0.0, 0.0);
		ArmorDefaultSave(custom, armorCat + ".plateIron", 		Item.itemRegistry.getNameForObject(Items.iron_chestplate), 	-0.5, 0.0, 2.5, 1.0, 1.0, 1.1, 0.0, 0.0);
		ArmorDefaultSave(custom, armorCat + ".legsIron", 		Item.itemRegistry.getNameForObject(Items.iron_leggings), 	-0.5, 0.0, 2.5, 1.0, 1.0, 1.1, 0.0, 0.0);
		ArmorDefaultSave(custom, armorCat + ".bootsIron", 		Item.itemRegistry.getNameForObject(Items.iron_boots), 		-0.5, 0.0, 2.5, 1.0, 1.0, 1.1, 0.0, 0.0);
		
		ArmorDefaultSave(custom, armorCat + ".helmetGold", 		Item.itemRegistry.getNameForObject(Items.golden_helmet), 		0.0, 0.0, 0.0, 1.0, 1.0, 1.2, 0.0, 0.0);
		ArmorDefaultSave(custom, armorCat + ".plateGold", 		Item.itemRegistry.getNameForObject(Items.golden_chestplate), 	0.0, 0.0, 0.0, 1.0, 1.0, 1.2, 0.0, 0.0);
		ArmorDefaultSave(custom, armorCat + ".legsGold", 		Item.itemRegistry.getNameForObject(Items.golden_leggings), 		0.0, 0.0, 0.0, 1.0, 1.0, 1.2, 0.0, 0.0);
		ArmorDefaultSave(custom, armorCat + ".bootsGold", 		Item.itemRegistry.getNameForObject(Items.golden_boots), 		0.0, 0.0, 0.0, 1.0, 1.0, 1.2, 0.0, 0.0);
		
		ArmorDefaultSave(custom, armorCat + ".helmetDiamond", 	Item.itemRegistry.getNameForObject(Items.diamond_helmet), 		0.0, 0.0, 0.0, 1.1, 1.0, 0.9, 0.0, 0.0);
		ArmorDefaultSave(custom, armorCat + ".plateDiamond", 	Item.itemRegistry.getNameForObject(Items.diamond_chestplate), 	0.0, 0.0, 0.0, 1.1, 1.0, 0.9, 0.0, 0.0);
		ArmorDefaultSave(custom, armorCat + ".legsDiamond", 	Item.itemRegistry.getNameForObject(Items.diamond_leggings), 	0.0, 0.0, 0.0, 1.1, 1.0, 0.9, 0.0, 0.0);
		ArmorDefaultSave(custom, armorCat + ".bootsDiamond", 	Item.itemRegistry.getNameForObject(Items.diamond_boots), 		0.0, 0.0, 0.0, 1.1, 1.0, 0.9, 0.0, 0.0);
		
		ItemDefaultSave(custom, itemsCat + ".potions", 		Item.itemRegistry.getNameForObject(Items.potionitem), 	-1, false, 0.0, 0.0, 0.0, -0.05, 0.0, 0.0, 25.0, 37.05);
		ItemDefaultSave(custom, itemsCat + ".melon", 		Item.itemRegistry.getNameForObject(Items.melon), 		-1, false, 0.0, 0.0, 0.0, -0.01, 0.0, 0.0, 5.0, 37.01);
		ItemDefaultSave(custom, itemsCat + ".carrot", 		Item.itemRegistry.getNameForObject(Items.carrot), 		-1, false, 0.0, 0.0, 0.0, -0.01, 0.0, 0.0, 5.0, 37.01);
		ItemDefaultSave(custom, itemsCat + ".goldCarrot", 	Item.itemRegistry.getNameForObject(Items.golden_apple), -1, false, 0.0, 0.0, 0.0, -0.01, 0.0, 0.0, 5.0, 37.01);
		ItemDefaultSave(custom, itemsCat + ".redApple", 	Item.itemRegistry.getNameForObject(Items.apple), 		-1, false, 0.0, 0.0, 0.0, -0.01, 0.0, 0.0, 5.0, 37.01);
		
		ItemDefaultSave(custom, itemsCat + ".bucketLava", 	Item.itemRegistry.getNameForObject(Items.lava_bucket), 		-1, true, 100.0, -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 37.0);
		ItemDefaultSave(custom, itemsCat + ".redFlower", 	Block.blockRegistry.getNameForObject(Blocks.red_flower), 	-1, false, 0.0, 0.01, 0.1, 0.0, 0.0, 0.0, 0.0, 37.0);
		ItemDefaultSave(custom, itemsCat + ".yellowFlower", Block.blockRegistry.getNameForObject(Blocks.yellow_flower), -1, false, 0.0, 0.01, 0.1, 0.0, 0.0, 0.0, 0.0, 37.0);
		ItemDefaultSave(custom, itemsCat + ".leaves", 		Block.blockRegistry.getNameForObject(Blocks.leaves), 		-1, false, 0.0, 0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 37.0);
		ItemDefaultSave(custom, itemsCat + ".snowBlock", 	Block.blockRegistry.getNameForObject(Blocks.snow), 			-1, true, -0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 37.0);
		ItemDefaultSave(custom, itemsCat + ".ice", 			Block.blockRegistry.getNameForObject(Blocks.ice), 			-1, true, -0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 37.0);
		ItemDefaultSave(custom, itemsCat + ".snowLayer",	Block.blockRegistry.getNameForObject(Blocks.snow_layer), 	-1, true, -0.05, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 37.0);
		ItemDefaultSave(custom, itemsCat + ".netherrack", 	Block.blockRegistry.getNameForObject(Blocks.netherrack), 	-1, true, 50.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 37.0);
		ItemDefaultSave(custom, itemsCat + ".soulSand", 	Block.blockRegistry.getNameForObject(Blocks.soul_sand), 	-1, false, 0.0, 0.0, -0.5, 0.0, 0.0, 0.0, 0.0, 37.0);
		ItemDefaultSave(custom, itemsCat + ".skull", 		Item.itemRegistry.getNameForObject(Items.skull), 			-1, false, 0.0, 0.0, -0.1, 0.0, 0.0, 0.0, 0.0, 37.0);
		ItemDefaultSave(custom, itemsCat + ".web", 			Block.blockRegistry.getNameForObject(Blocks.web), 			-1, false, 0.0, 0.0, -0.01, 0.0, 0.0, 0.0, 0.0, 37.0);
		ItemDefaultSave(custom, itemsCat + ".11", 			Item.itemRegistry.getNameForObject(Items.record_11), 		-1, false, 0.0, 0.0, -1, 0.0, 0.0, 0.0, 0.0, 37.0);
		
		EntityDefaultSave(custom, entityCat + ".blaze",		61, false, false, false, false, true, true, -0.01, 0.0, 75.0, 0.1, -0.05, 0.0, -0.01, -0.01);
		EntityDefaultSave(custom, entityCat + ".wither", 	64,	false, false, false, false, true, true, -0.1, -0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
		
		custom.save();	
	}
	
	private static void EntityDefaultSave(Configuration config, String catName, int id, boolean track, boolean dehydration, boolean bodyTemp, boolean airQ, boolean immuneToFrost, boolean immuneToHeat, double aSanity, double hSanity, double aTemp, double hTemp, double aAir, double hAir, double aHyd, double hHyd)
	{
		config.get(catName, EPName[0], id).getInt(id);
		config.get(catName, EPName[1], track).getBoolean(track);
		config.get(catName, EPName[2], dehydration).getBoolean(dehydration);
		config.get(catName, EPName[3], bodyTemp).getBoolean(bodyTemp);
		config.get(catName, EPName[4], airQ).getBoolean(airQ);
		config.get(catName, EPName[5], immuneToFrost).getBoolean(immuneToFrost);
		config.get(catName, EPName[6], immuneToHeat).getBoolean(immuneToHeat);
		config.get(catName, EPName[7], aSanity).getDouble(aSanity);
		config.get(catName, EPName[8], hSanity).getDouble(hSanity);
		config.get(catName, EPName[9], aTemp, "Overridden by body temp").getDouble(aTemp);
		config.get(catName, EPName[10], hTemp).getDouble(hTemp);
		config.get(catName, EPName[11], aAir).getDouble(aAir);
		config.get(catName, EPName[12], hAir).getDouble(hAir);
		config.get(catName, EPName[13], aHyd).getDouble(aHyd);
		config.get(catName, EPName[14], hHyd).getDouble(hHyd);
	}
	
	private static void ArmorDefaultSave(Configuration config, String catName, String name, double nightTemp, double shadeTemp, double sunTemp, double nightMult, double shadeMult, double sunMult, double sanity, double air)
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
	
	public static void SearchForModdedArmors()
	{
		EnviroMine.logger.log(Level.INFO, "Searcing for mod armors...");
		int armorCount = 0;
		
		Iterator<Item> iterator = Item.itemRegistry.iterator();
		
		while(iterator.hasNext())
		{
			Item item = iterator.next();
			if(item instanceof ItemArmor)
			{
				DetectedArmorGen((ItemArmor)item);
				armorCount += 1;
			}
		}
		EnviroMine.logger.log(Level.INFO, "Found " + armorCount + " mod armors");
	}
	
	private static void DetectedArmorGen(ItemArmor armor)
	{
		File armorFile = new File(customPath + armor.getClass().getSimpleName() + ".cfg");
		
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
		String catName = armorCat + "." + AddRemoveCustom.replaceULN(armor.getUnlocalizedName());
		
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
	
	private static void ItemDefaultSave(Configuration config, String catName, String name, int meta, boolean enableAmbTemp, double ambTemp, double ambAir, double ambSanity, double effTemp, double effAir, double effSanity, double effHydration, double tempCap)
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
	
	public static String SaveMyCustom(String type, String name, Object[] data)
	{
		
		// Check to make sure this is a Data File Before Editing
		File configFile = new File(customPath + "MyCustom.cfg");
		
		if(EM_Settings.genArmorConfigs && type.equalsIgnoreCase("ARMOR"))
		{
			configFile = new File(customPath + data.getClass().getSimpleName());
		}
		
		Configuration config;
		try
		{
			config = new Configuration(configFile, true);
		} catch(NullPointerException e)
		{
			e.printStackTrace();
			EnviroMine.logger.log(Level.WARN, "FAILED TO SAVE NEW OBJECT TO MYCUSTOM.CFG");
			return "Failed to Open MyCustom.cfg";
		} catch(StringIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			EnviroMine.logger.log(Level.WARN, "FAILED TO SAVE NEW OBJECT TO MYCUSTOM.CFG");
			return "Failed to Open MyCustom.cfg";
		}
		
		config.load();
		
		String returnValue = "";
		// Load Default Categories
		config.addCustomCategoryComment(armorCat, "Add Custom Armor");
		config.addCustomCategoryComment(blockCat, "Add Custom Blocks");
		config.addCustomCategoryComment(entityCat, "Custom Entities");
		
		if(type.equalsIgnoreCase("TILE"))
		{
			String nameULCat = blockCat + "." + name + " " + (Integer)data[1];
			
			if(config.hasCategory(nameULCat) == true)
			{
				config.removeCategory(config.getCategory(nameULCat));
				returnValue = "Removed";
			} else
			{
				config.addCustomCategoryComment(nameULCat, name);
				config.get(nameULCat, BPName[0], (String)data[0]).getString();
				config.get(nameULCat, BPName[1], (Integer)data[1]).getInt(0);
				config.get(nameULCat, BPName[2], (Integer)data[0]).getInt(0);
				config.get(nameULCat, BPName[3], (Integer)data[1]).getInt(0);
				config.get(nameULCat, BPName[4], 0).getInt(0);
				config.get(nameULCat, BPName[5], false).getBoolean(false);
				config.get(nameULCat, BPName[6], 0.00).getDouble(0.00);
				config.get(nameULCat, BPName[7], 0.00).getDouble(0.00);
				config.get(nameULCat, BPName[8], 0.00).getDouble(0.00);
				config.get(nameULCat, BPName[9], "loose").getString();
				config.get(nameULCat, BPName[10], false).getBoolean(false);
				config.get(nameULCat, BPName[11], false).getBoolean(false);
				returnValue = "Saved";
			}
		} else if(type.equalsIgnoreCase("ENTITY"))
		{
			
			String nameEntityCat = entityCat + "." + name;
			
			if(config.hasCategory(nameEntityCat) == true)
			{
				config.removeCategory(config.getCategory(nameEntityCat));
				returnValue = "Removed";
			} else
			{
				config.addCustomCategoryComment(nameEntityCat, "");
				config.get(nameEntityCat, EPName[0], (Integer)data[0]).getInt(0);
				config.get(nameEntityCat, EPName[1], true).getBoolean(true);
				config.get(nameEntityCat, EPName[2], true).getBoolean(true);
				config.get(nameEntityCat, EPName[3], true).getBoolean(true);
				config.get(nameEntityCat, EPName[4], true).getBoolean(true);
				config.get(nameEntityCat, EPName[5], false).getBoolean(false);
				config.get(nameEntityCat, EPName[6], false).getBoolean(false);
				config.get(nameEntityCat, EPName[7], 0.0D).getDouble(0.0D);
				config.get(nameEntityCat, EPName[8], 0.0D).getDouble(0.0D);
				config.get(nameEntityCat, EPName[9], 37.0D, "Overridden by body temp").getDouble(37.0D);
				config.get(nameEntityCat, EPName[10], 0.0D).getDouble(0.0D);
				config.get(nameEntityCat, EPName[11], 0.0D).getDouble(0.0D);
				config.get(nameEntityCat, EPName[12], 0.0D).getDouble(0.0D);
				config.get(nameEntityCat, EPName[13], 0.0D).getDouble(0.0D);
				config.get(nameEntityCat, EPName[14], 0.0D).getDouble(0.0D);
				returnValue = "Saved";
			}
			
		} else if(type.equalsIgnoreCase("ITEM"))
		{
			
			String nameItemCat = itemsCat + "." + name;
			
			if(config.hasCategory(nameItemCat) == true)
			{
				config.removeCategory(config.getCategory(nameItemCat));
				returnValue = "Removed";
			} else
			{
				config.addCustomCategoryComment(nameItemCat, name);
				config.get(nameItemCat, IPName[0], (String)data[0]).getString();
				config.get(nameItemCat, IPName[1], (Integer)data[1]).getInt(0);
				config.get(nameItemCat, IPName[2], false).getBoolean(false);
				config.get(nameItemCat, IPName[3], 0.00).getDouble(0.00);
				config.get(nameItemCat, IPName[4], 0.00).getDouble(0.00);
				config.get(nameItemCat, IPName[5], 0.00).getDouble(0.00);
				config.get(nameItemCat, IPName[6], 0.00).getDouble(0.00);
				config.get(nameItemCat, IPName[7], 0.00).getDouble(0.00);
				config.get(nameItemCat, IPName[8], 0.00).getDouble(0.00);
				config.get(nameItemCat, IPName[9], 0.00).getDouble(0.00);
				config.get(nameItemCat, IPName[10], 37.00).getDouble(37.00);
				returnValue = "Saved";
			}
			
		} else if(type.equalsIgnoreCase("ARMOR"))
		{
			String nameArmorCat = armorCat + "." + name;
			
			if(config.hasCategory(nameArmorCat) == true)
			{
				config.removeCategory(config.getCategory(nameArmorCat));
				returnValue = "Removed";
			} else
			{
				config.addCustomCategoryComment(nameArmorCat, name);
				config.get(nameArmorCat, APName[0], (String)data[0]).getString();
				config.get(nameArmorCat, APName[1], 0.00).getDouble(0.00);
				config.get(nameArmorCat, APName[2], 0.00).getDouble(0.00);
				config.get(nameArmorCat, APName[3], 0.00).getDouble(0.00);
				config.get(nameArmorCat, APName[4], 1.00).getDouble(1.00);
				config.get(nameArmorCat, APName[5], 1.00).getDouble(1.00);
				config.get(nameArmorCat, APName[6], 1.00).getDouble(1.00);
				config.get(nameArmorCat, APName[7], 0.00).getDouble(0.00);
				config.get(nameArmorCat, APName[8], 0.00).getDouble(0.00);
				returnValue = "Saved";
			}
		}
		
		config.save();
		
		return returnValue;
	}
	
	public static void loadStabilityTypes(File file)
	{
		Configuration config;
		try
		{
			config = new Configuration(file, true);
		} catch(NullPointerException e)
		{
			e.printStackTrace();
			EnviroMine.logger.log(Level.WARN, "FAILED TO LOAD MAIN CONFIG!\nBACKUP SETTINGS ARE NOW IN EFFECT!");
			return;
		} catch(StringIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			EnviroMine.logger.log(Level.WARN, "FAILED TO LOAD MAIN CONFIG!\nBACKUP SETTINGS ARE NOW IN EFFECT!");
			return;
		}
		
		config.load();
		
		loadDefaultStabilityTypes(config);
		
		// 	Grab all Categories in File
		List<String> catagory = new ArrayList<String>();
		Set<String> nameList = config.getCategoryNames();
		Iterator<String> nameListData = nameList.iterator();
		
		// add Categories to a List 
		while(nameListData.hasNext())
		{
			catagory.add(nameListData.next());
		}
		
		// Now Read/Save Each Category And Add into Proper Hash Maps
		
		for(int x = 0; x <= (catagory.size() - 1); x++)
		{
			String currentCat = catagory.get(x);
			
			boolean physEnable = 	config.get(currentCat, SPName[0], true).getBoolean(true);
			int supportDist = 		config.get(currentCat, SPName[1], 0).getInt(0);
			int minFall = 			config.get(currentCat, SPName[2], -1).getInt(-1);
			int maxFall = 			config.get(currentCat, SPName[3], -1).getInt(-1);
			boolean canHang = 		config.get(currentCat, SPName[4], false).getBoolean(false);
			boolean holdOther = 	config.get(currentCat, SPName[5], false).getBoolean(false);
			
			EM_Settings.stabilityTypes.put(currentCat, new StabilityType(physEnable, supportDist, minFall, maxFall, canHang, holdOther));
		}
		
		config.save();
	}
	
	public static void loadDefaultStabilityTypes(Configuration config)
	{
		boolean physEnable = 	config.get("sand-like", SPName[0], true).getBoolean(true);
		int supportDist = 		config.get("sand-like", SPName[1], 0).getInt(0);
		int minFall = 			config.get("sand-like", SPName[2], -1).getInt(-1);
		int maxFall = 			config.get("sand-like", SPName[3], -1).getInt(-1);
		boolean canHang = 		config.get("sand-like", SPName[4], false).getBoolean(false);
		boolean holdOther = 	config.get("sand-like", SPName[5], false).getBoolean(false);
		
		EM_Settings.stabilityTypes.put("sand-like", new StabilityType(physEnable, supportDist, minFall, maxFall, canHang, holdOther));
		
		physEnable = 	config.get("loose", SPName[0], true).getBoolean(true);
		supportDist = 	config.get("loose", SPName[1], 1).getInt(1);
		minFall = 		config.get("loose", SPName[2], 10).getInt(10);
		maxFall = 		config.get("loose", SPName[3], 15).getInt(15);
		canHang = 		config.get("loose", SPName[4], false).getBoolean(false);
		holdOther = 	config.get("loose", SPName[5], false).getBoolean(false);
		
		EM_Settings.stabilityTypes.put("loose", new StabilityType(physEnable, supportDist, minFall, maxFall, canHang, holdOther));
		
		physEnable = 	config.get("average", SPName[0], true).getBoolean(true);
		supportDist = 	config.get("average", SPName[1], 2).getInt(2);
		minFall = 		config.get("average", SPName[2], 15).getInt(15);
		maxFall = 		config.get("average", SPName[3], 22).getInt(22);
		canHang = 		config.get("average", SPName[4], false).getBoolean(false);
		holdOther = 	config.get("average", SPName[5], false).getBoolean(false);
		
		EM_Settings.stabilityTypes.put("average", new StabilityType(physEnable, supportDist, minFall, maxFall, canHang, holdOther));
		
		physEnable = 	config.get("strong", SPName[0], true).getBoolean(true);
		supportDist = 	config.get("strong", SPName[1], 3).getInt(3);
		minFall = 		config.get("strong", SPName[2], 22).getInt(22);
		maxFall = 		config.get("strong", SPName[3], 25).getInt(25);
		canHang = 		config.get("strong", SPName[4], true).getBoolean(true);
		holdOther = 	config.get("strong", SPName[5], false).getBoolean(false);
		
		EM_Settings.stabilityTypes.put("strong", new StabilityType(physEnable, supportDist, minFall, maxFall, canHang, holdOther));
	}
} // End of Page
