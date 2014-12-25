package enviromine.core;

import enviromine.trackers.properties.*;
import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EM_Settings
{
	public static final UUID FROST1_UUID = UUID.fromString("B0C5F86A-78F3-417C-8B5A-527B90A1E919");
	public static final UUID FROST2_UUID = UUID.fromString("5C4111A7-A66C-40FB-9FAD-1C6ADAEE7E27");
	public static final UUID FROST3_UUID = UUID.fromString("721E793E-2203-4F6F-883F-6F44D7DDCCE1");
	public static final UUID HEAT1_UUID = UUID.fromString("CA6E2CFA-4C53-4CD2-AAD3-3D6177A4F126");
	public static final UUID DEHY1_UUID = UUID.fromString("38909A39-E1A1-4E93-9016-B2CCBE83D13D");
	
	public static File worldDir = null;
	
	//Mod Data
	public static final String Version = "FWG_EM_VER";
	public static final String ModID = "enviromine";
	public static final String Channel = "EM_CH";
	public static final String Name = "EnviroMine";
	public static final String Proxy = "enviromine.core.proxies";
	
	public static boolean enablePhysics = true;
	public static boolean enableLandslide = true;
	@ShouldOverride
	public static boolean enableAirQ = true;
	@ShouldOverride
	public static boolean enableHydrate = true;
	@ShouldOverride
	public static boolean enableSanity = true;
	@ShouldOverride
	public static boolean enableBodyTemp = true;
	public static boolean trackNonPlayer = false;

	public static boolean spreadIce = false;
	
	public static boolean useFarenheit = false;
	public static String heatBarPos;
	public static String waterBarPos;
	public static String sanityBarPos;
	public static String oxygenBarPos;
	
	public static int dirtBottleID = 5001;
	public static int saltBottleID = 5002;
	public static int coldBottleID = 5003;
	public static int camelPackID = 5004;
	
	/*
	public static int gasMaskID = 5005;
	public static int airFilterID = 5006;
	public static int hardHatID = 5007;
	public static int rottenFoodID = 5008;
	
	public static int blockElevatorTopID = 501;
	public static int blockElevatorBottomID = 502;
	public static int gasBlockID = 503;
	public static int fireGasBlockID = 504;
	*/
	
	public static int hypothermiaPotionID = 27;
	public static int heatstrokePotionID = 28;
	public static int frostBitePotionID = 29;
	public static int dehydratePotionID = 30;
	public static int insanityPotionID = 31;
	
	//Gases
	public static boolean renderGases = false;
	public static int gasTickRate = 32; //GasFires are 4x faster than this
	public static int gasPassLimit = -1;
	public static boolean gasWaterLike = true;
	public static boolean slowGases; // Normal gases use random ticks to move
	public static boolean noGases = false;
	
	//World Gen
	public static boolean shaftGen = true;
	public static boolean gasGen = true;
	public static boolean oldMineGen = true;
	
	//Properties
	//@ShouldOverride("enviromine.network.packet.encoders.ArmorPropsEncoder")
	@ShouldOverride({String.class, ArmorProperties.class})
	public static HashMap<String,ArmorProperties> armorProperties = new HashMap<String,ArmorProperties>();
	//@ShouldOverride("enviromine.network.packet.encoders.BlocksPropsEncoder")
	@ShouldOverride({String.class, BlockProperties.class})
	public static HashMap<String,BlockProperties> blockProperties = new HashMap<String,BlockProperties>();
	@ShouldOverride({Integer.class, EntityProperties.class})
	public static HashMap<Integer,EntityProperties> livingProperties = new HashMap<Integer,EntityProperties>();
	@ShouldOverride({String.class, ItemProperties.class})
	public static HashMap<String,ItemProperties> itemProperties = new HashMap<String,ItemProperties>();
	@ShouldOverride({Integer.class, BiomeProperties.class})
	public static HashMap<Integer,BiomeProperties> biomeProperties = new HashMap<Integer,BiomeProperties>();
	@ShouldOverride({Integer.class, DimensionProperties.class})
	public static HashMap<Integer,DimensionProperties> dimensionProperties = new HashMap<Integer,DimensionProperties>();
	
	public static HashMap<String,StabilityType> stabilityTypes = new HashMap<String,StabilityType>();
	
	@ShouldOverride({String.class, RotProperties.class})
	public static HashMap<String,RotProperties> rotProperties = new HashMap<String,RotProperties>();
	
	public static int updateCap;
	public static boolean stoneCracks;
	public static String defaultStability;
	
	public static double sanityMult = 1.0D;
	public static double hydrationMult = 1.0D;
	public static double tempMult = 1.0D;
	public static double airMult = 1.0D;
	
	public static boolean updateCheck = true;
	public static boolean useDefaultConfig = true;
	public static boolean genConfigs = false;
	public static int physInterval;
	public static int worldDelay;
	public static int chunkDelay;
	public static int physBlockID;
	public static int entityFailsafe;
	public static boolean villageAssist;
	public static boolean minimalHud;
	public static boolean limitCauldron;
	public static boolean allowTinting;
	public static boolean torchesBurn;
	
	public static int caveDimID = -3;
	public static int caveBiomeID = 23;
	public static boolean disableCaves = false;
	public static int limitElevatorY = 10;
	public static boolean caveOreEvent = true;
	public static boolean caveLava = false;
	public static int caveRavineRarity = 30;
	public static int caveTunnelRarity = 7;
	public static int caveDungeons = 8;
	public static int caveLiquidY = 32;
	public static boolean caveFlood = true;
	public static boolean caveRespawn = false;
	public static ArrayList<CaveGenProperties> caveGenProperties = new ArrayList<CaveGenProperties>();
	public static ArrayList<CaveSpawnProperties> caveSpawnProperties = new ArrayList<CaveSpawnProperties>();
	
	public static boolean foodSpoiling = true;
	public static int foodRotTime = 7;
	
	/** Whether or not this overridden with server settings */
	public static boolean isOverridden = false;
	public static boolean enableConfigOverride = false;
	
	public static boolean enableQuakes = true;
	public static boolean quakePhysics = true;
	public static int quakeRarity = 100;
	public static int quakeDelay = 10;
	public static int quakeMode = 2;
	public static int quakeSpeed = 2;
	
	public static boolean finiteWater = false;
	public static boolean disableThing = false;
	
	public static float convertToFarenheit(float num)
	{
		return((num * (9 / 5)) + 32F);
	}
	
	public static float convertToCelcius(float num)
	{
		return((num - 32F) * (5 / 9));
	}
	
	/**
	 * Tells the server that this field should be sent to the client to overwrite<br>
	 * Usage:<br>
	 * <tt>@ShouldOverride</tt> - for ints/booleans/floats/Strings<br>
	 * <tt>@ShouldOverride(Class[] value)</tt> - for ArrayList or HashMap types
	 * */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ShouldOverride
	{
		Class<?>[] value() default {};
	}
}
