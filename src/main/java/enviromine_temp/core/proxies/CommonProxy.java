package enviromine_temp.core.proxies;

import enviromine.core.api.properties.PropertyRegistry;
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
		PropertyRegistry.RegisterProperty(new PropertyTemp(), EnviroTemp.instance, "body_temp");
	}
}
