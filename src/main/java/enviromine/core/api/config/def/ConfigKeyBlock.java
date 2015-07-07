package enviromine.core.api.config.def;

import net.minecraft.block.Block;
import enviromine.core.api.config.ConfigKey;

public class ConfigKeyBlock extends ConfigKey
{
	public Block block;
	public int[] metadata;
	
	public ConfigKeyBlock(Block block, int[] metadata)
	{
		this.block = block;
		this.metadata = metadata;
	}
	
	@Override
	public boolean SameKey(ConfigKey key) // Note: This will return true even if this key has more meta values than requested, as long as all requested values are present here.
	{
		if(!(key instanceof ConfigKeyBlock))
		{
			return false;
		}
		
		ConfigKeyBlock bKey = (ConfigKeyBlock)key;
		
		if(block != bKey.block)
		{
			return false;
		}
		
		if(metadata.length <= 0 && bKey.metadata.length <= 0) // Matching wildcard
		{
			return true;
		}
		
		toploop:
		for(int meta1 : bKey.metadata)
		{
			for(int meta2 : metadata)
			{
				if(meta1 == meta2)
				{
					continue toploop;
				}
			}
			
			return false; // This key is missing one of the required metadata values requested
		}
		
		return true;
	}
	
}
