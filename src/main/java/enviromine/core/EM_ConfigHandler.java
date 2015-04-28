package enviromine.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.registry.EntityRegistry;
import enviromine.client.gui.menu.config.EM_ConfigMenu;
import enviromine.handlers.Legacy.LegacyHandler;
import enviromine.trackers.properties.ArmorProperties;
import enviromine.trackers.properties.BiomeProperties;
import enviromine.trackers.properties.BlockProperties;
import enviromine.trackers.properties.CaveBaseProperties;
import enviromine.trackers.properties.CaveGenProperties;
import enviromine.trackers.properties.CaveSpawnProperties;
import enviromine.trackers.properties.DimensionProperties;
import enviromine.trackers.properties.EntityProperties;
import enviromine.trackers.properties.ItemProperties;
import enviromine.trackers.properties.RotProperties;
import enviromine.trackers.properties.StabilityType;
import enviromine.trackers.properties.helpers.PropertyBase;
import enviromine.utils.EnviroUtils;
import enviromine.utils.ModIdentification;
import enviromine.world.EM_WorldData;

public class EM_ConfigHandler
{
	// Dirs for Custom Files
	public static String configPath = "config/enviromine/";
	public static String customPath = "CustomProperties/";
	
	public static String profilePath = configPath + "profiles/";
	public static String defaultProfile = profilePath +"default/";
	
	/**
	 * Configuration version number. If changed the version file will be reset to defaults to prevent glitches
	 */
	public static final String CONFIG_VERSION = "1.0.0";
	
	/**
	 * The version of the configs last loaded from file. This will be compared to the version number above when determining whether a reset is necessary
	 */
	
	
	public static String loadedProfile = defaultProfile;
	
	static HashMap<String, PropertyBase> propTypes;
	public static HashMap<String, PropertyBase> globalTypes;
	
	public static List loadedConfigs = new ArrayList();
	
	
	
