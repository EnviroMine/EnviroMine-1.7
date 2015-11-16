package enviromine.core.api.hud;

import java.awt.Color;
import net.minecraft.util.ResourceLocation;

/**
 * Use this if your HudItem needs to tint the screen
 */
public interface IOverlay
{
	public ResourceLocation getOverlayTexture();
	public Color getOverlayColor();
	public float getOverlayAlpha();
}
