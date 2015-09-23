package enviromine.core.api.config;

public abstract class ConfigKey
{
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof ConfigKey)
		{
			return this.SameKey((ConfigKey)obj);
		} else
		{
			return false;
		}
	}
	
	public abstract ConfigKey copy();
	
	public abstract boolean isWildcard();
	public abstract void setWildcard();
	
	/**
	 * Checks whether the given configuration key is equal to this. This should account wildcards for partial matches such as testing a single damage value
	 * against a possibly list that this key could potentially apply to.
	 */
	public abstract boolean SameKey(ConfigKey key);
}
