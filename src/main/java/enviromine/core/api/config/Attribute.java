package enviromine.core.api.config;

import com.google.gson.JsonObject;

public abstract class Attribute
{
	public ConfigKey baseKey;
	
	public Attribute(ConfigKey baseKey)
	{
		this.baseKey = baseKey;
		
		GenDefaults();
	}
	
	/**
	 * Generates default values
	 */
	public void GenDefaults()
	{
	}
	
	public abstract void loadFromConfig(JsonObject json);
}
