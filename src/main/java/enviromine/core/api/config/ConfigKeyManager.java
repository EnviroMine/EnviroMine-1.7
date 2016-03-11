package enviromine.core.api.config;

import com.google.gson.JsonObject;

/**
 * In charge of holding the key objects to the given attribute category when loaded.
 */
public abstract class ConfigKeyManager
{
	/**
	 * Returns the key for this configuration category. This key will be passed to each attribute manager for generating defaults and storing attributes.
	 * Return null if the given information in the category is invalid and cannot be loaded correctly.
	 */
	public abstract ConfigKey getKey(JsonObject json);
	public abstract String CategoryName();
}
