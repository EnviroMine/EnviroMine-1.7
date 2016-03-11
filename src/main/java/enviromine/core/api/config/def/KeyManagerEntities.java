package enviromine.core.api.config.def;

import net.minecraft.entity.EntityList;
import com.google.gson.JsonObject;
import enviromine.core.api.config.ConfigKey;
import enviromine.core.api.config.ConfigKeyManager;
import enviromine.core.api.helpers.JsonHelper;

public class KeyManagerEntities extends ConfigKeyManager
{
	
	@Override
	public ConfigKey getKey(JsonObject json)
	{
		String id = JsonHelper.GetString(json, "entityID", "Pig");
		
		if(EntityList.stringToClassMapping.containsKey(id))
		{
			return new ConfigKeyEntity(id);
		} else
		{
			return null;
		}
	}
	
	@Override
	public String CategoryName()
	{
		return "Entities";
	}
	
}
