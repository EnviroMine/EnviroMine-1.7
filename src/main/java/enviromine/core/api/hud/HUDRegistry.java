package enviromine.core.api.hud;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.lwjgl.opengl.GL11;
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
import enviromine.core.api.hud.HudItem.Align;
import enviromine.core.utils.RenderAssist;

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
			if(hud == null || !hud.isEnabled() || !hud.canRenderOnLayer(event.type))
			{
				continue;
			}
			
            hud.preRenderHud(event.type, scaledRes.getScaledWidth(), scaledRes.getScaledHeight());
            
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
		
		// --- DRAW TINT OVERLAYS ---
		
		float scaleX = scaledRes.getScaledWidth()/256F;
		float scaleY = scaledRes.getScaledHeight()/256F;
		
		GL11.glPushMatrix();
		
		GL11.glScalef(scaleX, scaleY, 1F);
		
		for(Entry<ResourceLocation,Color> entry : overlayList.entrySet())
		{
			Color c = entry.getValue();
			ResourceLocation r = entry.getKey();
			
			if(c == null || r == null || c.getAlpha() <= 0)
			{
				continue;
			}
			
			RenderAssist.bindTexture(r);
			GL11.glColor4f(c.getRed()/255F, c.getGreen()/255F, c.getBlue()/255F, c.getAlpha()/255F);
			RenderAssist.drawTexturedModalRect(0, 0, 0, 0, 256, 256);
		}
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		GL11.glPopMatrix();
	}
	
	public static ArrayList<HudItem> getAllHudItems()
	{
		return new ArrayList<HudItem>(hudItemList.values());
	}
	
	public static ArrayList<HudItem> getActiveHudItems()
	{
		ArrayList<HudItem> list = new ArrayList<HudItem>();
		
		for(HudItem hud : hudItemList.values())
		{
			if(hud != null && hud.isEnabled())
			{
				list.add(hud);
			}
		}
		
		return list;
	}
	
	public static HudItem getHudItemByID(int id)
	{
		return hudItemList.get(id);
	}
	
	/**
	 * Arranges all HUD items in the bottom corners of the screen
	 */
	public static void autoArrange()
	{
		int i = 0;
		
		int[] y = new int[]{16, 16};
		
		for(HudItem hud : hudItemList.values())
		{
			if(hud == null)
			{
				continue;
			}
			
			if(i%2 == 0)
			{
				hud.offsetX = 16;
				hud.offsetY = y[0];
				hud.alignment = Align.BOT_LEFT;
				y[0] += hud.getHeight() + 4;
			} else
			{
				hud.offsetX = 16;
				hud.offsetY = y[1];
				hud.alignment = Align.BOT_RIGHT;
				y[1] += hud.getHeight() + 4;
			}
			
			i++;
		}
	}
	
	public static void resetAllDefaults()
	{
		for(HudItem hud : hudItemList.values())
		{
			if(hud == null)
			{
				continue;
			}
			
			hud.resetDefault();
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