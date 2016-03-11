package enviromine.core.api.config.def;

import java.util.ArrayList;
import net.minecraft.item.Item;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import enviromine.core.api.config.ConfigKey;
import enviromine.core.api.config.ConfigKeyManager;
import enviromine.core.api.helpers.JsonHelper;

public class KeyManagerItems extends ConfigKeyManager
{
	@Override
	public ConfigKey getKey(JsonObject json)
	{
		String itemID = JsonHelper.GetString(json, "itemID", "minecraft:stone");
		
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		for(JsonElement je : JsonHelper.GetArray(json, "damage"))
		{
			if(je == null || !je.isJsonPrimitive() || !je.getAsJsonPrimitive().isNumber())
			{
				continue;
			}
			
			tmp.add(je.getAsInt());
		}
		
		int[] dmgList = new int[tmp.size()];
		for(int i = 0; i < tmp.size(); i++)
		{
			dmgList[i] = tmp.get(i);
		}
		
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
