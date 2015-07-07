package enviromine.core.api.config.def;

import net.minecraft.item.Item;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import enviromine.core.api.config.ConfigKey;
import enviromine.core.api.config.ConfigKeyManager;

public class KeyManagerItems extends ConfigKeyManager
{
	@Override
	public ConfigKey getKey(Configuration config, ConfigCategory category)
	{
		String itemID = config.getString("Item ID", category.getQualifiedName(), "minecraft:stone", "Full item ID including mod ID prefix");
		int[] dmgList = config.get(category.getQualifiedName(), "Damage", new int[]{}).getIntList();
		
		Item item = (Item)Item.itemRegistry.getObject(itemID);
		
		if(item == null)
		{
			return null;
		} else
		{
			return new ConfigKeyItem(item, dmgList);
		}
	}

	@Override
	public String CategoryName()
	{
		return "Items";
	}
}
