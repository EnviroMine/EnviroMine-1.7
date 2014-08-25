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
        
        // ====================== Render item texture ======================
        IIcon icon = itemStack.getIconIndex();
        renderItem.renderIcon(0, 0, icon, 16, 16);

		
		if (itemStack != null && (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("camelPackFill"))) 
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
			
			// 	====================== Render text ======================
			// 	GL11.glEnable(GL11.GL_TEXTURE_2D);
			//  	String text = Integer.toString(itemStack.getTagCompound().getInteger("camelPackFill"));
			//    fontRenderer.drawStringWithShadow(text, 1, 1, 0xFFFFFF);
		}
    }

}
