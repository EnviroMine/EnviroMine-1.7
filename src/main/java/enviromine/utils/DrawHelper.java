package enviromine.utils;

import java.awt.Color;
import java.nio.ByteOrder;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DrawHelper 
{

	
	/**
	 * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height, scale(Size) 
	 */
	public static void scaledTexturedModalRect(int x, int y, int u, int v, int width, int height, int scale)
	{
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV((double)(x + 0), (double)(y + (height * scale)), 0, (double)((float)(u + 0) * f), (double)((float)(v + height) * f1));
		tessellator.addVertexWithUV((double)(x + (width * scale)), (double)(y + (height * scale)), 0, (double)((float)(u + width) * f), (double)((float)(v + height) * f1));
		tessellator.addVertexWithUV((double)(x + (width * scale)), (double)(y + 0), 0, (double)((float)(u + width) * f), (double)((float)(v + 0) * f1));
		tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), 0, (double)((float)(u + 0) * f), (double)((float)(v + 0) * f1));
		tessellator.draw();
	}
}
