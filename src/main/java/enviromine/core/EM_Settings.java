package enviromine.core;

import enviromine.trackers.ArmorProperties;
import enviromine.trackers.BlockProperties;
import enviromine.trackers.EntityProperties;
import enviromine.trackers.ItemProperties;
import enviromine.trackers.StabilityType;

import java.util.HashMap;
import java.util.UUID;

public class EM_Settings
{
	public static final UUID FROST1_UUID = UUID.fromString("B0C5F86A-78F3-417C-8B5A-527B90A1E919");
	public static final UUID FROST2_UUID = UUID.fromString("5C4111A7-A66C-40FB-9FAD-1C6ADAEE7E27");
	public static final UUID FROST3_UUID = UUID.fromString("721E793E-2203-4F6F-883F-6F44D7DDCCE1");
	public static final UUID HEAT1_UUID = UUID.fromString("CA6E2CFA-4C53-4CD2-AAD3-3D6177A4F126");
	public static final UUID DEHY1_UUID = UUID.fromString("38909A39-E1A1-4E93-9016-B2CCBE83D13D");
	
	//Mod Data
	public static final String Version = "FWG_EM_VER";
	public static final String ID = "enviromine";
	public static final String Channel = "EM_CH";
	public static final String Name = "EnviroMine";
	public static final String Proxy = "enviromine.core.proxies";
	
	public static boolean enablePhysics = true;
	public static boolean enableLandslide = true;
	public static boolean enableAirQ = true;
	public static boolean enableHydrate = true;
	public static boolean enableSanity = true;
	public static boolean enableBodyTemp = true;
	public static boolean trackNonPlayer;
	
	public static boolean ShowGuiIcons;
	
	public static boolean spreadIce = false;
	
	//Gui settings
	public static boolean sweatParticals;
	public static boolean insaneParticals;
	
	public static boolean useFarenheit = false;
	public static String heatBarPos;
	public static String waterBarPos;
	public static String sanityBarPos;
	public static String oxygenBarPos;
	
	public static boolean ShowText;
	public static boolean ShowDebug;
	
	public static int hypothermiaPotionID = 27;
	public static int heatstrokePotionID = 28;
	public static int frostBitePotionID = 29;
	public static int dehydratePotionID = 30;
	public static int insanityPotionID = 31;
	
	//World Gen
	public static boolean shaftGen;
	
	//Properties
	public static HashMap<Integer,ArmorProperties> armorProperties = new HashMap<Integer,ArmorProperties>();
	public static HashMap<String,BlockProperties> blockProperties = new HashMap<String,BlockProperties>();
	public static HashMap<Integer,EntityProperties> livingProperties = new HashMap<Integer,EntityProperties>();
	public static HashMap<String,ItemProperties> itemProperties = new HashMap<String,ItemProperties>();
	
	public static HashMap<String,StabilityType> stabilityTypes = new HashMap<String,StabilityType>();
	public static int updateCap;
	public static boolean stoneCracks;
	public static String defaultStability;
	
	public static double sanityMult = 1.0D;
	public static double hydrationMult = 1.0D;
	public static double tempMult = 1.0D;
	public static double airMult = 1.0D;
	
	public static boolean updateCheck = true;
	public static boolean useDefaultConfig = true;
	public static boolean genArmorConfigs = false;
	public static int physInterval;
	public static int worldDelay;
	public static int chunkDelay;
	public static int physBlockID;
	public static int entityFailsafe;
	public static boolean villageAssist;
	public static boolean minimalHud;
	
	public static float convertToFarenheit(float num)
	{
		return((num * (9 / 5)) + 32F);
	}
	
	public static float convertToCelcius(float num)
	{
		return((num - 32F) * (5 / 9));
	}
}
