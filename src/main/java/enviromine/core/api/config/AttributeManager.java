package enviromine.core.api.config;

import java.util.HashMap;

public abstract class AttributeManager
{
	public HashMap<ConfigKey, Attribute> cache = new HashMap<ConfigKey, Attribute>();
	
	public abstract String getConfigID();
	
	public void ResetCache()
	{
		cache.clear();
	}
	
	public final Attribute getAttribute(ConfigKey key)
	{
		if(cache.containsKey(key))
		{
			return cache.get(key);
		} else
		{
			Attribute att = this.createAttribute(key);
			cache.put(key, att);
			return att;
		}
	}
	
	/**
	 * Creates and returns a new Attribute.<br>
	 * <b>NOTE:</b> This method should pass the key values onto a purpose built handler
	 * if multiple types are being handled.
	 */
	protected abstract Attribute createAttribute(ConfigKey config);
}
