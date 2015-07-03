package enviromine.core.api.attributes;

import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;

public abstract class AttributeManager
{
	public HashMap<String, Attribute> cacheBlocks = new HashMap<String, Attribute>();
	public HashMap<String, Attribute> cacheItem = new HashMap<String, Attribute>();
	public HashMap<String, Attribute> cacheEntity = new HashMap<String, Attribute>();
	
	public abstract String getConfigID();
	
	public void ResetCache()
	{
		cacheBlocks.clear();
		cacheItem.clear();
		cacheEntity.clear();
	}
	
	// ===== BLOCKS =====
	
	public final Attribute getBlockAttribute(Block block, int meta)
	{
		meta = MathHelper.clamp_int(meta, -1, 15);
		String key = Block.blockRegistry.getNameForObject(block) + ":" + meta;
		
		if(cacheBlocks.containsKey(key))
		{
			return cacheBlocks.get(key);
		} else
		{
			Attribute att = this.createBlockAttribute(block, meta);
			cacheBlocks.put(key, att);
			return att;
		}
	}
	
	/**
	 * Creates and returns a new Block Attribute.<br>
	 * <b>NOTE:</b> A metadata of -1 should be treated as a wildcard
	 */
	protected abstract Attribute createBlockAttribute(Block block, int meta);
	
	// ===== ITEMS =====
	
	public final Attribute getItemAttribute(Item item, int damage)
	{
		damage = item.isDamageable()? -1 : damage;
		
		String key = Item.itemRegistry.getNameForObject(item) + ":" + damage;
		
		if(cacheItem.containsKey(key))
		{
			return cacheItem.get(key);
		} else
		{
			Attribute att = this.createItemAttribute(item, damage);
			cacheItem.put(key, att);
			return att;
		}
	}
	
	/**
	 * Creates and returns a new Item Attribute.<br>
	 * <b>NOTE:</b> Damageable items such as tools will always be passed with -1 damage.
	 */
	protected abstract Attribute createItemAttribute(Item item, int damage);
	
	// ===== ENTITIES =====
	
	public final Attribute getEntityAttribute(Entity entity)
	{
		String key = EntityList.getEntityString(entity);
		
		if(key == null || key.length() <= 0)
		{
			return null;
		}
		
		if(cacheEntity.containsKey(key))
		{
			return cacheEntity.get(key);
		} else
		{
			Attribute att = this.createEntityAttribute(entity);
			cacheEntity.put(key, att);
			return att;
		}
	}
	
	protected abstract Attribute createEntityAttribute(Entity entity);
}
