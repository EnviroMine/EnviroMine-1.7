package enviromine.core.api.config.def;

import java.util.ArrayList;
import net.minecraft.block.Block;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import enviromine.core.api.config.ConfigKey;
import enviromine.core.api.config.ConfigKeyManager;
import enviromine.core.api.helpers.JsonHelper;

public class KeyManagerBlocks extends ConfigKeyManager
{
	@Override
	public ConfigKey getKey(JsonObject json)
	{
		String blockID = JsonHelper.GetString(json, "blockID", "minecraft:stone");
		
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		for(JsonElement je : JsonHelper.GetArray(json, "meta"))
		{
			if(je == null || !je.isJsonPrimitive() || !je.getAsJsonPrimitive().isNumber())
			{
				continue;
			}
			
			tmp.add(je.getAsInt());
		}
		
		int[] metaList = new int[tmp.size()];
		for(int i = 0; i < tmp.size(); i++)
		{
			metaList[i] = tmp.get(i);
		}
		
		Block block = (Block)Block.blockRegistry.getObject(blockID);
		
		if(block == null)
		{
			return null;
		} else
		{
			return new ConfigKeyBlock(block, metaList);
		}
	}

	@Override
	public String CategoryName()
	{
		return "Blocks";
	}
}
