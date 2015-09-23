package enviromine_temp.properties;

import java.awt.Color;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.config.Configuration;
import enviromine.core.api.properties.PropertyRenderer;
import enviromine.core.api.properties.PropertyTracker;
import enviromine.core.utils.RenderAssist;

@SideOnly(Side.CLIENT)
public class RendererTemp extends PropertyRenderer
{
	@Override
	public void drawGui(PropertyTracker tracker)
	{
		TrackerTemp tempTrack = (TrackerTemp)tracker;
		RenderAssist.drawString("Body Temp: " + tempTrack.bodyTemp, posX, posY, Color.WHITE, true);
		RenderAssist.drawString("Air Temp: " + tempTrack.GetAirTemp(), posX, posY + 12, Color.WHITE, true);
		RenderAssist.drawString("Rate: " + tempTrack.changeRate, posX, posY + 24, Color.WHITE, true);
	}
	
	@Override
	public void LoadConfigurables(Configuration config)
	{
	}
}
