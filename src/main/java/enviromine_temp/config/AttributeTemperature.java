package enviromine_temp.config;

import com.google.gson.JsonObject;
import enviromine.core.api.config.Attribute;
import enviromine.core.api.config.ConfigKey;
import enviromine.core.api.config.def.ConfigKeyBlock;
import enviromine.core.api.helpers.JsonHelper;
import enviromine_temp.core.TempUtils;

public class AttributeTemperature extends Attribute
{
	public float ambTemp = 25F;
	public float effTemp = 0F;
	
	public AttributeTemperature(ConfigKey key, float ambTemp, float effTemp)
	{
		this(key);
		this.ambTemp = ambTemp;
		this.effTemp = effTemp;
	}
	
	public AttributeTemperature(ConfigKey baseKey)
	{
		super(baseKey);
	}
	
	@Override
	public void GenDefaults()
	{
		if(this.baseKey instanceof ConfigKeyBlock)
		{
			ConfigKeyBlock key = (ConfigKeyBlock)this.baseKey;
			ambTemp = TempUtils.GetBlockTemp(key.block, -1);
			System.out.println("Setting block " + key.block.getLocalizedName() + " to default temperature: " + ambTemp);
		}
	}
	
	@Override
	public void loadFromConfig(JsonObject json)
	{
		ambTemp = JsonHelper.GetNumber(json, "ambientTemp", 37F).floatValue();
		effTemp = JsonHelper.GetNumber(json, "effectTemp", 0F).floatValue();
	}
}
