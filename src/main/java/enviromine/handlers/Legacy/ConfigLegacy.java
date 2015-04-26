package enviromine.handlers.Legacy;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import net.minecraft.potion.Potion;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.apache.logging.log4j.Level;

import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;

public class ConfigLegacy extends LegacyHandler
{
	// Dirs for Custom Files
	private static String configPath = "config/enviromine/";
	private static String customPath = configPath + "CustomProperties/";
	private static File configFile = new File(configPath + "EnviroMine.cfg");
	private static Configuration config;
	private static boolean didRun = false;
	
	@Override
	public boolean initCheck() 
	{

		if(configFile.exists() && !configFile.isDirectory())
		{
			try
			{
				config = new Configuration(configFile, true);
				config.load();
			} catch(Exception e)
			{
				EnviroMine.logger.log(Level.WARN, "Failed to load Legacy configuration file!", e);
				return false;
			}
			
		    EnviroMine.logger.log(Level.INFO, "Legacy: Config File Loaded");
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void runLegacy() 
	{
		// Version 0
		loadGeneralConfig(configFile);
		MoveCustomProperties();
		this.didRun = true;
	}
	
	
	@Override
	public boolean didRun() 
	{
		// TODO Auto-generated method stub
		return this.didRun;
	}
	
	private static void MoveCustomProperties()
	{
		File customsDir = new File(customPath);
		
		if(customsDir.exists() && customsDir.listFiles().length > 0)
		{
			
			try
			{
				File sourceDir = new File(customPath);
				
				CopyOption[] options = new CopyOption[]{};
				
				File newPath = new File(EM_ConfigHandler.defaultProfile + EM_ConfigHandler.customPath);
				
				
				if(!newPath.isDirectory())Files.createDirectories(newPath.toPath());
				
				for(File file : sourceDir.listFiles())
				{
					Files.move(sourceDir.toPath().resolve(file.getName()) , newPath.toPath().resolve(file.getName()), options);	
				}
				
				if(sourceDir.isDirectory())Files.delete(sourceDir.toPath());
				
				File Stability = new File(configPath + "StabilityTypes.cfg");
				
				if(Stability.exists()) Files.move(Stability.toPath(), Paths.get(EM_ConfigHandler.defaultProfile).resolve("StabilityTypes.cfg"), options);
				
				//File CaveDimension = new File(configPath + "CaveDimension.cfg");
				
				//if(CaveDimension.exists()) Files.move(CaveDimension.toPath(), Paths.get(EM_ConfigHandler.defaultProfile).resolve("CaveDimension.cfg"), options);				
				
				//Files.move(source, target, options)
				
			}
			catch(IOException e)
			{
				EnviroMine.logger.log(Level.ERROR, "Legacy Failed to Copy Custom Configs to New Dir! " + e);
			}
			
		}
		
	}
	
	
	/**
	 * Register all property types and their category names here. The rest is handled automatically.
	 */
	
	private static void loadGeneralConfig(File file)
	{	
		//World Generation
		EM_Settings.shaftGen = config.get("World Generation", "Enable Village MineShafts", true, "Generates mineshafts in villages").getBoolean(true);
		EM_Settings.oldMineGen = config.get("World Generation", "Enable New Abandoned Mineshafts", true, "Generates massive abandoned mineshafts (size doesn't cause lag)").getBoolean(true);
		EM_Settings.gasGen = config.get("World Generation", "Generate Gases", true).getBoolean(true);
		//EM_Settings.disableCaves = config.get("World Generation", "Disable Cave Dimension", false).getBoolean(false); // Moved to CaveBaseProperties
		//EM_Settings.limitElevatorY = config.get("World Generation", "Limit Elevator Height", true).getBoolean(true); // Moved to CaveBaseProperties
		
		//General Settings
		EM_Settings.enablePhysics = config.get(Configuration.CATEGORY_GENERAL, "Enable Physics", true, "Turn physics On/Off").getBoolean(true);
		EM_Settings.enableLandslide = config.get(Configuration.CATEGORY_GENERAL, "Enable Physics Landslide", true).getBoolean(true);
		EM_Settings.enableSanity = config.get(Configuration.CATEGORY_GENERAL, "Allow Sanity", true).getBoolean(true);
		EM_Settings.enableHydrate = config.get(Configuration.CATEGORY_GENERAL, "Allow Hydration", true).getBoolean(true);
		EM_Settings.enableBodyTemp = config.get(Configuration.CATEGORY_GENERAL, "Allow Body Temperature", true).getBoolean(true);
		EM_Settings.enableAirQ = config.get(Configuration.CATEGORY_GENERAL, "Allow Air Quality", true, "True/False to turn Enviromine Trackers for Sanity, Air Quality, Hydration, and Body Temperature.").getBoolean(true);
		EM_Settings.trackNonPlayer = config.get(Configuration.CATEGORY_GENERAL, "Track NonPlayer entities", false, "Track enviromine properties on Non-player entities(mobs & animals)").getBoolean(false);
		EM_Settings.updateCheck = config.get(Configuration.CATEGORY_GENERAL, "Check For Updates", true).getBoolean(true);
		EM_Settings.villageAssist = config.get(Configuration.CATEGORY_GENERAL, "Enable villager assistance", true).getBoolean(true);
		EM_Settings.foodSpoiling = config.get(Configuration.CATEGORY_GENERAL, "Enable food spoiling", true).getBoolean(true);
		EM_Settings.foodRotTime = config.get(Configuration.CATEGORY_GENERAL, "Default spoil time (days)", 7).getInt(7);
		EM_Settings.torchesBurn = config.get(Configuration.CATEGORY_GENERAL, "Torches burn", true).getBoolean(true);
		EM_Settings.torchesGoOut = config.get(Configuration.CATEGORY_GENERAL, "Torches go out", true).getBoolean(true);
		EM_Settings.finiteWater = config.get(Configuration.CATEGORY_GENERAL, "Finite Water", false).getBoolean(false);
		EM_Settings.noNausea = config.get(Configuration.CATEGORY_GENERAL, "Blindness instead of Nausea", false).getBoolean(false);
		EM_Settings.keepStatus = config.get(Configuration.CATEGORY_GENERAL, "Keep statuses on death", false).getBoolean(false);
		EM_Settings.renderGear = config.get(Configuration.CATEGORY_GENERAL, "Render Gear", true ,"Render 3d gear worn on player. Must reload game to take effect").getBoolean(true);
		
		// Physics Settings
		String PhySetCat = "Physics";
		int minPhysInterval = 6;
		EM_Settings.spreadIce = config.get(PhySetCat, "Large Ice Cracking", false, "Setting Large Ice Cracking to true can cause Massive Lag").getBoolean(false);
		EM_Settings.updateCap = config.get(PhySetCat, "Consecutive Physics Update Cap", 128, "This will change maximum number of blocks that can be updated with physics at a time. - 1 = Unlimited").getInt(128);
		EM_Settings.physInterval = getConfigIntWithMinInt(config.get(PhySetCat, "Physics Interval", minPhysInterval, "The number of ticks between physics update passes (must be " + minPhysInterval + " or more)"), minPhysInterval);
		EM_Settings.stoneCracks = config.get(PhySetCat, "Stone Cracks Before Falling", true).getBoolean(true);
		EM_Settings.defaultStability = config.get(PhySetCat, "Default Stability Type (BlockIDs > 175)", "loose").getString();
		EM_Settings.worldDelay = config.get(PhySetCat, "World Start Delay", 1000, "How long after world start until the physics system kicks in (DO NOT SET TOO LOW)").getInt(1000);
		EM_Settings.chunkDelay = config.get(PhySetCat, "Chunk Physics Delay", 500, "How long until individual chunk's physics starts after loading (DO NOT SET TOO LOW)").getInt(500);
		EM_Settings.physInterval = EM_Settings.physInterval >= 2 ? EM_Settings.physInterval : 2;
		EM_Settings.entityFailsafe = config.get(PhySetCat, "Physics entity fail safe level", 1, "0 = No action, 1 = Limit to < 100 per 8x8 block area, 2 = Delete excessive entities & Dump physics (EMERGENCY ONLY)").getInt(1);
		
		// Config Gas
		EM_Settings.noGases = config.get("Gases", "Disable Gases", false, "Disables all gases and slowly deletes existing pockets").getBoolean(false);
		EM_Settings.slowGases = config.get("Gases", "Slow Gases", false, "Normal gases will move extremely slowly and reduce TPS lag").getBoolean(false);
		EM_Settings.renderGases = config.get("Gases", "Render normal gas", false, "Whether to render gases not normally visible").getBoolean(false);
		EM_Settings.gasTickRate = config.get("Gases", "Gas Tick Rate", 256, "How many ticks between gas updates. Gas fires are 1/4 of this.").getInt(256);
		EM_Settings.gasPassLimit = config.get("Gases", "Gas Pass Limit", 2048, "How many gases can be processed in a single pass per chunk (-1 = infinite)").getInt(-1);
		EM_Settings.gasWaterLike = config.get("Gases", "Water like spreading", true, "Whether gases should spread like water (faster) or even out as much as possible (realistic)").getBoolean(true);
		
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
		
		EM_Settings.tempMult = EM_Settings.tempMult < 0 ? 0F : EM_Settings.tempMult;
		EM_Settings.hydrationMult = EM_Settings.hydrationMult < 0 ? 0F : EM_Settings.hydrationMult;
		EM_Settings.airMult = EM_Settings.airMult < 0 ? 0F : EM_Settings.airMult;
		EM_Settings.sanityMult = EM_Settings.sanityMult < 0 ? 0F : EM_Settings.sanityMult;
		
		// Config Options
		String ConSetCat = "Config";

		
		EM_Settings.enableConfigOverride = config.get(ConSetCat, "Client Config Override (SMP)", false, "[DISABLED][WIP] Temporarily overrides client configurations with the server's (NETWORK INTESIVE!)").getBoolean(false);
		
		// Earthquake
		String EarSetCat = "Earthquakes";
		EM_Settings.enableQuakes = config.get(EarSetCat, "Enable Earthquakes", true).getBoolean(true);
		EM_Settings.quakePhysics = config.get(EarSetCat, "Triggers Physics", true, "Can cause major lag at times (Requires main physics to be enabled)").getBoolean(true);
		EM_Settings.quakeRarity = config.get(EarSetCat, "Rarity", 100).getInt(100);
		EM_Settings.quakeMode = config.get(EarSetCat, "Mode", 2, "Changes how quakes are created (-1 = random, 0 = wave normal, 1 = centre normal, 2 = centre tear, 3 = wave tear)").getInt(2);
		EM_Settings.quakeDelay = config.get(EarSetCat, "Tick delay", 10).getInt(10);
		EM_Settings.quakeSpeed = config.get(EarSetCat, "Speed", 2, "How many layers of rock it can eat through at a time").getInt(2);
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
		
		
		config = null;

		try {
			Files.delete(configFile.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			EnviroMine.logger.log(Level.ERROR, "Legacy Tried to Remove Enviromine.cfg... But failed! " +  e);
		}

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


}
