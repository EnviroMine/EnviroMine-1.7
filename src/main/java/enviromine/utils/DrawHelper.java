package enviromine.utils;

import java.awt.Color;
import java.nio.ByteOrder;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DrawHelper 
{
	public static int getColorFromRGBA_F(float par1, float par2, float par3, float par4)
	{
		int R = (int)(par1 * 255.0F);
		int G = (int)(par2 * 255.0F);
		int B = (int)(par3 * 255.0F);
		int A = (int)(par4 * 255.0F);
		
		return getColorFromRGBA(R, G, B, A);
	}
	
	public static int getColorFromRGBA(int R, int G, int B, int A)
	{
		if(R > 255)
		{
			R = 255;
		}
		
		if(G > 255)
		{
			G = 255;
		}
		
		if(B > 255)
		{
			B = 255;
		}
		
		if(A > 255)
		{
			A = 255;
		}
		
		if(R < 0)
		{
			R = 0;
		}
		
		if(G < 0)
		{
			G = 0;
		}
		
		if(B < 0)
		{
			B = 0;
		}
		
		if(A < 0)
		{
			A = 0;
		}
		
		if(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
		{
			return A << 24 | R << 16 | G << 8 | B;
		} else
		{
			return B << 24 | G << 16 | R << 8 | A;
		}
	}
	
	public static Color blendColors(int a, int b, float ratio)
	{
		if(ratio > 1f)
		{
			ratio = 1f;
		} else if(ratio < 0f)
		{
			ratio = 0f;
		}
		float iRatio = 1.0f - ratio;
		
		int aA = (a >> 24 & 0xff);
		int aR = ((a & 0xff0000) >> 16);
		int aG = ((a & 0xff00) >> 8);
		int aB = (a & 0xff);
		
		int bA = (b >> 24 & 0xff);
		int bR = ((b & 0xff0000) >> 16);
		int bG = ((b & 0xff00) >> 8);
		int bB = (b & 0xff);
		
		int A = (int)((aA * iRatio) + (bA * ratio));
		int R = (int)((aR * iRatio) + (bR * ratio));
		int G = (int)((aG * iRatio) + (bG * ratio));
		int B = (int)((aB * iRatio) + (bB * ratio));
		
		return new Color(R, G, B, A);
		//return A << 24 | R << 16 | G << 8 | B;
	}
	
	/**
	 * drawScreenBlur(Width, Height, Image, Alpha, Red, Green, Blue)
	 * Draws Full Screen Screen Overlay
	 */
	@SideOnly(Side.CLIENT)
	public static void drawScreenOverlay(int par1, int par2, int par5)
	{
		float f = (float)(par5 >> 24 & 255) / 255.0F;
		float f1 = (float)(par5 >> 16 & 255) / 255.0F;
		float f2 = (float)(par5 >> 8 & 255) / 255.0F;
		float f3 = (float)(par5 & 255) / 255.0F;
				
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(f1, f2, f3, f);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(0.0D, (double)par2, -90.0D, 0.0D, 1.0D);
		tessellator.addVertexWithUV((double)par1, (double)par2, -90.0D, 1.0D, 1.0D);
		tessellator.addVertexWithUV((double)par1, 0.0D, -90.0D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
		tessellator.draw();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
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
