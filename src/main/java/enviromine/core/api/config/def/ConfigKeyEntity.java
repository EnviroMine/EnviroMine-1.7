package enviromine.core.api.config.def;

import enviromine.core.api.config.ConfigKey;

public class ConfigKeyEntity extends ConfigKey
{
	String entityID;
	
	public ConfigKeyEntity(String entityID)
	{
		this.entityID = entityID;
	}
	
	@Override
	public boolean SameKey(ConfigKey key)
	{
		if(!(key instanceof ConfigKeyEntity))
		{
			return false;
		}
		
		ConfigKeyEntity eKey = (ConfigKeyEntity)key;
		
		return entityID.equalsIgnoreCase(eKey.entityID);
	}
	
}
