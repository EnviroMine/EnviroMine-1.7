package enviromine.core.utils;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderAssist
{
	/*
	 * This is a stripped down version of the original RenderAssist. Many of the original methods were never used and probably never would be.
	 * The screen overlay can be done using the standard 'drawTexturedModalRect' method as GuiScreen would do normally.
	 * Also note that color indexes have been replaced with actual objects as Java is perfectly capable of handling this for us.
	 */
	
	public static float zLevel;
	
	public static void drawTexturedModalRect(float x, float y, float u, float v, float width, float height)
	{
		float f = 0.00390625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + height, zLevel, (u + 0) * f, (v + height) * f);
		tessellator.addVertexWithUV(x + width, y + height, zLevel, (u + width) * f, (v + height) * f);
		tessellator.addVertexWithUV(x + width, y + 0, zLevel, (u + width) * f, (v + 0) * f);
		tessellator.addVertexWithUV(x + 0, y + 0, zLevel, (u + 0) * f, (v + 0) * f);
		tessellator.draw();
	}
	
	public static void drawString(String text, int x, int y, Color c, boolean shadow)
	{
		Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, c.getRGB(), shadow);
	}
	
	public static void bindTexture(ResourceLocation res)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(res);
	}
	
	public static void drawRect(float x1, float y1, float x2, float y2, Color c)
	{
		float tmp;
		
		if(x1 < x2)
		{
			tmp = x1;
			x1 = x2;
			x2 = tmp;
		}
		
		if(y1 < y2)
		{
			tmp = y1;
			y1 = y2;
			y2 = tmp;
		}
		
		Tessellator tessellator = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(c.getRed()/255F, c.getGreen()/255F, c.getBlue()/255F, c.getAlpha()/255F);
		tessellator.startDrawingQuads();
		tessellator.addVertex(x1, y2, 0.0D);
		tessellator.addVertex(x2, y2, 0.0D);
		tessellator.addVertex(x2, y1, 0.0D);
		tessellator.addVertex(x1, y1, 0.0D);
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static Color blendColors(Color... colors)
	{
		int r = 0;
		int g = 0;
		int b = 0;
		int a = 0;
		
		for(Color c : colors)
		{
			r += c.getRed();
			g += c.getGreen();
			b += c.getBlue();
			a += c.getAlpha();
		}
		
		r /= colors.length;
		g /= colors.length;
		b /= colors.length;
		a /= colors.length;
		
		return new Color(r, g, b, a);
	}
}
