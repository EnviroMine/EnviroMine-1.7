package enviromine.trackers.properties.helpers;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

/**
 * Use as the base class for all custom configs for consistency
 */
public interface PropertyBase
{
	/**
	 * Category name
	 */
	public abstract String categoryName();
	
	/**
	 * Category description
	 */
	public abstract String categoryDescription();
	
	/**
	 * Create an instance of this based on the given properties
	 */
	public abstract void LoadProperty(Configuration config, String category);
	
	/**
	 * Save this instance to the configuration category (DO NOT call on the base instance)
	 */
	public abstract void SaveProperty(Configuration config, String category);
	
	/**
	 * Generate all the default entries for this property type
	 */
	public abstract void GenDefaults();
	
	/**
	 * Gets the default file for this configuration
	 */
	public abstract File GetDefaultFile();
	
	/**
	 * Check whether the given object has a default configuration. Used in determining whether a blank field should be generated.
	 */
	public abstract boolean hasDefault(Object obj);
	
	/**
	 * Generates a blank configuration entry for the given object
	 */
	public abstract void generateEmpty(Configuration config, Object obj);
	
	/**
	 * Whether or not this property should use the custom config folder or its own file
	 */
	public abstract boolean useCustomConfigs();
	
	/**
	 * This will be called if this property does not use the standard custom config files
	 */
	public abstract void customLoad();
}
