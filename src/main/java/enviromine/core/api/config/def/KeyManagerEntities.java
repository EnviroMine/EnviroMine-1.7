package enviromine.core.api.config.def;

import net.minecraft.entity.EntityList;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import enviromine.core.api.config.ConfigKey;
import enviromine.core.api.config.ConfigKeyManager;

public class KeyManagerEntities extends ConfigKeyManager
{
	
	@Override
	public ConfigKey getKey(Configuration config, ConfigCategory category)
	{
		String id = config.getString("Entity ID", category.getQualifiedName(), "Pig", "Full entity ID");
		
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
