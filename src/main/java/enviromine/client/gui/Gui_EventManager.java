package enviromine.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.client.gui.hud.HUDRegistry;
import enviromine.client.gui.hud.HudItem;
import enviromine.client.gui.hud.items.Debug_Info;
import enviromine.client.gui.hud.items.GasMaskHud;
import enviromine.client.gui.menu.EM_Gui_Menu;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.handlers.EM_StatusManager;
import enviromine.trackers.EnviroDataTracker;
import enviromine.utils.RenderAssist;
import enviromine.world.ClientQuake;

@SideOnly(Side.CLIENT)
public class Gui_EventManager 
{

	int	width, height;
	
	
	//Render HUD
	
	
	//Render Player
	
	
	// Button Functions
	
	@SubscribeEvent
	public void renderevent(InitGuiEvent.Post event)
	{
		width = event.gui.width;
		height = event.gui.height;

		if(event.gui instanceof GuiIngameMenu)
		{
			try
			{
		        byte b0 = -16;
	   	        event.buttonList.set(1,new GuiButton(4, width / 2 - 100, height / 4 + 0 + b0, I18n.format("menu.returnToGame", new Object[0])));
		        event.buttonList.add(new GuiButton(1348, width / 2 - 100, height / 4 + 24 + b0, StatCollector.translateToLocal("options.enviromine.menu.title")));
	
			}catch(Exception e)
			{
				EnviroMine.logger.log(Level.ERROR, "Error shifting Minecrafts Menu to add in new button: "+ e);
				event.buttonList.add(new GuiButton(1348, width - 175, height  - 30, 160, 20, StatCollector.translateToLocal("options.enviromine.menu.title")));
			}
		}
	}
	@SubscribeEvent
	public void action(ActionPerformedEvent.Post event)
	{
		if(event.gui instanceof GuiIngameMenu)
		{
			if(event.button.id == 1348)
			{
				Minecraft.getMinecraft().displayGuiScreen(new EM_Gui_Menu(event.gui));
			}
	
		}	
	}
	

    
    
	public static int scaleTranslateX, scaleTranslateY;
	
    private Minecraft mc = Minecraft.getMinecraft();
    
	public static final ResourceLocation guiResource = new ResourceLocation("enviromine", "textures/gui/status_Gui.png");
	public static final ResourceLocation blurOverlayResource = new ResourceLocation("enviromine", "textures/misc/blur.png");
	
