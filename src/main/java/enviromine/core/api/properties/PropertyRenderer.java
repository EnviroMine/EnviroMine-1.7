package enviromine.core.api.properties;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class PropertyRenderer
{
	public int posX = 5;
	public int posY = 5;
	
	/**
	 * Loads required configs before passing it off to the customizable method. This is to ensure that the core functionality of the GUI is not ignored
	 * @param config
	 */
	public final void LoadAllConfigs(Configuration config)
	{
		posX = config.getInt("X Position", Configuration.CATEGORY_GENERAL, 5, 0, 100, "Position of this GUI on the X axis");
		posY = config.getInt("Y Position", Configuration.CATEGORY_GENERAL, 5, 0, 100, "Position of this GUI on the Y axis");
		
		this.LoadConfigurables(config);
	}
	
	public abstract void LoadConfigurables(Configuration config);
	public abstract void drawGui(PropertyTracker tracker);
}
