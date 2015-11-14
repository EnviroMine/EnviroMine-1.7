package enviromine_temp.client.gui.hud;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import enviromine.client.gui.hud.HUDRegistry;
import enviromine.core.api.hud.HudItem;
import enviromine.core.api.hud.IRotate;
import enviromine.utils.Alignment;

public class HudTemperature extends HudItem implements IRotate
{
	private boolean rotated = false;
	
	
	@Override
	public String getUnLocolizedName() 
	{
		return "EMTemperature";
	}

	@Override
	public String getLocalizedName() 
	{
		return StatCollector.translateToLocal("enviromine.property.temp.name");
	}

	@Override
	public boolean isInMenu() 
	{
		return true;
	}

	@Override
	public String getButtonLabel() 
	{
		return StatCollector.translateToLocal("enviromine.property.temp.button");
	}

	@Override
	public Alignment getDefaultAlignment() 
	{
		return Alignment.BOTTOMLEFT;
	}

	@Override
	public int getDefaultPosX() 
	{
		return 8;
	}

	@Override
	public int getDefaultPosY() 
	{
		return (HUDRegistry.screenHeight - 30);
	}

	@Override
	public int getWidth() 
	{
		return !rotated ? 0 : 64;
	}

	@Override
	public int getHeight() 
	{
		return 8;
	}

	@Override
	public boolean isEnabledByDefault()
	{
		return true;
	}
	
	@Override
	public ResourceLocation getResource(String type) 
	{
		return null;
	}

	@Override
	public int getDefaultID() 
	{
		return 0;
	}

	@Override
	public void render() 
	{
		
	}

	@Override
	public boolean canRotate() 
	{
		return true;
	}

	@Override
	public boolean isRotated() 
	{
		return this.rotated;
	}

	@Override
	public void setRotated(boolean value) 
	{
		this.rotated = value;
	}

}
