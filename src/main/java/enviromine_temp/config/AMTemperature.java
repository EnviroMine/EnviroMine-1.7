package enviromine_temp.config;

import enviromine.core.api.config.Attribute;
import enviromine.core.api.config.AttributeManager;
import enviromine.core.api.config.ConfigKey;

public class AMTemperature extends AttributeManager
{
	@Override
	public String getConfigID()
	{
		return "Temperature";
	}

	@Override
	protected Attribute createAttribute(ConfigKey config)
	{
		return null;
	}
	
}
