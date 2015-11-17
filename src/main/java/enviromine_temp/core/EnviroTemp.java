package enviromine_temp.core;

import org.apache.logging.log4j.Logger;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import enviromine_temp.core.proxies.CommonProxy;
import enviromine_temp.properties.PropertyTemp;

@Mod(modid = EnviroTemp.MODID, name = EnviroTemp.NAME, version = EnviroTemp.VERSION)
public class EnviroTemp
{
	public static final String MODID = "envirotemp";
	public static final String VERSION = "1.0.0";
	public static final String NAME = "EnviroMine-Temperature";
	public static final String PROXY = "enviromine_temp.core.proxies";
	
	public static Logger logger;
	public static PropertyTemp tempProperty = new PropertyTemp();
	
	@Instance(MODID)
	public static EnviroTemp instance;
	
	@SidedProxy(clientSide = PROXY + ".ClientProxy", serverSide = PROXY + ".CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		
		proxy.registerHandlers();
	}
}
