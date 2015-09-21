package enviromine_temp.config;

import net.minecraftforge.common.config.Configuration;
import enviromine.core.api.config.Attribute;
import enviromine.core.api.config.ConfigKey;

public class AttributeTemperature extends Attribute
{
	public boolean enabled = true;
	public float ambTemp = 37F;
	public float effTemp = 0F;
	
	public AttributeTemperature(ConfigKey baseKey)
	{
		super(baseKey);
	}
	
	@Override
	public void GenDefaults()
	{
	}
	
	@Override
	public void loadFromConfig(Configuration config, String category)
	{
		enabled = config.getBoolean("Enable Temp", category, true, "Enable temperature effects on this object");
		ambTemp = config.getFloat("Ambient Temperature", category, 37F, Float.MIN_VALUE, Float.MAX_VALUE, "");
		effTemp = config.getFloat("Effect Temperature", category, 0F, Float.MIN_VALUE, Float.MAX_VALUE, "Temperature effect consuming/touching this object will cause");
	}
}
