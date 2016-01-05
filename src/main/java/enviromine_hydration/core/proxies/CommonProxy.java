package enviromine_hydration.core.proxies;

import enviromine.core.api.config.ConfigRegistry;
import enviromine.core.api.properties.PropertyRegistry;
import enviromine_hydration.config.AMHydration;
import enviromine_hydration.core.EnviroHydration;
import enviromine_hydration.properties.PropertyHydration;

public class CommonProxy
{
	public boolean isClient()
	{
		return false;
	}
	
	public void registerHandlers()
	{
		// Register entity tracker
		PropertyRegistry.RegisterProperty(new PropertyHydration(), EnviroHydration.instance, "hydration");
		
		// Register attribute manager
		ConfigRegistry.registerManager(ConfigRegistry.BLOCK, AMHydration.instance);
		ConfigRegistry.registerManager(ConfigRegistry.ENTITY, AMHydration.instance);
		ConfigRegistry.registerManager(ConfigRegistry.ITEM, AMHydration.instance);
	}
}
