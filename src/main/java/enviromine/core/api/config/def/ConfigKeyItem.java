package enviromine.core.api.config.def;

import net.minecraft.item.Item;
import enviromine.core.api.config.ConfigKey;

public class ConfigKeyItem extends ConfigKey
{
	public Item item;
	public int[] damages;
	
	public ConfigKeyItem(Item item, int[] damages)
	{
		this.item = item;
		this.damages = damages;
	}
	
	@Override
	public boolean SameKey(ConfigKey key)
	{
		if(!(key instanceof ConfigKeyItem))
		{
			return false;
		}
		
		ConfigKeyItem iKey = (ConfigKeyItem)key;
		
		if(item != iKey.item)
		{
			return false;
		}
		
		if(iKey.isWildcard()) // Requeste is wildcard
		{
			return true;
		} else if(this.isWildcard()) // Request is for specifics only
		{
			return false;
		}
		
		toploop:
		for(int dmg1 : iKey.damages)
		{
			for(int dmg2 : damages)
			{
				if(dmg1 == dmg2)
				{
					continue toploop;
				}
			}
			
			return false; // This key is missing one of the required damage values requested
		}
		
		return true;
	}

	@Override
	public boolean isWildcard()
	{
		return damages.length <= 0;
	}

	@Override
	public void setWildcard()
	{
		damages = new int[]{};
	}
	
	@Override
	public ConfigKey copy()
	{
		return new ConfigKeyItem(item, damages);
	}
}