	public static EnviroDataTracker tracker = null;
    
    
    @SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onGuiRender(RenderGameOverlayEvent.Post event)
	{

		if(event.type != ElementType.HELMET || event.isCancelable())
		{

			return;
		}
		
		mc.thePlayer.yOffset = 1.62F;
		if(ClientQuake.GetQuakeShake(mc.theWorld, mc.thePlayer) > 0)
		{
			if(mc.thePlayer == null || mc.thePlayer.isPlayerSleeping() || !mc.thePlayer.onGround || (mc.currentScreen != null && mc.currentScreen.doesGuiPauseGame()))
			{
				return;
			}
			
			float shakeMult = ClientQuake.GetQuakeShake(mc.theWorld, mc.thePlayer);
			
			double shakeSpeed = 2D * shakeMult;
			float offsetY = 0.2F * shakeMult;
			
			double shake = (int)(mc.theWorld.getTotalWorldTime()%24000L) * shakeSpeed;
			
			mc.thePlayer.yOffset -= (Math.sin(shake) * (offsetY/2F)) + (offsetY/2F);
			mc.thePlayer.cameraPitch = (float)(Math.sin(shake) * offsetY/4F);
			mc.thePlayer.cameraYaw = (float)(Math.sin(shake) * offsetY/4F);
			
			//super.updateCameraAndRender(partialTick);
		}
		
	 	HUDRegistry.checkForResize();


		if(tracker != null && (tracker.trackedEntity == null || tracker.trackedEntity.isDead || tracker.trackedEntity.getHealth() <= 0F) && !tracker.isDisabled)
		{
			EntityPlayer player = EM_StatusManager.findPlayer(this.mc.thePlayer.getCommandSenderName());
			
			if(player != null)
			{
				tracker.trackedEntity = player;
				tracker.isDisabled = false;
				tracker.loadNBTTags();
			} else
			{
				tracker.resetData();
				EM_StatusManager.saveAndRemoveTracker(tracker);
				tracker = null;
			}
		}
		
		if(tracker == null)
		{
			if(!(EM_Settings.enableAirQ == false && EM_Settings.enableBodyTemp == false && EM_Settings.enableHydrate == false && EM_Settings.enableSanity == false))
			{
//				Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("NO ENVIRONMENT DATA", xPos, (height - yPos) - 8, 16777215);
				tracker = EM_StatusManager.lookupTrackerFromUsername(this.mc.thePlayer.getCommandSenderName());
			}
		} else if(tracker.isDisabled || !EM_StatusManager.trackerList.containsValue(tracker))
		{
			tracker = null;
		}
		else
		{

			//ScaledResolution scaleRes = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
			//scaledwidth = scaleRes.getScaledWidth();
			//scaledheight = scaleRes.getScaledHeight();
			
			HudItem.blinkTick++;
	
			// Render GasMask Overlays
			if(UI_Settings.overlay)
			{
				GasMaskHud.renderGasMask(HUDRegistry.screenHeight, HUDRegistry.screenHeight, mc);
			}
						
			// Render Hud Items	
			for (HudItem huditem : HUDRegistry.getActiveHudItemList()) 
			{
				if (mc.playerController.isInCreativeMode() && !huditem.isRenderedInCreative()) 
				{
					continue;
				}

				if (mc.thePlayer.ridingEntity instanceof EntityLivingBase) 
				{
					if (huditem.shouldDrawOnMount()) 
					{
						//	Overlay overlay = OverlayHandler.getHudItemByID(huditem.getOverlayID());
    				
    				
						if(UI_Settings.overlay) 
						{
							RenderAssist.bindTexture(huditem.getResource("TintOverlay"));
							//Minecraft.getMinecraft().renderEngine.bindTexture(huditem.getResource("TintOverlay"));
							huditem.renderScreenOverlay(HUDRegistry.screenHeight, HUDRegistry.screenHeight);
						}
    				
						//Minecraft.getMinecraft().renderEngine.bindTexture(huditem.getResource(""));
						RenderAssist.bindTexture(huditem.getResource(""));
						
						GL11.glPushMatrix();
						
							float transx = (float) ( huditem.posX - (huditem.posX * UI_Settings.guiScale));
							float transy = (float) (huditem.posY - (huditem.posY * UI_Settings.guiScale));

							GL11.glTranslated(transx,transy, 0);
							
							GL11.glScalef((float) UI_Settings.guiScale, (float) UI_Settings.guiScale, (float) UI_Settings.guiScale);
							
							huditem.fixBounds();
								huditem.render();
							
						GL11.glPopMatrix();
					}
				} else 
				{
					if (huditem.shouldDrawAsPlayer()) 
					{

						//Overlay overlay = OverlayHandler.getHudItemByID(huditem.getOverlayID());
    				
						if(UI_Settings.overlay) 
						{
							RenderAssist.bindTexture(huditem.getResource("TintOverlay"));
							//Minecraft.getMinecraft().renderEngine.bindTexture(huditem.getResource("TintOverlay"));
							huditem.renderScreenOverlay(HUDRegistry.screenWidth, HUDRegistry.screenHeight);
						}
    				 		
						RenderAssist.bindTexture(huditem.getResource(""));
						//Minecraft.getMinecraft().renderEngine.bindTexture(huditem.getResource(""));

						
						GL11.glPushMatrix();
						
						float transx = (float) (huditem.posX - (huditem.posX * UI_Settings.guiScale));
						float transy = (float) (huditem.posY - (huditem.posY * UI_Settings.guiScale));

						GL11.glTranslated(transx,transy, 0);

						GL11.glScalef((float) UI_Settings.guiScale, (float) UI_Settings.guiScale, (float) UI_Settings.guiScale);

						huditem.fixBounds();
							huditem.render();
							
							GL11.glTranslated(0, 0, 0);
						GL11.glPopMatrix();
					}
				}
				
				
			}// For hud Items	
    	
			
			Debug_Info.ShowDebugText(event, mc);
		}

	}
    

	
}

