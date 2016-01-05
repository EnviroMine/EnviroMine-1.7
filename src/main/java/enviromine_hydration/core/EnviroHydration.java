package enviromine_hydration.core;

import org.apache.logging.log4j.Logger;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import enviromine_hydration.core.proxies.CommonProxy;
import enviromine_hydration.properties.PropertyHydration;

@Mod(modid = EnviroHydration.MODID, name = EnviroHydration.NAME, version = EnviroHydration.VERSION)
public class EnviroHydration
{
	public static final String MODID = "envirohydration";
	public static final String VERSION = "1.0.0";
	public static final String NAME = "EnviroMine-Hydration";
	public static final String PROXY = "enviromine_hydration.core.proxies";
	
	public static Logger logger;
	public static PropertyHydration hydrationProperty = new PropertyHydration();
	
	@Instance(MODID)
	public static EnviroHydration instance;
	
	@SidedProxy(clientSide = PROXY + ".ClientProxy", serverSide = PROXY + ".CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		
		proxy.registerHandlers();
	}
}
