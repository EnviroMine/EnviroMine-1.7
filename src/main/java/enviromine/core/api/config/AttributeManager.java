package enviromine.core.api.config;

import java.util.HashMap;
import java.util.Map.Entry;

public abstract class AttributeManager
{
	public HashMap<ConfigKey, Attribute> cache = new HashMap<ConfigKey, Attribute>();
	
	public abstract String getConfigID();
	
	public void ResetCache()
	{
		cache.clear();
	}
	
	/**
	 * Gets an existing attribute if it has been cached or creates a new one as a wildcard
	 */
	public final Attribute getAttribute(ConfigKey key)
	{
		if(key == null)
		{
			return null;
		}
		
		ConfigKey wildKey = key.copy();
		wildKey.setWildcard();
		
		Attribute att = GetCacheKey(key); // Get specific
		att = att != null? att : GetCacheKey(wildKey); // Get wildcard if no specific entry exists
		
		if(att == null) // Still can't be found
		{
			att = this.createAttribute(wildKey);
			cache.put(wildKey, att);
			System.out.println("Cache size is now " + cache.size());
		}
		
		return att;
	}
	
	public Attribute GetCacheKey(ConfigKey key)
	{
		for(Entry<ConfigKey,Attribute> entry : cache.entrySet())
		{
			if(entry.getKey().equals(key))
			{
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * Creates and returns a new Attribute.<br>
	 * <b>NOTE:</b> This method should pass the key values onto a purpose built handler
	 * if multiple types are being handled.
	 */
	public abstract Attribute createAttribute(ConfigKey config);
	
	/**
	 * Load all the default attributes into the cache
	 */
	public abstract void LoadDefaults();
}
