package enviromine.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
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
	
	int width, height;
	
	//Render HUD
	
	//Render Player
	
	// Button Functions
	GuiButton enviromine;
	
	// Captures the initiation of vanilla menus to render new buttons
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void renderevent(InitGuiEvent.Post event)
	{
		width = event.gui.width;
		height = event.gui.height;
		
		if(event.gui instanceof GuiIngameMenu)
		{
			String newPost = UpdateNotification.isNewPost() ? " " + StatCollector.translateToLocal("news.enviromine.newpost") : "";
			
			try
			{
				byte b0 = -16;
				enviromine = new GuiButton(1348, width / 2 - 100, height / 4 + 24 + b0, StatCollector.translateToLocal("options.enviromine.menu.title") + newPost);
				
				event.buttonList.set(1, new GuiButton(4, width / 2 - 100, height / 4 + 0 + b0, I18n.format("menu.returnToGame", new Object[0])));
				event.buttonList.add(enviromine);
				
			} catch(Exception e)
			{
				enviromine = new GuiButton(1348, width - 175, height - 30, 160, 20, StatCollector.translateToLocal("options.enviromine.menu.title") + newPost);
				EnviroMine.logger.log(Level.ERROR, "Error shifting Minecrafts Menu to add in new button: " + e);
				event.buttonList.add(enviromine);
			}
		}
	}
	
	// Used to capture when an Enviromine button is hit in a vanilla menu
	@SubscribeEvent
	public void action(ActionPerformedEvent.Post event)
	{
		if(event.gui instanceof GuiIngameMenu)
		{
			if(event.button.id == enviromine.id)
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
	
	/**
	 * All Enviromine Gui and Hud Items will render here
	 * @param event
	 */
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
			} else
			{
				float shakeMult = ClientQuake.GetQuakeShake(mc.theWorld, mc.thePlayer);
				
				double shakeSpeed = 2D * shakeMult;
				float offsetY = 0.2F * shakeMult;
				
				double shake = (int)(mc.theWorld.getTotalWorldTime() % 24000L) * shakeSpeed;
				
				mc.thePlayer.yOffset -= (Math.sin(shake) * (offsetY / 2F)) + (offsetY / 2F);
				mc.thePlayer.cameraPitch = (float)(Math.sin(shake) * offsetY / 4F);
				mc.thePlayer.cameraYaw = (float)(Math.sin(shake) * offsetY / 4F);
			}
		}
		
		HUDRegistry.checkForResize();
		
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
		} else
		{
			
			HudItem.blinkTick++;
			
			// Render GasMask Overlays
			if(UI_Settings.overlay)
			{
				GasMaskHud.renderGasMask(mc);
			}
			
			// Render Hud Items	
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_LIGHTING);
	        GL11.glColor4f(1F, 1F, 1F, 1F);
			for(HudItem huditem : HUDRegistry.getActiveHudItemList())
			{
				if(mc.playerController.isInCreativeMode() && !huditem.isRenderedInCreative())
				{
					continue;
				}
				
				if(mc.thePlayer.ridingEntity instanceof EntityLivingBase)
				{
					if(huditem.shouldDrawOnMount())
					{
						if(UI_Settings.overlay)
						{
							RenderAssist.bindTexture(huditem.getResource("TintOverlay"));
							huditem.renderScreenOverlay(HUDRegistry.screenWidth, HUDRegistry.screenHeight);
						}
						
						RenderAssist.bindTexture(huditem.getResource(""));
						
						//float transx = (float)(huditem.posX - (huditem.posX * UI_Settings.guiScale));
						//float transy = (float)(huditem.posY - (huditem.posY * UI_Settings.guiScale));
						
						//GL11.glTranslated(transx, transy, 0);
						
						//GL11.glScalef((float)UI_Settings.guiScale, (float)UI_Settings.guiScale, (float)UI_Settings.guiScale);
						
						huditem.fixBounds();
						huditem.render();
						
						
					}
				} else
				{
					if(huditem.shouldDrawAsPlayer())
					{
						if(UI_Settings.overlay)
						{
							RenderAssist.bindTexture(huditem.getResource("TintOverlay"));
							huditem.renderScreenOverlay(HUDRegistry.screenWidth, HUDRegistry.screenHeight);
						}
						
						RenderAssist.bindTexture(huditem.getResource(""));
						
						//float transx = (float)(huditem.posX - (huditem.posX * UI_Settings.guiScale));
						//float transy = (float)(huditem.posY - (huditem.posY * UI_Settings.guiScale));
						
						//GL11.glTranslated(transx, transy, 0);
						
						//GL11.glScalef((float)UI_Settings.guiScale, (float)UI_Settings.guiScale, (float)UI_Settings.guiScale);
						
						huditem.fixBounds();
						huditem.render();
						
						//GL11.glTranslated(0, 0, 0);
						
					}
				}
				
			}
			Debug_Info.ShowDebugText(event, mc);
			GL11.glPopMatrix();
		}
		
	}
	
}
