package enviromine.client.gui.hud.items;

import java.math.BigDecimal;
import java.math.RoundingMode;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.client.gui.Gui_EventManager;
import enviromine.client.gui.UI_Settings;
import enviromine.core.EM_Settings;
import enviromine.utils.EnviroUtils;

public class Debug_Info 
{

	public static float DB_bodyTemp = 0;
	public static float DB_abientTemp = 0;
	public static float DB_sanityrate = 0;
	public static float DB_airquality = 0;
	
	public static float DB_tempchange = 0;
	
	public static float DB_cooling = 0;
	public static float DB_dehydrateRate = 0;
	
	public static String DB_timer = "";
	public static String DB_physTimer = "";
	public static int DB_physUpdates = 0;
	public static int DB_physBuffer = 0;
	public static String DB_gasTimer = "";
	public static int DB_gasUpdates = 0;
	public static int DB_gasBuffer = 0;
	public static int DB_gasfireBuffer = 0;
	
	public static String DB_biomeName = "";
	public static int DB_biomeID = 0;
	
	@SideOnly(Side.CLIENT)
	public static void ShowDebugText(RenderGameOverlayEvent event , Minecraft mc)
	{
		if(event.type != ElementType.HELMET || event.isCancelable())
		{
			return;
		}
		
		if(!UI_Settings.ShowDebug || mc.gameSettings.showDebugInfo)
		{
			return;
		}
		
		try
		{
			DB_abientTemp = Gui_EventManager.tracker.airTemp;
			DB_biomeName = Gui_EventManager.tracker.trackedEntity.worldObj.getBiomeGenForCoords(MathHelper.floor_double(Gui_EventManager.tracker.trackedEntity.posX), MathHelper.floor_double(Gui_EventManager.tracker.trackedEntity.posZ)).biomeName;
			DB_biomeID = Gui_EventManager.tracker.trackedEntity.worldObj.getBiomeGenForCoords(MathHelper.floor_double(Gui_EventManager.tracker.trackedEntity.posX), MathHelper.floor_double(Gui_EventManager.tracker.trackedEntity.posZ)).biomeID;
			DB_tempchange = new BigDecimal(String.valueOf(Gui_EventManager.tracker.bodyTemp - Gui_EventManager.tracker.prevBodyTemp)).setScale(3, RoundingMode.HALF_UP).floatValue();
			DB_sanityrate = new BigDecimal(String.valueOf(Gui_EventManager.tracker.sanity - Gui_EventManager.tracker.prevSanity)).setScale(3, RoundingMode.HALF_UP).floatValue();
			DB_airquality = new BigDecimal(String.valueOf(Gui_EventManager.tracker.airQuality - Gui_EventManager.tracker.prevAirQuality)).setScale(3, RoundingMode.HALF_UP).floatValue();
			DB_dehydrateRate = new BigDecimal(String.valueOf(Gui_EventManager.tracker.hydration - Gui_EventManager.tracker.prevHydration)).setScale(3, RoundingMode.HALF_UP).floatValue();
			
			if(UI_Settings.useFarenheit == true)
			{
				Minecraft.getMinecraft().fontRenderer.drawString("Body Temp: " + EnviroUtils.convertToFarenheit(Gui_EventManager.tracker.bodyTemp) + "F", 10, 10, 16777215);
				Minecraft.getMinecraft().fontRenderer.drawString("Ambient Temp: " + EnviroUtils.convertToFarenheit(DB_abientTemp) + "F | Cur Biome: " + DB_biomeName + " (" + DB_biomeID + ")", 10, 10 * 2, 16777215);
				Minecraft.getMinecraft().fontRenderer.drawString("Temp Rate: " + EnviroUtils.convertToFarenheit(DB_tempchange) + "F", 10, 10 * 3, 16777215);
				
			} else
			{
				Minecraft.getMinecraft().fontRenderer.drawString("Body Temp: " + Gui_EventManager.tracker.bodyTemp + "C", 10, 10, 16777215);
				Minecraft.getMinecraft().fontRenderer.drawString("Ambient Temp: " + DB_abientTemp + "C (" + (DB_abientTemp + 12F) + "C) | Cur Biome: " + DB_biomeName + " (" + DB_biomeID + ")", 10, 10 * 2, 16777215);
				Minecraft.getMinecraft().fontRenderer.drawString("Temp Rate: " + DB_tempchange + "C", 10, 10 * 3, 16777215);
			}
			
			Minecraft.getMinecraft().fontRenderer.drawString("Sanity Rate: " + DB_sanityrate + "%", 10, 10 * 4, 16777215);
			Minecraft.getMinecraft().fontRenderer.drawString("Air Quality Rate: " + DB_airquality + "%", 10, 10 * 5, 16777215);
			Minecraft.getMinecraft().fontRenderer.drawString("Dehydration Rate: " + DB_dehydrateRate + "%", 10, 10 * 6, 16777215);
			Minecraft.getMinecraft().fontRenderer.drawString("Status Update Speed: " + DB_timer, 10, 10 * 8, 16777215);
			//Minecraft.getMinecraft().fontRenderer.drawString("The Thing: " + tracker.trackedEntity.getEntityData().getInteger("EM_THING"), 10, 10 * 12, 16777215);
		} catch(NullPointerException e)
		{
			
		}
		
		if(EM_Settings.enablePhysics)
		{
			Minecraft.getMinecraft().fontRenderer.drawString("Physics Update Speed: " + DB_physTimer, 10, 10 * 9, 16777215);
			Minecraft.getMinecraft().fontRenderer.drawString("No. Physics Updates: " + DB_physUpdates, 10, 10 * 10, 16777215);
			Minecraft.getMinecraft().fontRenderer.drawString("No. Buffered Updates: " + DB_physBuffer, 10, 10 * 11, 16777215);
		}
		
		Minecraft.getMinecraft().fontRenderer.drawString("Gas Update Speed: " + DB_gasTimer, 10, 10 * 12, 16777215);
		Minecraft.getMinecraft().fontRenderer.drawString("No. Gas Updates: " + DB_gasUpdates, 10, 10 * 13, 16777215);
		Minecraft.getMinecraft().fontRenderer.drawString("No. Buffered Normal Gas Updates: " + DB_gasBuffer, 10, 10 * 14, 16777215);
		Minecraft.getMinecraft().fontRenderer.drawString("No. Buffered Burning Gas Updates: " + DB_gasfireBuffer, 10, 10 * 15, 16777215);
	}
}
