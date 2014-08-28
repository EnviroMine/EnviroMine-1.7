package enviromine.client.renderer.itemInventory;

import javax.swing.Icon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import enviromine.EnviroUtils;
import enviromine.handlers.ObjectHandler;

public class ArmoredCamelPackRenderer implements IItemRenderer {
    
	private static RenderItem renderItem = new RenderItem();

	public static final ResourceLocation camelpackOverlay = new ResourceLocation("enviromine", "textures/items/camel_pack.png");

	@Override
    public boolean handleRenderType(ItemStack itemStack, ItemRenderType type) 
	{
		return type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) 
    {
            return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data) 
    {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, a renderEffect can derp them up.
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);     
        // ====================== Render item texture ======================
        IIcon icon = itemStack.getIconIndex();
        renderItem.renderIcon(0, 0, icon, 16, 16);

        GL11.glDisable(GL11.GL_BLEND);
		if(itemStack.getItem() == ObjectHandler.camelPack)
		{
	        renderFillBar(itemStack);
		}
		else if (itemStack != null && (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("camelPackFill"))) 
		{
			//model = new ModelCamelPack();
			  
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().renderEngine.bindTexture(camelpackOverlay);
			
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(2, 6, 1, 0, 0);
			tessellator.addVertexWithUV(2, 14, 1, 0, 1);
			tessellator.addVertexWithUV(14, 14, 1, 1, 1);
			tessellator.addVertexWithUV(14, 6, 1, 1, 0);
                	
			tessellator.draw();
			GL11.glDisable(GL11.GL_BLEND);
			
			
	        renderFillBar(itemStack);
			// 	====================== Render text ======================
			// 	GL11.glEnable(GL11.GL_TEXTURE_2D);
			//  	String text = Integer.toString(itemStack.getTagCompound().getInteger("camelPackFill"));
			//    fontRenderer.drawStringWithShadow(text, 1, 1, 0xFFFFFF);
		}
		

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        
    }
    
    public void renderFillBar(ItemStack itemStack)
    {
    	int x = 2;
    	int y = 2;
    	int height = 10;
    	int width = 1;
		int i = itemStack.getTagCompound().getInteger("camelPackFill");
		int max = 100;
		int disp = (i <= 0 ? 0 : i > max ? 100 : (int)(i/(max/100F)));

            double currentFill = itemStack.getTagCompound().getInteger("camelPackFill");
            //int j1 = (int)Math.round(13.0D - disp * 13.0D);
            int j1 = (int)Math.round(((double)height/100) * disp);
            int k = (int)Math.round((255.0D/100) * disp);
           
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            Tessellator tessellator = Tessellator.instance;
            int l = 255 - k << 16 | k << 8;
            int i1 = (255 - k) / 4 << 16 | 16128;
            this.renderQuad(tessellator, 0 + x, 1 + y, width+1, height, EnviroUtils.getColorFromRGBA(172, 172, 172, 255));
            this.renderQuad(tessellator, 0 + x, 0 + y, width, height, EnviroUtils.getColorFromRGBA(42, 85, 210, k));
            this.renderQuad(tessellator, 0 + x, 0 + y, width, height-j1, 0);
            //GL11.glEnable(GL11.GL_BLEND); // Forge: Disable Bled because it screws with a lot of things down the line.
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    private void renderQuad(Tessellator tessellator, int x1, int y1, int width, int height, int p_77017_6_)
    {
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(p_77017_6_);
        tessellator.addVertex((double)(x1 + 0), (double)(y1 + 0), 0.0D);
        tessellator.addVertex((double)(x1 + 0), (double)(y1 + height), 0.0D);
        tessellator.addVertex((double)(x1 + width), (double)(y1 + height), 0.0D);
        tessellator.addVertex((double)(x1 + width), (double)(y1 + 0), 0.0D);
        tessellator.draw();
    }

}
