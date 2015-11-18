package enviromine_temp.client.gui.hud;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import enviromine.core.api.hud.HudItem;
import enviromine.core.utils.RenderAssist;
import enviromine_temp.core.EnviroTemp;
import enviromine_temp.properties.TrackerTemp;

public class HudTemperature extends HudItem
{
	public HudTemperature(String ID)
	{
		super(ID);
	}

	@Override
	public String getUnlocalizedName() 
	{
		return "enviromine.property.temp.name";
	}

	@Override
	public boolean isInMenu() 
	{
		return true;
	}

	@Override
	public int getWidth() 
	{
		return 108;
	}

	@Override
	public int getHeight() 
	{
		return 32;
	}

	@Override
	public void renderHud(ElementType layer, int dispWidth, int dispHeight)
	{
		// You can query the rotation value from the inheriting class if you need to glRotatef any icons, etc.
		RenderAssist.drawRect(0F, 0F, this.getWidth(), this.getHeight(), Color.BLACK);
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		TrackerTemp tempTrack = (TrackerTemp)EnviroTemp.tempProperty.getTracker(player);
		RenderAssist.drawString("Air Temp: " + tempTrack.GetAirTemp(), 0, 24, Color.WHITE, true);
		RenderAssist.drawString("Core Temp: " + tempTrack.coreTemp, 0, 0, Color.WHITE, true);
		RenderAssist.drawString("Skin Temp: " + tempTrack.skinTemp, 0, 12, Color.WHITE, true);
	}

	@Override
	public void resetDefault()
	{
	}
}
