package enviromine.client.gui.hud.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import enviromine.EnviroUtils;
import enviromine.client.gui.UI_Settings;
import enviromine.client.gui.hud.OverlayHandler;
import enviromine.handlers.ObjectHandler;


public class GasMaskHud 
{
	public static OverlayHandler maskBreathing = new OverlayHandler(1, true).setPulseVar(111, 200, 0, 2, 4);
    
	public static final ResourceLocation gasMaskResource = new ResourceLocation("enviromine", "textures/misc/maskblur2.png");
    public static final ResourceLocation breathMaskResource = new ResourceLocation("enviromine", "textures/misc/breath.png");
    

    private static int alpha;
    
    public static void renderGasMask(ScaledResolution scaleRes, Minecraft mc)
    {

    	ItemStack itemstack = mc.thePlayer.inventory.armorItemInSlot(3);
		
		if(itemstack != null && itemstack.getItem() != null)
		{
			if(itemstack.getItem() == ObjectHandler.gasMask)
			{
				
				Renderbreath(scaleRes, mc, itemstack);
				
				if(mc.gameSettings.thirdPersonView == 0)
				{
					mc.renderEngine.bindTexture(gasMaskResource);
					//Draw gasMask Overlay
					EnviroUtils.drawScreenOverlay(scaleRes.getScaledWidth(), scaleRes.getScaledHeight(), EnviroUtils.getColorFromRGBA(255, 255, 255, 255));
				}
			}
		}
    	
    	
    }
    
    public static void Renderbreath(ScaledResolution scaleRes, Minecraft mc, ItemStack itemstack)
    {
		mc.renderEngine.bindTexture(breathMaskResource);

		if(maskBreathing.phase == 0)
		{
			if(UI_Settings.breathSound == true)
			{
				mc.thePlayer.playSound("enviromine:gasmask",  UI_Settings.breathVolume, 1.0F);
			}
		}
		
		
		if(itemstack.hasTagCompound() && itemstack.getTagCompound().getInteger("gasMaskFill") <= 20 && mc.gameSettings.thirdPersonView == 0)
		{
			alpha = OverlayHandler.PulseWave(maskBreathing);
			EnviroUtils.drawScreenOverlay(scaleRes.getScaledWidth(), scaleRes.getScaledHeight(), maskBreathing.getRGBA(alpha));
		}

    }
}
