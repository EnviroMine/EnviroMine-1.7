package enviromine_temp.core.proxies;

import enviromine.core.api.config.ConfigRegistry;
import enviromine.core.api.properties.PropertyRegistry;
import enviromine_temp.config.AMTemperature;
import enviromine_temp.core.EnviroTemp;
import enviromine_temp.properties.PropertyTemp;

public class CommonProxy
{
	public boolean isClient()
	{
		return false;
	}
	
	public void registerHandlers()
	{
		// Register entity tracker
		PropertyRegistry.RegisterProperty(new PropertyTemp(), EnviroTemp.instance, "body_temp");
		
		// Register attribute manager
		ConfigRegistry.registerManager(ConfigRegistry.BLOCK, AMTemperature.instance);
		ConfigRegistry.registerManager(ConfigRegistry.ENTITY, AMTemperature.instance);
		ConfigRegistry.registerManager(ConfigRegistry.ITEM, AMTemperature.instance);
	}
}
