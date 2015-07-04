package enviromine.core;

import org.apache.logging.log4j.Logger;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import enviromine.core.network.PacketEnviroProperty;
import enviromine.core.proxies.EM_CommonProxy;
import enviromine.world.biomes.BiomeGenCaves;

@Mod(modid = EnviroMine.ModID, name = EnviroMine.Name, version = EnviroMine.Version, guiFactory = "enviromine.client.gui.menu.config.EnviroMineGuiFactory")
public class EnviroMine
{
	public static final String Version = "FWG_EM_VER";
	public static final String ModID = "enviromine";
	public static final String Channel = "EM_CH";
	public static final String Name = "EnviroMine-Core";
	public static final String Proxy = "enviromine.core.proxies";
	
	public static Logger logger;
	public static BiomeGenCaves caves;
	public static EnviroTab enviroTab;
	
	@Instance(ModID)
	public static EnviroMine instance;
	
	@SidedProxy(clientSide = Proxy + ".EM_ClientProxy", serverSide = Proxy + ".EM_CommonProxy")
	public static EM_CommonProxy proxy;
	
	public SimpleNetworkWrapper network;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		
		enviroTab = new EnviroTab("enviromine.enviroTab");
		
		network = new SimpleNetworkWrapper(Channel);
		network.registerMessage(PacketEnviroProperty.HandleClientPacket.class, PacketEnviroProperty.class, 0, Side.CLIENT);
		
		proxy.registerHandlers();
		
		ConfigLoader.LoadConfigs();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
	}
}
