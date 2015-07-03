package enviromine.core.api.attributes;

import net.minecraftforge.common.config.Configuration;

public abstract class Attribute
{
	protected Attribute(Object obj)
	{
		this.GenDefaults();
	}
	
	public abstract void GenDefaults();
	
	public abstract void loadFromConfig(Configuration config, String category);
}
