package enviromine.core;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.EnviroPotion;
import enviromine.client.gui.EM_GuiAuthWarn;
import enviromine.core.commands.CommandPhysics;
import enviromine.core.commands.EnviroCommand;
import enviromine.core.commands.QuakeCommand;
import enviromine.core.proxies.EM_CommonProxy;
import enviromine.handlers.EnviroAchievements;
import enviromine.handlers.EnviroShaftCreationHandler;
import enviromine.handlers.ObjectHandler;
import enviromine.network.packet.PacketAutoOverride;
import enviromine.network.packet.PacketEnviroMine;
import enviromine.network.packet.PacketServerOverride;
import enviromine.utils.EnviroUtils;
import enviromine.utils.LockedClass;
import enviromine.world.WorldProviderCaves;
import enviromine.world.biomes.BiomeGenCaves;
import enviromine.world.features.WorldFeatureGenerator;
import enviromine.world.features.mineshaft.EM_VillageMineshaft;

@Mod(modid = EM_Settings.ModID, name = EM_Settings.Name, version = EM_Settings.Version, guiFactory = "enviromine.client.gui.menu.config.EnviroMineGuiFactory")
public class EnviroMine
{
	public static Logger logger;
	public static BiomeGenCaves caves;
	public static EnviroTab enviroTab;
	
	@Instance(EM_Settings.ModID)
	public static EnviroMine instance;
	
	@SidedProxy(clientSide = EM_Settings.Proxy + ".EM_ClientProxy", serverSide = EM_Settings.Proxy + ".EM_CommonProxy")
	public static EM_CommonProxy proxy;
	
	public SimpleNetworkWrapper network;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		
		FunwayModAuthentication.CheckAndUnlockMod();
		
		if(proxy.isClient())
		{
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		if(LockedClass.IsLocked())
		{
			if(proxy.isClient())
			{
				EM_GuiAuthWarn.shouldWarn = true;
			}
			return;
		}
		
		enviroTab = new EnviroTab("enviromine.enviroTab");
		proxy.preInit(event);
		
		ObjectHandler.initItems();
		ObjectHandler.registerItems();
		ObjectHandler.initBlocks();
		ObjectHandler.registerBlocks();
		
		// Load Configuration files And Custom files
		EM_ConfigHandler.initConfig();
		
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
		this.network.registerMessage(PacketAutoOverride.Handler.class, PacketAutoOverride.class, 2, Side.CLIENT);
		this.network.registerMessage(PacketServerOverride.Handler.class, PacketServerOverride.class, 3, Side.CLIENT);

		
		GameRegistry.registerWorldGenerator(new WorldFeatureGenerator(), 20);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		if(LockedClass.IsLocked())
		{
			return;
		}
		
		proxy.init(event);
		
		ObjectHandler.registerRecipes();
		
		EnviroUtils.extendPotionList();
		
		EnviroPotion.RegisterPotions();
		
		EnviroAchievements.InitAchievements();
		
		caves = (BiomeGenCaves)(new BiomeGenCaves(EM_Settings.caveBiomeID).setColor(0).setBiomeName("Caves").setDisableRain().setTemperatureRainfall(1.0F, 0.0F));
		//GameRegistry.addBiome(caves);
		BiomeDictionary.registerBiomeType(caves, Type.WASTELAND);
		
		
		DimensionManager.registerProviderType(EM_Settings.caveDimID, WorldProviderCaves.class, false);
		DimensionManager.registerDimension(EM_Settings.caveDimID, EM_Settings.caveDimID);
		
		
		proxy.registerTickHandlers();
		proxy.registerEventHandlers();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		if(LockedClass.IsLocked())
		{
			return;
		}
		
		proxy.postInit(event);
		
		ObjectHandler.LoadIgnitionSources();

		EM_ConfigHandler.initConfig(); // Second pass for object initialized after pre-init

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
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event)
	{
		if(LockedClass.IsLocked())
		{
			return;
		}
		
		MinecraftServer server = MinecraftServer.getServer();
		ICommandManager command = server.getCommandManager();
		ServerCommandManager manager = (ServerCommandManager) command;
		
		manager.registerCommand(new CommandPhysics());
		manager.registerCommand(new EnviroCommand());
		manager.registerCommand(new QuakeCommand());
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onGuiOpen(GuiOpenEvent event)
	{
		if(event.gui instanceof GuiMainMenu && EM_GuiAuthWarn.shouldWarn)// && !EM_Settings.Version.equals("FWG_" + "EM_VER"))
		{
			event.gui = new EM_GuiAuthWarn(event.gui);
		}
	}
}
