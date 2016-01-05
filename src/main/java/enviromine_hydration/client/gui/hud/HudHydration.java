package enviromine_hydration.client.gui.hud;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import enviromine.core.api.hud.HudItem;
import enviromine.core.utils.RenderAssist;
import enviromine_hydration.core.EnviroHydration;
import enviromine_hydration.properties.TrackerHydration;

public class HudHydration extends HudItem
{
	public HudHydration(String ID)
	{
		super(ID);
	}

	@Override
	public String getUnlocalizedName() 
	{
		return "enviro_hydration.property.hydration.name";
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
		TrackerHydration tempTrack = (TrackerHydration)EnviroHydration.hydrationProperty.getTracker(player);
		RenderAssist.drawString("Hydration: " + tempTrack.hydration, 0, 24, Color.WHITE, true);
	}

	@Override
	public void resetDefault()
	{
	}
}
