package enviromine_hydration.config;

import com.google.gson.JsonObject;
import enviromine.core.api.config.Attribute;
import enviromine.core.api.config.ConfigKey;

public class AttributeHydration extends Attribute
{
	public AttributeHydration(ConfigKey baseKey)
	{
		super(baseKey);
	}
	
	@Override
	public void loadFromConfig(JsonObject json)
	{
	}
}