package enviromine_hydration.config;

import net.minecraftforge.common.config.Configuration;
import enviromine.core.api.config.Attribute;
import enviromine.core.api.config.ConfigKey;

public class AttributeHydration extends Attribute
{
	public AttributeHydration(ConfigKey baseKey)
	{
		super(baseKey);
	}
	
	@Override
	public void loadFromConfig(Configuration config, String category)
	{
	}
}