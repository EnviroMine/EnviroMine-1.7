package enviromine.core.api.config;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.core.EnviroMine;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

/**
 * In charge of holding the key objects to the given attribute category when loaded.
 */
public abstract class ConfigKeyManager
{
	/**
	 * Returns the key for this configuration category. This key will be passed to each attribute manager for generating defaults and storing attributes.
	 * Return null if the given information in the category is invalid and cannot be loaded correctly.
	 */
	public abstract ConfigKey getKey(Configuration config, ConfigCategory category);
	public abstract String CategoryName();
	
	/**
	 * Returns the editor to use for this configuration category type. Defaults to the Forge standard editor
	 */
	@SuppressWarnings("rawtypes")
	@SideOnly(Side.CLIENT)
	public GuiScreen getEditor(GuiScreen parent, Configuration config, ConfigCategory category)
	{
		return new GuiConfig(parent, new ConfigElement<IConfigElement>(category).getChildElements(), EnviroMine.ModID, false, false, this.CategoryName());
	}
}
