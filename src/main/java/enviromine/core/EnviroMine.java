package enviromine.core;

import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import enviromine.EM_VillageMineshaft;
import enviromine.EnviroPotion;
import enviromine.EnviroUtils;
import enviromine.core.commands.CommandPhysics;
import enviromine.core.commands.EnviroCommand;
import enviromine.core.proxies.EM_CommonProxy;
import enviromine.handlers.EnviroAchievements;
import enviromine.handlers.EnviroShaftCreationHandler;
import enviromine.handlers.ObjectHandler;
import enviromine.network.packet.PacketEnviroMine;
import enviromine.network.packet.PacketServerOverride;
import enviromine.world.WorldProviderCaves;
import enviromine.world.biomes.BiomeGenCaves;
import enviromine.world.features.WorldFeatureGenerator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

@Mod(modid = EM_Settings.ID, name = EM_Settings.Name, version = EM_Settings.Version)
public class EnviroMine
{
	public static Logger logger;
	public static BiomeGenBase caves;
	public static CreativeTabs enviroTab = new EnviroTab("enviroTab");
	
	@Instance(EM_Settings.ID)
	public static EnviroMine instance;
	
	@SidedProxy(clientSide = EM_Settings.Proxy + ".EM_ClientProxy", serverSide = EM_Settings.Proxy + ".EM_CommonProxy")
	public static EM_CommonProxy proxy;
	
	public SimpleNetworkWrapper network;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		
		proxy.preInit(event);
		
		// Load Configuration files And Custom files
		EM_ConfigHandler.initConfig();
		
		ObjectHandler.initItems();
		ObjectHandler.registerItems();
		ObjectHandler.initBlocks();
		ObjectHandler.registerBlocks();
		ObjectHandler.registerGases();
		ObjectHandler.registerEntities();
		
		if(EM_Settings.shaftGen == true)
		{
			VillagerRegistry.instance().registerVillageCreationHandler(new EnviroShaftCreationHandler());
			MapGenStructureIO.func_143031_a(EM_VillageMineshaft.class, "ViMS");
		}
		
		this.network = NetworkRegistry.INSTANCE.newSimpleChannel(EM_Settings.Channel);
		this.network.registerMessage(PacketEnviroMine.HandlerServer.class, PacketEnviroMine.class, 0, Side.SERVER);
		this.network.registerMessage(PacketEnviroMine.HandlerClient.class, PacketEnviroMine.class, 1, Side.CLIENT);
		this.network.registerMessage(PacketServerOverride.Handler.class, PacketServerOverride.class, 2, Side.CLIENT);

		
		GameRegistry.registerWorldGenerator(new WorldFeatureGenerator(), 20);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init(event);
		
		ObjectHandler.registerRecipes();
		
		EnviroUtils.extendPotionList();
		
		EnviroPotion.RegisterPotions();
		
		EnviroAchievements.InitAchievements();
		
		caves = (new BiomeGenCaves(23)).setColor(16711680).setBiomeName("Caves").setDisableRain().setTemperatureRainfall(1.0F, 0.0F);
		//GameRegistry.addBiome(caves); TODO
		BiomeDictionary.registerBiomeType(caves, Type.WASTELAND);
		
		
		DimensionManager.registerProviderType(EM_Settings.caveDimID, WorldProviderCaves.class, false);
		DimensionManager.registerDimension(EM_Settings.caveDimID, EM_Settings.caveDimID);
		
		
		proxy.registerTickHandlers();
		proxy.registerEventHandlers();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
		
		if(EM_Settings.genArmorConfigs)
		{
			EM_ConfigHandler.SearchForModdedArmors();
		}
		
		EM_ConfigHandler.SearchForDimensions();
		EM_ConfigHandler.SearchForBiomes();
		
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.armorProperties.size() + " armor properties");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.blockProperties.size() + " block properties");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.livingProperties.size() + " entity properties");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.itemProperties.size() + " item properties");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.biomeProperties.size() + " biome properties");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.dimensionProperties.size() + " dimension properties");
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event)
	{
		MinecraftServer server = MinecraftServer.getServer();
		ICommandManager command = server.getCommandManager();
		ServerCommandManager manager = (ServerCommandManager) command;
		
		manager.registerCommand(new CommandPhysics());
		manager.registerCommand(new EnviroCommand());
	}
}
