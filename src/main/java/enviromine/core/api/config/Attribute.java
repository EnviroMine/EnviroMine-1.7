package enviromine.core.api.config;

import net.minecraftforge.common.config.Configuration;

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
	
	public abstract void loadFromConfig(Configuration config, String category);
}