	/**
	 * Register all property types and their category names here. The rest is handled automatically.
	 */
	static
	{
		propTypes = new HashMap<String, PropertyBase>();
		
		propTypes.put(BiomeProperties.base.categoryName(), BiomeProperties.base);
		propTypes.put(ArmorProperties.base.categoryName(), ArmorProperties.base);
		propTypes.put(BlockProperties.base.categoryName(), BlockProperties.base);
		propTypes.put(DimensionProperties.base.categoryName(), DimensionProperties.base);
		propTypes.put(EntityProperties.base.categoryName(), EntityProperties.base);
		propTypes.put(ItemProperties.base.categoryName(), ItemProperties.base);
		propTypes.put(RotProperties.base.categoryName(), RotProperties.base);
		
		globalTypes = new HashMap<String, PropertyBase>();
		globalTypes.put(CaveGenProperties.base.categoryName(), CaveGenProperties.base);
		globalTypes.put(CaveSpawnProperties.base.categoryName(), CaveSpawnProperties.base);
		globalTypes.put(CaveBaseProperties.base.categoryName(), CaveBaseProperties.base);
		
	}

	
	public static void initProfile()
	{	
		EM_WorldData theWorldEM = EnviroMine.theWorldEM;
		
		String profile = theWorldEM.getProfile();
		
		File profileDir = new File(profilePath + profile +"/"+ customPath);

		CheckDir(profileDir);
		
		if(!profileDir.exists())
		{
			EnviroMine.logger.log(Level.ERROR, "Failed to load Profile:"+ profile +". Loading Default");	
			profileDir = new File(defaultProfile + customPath);
			loadedProfile = defaultProfile;
		}else 
		{
			loadedProfile = profilePath + profile +"/";
			EnviroMine.logger.log(Level.INFO, "Loading Profile: "+ profile);
		}
		
		File ProfileSettings = new File(loadedProfile + profile +"_Settings.cfg");
		loadProfileConfig(ProfileSettings);
		// load defaults
		
		//These must be run before the block configs generate/load
		StabilityType.base.GenDefaults();
		StabilityType.base.customLoad();
		
		if(EM_Settings.genDefaults)
		{
			loadDefaultProperties();
		}
		
		
		// Now load Files from "Custom Objects"
		File[] customFiles = GetFileList(loadedProfile + customPath);
		for(int i = 0; i < customFiles.length; i++)
		{
			LoadCustomObjects(customFiles[i]);
		}
				
				
			
				
		Iterator<PropertyBase> iterator = propTypes.values().iterator();
				
		// Load non standard property files
		while(iterator.hasNext())
		{
			PropertyBase props = iterator.next();
				
			if(!props.useCustomConfigs())
			{
				props.customLoad();
			}
		}
		

		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.stabilityTypes.size() + " stability types");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.armorProperties.size() + " armor properties");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.blockProperties.size() + " block properties");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.livingProperties.size() + " entity properties");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.itemProperties.size() + " item properties");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.rotProperties.size() + " rot properties");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.biomeProperties.size() + " biome properties");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.dimensionProperties.size() + " dimension properties");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.caveGenProperties.size() + " cave ore properties");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.caveSpawnProperties.size() + " cave entity properties");

				
	}	
	
	
	public static int initConfig()
	{
		// Check for Data Directory 
		//CheckDir(new File(customPath));
		
		EnviroMine.logger.log(Level.INFO, "Loading configs...");
	
		// Load Global Configs
		File configFile = new File(configPath + "Global_Settings.cfg");
		loadGlobalConfig(configFile);
		
		Iterator<PropertyBase> iterator = globalTypes.values().iterator();
		
		// Load non standard property files
		while(iterator.hasNext())
		{
			PropertyBase props = iterator.next();
				
			if(!props.useCustomConfigs())
			{
				props.customLoad();
			}
		}
		
		int Total = EM_Settings.armorProperties.size() + EM_Settings.blockProperties.size() + EM_Settings.livingProperties.size() + EM_Settings.itemProperties.size() + EM_Settings.biomeProperties.size() + EM_Settings.dimensionProperties.size() + EM_Settings.caveGenProperties.size() + EM_Settings.caveSpawnProperties.size();
		
		return Total;
	}
	
	
	private static void loadGlobalConfig(File file)
	{
		Configuration config;
		try
		{
			config = new Configuration(file, true);
		} catch(Exception e)
		{
			EnviroMine.logger.log(Level.WARN, "Failed to load main configuration file!", e);
			return;
		}
		
		config.load();

		//World Generation
		EM_Settings.shaftGen = config.get("World Generation", "Enable Village MineShafts", EM_Settings.shaftGen, "Generates mineshafts in villages").getBoolean(EM_Settings.shaftGen);
		EM_Settings.oldMineGen = config.get("World Generation", "Enable New Abandoned Mineshafts", EM_Settings.oldMineGen, "Generates massive abandoned mineshafts (size doesn't cause lag)").getBoolean(EM_Settings.oldMineGen);
		EM_Settings.gasGen = config.get("World Generation", "Generate Gases", EM_Settings.gasGen).getBoolean(EM_Settings.gasGen);
		//EM_Settings.disableCaves = config.get("World Generation", "Disable Cave Dimension", false).getBoolean(false); // Moved to CaveBaseProperties
		//EM_Settings.limitElevatorY = config.get("World Generation", "Limit Elevator Height", true).getBoolean(true); // Moved to CaveBaseProperties
	
		config.get("Do not Edit", "Current Config Version", CONFIG_VERSION).getString();

		EM_Settings.updateCheck = config.get(Configuration.CATEGORY_GENERAL, "Check For Updates", EM_Settings.updateCheck).getBoolean(EM_Settings.updateCheck);
		EM_Settings.noNausea = config.get(Configuration.CATEGORY_GENERAL, "Blindness instead of Nausea", EM_Settings.noNausea).getBoolean(EM_Settings.noNausea);
		EM_Settings.keepStatus = config.get(Configuration.CATEGORY_GENERAL, "Keep statuses on death", EM_Settings.keepStatus).getBoolean(EM_Settings.keepStatus);
		EM_Settings.renderGear = config.get(Configuration.CATEGORY_GENERAL, "Render Gear", EM_Settings.renderGear ,"Render 3d gear worn on player. Must reload game to take effect").getBoolean(EM_Settings.renderGear);
		EM_Settings.finiteWater = config.get(Configuration.CATEGORY_GENERAL, "Finite Water", EM_Settings.finiteWater).getBoolean(EM_Settings.finiteWater);
		
		// Physics Settings
		String PhySetCat = "Physics";

		int minPhysInterval = 6;
		EM_Settings.spreadIce = config.get(PhySetCat, "Large Ice Cracking", EM_Settings.spreadIce , "Setting Large Ice Cracking to true can cause Massive Lag").getBoolean(EM_Settings.spreadIce );
		EM_Settings.updateCap = config.get(PhySetCat, "Consecutive Physics Update Cap", EM_Settings.updateCap, "This will change maximum number of blocks that can be updated with physics at a time. - 1 = Unlimited").getInt(EM_Settings.updateCap);
		EM_Settings.physInterval = getConfigIntWithMinInt(config.get(PhySetCat, "Physics Interval", minPhysInterval, "The number of ticks between physics update passes (must be " + minPhysInterval + " or more)"), minPhysInterval);
		EM_Settings.worldDelay = config.get(PhySetCat, "World Start Delay", EM_Settings.worldDelay, "How long after world start until the physics system kicks in (DO NOT SET TOO LOW)").getInt(EM_Settings.worldDelay);
		EM_Settings.chunkDelay = config.get(PhySetCat, "Chunk Physics Delay", EM_Settings.chunkDelay, "How long until individual chunk's physics starts after loading (DO NOT SET TOO LOW)").getInt(EM_Settings.chunkDelay);
		EM_Settings.physInterval = EM_Settings.physInterval >= 2 ? EM_Settings.physInterval : 2;
		EM_Settings.entityFailsafe = config.get(PhySetCat, "Physics entity fail safe level", EM_Settings.entityFailsafe, "0 = No action, 1 = Limit to < 100 per 8x8 block area, 2 = Delete excessive entities & Dump physics (EMERGENCY ONLY)").getInt(EM_Settings.entityFailsafe);

		//TODO Legacy Check
		if(!LegacyHandler.getByKey("ConfigHandlerLegacy").didRun())
		{
			//Potion ID's
			EM_Settings.hypothermiaPotionID = nextAvailPotion(EM_Settings.hypothermiaPotionID);
			EM_Settings.heatstrokePotionID = nextAvailPotion(EM_Settings.heatstrokePotionID);
			EM_Settings.frostBitePotionID = nextAvailPotion(EM_Settings.frostBitePotionID);
			EM_Settings.dehydratePotionID = nextAvailPotion(EM_Settings.dehydratePotionID);
			EM_Settings.insanityPotionID = nextAvailPotion(EM_Settings.insanityPotionID);
		}
			EM_Settings.hypothermiaPotionID = config.get("Potions", "Hypothermia", EM_Settings.hypothermiaPotionID).getInt(EM_Settings.hypothermiaPotionID);
			EM_Settings.heatstrokePotionID = config.get("Potions", "Heat Stroke", EM_Settings.heatstrokePotionID).getInt(EM_Settings.heatstrokePotionID);
			EM_Settings.frostBitePotionID = config.get("Potions", "Frostbite", EM_Settings.frostBitePotionID).getInt(EM_Settings.frostBitePotionID);
			EM_Settings.dehydratePotionID = config.get("Potions", "Dehydration", EM_Settings.dehydratePotionID).getInt(EM_Settings.dehydratePotionID);
			EM_Settings.insanityPotionID = config.get("Potions", "Insanity", EM_Settings.insanityPotionID).getInt(EM_Settings.insanityPotionID);

		// Config Options

		String ConSetCat = "Config";
		EM_Settings.enableConfigOverride = config.get(ConSetCat, "Client Config Override (SMP)", EM_Settings.enableConfigOverride, "[DISABLED][WIP] Temporarily overrides client configurations with the server's (NETWORK INTESIVE!)").getBoolean(EM_Settings.enableConfigOverride);
		
		// Config Gas
		EM_Settings.noGases = config.get("Gases", "Disable Gases", EM_Settings.noGases, "Disables all gases and slowly deletes existing pockets").getBoolean(EM_Settings.noGases);
		EM_Settings.slowGases = config.get("Gases", "Slow Gases", EM_Settings.slowGases, "Normal gases will move extremely slowly and reduce TPS lag").getBoolean(EM_Settings.slowGases);
		EM_Settings.renderGases = config.get("Gases", "Render normal gas", EM_Settings.renderGases, "Whether to render gases not normally visible").getBoolean(EM_Settings.renderGases);
		EM_Settings.gasTickRate = config.get("Gases", "Gas Tick Rate", EM_Settings.gasTickRate, "How many ticks between gas updates. Gas fires are 1/4 of this.").getInt(EM_Settings.gasTickRate);
		EM_Settings.gasPassLimit = config.get("Gases", "Gas Pass Limit", EM_Settings.gasPassLimit, "How many gases can be processed in a single pass per chunk (-1 = infinite)").getInt(EM_Settings.gasPassLimit);
		EM_Settings.gasWaterLike = config.get("Gases", "Water like spreading", EM_Settings.gasWaterLike, "Whether gases should spread like water (faster) or even out as much as possible (realistic)").getBoolean(EM_Settings.gasWaterLike);
		
	
		config.save();
		
	
	}
	
	private static void loadProfileConfig(File file)
	{
		Configuration config;
		try
		{
			config = new Configuration(file, true);
		} catch(Exception e)
		{
			EnviroMine.logger.log(Level.WARN, "Failed to load main configuration file!", e);
			return;
		}
		
		config.load();
		
		//General Settings
		EM_Settings.enablePhysics = config.get(Configuration.CATEGORY_GENERAL, "Enable Physics", EM_Settings.enablePhysics, "Turn physics On/Off").getBoolean(EM_Settings.enablePhysics);
		EM_Settings.enableLandslide = config.get(Configuration.CATEGORY_GENERAL, "Enable Physics Landslide", EM_Settings.enableLandslide).getBoolean(EM_Settings.enableLandslide);
		EM_Settings.enableSanity = config.get(Configuration.CATEGORY_GENERAL, "Allow Sanity", EM_Settings.enableSanity).getBoolean(EM_Settings.enableSanity);
		EM_Settings.enableHydrate = config.get(Configuration.CATEGORY_GENERAL, "Allow Hydration", EM_Settings.enableHydrate).getBoolean(EM_Settings.enableHydrate);
		EM_Settings.enableBodyTemp = config.get(Configuration.CATEGORY_GENERAL, "Allow Body Temperature", EM_Settings.enableBodyTemp).getBoolean(EM_Settings.enableBodyTemp);
		EM_Settings.enableAirQ = config.get(Configuration.CATEGORY_GENERAL, "Allow Air Quality", EM_Settings.enableAirQ, "True/False to turn Enviromine Trackers for Sanity, Air Quality, Hydration, and Body Temperature.").getBoolean(EM_Settings.enableAirQ);
		EM_Settings.trackNonPlayer = config.get(Configuration.CATEGORY_GENERAL, "Track NonPlayer entities", EM_Settings.trackNonPlayer, "Track enviromine properties on Non-player entities(mobs & animals)").getBoolean(EM_Settings.trackNonPlayer);
		EM_Settings.villageAssist = config.get(Configuration.CATEGORY_GENERAL, "Enable villager assistance", EM_Settings.villageAssist).getBoolean(EM_Settings.villageAssist);
		EM_Settings.foodSpoiling = config.get(Configuration.CATEGORY_GENERAL, "Enable food spoiling", EM_Settings.foodSpoiling).getBoolean(EM_Settings.foodSpoiling);
		EM_Settings.foodRotTime = config.get(Configuration.CATEGORY_GENERAL, "Default spoil time (days)", EM_Settings.foodRotTime).getInt(EM_Settings.foodRotTime);
		EM_Settings.torchesBurn = config.get(Configuration.CATEGORY_GENERAL, "Torches burn", EM_Settings.torchesBurn).getBoolean(EM_Settings.torchesBurn);
		EM_Settings.torchesGoOut = config.get(Configuration.CATEGORY_GENERAL, "Torches go out", EM_Settings.torchesGoOut).getBoolean(EM_Settings.torchesGoOut);
			
		// Physics Settings
		String PhySetCat = "Physics";
		EM_Settings.stoneCracks = config.get(PhySetCat, "Stone Cracks Before Falling", EM_Settings.stoneCracks).getBoolean(EM_Settings.stoneCracks);
		EM_Settings.defaultStability = config.get(PhySetCat, "Default Stability Type (BlockIDs > 175)", EM_Settings.defaultStability).getString();
		
	
		// Multipliers ID's
		EM_Settings.tempMult = config.get("Speed Multipliers", "BodyTemp", EM_Settings.tempMult).getDouble(EM_Settings.tempMult);
		EM_Settings.hydrationMult = config.get("Speed Multipliers", "Hydration", EM_Settings.hydrationMult).getDouble(EM_Settings.hydrationMult);
		EM_Settings.airMult = config.get("Speed Multipliers", "AirQuality", EM_Settings.airMult).getDouble(EM_Settings.airMult);
		EM_Settings.sanityMult = config.get("Speed Multipliers", "Sanity", EM_Settings.sanityMult).getDouble(EM_Settings.sanityMult);
		
		EM_Settings.tempMult = EM_Settings.tempMult < 0 ? 0F : EM_Settings.tempMult;
		EM_Settings.hydrationMult = EM_Settings.hydrationMult < 0 ? 0F : EM_Settings.hydrationMult;
		EM_Settings.airMult = EM_Settings.airMult < 0 ? 0F : EM_Settings.airMult;
		EM_Settings.sanityMult = EM_Settings.sanityMult < 0 ? 0F : EM_Settings.sanityMult;
		
		// Config Options
		String ConSetCat = "Config";
		Property genConfig = config.get(ConSetCat, "Generate Blank Configs", false, "Will attempt to find and generate blank configs for any custom items/blocks/etc loaded before EnviroMine. Pack developers are highly encouraged to enable this! (Resets back to false after use)");
		if(!EM_Settings.genConfigs)
		{
			EM_Settings.genConfigs = genConfig.getBoolean(false);
		}
		genConfig.set(false);
		
		Property genDefault = config.get(ConSetCat, "Generate Defaults", true, "Generates EnviroMines initial default files");
		if(!EM_Settings.genDefaults)
		{
			EM_Settings.genDefaults = genDefault.getBoolean(true);
		}
		genDefault.set(false);
		
		EM_Settings.enableConfigOverride = config.get(ConSetCat, "Client Config Override (SMP)", EM_Settings.enableConfigOverride, "[DISABLED][WIP] Temporarily overrides client configurations with the server's (NETWORK INTESIVE!)").getBoolean(EM_Settings.enableConfigOverride);
		
		// Earthquake
		String EarSetCat = "Earthquakes";
		EM_Settings.enableQuakes = config.get(EarSetCat, "Enable Earthquakes", EM_Settings.enableQuakes).getBoolean(EM_Settings.enableQuakes);
		EM_Settings.quakePhysics = config.get(EarSetCat, "Triggers Physics", EM_Settings.quakePhysics, "Can cause major lag at times (Requires main physics to be enabled)").getBoolean(EM_Settings.quakePhysics);
		EM_Settings.quakeRarity = config.get(EarSetCat, "Rarity", EM_Settings.quakeRarity).getInt(EM_Settings.quakeRarity);
		EM_Settings.quakeMode = config.get(EarSetCat, "Mode", EM_Settings.quakeMode, "Changes how quakes are created (-1 = random, 0 = wave normal, 1 = centre normal, 2 = centre tear, 3 = wave tear)").getInt(EM_Settings.quakeMode);
		EM_Settings.quakeDelay = config.get(EarSetCat, "Tick delay", EM_Settings.quakeDelay).getInt(EM_Settings.quakeDelay);
		EM_Settings.quakeSpeed = config.get(EarSetCat, "Speed", EM_Settings.quakeSpeed, "How many layers of rock it can eat through at a time").getInt(EM_Settings.quakeSpeed);
		if(EM_Settings.quakeRarity < 0)
		{
			EM_Settings.quakeRarity = 0;
		}
		if(EM_Settings.quakeSpeed <= 0)
		{
			EM_Settings.quakeSpeed = 1;
		}
		
		// Easter Eggs!
		String eggCat = "Easter Eggs";
		EM_Settings.thingChance = config.getFloat("Cave Dimension Grue", eggCat, 0.000001F, 0F, 1F, "Chance the (extremely rare) grue in the cave dimension will attack in the dark (ignored on Halloween or Friday 13th)");
			
		config.save();

	}
	
	/**
	 * @deprecated Use config.getInt(...) instead as it provides min & max value caps
	 */
	@Deprecated
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
	private static File[] GetFileList(String path)
	{
		
		// Will be used Auto Load Custom Objects from ??? Dir 
		File f = new File(path);
		File[] list = f.listFiles();
		
		return list;
	}
	
	private static boolean isCFGFile(File file)
	{
		String fileName = file.getName();
		
		if(file.isHidden()) return false;
		
		//Matcher
		String patternString = "(.*\\.cfg$)";
		
		Pattern pattern;
		Matcher matcher;
		// Make Sure its a .cfg File
		pattern = Pattern.compile(patternString);
		matcher = pattern.matcher(fileName);
		
		String MacCheck = ".DS_Store.cfg";
		
		if (matcher.matches() && matcher.group(0).toString().toLowerCase() == MacCheck.toLowerCase()) { return false;}
		
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
			EnviroMine.logger.log(Level.INFO, "Dir already exist:"+ Dir.getName());
			return;
		}
		
		try
		{
			Dir.setWritable(true);
			dirFlag = Dir.mkdirs();
			EnviroMine.logger.log(Level.INFO, "Created new Folder "+ Dir.getName());
		} catch(Exception e)
		{
			EnviroMine.logger.log(Level.ERROR, "Error occured while creating config directory: " + Dir.getAbsolutePath(), e);
		}
		
		if(!dirFlag)
		{
			EnviroMine.logger.log(Level.ERROR, "Failed to create config directory: " + Dir.getAbsolutePath());
		}
	}
	
	/**
	 * Load Custom Objects          
	 * Used to Load Custom Blocks,Armor
	 * Entitys, & Items from Custom Config Dir        
	 */
	private static void LoadCustomObjects(File customFiles)
	{
		boolean datFile = isCFGFile(customFiles);
		
		// Check to make sure this is a Data File Before Editing
		if(datFile == true)
		{
			Configuration config;
			try
			{
				config = new Configuration(customFiles, true);
				
				//EnviroMine.logger.log(Level.INFO, "Loading Config File: " + customFiles.getAbsolutePath());
	
				config.load();


			// 	Grab all Categories in File
			List<String> catagory = new ArrayList<String>();
			Set<String> nameList = config.getCategoryNames();
			Iterator<String> nameListData = nameList.iterator();
			
			// add Categories to a List 
			while(nameListData.hasNext())
			{
				catagory.add(nameListData.next());
			}
			
			for(int x = 0; x < catagory.size(); x++)
			{
				String CurCat = catagory.get(x);
				
				if(!CurCat.isEmpty() && CurCat.contains(Configuration.CATEGORY_SPLITTER))
				{
					String parent = CurCat.split("\\" + Configuration.CATEGORY_SPLITTER)[0];
					
					if(propTypes.containsKey(parent) && propTypes.get(parent).useCustomConfigs())
					{
						PropertyBase property = propTypes.get(parent);
						property.LoadProperty(config, catagory.get(x));
					} else
					{
						EnviroMine.logger.log(Level.WARN, "Failed to load object " + CurCat);
					}
					
				}
			}
			
			config.save();
			
			// Add to list of loaded Config files
			loadedConfigs.add(config.getConfigFile().getName());
			
			} catch(Exception e)
			{
				e.printStackTrace();
				EnviroMine.logger.log(Level.ERROR, "FAILED TO LOAD CUSTOM CONFIG: " + customFiles.getName() + "\nNEW SETTINGS WILL BE IGNORED!", e);
				return;
			}
		}
			
			
	}
	
	public static ArrayList<String> getSubCategories(Configuration config, String mainCat)
	{
		ArrayList<String> category = new ArrayList<String>();
		Set<String> nameList = config.getCategoryNames();
		Iterator<String> nameListData = nameList.iterator();
		
		// add Categories to a List 
		while(nameListData.hasNext())
		{
			String catName = nameListData.next();
			
			if(catName.startsWith(mainCat + "."))
			{
				category.add(catName);
			}
		}
		
		return category;
	}
	
	public static String getProfileName()
	{
		return getProfileName(loadedProfile);
	}
	
	public static String getProfileName(String profile)
	{
		return profile.substring(profilePath.length(),profile.length()-1).toUpperCase();		
	}
	
	public static boolean ReloadConfig()
	{
				 	try
				 	{

				 		EM_Settings.armorProperties.clear();
				 		EM_Settings.blockProperties.clear();
				 		EM_Settings.itemProperties.clear();
				 		EM_Settings.livingProperties.clear();
				 		EM_Settings.stabilityTypes.clear();
				 		EM_Settings.biomeProperties.clear();
				 		EM_Settings.dimensionProperties.clear();
				 		EM_Settings.rotProperties.clear();
				 		EM_Settings.caveGenProperties.clear();
				 		EM_Settings.caveSpawnProperties.clear();
			
				 		int Total = initConfig();
			
				 		initProfile();
			
				 		EnviroMine.caves.RefreshSpawnList();
				 		return true;
				 		
				 	} //try
					catch(NullPointerException e)
					{
						return false;
					}
	            

	}
	
	public static void loadDefaultProperties()
	{
		Iterator<PropertyBase> iterator = propTypes.values().iterator();
		
		while(iterator.hasNext())
		{
			iterator.next().GenDefaults();
		}
	}

	public static Configuration getConfigFromObj(Object obj)
	{
		String ModID = ModIdentification.idFromObject(obj);
	
		File configFile = new File(loadedProfile+ customPath + ModID +".cfg");
		
		Configuration config;
		try
		{
			config = new Configuration(configFile, true);
		} catch(NullPointerException e)
		{
			e.printStackTrace();
			EnviroMine.logger.log(Level.WARN, "FAILED TO LOAD Config from OBJECT TO "+ModID+".CFG");
			return null;
		} catch(StringIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			EnviroMine.logger.log(Level.WARN, "FAILED TO LOAD Config from OBJECT TO "+ModID+".CFG");
			return null;
		}


		return config;
	}
	
	public static String SaveMyCustom(Object obj)
	{
		return SaveMyCustom(obj, null);
	}
	
	public static String SaveMyCustom(Object obj, Object type)
	{
		
		String ModID = ModIdentification.idFromObject(obj);

		
		// Check to make sure this is a Data File Before Editing
		File configFile = new File(loadedProfile+ customPath + ModID +".cfg");
		
		Configuration config;
		try
		{
			config = new Configuration(configFile, true);
		} catch(NullPointerException e)
		{
			e.printStackTrace();
			EnviroMine.logger.log(Level.WARN, "FAILED TO SAVE NEW OBJECT TO "+ModID+".CFG");
			return "Failed to Open "+ModID+".cfg";
		} catch(StringIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			EnviroMine.logger.log(Level.WARN, "FAILED TO SAVE NEW OBJECT TO "+ModID+".CFG");
			return "Failed to Open "+ModID+".cfg";
		}

		config.load();

		String returnValue = "";
		
		if(obj instanceof Block)
		{
			
			BlockProperties.base.generateEmpty(config, obj);
			returnValue = "(Block) Saved to "+ ModID + ".cfg on Profile "+ getProfileName();

		} else if(obj instanceof Entity)
		{		
			
			Entity en = (Entity) obj;
			int id = 0;
			if(EntityList.getEntityID(en) > 0)
			{
				id = EntityList.getEntityID(en);
			} else if(EntityRegistry.instance().lookupModSpawn(en.getClass(), false) != null)
			{
				id = EntityRegistry.instance().lookupModSpawn(en.getClass(), false).getModEntityId() + 128;
			} else
			{
				returnValue = "Failed to add config entry. " + en.getCommandSenderName() + " has no ID!";
				EnviroMine.logger.log(Level.WARN, "Failed to add config entry. " + en.getCommandSenderName() + " has no ID!");
			}
			EntityProperties.base.generateEmpty(config, id);
			returnValue = "(Entity) Saved to "+ ModID + ".cfg on Profile "+ getProfileName();
		} else if(obj instanceof Item && type == null )
		{
				ItemProperties.base.generateEmpty(config, obj);
				returnValue = "(Item) Saved to "+ ModID + ".cfg on Profile "+ getProfileName();
		} else if(obj instanceof ItemArmor && type instanceof ArmorProperties)
		{
				ArmorProperties.base.generateEmpty(config, obj);
				returnValue = "(ItemArmor) Saved to "+ ModID + ".cfg on Profile "+ getProfileName();
		}
		
		config.save();
		
		
		return returnValue;
		
		//return null;
	}
	
	private void removeProperty(Configuration config, String oldCat, String propName)
	{
		String remove = "Remove";
		config.moveProperty(oldCat, propName, remove);
		config.removeCategory(config.getCategory(remove));
	}

} // End of Page

