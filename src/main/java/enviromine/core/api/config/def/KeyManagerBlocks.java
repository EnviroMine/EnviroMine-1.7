package enviromine.core.api.config.def;

import net.minecraft.block.Block;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import enviromine.core.api.config.ConfigKey;
import enviromine.core.api.config.ConfigKeyManager;

public class KeyManagerBlocks extends ConfigKeyManager
{
	@Override
	public ConfigKey getKey(Configuration config, ConfigCategory category)
	{
		String blockID = config.getString("Block ID", category.getQualifiedName(), "minecraft:stone", "Full block ID including mod ID prefix");
		int[] metaList = config.get(category.getQualifiedName(), "Metadata", new int[]{-1}).getIntList();
		
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
