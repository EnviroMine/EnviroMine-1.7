package enviromine.utils;

import java.awt.Color;
import java.nio.ByteOrder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 
 * Some methods which are usually in GuiIngame, but since we don't have<br>
 * direct access when rendering in HudItem, you may need to use these.
 * 
 * @author maxpowa
 * 
 */
public class RenderAssist {

    /**
     * Controls render "level" for layering textures overtop one another.
     */
    public static float zLevel;

    /** 
     *  Draws an unfilled Circle
     *  
     * @param posX
     * @param posY
     * @param radius
     * @param num_segments
     * @param color
     */
    public static void drawUnfilledCircle(float posX, float posY, float radius, int num_segments, int color) {
        float f = (color >> 24 & 255) / 255.0F;
        float f1 = (color >> 16 & 255) / 255.0F;
        float f2 = (color >> 8 & 255) / 255.0F;
        float f3 = (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        tessellator.startDrawing(GL11.GL_LINE_LOOP);
        for (int i = 0; i < num_segments; i++) {
            double theta = 2.0f * Math.PI * i / num_segments;// get the current
                                                             // angle

            double x = radius * Math.cos(theta);// calculate the x component
            double y = radius * Math.sin(theta);// calculate the y component

            tessellator.addVertex(x + posX, y + posY, 0.0D);// output vertex

        }
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     *  Drawys a filled Circle
     * @param posX
     * @param posY
     * @param radius
     * @param num_segments
     * @param color
     */
    public static void drawCircle(float posX, float posY, float radius, int num_segments, int color) {
        float f = (color >> 24 & 255) / 255.0F;
        float f1 = (color >> 16 & 255) / 255.0F;
        float f2 = (color >> 8 & 255) / 255.0F;
        float f3 = (color & 255) / 255.0F;
        // Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        // GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(posX, posY); // center of circle
        for (int i = num_segments; i >= 0; i--) {
            double theta = i * (Math.PI*2) / num_segments;
            GL11.glVertex2d(posX + radius * Math.cos(theta), posY + radius * Math.sin(theta));
        }
        GL11.glEnd();
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Draws a color rectangle outline with the specified coordinates and color.<br>
     * Color must have all four hex elements (0xFFFFFFFF)
     * 
     * @param x1
     *            First X value
     * @param g
     *            First Y value
     * @param x2
     *            Second X value
     * @param y2
     *            Second Y Value
     */
    public static void drawUnfilledRect(float x1, float g, float x2, float y2, int color) {
        float j1;

        if (x1 < x2) {
            j1 = x1;
            x1 = x2;
            x2 = j1;
        }

        if (g < y2) {
            j1 = g;
            g = y2;
            y2 = j1;
        }

        float f = (color >> 24 & 255) / 255.0F;
        float f1 = (color >> 16 & 255) / 255.0F;
        float f2 = (color >> 8 & 255) / 255.0F;
        float f3 = (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        tessellator.startDrawing(GL11.GL_LINE_LOOP);
        tessellator.addVertex(x1, y2, 0.0D);
        tessellator.addVertex(x2, y2, 0.0D);
        tessellator.addVertex(x2, g, 0.0D);
        tessellator.addVertex(x1, g, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void drawHorizontalLine(int par1, int par2, int par3, int par4) {
        if (par2 < par1) {
            int i1 = par1;
            par1 = par2;
            par2 = i1;
        }

        drawRect(par1, par3, par2 + 1, par3 + 1, par4);
    }

    public static void drawVerticalLine(int par1, int par2, int par3, int par4) {
        if (par3 < par2) {
            int i1 = par2;
            par2 = par3;
            par3 = i1;
        }

        drawRect(par1, par2 + 1, par1 + 1, par3, par4);
    }

    /**
     * Draws a textured rectangle at the stored z-value.
     * 
     * @param x
     *            X-Axis position to render the sprite into the GUI.
     * @param y
     *            Y-Axis position to render the sprite into the GUI.
     * @param u
     *            X-Axis position on the spritesheet which this sprite is found.
     * @param v
     *            Y-Axis position on the spritesheet which this sprite is found.
     * @param width
     *            Width to render the sprite.
     * @param height
     *            Height to render the sprite.
     */
    public static void drawTexturedModalRect(float x, float y, float u, float v, float width, float height) {
        float f = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + height, RenderAssist.zLevel, (u + 0) * f, (v + height) * f);
        tessellator.addVertexWithUV(x + width, y + height, RenderAssist.zLevel, (u + width) * f, (v + height) * f);
        tessellator.addVertexWithUV(x + width, y + 0, RenderAssist.zLevel, (u + width) * f, (v + 0) * f);
        tessellator.addVertexWithUV(x + 0, y + 0, RenderAssist.zLevel, (u + 0) * f, (v + 0) * f);
        tessellator.draw();
    }

    public static void bindTexture(ResourceLocation res) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(res);
    }

    /**
     * Binds a texture, similar to the way renderEngine.bindTexture(String str)
     * used to work.
     * 
     * @param textureLocation
     *            Path to location, you should know how to format this.
     */
    public static void bindTexture(String textureLocation) {
        ResourceLocation res = new ResourceLocation(textureLocation);
        Minecraft.getMinecraft().getTextureManager().bindTexture(res);
    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color.
     * 
     * @param g
     *            First X value
     * @param h
     *            First Y value
     * @param i
     *            Second X value
     * @param j
     *            Second Y Value
     */
    public static void drawRect(float g, float h, float i, float j, int color) {
        float j1;

        if (g < i) {
            j1 = g;
            g = i;
            i = j1;
        }

        if (h < j) {
            j1 = h;
            h = j;
            j = j1;
        }

        float f = (color >> 24 & 255) / 255.0F;
        float f1 = (color >> 16 & 255) / 255.0F;
        float f2 = (color >> 8 & 255) / 255.0F;
        float f3 = (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        tessellator.startDrawingQuads();
        tessellator.addVertex(g, j, 0.0D);
        tessellator.addVertex(i, j, 0.0D);
        tessellator.addVertex(i, h, 0.0D);
        tessellator.addVertex(g, h, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Renders the specified item of the inventory slot at the specified
     * location.
     */
    public static void renderInventorySlot(int slot, int x, int y, float partialTick, Minecraft mc) {
        RenderItem itemRenderer = new RenderItem();
        ItemStack itemstack = mc.thePlayer.inventory.mainInventory[slot];
        x += 91;
        y += 12;

        if (itemstack != null) {
            float f1 = itemstack.animationsToGo - partialTick;

            if (f1 > 0.0F) {
                GL11.glPushMatrix();
                float f2 = 1.0F + f1 / 5.0F;
                GL11.glTranslatef(x + 8, y + 12, 0.0F);
                GL11.glScalef(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
                GL11.glTranslatef(-(x + 8), -(y + 12), 0.0F);
            }

            itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y);

            if (f1 > 0.0F) {
                GL11.glPopMatrix();
            }

            itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y);
        }
    }

	public static int getColorFromRGBA_F(float par1, float par2, float par3, float par4)
	{
		int R = (int)(par1 * 255.0F);
		int G = (int)(par2 * 255.0F);
		int B = (int)(par3 * 255.0F);
		int A = (int)(par4 * 255.0F);
		
		return getColorFromRGBA(R, G, B, A);
	}
	
	/**
	 * 
	 * @param R
	 * @param G
	 * @param B
	 * @param A
	 * @return
	 */
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
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @param ratio
	 * @return
	 */
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
 * 
 * @param width
 * @param height
 * @param color
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
