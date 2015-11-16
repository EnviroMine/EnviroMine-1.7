package enviromine.core.api.hud;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.utils.Alignment;

public class HUDRegistry
{
	public static final ResourceLocation defOverlay = new ResourceLocation("enviromine", "textures/misc/blur.png");
	protected static HashMap<String,HudItem> hudItemList = new HashMap<String,HudItem>();
	
	public static void registerHudItem(HudItem hudItem)
	{
		ModContainer mod = Loader.instance().activeModContainer();
		
		if(mod == null)
		{
			throw new NullPointerException("Tried to register HudItem without an active mod");
		}
		
		String key = mod.getModId() + ":" + hudItem.ID;
		
		if(hudItemList.containsKey(key) || hudItemList.containsValue(hudItem))
		{
			throw new IllegalArgumentException("Tried to register duplicate HudItem");
		}
		
		hudItemList.put(key, hudItem);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onGuiRender(RenderGameOverlayEvent.Post event)
	{
		if(event.isCanceled())
		{
			return;
		}
		
		HashMap<ResourceLocation, Color> overlayList = new HashMap<ResourceLocation, Color>();
		
		Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledRes = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		
		for(HudItem hud : hudItemList.values())
		{
			if(hud == null || hud.isEnabled())
			{
				continue;
			}
			
            hud.renderHud(event.type, scaledRes.getScaledWidth(), scaledRes.getScaledHeight());
            
            if(hud instanceof IOverlay && event.type == RenderGameOverlayEvent.ElementType.HELMET)
            {
            	IOverlay overlay = (IOverlay)hud;
            	ResourceLocation resource = overlay.getOverlayTexture();
            	resource = resource != null? resource : defOverlay;
            	
            	if(overlayList.containsKey(resource))
            	{
            		Color c1 = overlay.getOverlayColor();
            		Color c2 = overlayList.get(resource);
            		Color c3 = new Color(c1.getRed()/2F + c2.getRed()/2F, c1.getGreen()/2F + c2.getGreen()/2F, c1.getBlue()/2F + c2.getBlue()/2F, Math.max(overlay.getOverlayAlpha(), c2.getAlpha()/255F));
            		overlayList.put(resource, c3);
            	} else
            	{
            		Color c1 = overlay.getOverlayColor();
            		overlayList.put(resource, new Color(c1.getRed()/255F, c1.getGreen()/255F, c1.getBlue()/255F, overlay.getOverlayAlpha()));
            	}
            }
		}
		
		for(Entry<ResourceLocation,Color> entry : overlayList.entrySet())
		{
			/* 
			 * TODO: Draw overlays
			 * Draw overlays here. Overlay alpha has been mixed into the alpha channel of the color
			 * Use scaledRes to get the size of the area to stretch the texture over
			 */
		}
	}
	
	public static ArrayList<HudItem> getHudItems()
	{
		return new ArrayList<HudItem>(hudItemList.values());
	}
	
	// This can be toggled through the item specific configuration screen or programmatically using 'isEnabled'
	/*public static void enableHudItem(HudItem hudItem) 
	{
	    if (hudItemList.contains(hudItem) && !hudItemListActive.contains(hudItem)) 
	    {
	    	//System.out.println(hudItem.getName() +" is Active now");
	        hudItemListActive.add(hudItem);
	    }
	}*/
	
	public static HudItem getHudItemByID(int id)
	{
		return hudItemList.get(id);
	}
	
	public static void resetAllDefaults()
	{
		for(HudItem huditem : hudItemList.values())
		{
			if(huditem instanceof IRotate)
				((IRotate)huditem).setRotated(false);
			
			huditem.alignment = Alignment.TOPLEFT;//huditem.getDefaultAlignment();
			huditem.posX = 16;//huditem.getDefaultPosX();
			huditem.posY = 16;//huditem.getDefaultPosY();
		}
	}
	
	public static void readFromNBT(NBTTagCompound nbt)
	{
		for(Entry<String,HudItem> entry : hudItemList.entrySet())
		{
			entry.getValue().loadFromNBT(nbt.getCompoundTag(entry.getKey()));
		}
	}
	
	public static void writeToNBT(NBTTagCompound nbt)
	{
		for(Entry<String,HudItem> entry : hudItemList.entrySet())
		{
			NBTTagCompound hudTag = new NBTTagCompound();
			entry.getValue().saveToNBT(hudTag);
			nbt.setTag(entry.getKey(), hudTag);
		}
	}
}