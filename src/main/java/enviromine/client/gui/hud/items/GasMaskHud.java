package enviromine.client.gui.hud.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import enviromine.client.gui.UI_Settings;
import enviromine.client.gui.hud.OverlayHandler;
import enviromine.handlers.ObjectHandler;
import enviromine.utils.RenderAssist;

public class GasMaskHud 
{
	public static OverlayHandler maskBreathing = new OverlayHandler(1, true).setPulseVar(111, 200, 0, 2, 4);
    
	public static final ResourceLocation gasMaskResource = new ResourceLocation("enviromine", "textures/misc/maskblur2.png");
    public static final ResourceLocation breathMaskResource = new ResourceLocation("enviromine", "textures/misc/breath.png");
    

    private static int alpha;
    
    public static void renderGasMask(Minecraft mc)
    {
        ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        
    	ItemStack itemstack = mc.thePlayer.inventory.armorItemInSlot(3);
		
		if(itemstack != null && itemstack.getItem() != null)
		{
			if(itemstack.getItem() == ObjectHandler.gasMask)
			{
				
				Renderbreath(scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), mc, itemstack);
				
				if(mc.gameSettings.thirdPersonView == 0)
				{
					mc.renderEngine.bindTexture(gasMaskResource);
					//Draw gasMask Overlay
					RenderAssist.drawScreenOverlay(scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), RenderAssist.getColorFromRGBA(255, 255, 255, 255));
				}
			}
		}
    	
    	
    }
    
    public static void Renderbreath(int screenWidth, int screenHeight, Minecraft mc, ItemStack itemstack)
    {
		mc.renderEngine.bindTexture(breathMaskResource);
		
		if(itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("gasMaskFill"))
		{
			alpha = OverlayHandler.PulseWave(maskBreathing);
			
			if(itemstack.getTagCompound().getInteger("gasMaskFill") <= 20 && mc.gameSettings.thirdPersonView == 0)
			{
			
				RenderAssist.drawScreenOverlay(screenWidth, screenHeight, maskBreathing.getRGBA(alpha));
			}
			
			
			if(maskBreathing.phase == 0)
			{
				if(UI_Settings.breathSound == true)
				{
					mc.thePlayer.playSound("enviromine:gasmask",  UI_Settings.breathVolume, 1.0F);
				}
			}
		}

    }
}
