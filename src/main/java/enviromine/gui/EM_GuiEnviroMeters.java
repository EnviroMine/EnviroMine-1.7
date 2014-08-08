package enviromine.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import enviromine.EnviroUtils;
import enviromine.core.EM_Settings;
import enviromine.handlers.EM_StatusManager;
import enviromine.handlers.ObjectHandler;
import enviromine.trackers.EnviroDataTracker;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.lwjgl.opengl.GL11;

public class EM_GuiEnviroMeters extends Gui
{
	public Minecraft mc;
	public IResourceManager resourceManager;
	
	public static final String guiResource = "textures/gui/status_Gui.png";
	public static final ResourceLocation gasMaskResource = new ResourceLocation("enviromine", "textures/misc/maskblur2.png");
	public static final ResourceLocation breathMaskResource = new ResourceLocation("enviromine", "textures/misc/breath.png");
	public static final ResourceLocation bloodshotResource = new ResourceLocation("enviromine", "textures/misc/bloodshot.png");
	public static final ResourceLocation blurOverlayResource = new ResourceLocation("enviromine", "textures/misc/blur.png");
	
	public static final int meterWidth = 96;
	public static final int meterHeight = 8;
	public static int barWidth = 64;
	public static final int textWidth = 32;
	public static final int iconWidth = 16;
	
	private static int ticktimer = 1;
	private static boolean blink = false;
	
	EntityRenderer preRender = null;
	RenderCameraShake camShake = null;
	
	public static EnviroDataTracker tracker = null;
	
	public EM_GuiEnviroMeters(Minecraft mc, IResourceManager resManager)
	{
		this.mc = mc;
		this.resourceManager = resManager;
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onGuiRender(RenderGameOverlayEvent.Post event)
	{
		if(this.camShake == null)
		{
			this.camShake = new RenderCameraShake(this.mc, this.resourceManager);
		}
		
		if(this.mc.entityRenderer != camShake)
		{
			this.preRender = this.mc.entityRenderer;
			this.mc.entityRenderer = camShake;
		}
		
		if(event.type != ElementType.HELMET || event.isCancelable())
		{
			return;
		}
		
		// count gui ticks
		if(ticktimer >= 60)
		{
			blink = !blink;
			ticktimer = 1;
		} else
		{
			ticktimer++;
		}
		
		if(EM_Settings.minimalHud)
		{
			barWidth = 0;
			EM_Settings.ShowText = true;
			EM_Settings.ShowGuiIcons = true;
		} else
		{
			barWidth = 64;
		}
		
		int xPos = 4;
		int yPos = 4;
		
		ScaledResolution scaleRes = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		int scaledwidth = scaleRes.getScaledWidth();
		int scaledheight = scaleRes.getScaledHeight();
		
		// Rend Mask Overlays
		RenderOverlays(scaledwidth, scaledheight);
		
		// GUI Scaling Code 
		GL11.glPushMatrix(); // Isolate this GUI from the vanilla GUI
		float scale = EM_Settings.guiScale;
		
		double translate = new BigDecimal(String.valueOf(1 / scale)).setScale(3, RoundingMode.HALF_UP).doubleValue();
		
		GL11.glScalef((float)scale, (float)scale, (float)scale);
		
		int width = MathHelper.ceiling_float_int((float)(scaledwidth * translate));
		int height = MathHelper.ceiling_float_int((float)(scaledheight * translate));
		
		//		int width = MathHelper.ceiling_float_int((float)(scaleRes.getScaledWidth() * translate));
		//		int height = MathHelper.ceiling_float_int((float)(scaleRes.getScaledHeight() * translate));
		// End of scaling Code
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		if(tracker == null)
		{
			if(!(EM_Settings.enableAirQ == false && EM_Settings.enableBodyTemp == false && EM_Settings.enableHydrate == false && EM_Settings.enableSanity == false))
			{
				Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("NO ENVIRONMENT DATA", xPos, (height - yPos) - 8, 16777215);
				tracker = EM_StatusManager.lookupTrackerFromUsername(this.mc.thePlayer.getCommandSenderName());
			}
		} else if(tracker.isDisabled)
		{
			tracker = null;
		} else
		{
			int waterBar = MathHelper.ceiling_float_int((tracker.hydration / 100) * barWidth);
			int heatBar = MathHelper.ceiling_float_int(((tracker.bodyTemp + 50) / 150) * barWidth);
			int preheatBar = MathHelper.ceiling_float_int(((tracker.airTemp + 50) / 150) * barWidth);
			int preheatIco = 16- MathHelper.ceiling_float_int(((tracker.airTemp + 50) / 150) * 16);
			int sanityBar = MathHelper.ceiling_float_int((tracker.sanity / 100) * barWidth);
			int airBar = MathHelper.ceiling_float_int((tracker.airQuality / 100) * barWidth);
			
			float dispHeat = new BigDecimal(String.valueOf(tracker.bodyTemp)).setScale(2, RoundingMode.DOWN).floatValue();
			float FdispHeat = new BigDecimal(String.valueOf((tracker.bodyTemp * 1.8) + 32)).setScale(2, RoundingMode.DOWN).floatValue();
			float dispSanity = new BigDecimal(String.valueOf(tracker.sanity)).setScale(2, RoundingMode.DOWN).floatValue();
			
			if(waterBar > barWidth)
			{
				waterBar = barWidth;
			} else if(waterBar < 0)
			{
				waterBar = 0;
			}
			
			if(heatBar > barWidth)
			{
				heatBar = barWidth;
			} else if(heatBar < 0)
			{
				heatBar = 0;
			}
			
			if(preheatBar > barWidth)
			{
				preheatBar = barWidth;
			} else if(preheatBar < 0)
			{
				preheatBar = 0;
			}
			
			if(preheatIco > 24)
			{
				preheatIco = 24;
			} else if(preheatIco < 0)
			{
				preheatIco = 0;
			}
			
			if(sanityBar > barWidth)
			{
				sanityBar = barWidth;
			} else if(sanityBar < 0)
			{
				sanityBar = 0;
			}
			
			if(airBar > barWidth)
			{
				airBar = barWidth;
			} else if(airBar < 0)
			{
				airBar = 0;
			}
			
			this.mc.renderEngine.bindTexture(new ResourceLocation("enviromine", guiResource));
			
			// Static Vars for Bar Positions..
			
			int Top_Left_X = xPos;
			int Top_Left_Y = yPos;
			
			int Top_Right_X = (width - xPos) - barWidth;
			int Top_Right_Y = yPos;
			
			int Top_Center_X = (width / 2) - (barWidth / 2);
			int Top_Center_Y = yPos;
			
			int Middle_Left_X = xPos;
			int Middle_Left_Y = (height/2 + meterHeight*2) - yPos;
			
			int Middle_Right_X = (width - xPos) - barWidth;
			int Middle_Right_Y = (height/2 + meterHeight*2) - yPos;
			
			int Bottom_Center_Left_X = (width / 2) - (barWidth / 2) - barWidth;
			int Bottom_Center_Left_Y = (height - yPos) - 50;
			
			int Bottom_Center_Right_X = (width / 2) - (barWidth / 2) + barWidth;
			int Bottom_Center_Right_Y = (height - yPos) - 50;
			
			int Bottom_Left_X = xPos;
			int Bottom_Left_Y = (height - yPos);
			
			int Bottom_Right_X = (width - xPos) - barWidth;
			int Bottom_Right_Y = (height - yPos);
			
			//EM_Settings.ShowText = false;
			
			// Add Bars to String Array for looping
			String[] barPos = new String[4];
			barPos[0] = EM_Settings.sanityBarPos;
			barPos[1] = EM_Settings.oxygenBarPos;
			barPos[2] = EM_Settings.waterBarPos;
			barPos[3] = EM_Settings.heatBarPos;
			
			boolean[] barTrue = new boolean[4];
			barTrue[0] = EM_Settings.enableSanity;
			barTrue[1] = EM_Settings.enableAirQ;
			barTrue[2] = EM_Settings.enableHydrate;
			barTrue[3] = EM_Settings.enableBodyTemp;
			
			// Cnt for Each section of screen
			int BL = -1;
			int BR = -1;
			int BCR = -1;
			int BCL = -1;
			int TL = -1;
			int TR = -1;
			int TC = -1;
			int ML = -1;
			int MR = -1;
			int addTW = 0;
			int AQcurX = 0;
			int AQcurY = 0;
			int HTcurX = 0;
			int HTcurY = 0;
			int SAcurX = 0;
			int SAcurY = 0;
			int WAcurX = 0;
			int WAcurY = 0;
			int textPos = 0;
			int iconPos = 0;
			//Draw bars Pos Based on Settings
			for(int i = 0; i <= barPos.length - 1; i++)
			{
				int curMeterHeight = 0;
				int curPosX = 0;
				int curPosY = 0;
				int frameborder = 4;
				
				if(EM_Settings.ShowText == true)
				{
					addTW = 1;
				}
				if(!(barTrue[i]))
				{
					if(i <= 2)
						i += 1;
					else
						break;
				}
				
				String barPosName = barPos[i].toLowerCase().trim();
				
				if(barPosName.equalsIgnoreCase("top_left"))
				{
					TL += 2;
					curMeterHeight = meterHeight * TL;
					curPosX = Top_Left_X;
					curPosY = Top_Left_Y + curMeterHeight;
					textPos = Top_Left_X + barWidth;
					iconPos = textPos + (textWidth * addTW);
				} else if(barPosName.equalsIgnoreCase("top_right"))
				{
					TR += 2;
					curMeterHeight = meterHeight * TR;
					curPosX = Top_Right_X;
					curPosY = Top_Right_Y + curMeterHeight;
					textPos = Top_Right_X - (textWidth * addTW);
					iconPos = textPos - iconWidth;
				} else if(barPosName.equalsIgnoreCase("top_center"))
				{
					TC += 2;
					curMeterHeight = meterHeight * TC;
					curPosX = Top_Center_X;
					curPosY = Top_Center_Y + curMeterHeight;
					textPos = Top_Center_X + barWidth;
					iconPos = textPos + (textWidth * addTW);
				} else if(barPosName.equalsIgnoreCase("bottom_left"))
				{
					BL += 2;
					curMeterHeight = meterHeight * BL;
					curPosX = Bottom_Left_X;
					curPosY = Bottom_Left_Y - curMeterHeight;
					textPos = Bottom_Left_X + barWidth;
					iconPos = textPos + (textWidth * addTW);
				} else if(barPosName.equalsIgnoreCase("bottom_right"))
				{
					BR += 2;
					curMeterHeight = meterHeight * BR;
					curPosX = Bottom_Right_X;
					curPosY = Bottom_Right_Y - curMeterHeight;
					textPos = Bottom_Right_X - (textWidth * addTW);
					iconPos = textPos - iconWidth;
				} else if(barPosName.equalsIgnoreCase("bottom_center_right"))
				{
					BCR += 2;
					curMeterHeight = meterHeight * BCR;
					curPosX = Bottom_Center_Right_X;
					curPosY = Bottom_Center_Right_Y - curMeterHeight;
					textPos = Bottom_Center_Right_X + barWidth;
					iconPos = textPos + (textWidth * addTW);
				} else if(barPosName.equalsIgnoreCase("bottom_center_left"))
				{
					BCL += 2;
					curMeterHeight = meterHeight * BCL;
					curPosX = Bottom_Center_Left_X;
					curPosY = Bottom_Center_Left_Y - curMeterHeight;
					textPos = Bottom_Center_Left_X - (textWidth * addTW);
					iconPos = textPos - iconWidth;
				} else if(barPosName.equalsIgnoreCase("middle_right"))
				{
					MR += 2;
					curMeterHeight = meterHeight * MR;
					curPosX = Middle_Right_X;
					curPosY = Middle_Right_Y - curMeterHeight;
					textPos = Middle_Right_X - (textWidth * addTW);
					iconPos = textPos - iconWidth;
				} else if(barPosName.equalsIgnoreCase("middle_left"))
				{
					ML += 2;
					curMeterHeight = meterHeight * ML;
					curPosX = Middle_Left_X;
					curPosY = Middle_Left_Y - curMeterHeight;
					textPos = Middle_Left_X + barWidth;
					iconPos = textPos + (textWidth * addTW);
				} else if(barPosName.startsWith("custom_"))
				{
					barPosName = barPosName.replaceFirst("custom_", "").trim();
					String pos[] = barPosName.split(",");
					if(pos.length == 2)
					{
						try
						{
							int cX = Integer.parseInt(pos[0].trim());
							int cY = Integer.parseInt(pos[1].trim());
							
							if(cX < 0)
							{
								cX = 0;
							} else if(cX > 100)
							{
								cX = 100;
							}
							
							if(cY < 0)
							{
								cY = 0;
							} else if(cY > 100)
							{
								cY = 100;
							}
							
							curPosY = MathHelper.floor_float(cY/100F * (float)height);
							curPosX = MathHelper.floor_float(cX/100F * (float)width);
							
							if(cX > 50)
							{
								curPosX -= (barWidth);
								textPos = curPosX - (textWidth * addTW);
								iconPos = textPos - iconWidth;
							} else
							{
								textPos = curPosX + barWidth;
								iconPos = textPos + (textWidth * addTW);
							}
						} catch (NumberFormatException e)
						{
						}
					} else
					{
					}
				}
				
				// 0 = Sanity Bar
				if(i == 0 && EM_Settings.enableSanity == true)
				{
					SAcurX = textPos;
					SAcurY = curPosY;
					
					this.drawTexturedModalRect(curPosX, curPosY, 0, 16, barWidth, meterHeight);
					this.drawTexturedModalRect(curPosX, curPosY, 64, 16, sanityBar, meterHeight);
					if(!EM_Settings.minimalHud)
					{
						this.drawTexturedModalRect(curPosX + sanityBar - 2, curPosY + 2, 28, 64, 4, 4);
					}
					
					// sanity frame
					if(blink && tracker.sanity < 25)
					{
						frameborder = 5;
					}
					
					if(barWidth > 0)
					{
						this.drawTexturedModalRect(curPosX, curPosY, 0, meterHeight * frameborder, meterWidth - 32, meterHeight);
					}
					
					if(EM_Settings.ShowGuiIcons == true)
						this.drawTexturedModalRect(iconPos, SAcurY - 4, 32, 80, 16, 16);
				}
				
				// 1 = Air Quality Bar
				else if(i == 1 && EM_Settings.enableAirQ == true)
				{
					AQcurX = textPos;
					AQcurY = curPosY;
					
					this.drawTexturedModalRect(curPosX, curPosY, 0, 8, barWidth, meterHeight);
					this.drawTexturedModalRect(curPosX, curPosY, 64, 8, airBar, meterHeight);
					if(!EM_Settings.minimalHud)
					{
						this.drawTexturedModalRect(curPosX + airBar - 2, curPosY + 2, 8, 64, 4, 4);
					}
					
					// oxygen frame
					if(blink && tracker.airQuality < 25)
					{
						frameborder = 5;
					}
					
					if(barWidth > 0)
					{
						this.drawTexturedModalRect(curPosX, curPosY, 0, meterHeight * frameborder, meterWidth - 32, meterHeight);
					}
					
					if(EM_Settings.ShowGuiIcons == true)
						this.drawTexturedModalRect(iconPos, AQcurY - 4, 48, 80, 16, 16);
					
				}
				// 2 = Water Bar
				else if(i == 2 && EM_Settings.enableHydrate == true)
				{
					WAcurX = textPos;
					WAcurY = curPosY;
					//water bar
					this.drawTexturedModalRect(curPosX, curPosY, 0, 0, barWidth, meterHeight);
					this.drawTexturedModalRect(curPosX, curPosY, 64, 0, waterBar, meterHeight);
					if(!EM_Settings.minimalHud)
					{
						this.drawTexturedModalRect(curPosX + waterBar - 2, curPosY + 2, 16, 64, 4, 4);
					}
					
					//EnviroUtils.scaledTexturedModalRect(curPosX, curPosY, 0, 0, barWidth, meterHeight, 1);
					//EnviroUtils.scaledTexturedModalRect(curPosX, curPosY, 64, 0, waterBar, meterHeight, 1);
					//EnviroUtils.scaledTexturedModalRect(curPosX + waterBar - 2, curPosY, 16, 64, 4, 4, 1);
					
					// water frame
					
					if(blink && tracker.hydration < 25)
					{
						frameborder = 5;
					}
					
					if(barWidth > 0)
					{
						this.drawTexturedModalRect(curPosX, curPosY, 0, meterHeight * frameborder, meterWidth - 32, meterHeight);
					}
					
					if(EM_Settings.ShowGuiIcons == true)
					{
						this.drawTexturedModalRect(iconPos, WAcurY - 4, 16, 80, 16, 16);
					}
					
				}
				// 3 = Heat Bar
				else if(i == 3 && EM_Settings.enableBodyTemp == true)
				{
					HTcurX = textPos;
					HTcurY = curPosY;
					
					// heat Bar
					this.drawTexturedModalRect(curPosX, curPosY, 0, 24, barWidth, meterHeight);
					if(!EM_Settings.minimalHud)
					{
						this.drawTexturedModalRect(curPosX + preheatBar - 4, curPosY, 32, 64, 8, 8);
						this.drawTexturedModalRect(curPosX + heatBar - 2, curPosY + 2, 20, 64, 4, 4);
					}
					
					// heat frame
					if(blink && tracker.bodyTemp < 35 || blink && tracker.bodyTemp > 39)
					{
						frameborder = 5;
					}
					
					if(barWidth > 0)
					{
						this.drawTexturedModalRect(curPosX, curPosY, 0, meterHeight * frameborder, meterWidth - 32, meterHeight);
					}
					
					if(EM_Settings.ShowGuiIcons == true)
					{
						this.drawTexturedModalRect(iconPos, HTcurY - 4, 0, 80, 16, 16);
						if(preheatIco >= 8)
						{
							this.drawTexturedModalRect(iconPos, HTcurY - 4 + preheatIco, 16, 96 + preheatIco, 16, 16-preheatIco);
						} else
						{
							this.drawTexturedModalRect(iconPos, HTcurY - 4 + preheatIco, 0, 96 + preheatIco, 16, 16-preheatIco);
						}
					}
				}
				
			}
			
			// Display Debugging Text
			if(EM_Settings.ShowText == true)
			{
				if(EM_Settings.enableAirQ)
				{
					this.mc.renderEngine.bindTexture(new ResourceLocation("enviromine", guiResource));
					this.drawTexturedModalRect(AQcurX, AQcurY, 64, meterHeight * 4, 32, meterHeight);
					Minecraft.getMinecraft().fontRenderer.drawString(tracker.airQuality + "%", AQcurX, AQcurY, 16777215);
				}
				
				if(EM_Settings.enableBodyTemp)
				{
					this.mc.renderEngine.bindTexture(new ResourceLocation("enviromine", guiResource));
					this.drawTexturedModalRect(HTcurX, HTcurY, 64, meterHeight * 4, 32, meterHeight);
					if(EM_Settings.useFarenheit == true)
					{
						Minecraft.getMinecraft().fontRenderer.drawString( FdispHeat + "F", HTcurX, HTcurY, 16777215);
					} else
					{
						Minecraft.getMinecraft().fontRenderer.drawString(dispHeat + "C", HTcurX, HTcurY, 16777215);
					}
				}
				
				if(EM_Settings.enableHydrate)
				{
					this.mc.renderEngine.bindTexture(new ResourceLocation("enviromine", guiResource));
					//this.drawTexturedModalRect(WAcurX, WAcurY, 64, meterHeight * 4, 32, meterHeight);
					this.drawTexturedModalRect(WAcurX, WAcurY, 64, meterHeight * 4, 32, meterHeight);
					Minecraft.getMinecraft().fontRenderer.drawString(tracker.hydration + "%", WAcurX, WAcurY, 16777215);
				}
				
				if(EM_Settings.enableSanity)
				{
					
					this.mc.renderEngine.bindTexture(new ResourceLocation("enviromine", guiResource));
					this.drawTexturedModalRect(SAcurX, SAcurY, 64, meterHeight * 4, 32, meterHeight);
					Minecraft.getMinecraft().fontRenderer.drawString(dispSanity + "%", SAcurX, SAcurY, 16777215);
					
				}
				}
			}
			
		GL11.glPopMatrix();
		
		ShowDebugText(event);
	}
	
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
	
	public static String DB_biomeName = "";
	
	@SideOnly(Side.CLIENT)
	private void ShowDebugText(RenderGameOverlayEvent event)
	{
		if(event.type != ElementType.HELMET || event.isCancelable())
		{
			return;
		}
		
		if(!EM_Settings.ShowDebug || this.mc.gameSettings.showDebugInfo)
		{
			return;
		}
		
		try
		{
			DB_abientTemp = tracker.airTemp;
			DB_biomeName = tracker.trackedEntity.worldObj.getBiomeGenForCoords(MathHelper.floor_double(tracker.trackedEntity.posX), MathHelper.floor_double(tracker.trackedEntity.posZ)).biomeName;
			DB_tempchange = new BigDecimal(String.valueOf(tracker.bodyTemp - tracker.prevBodyTemp)).setScale(3, RoundingMode.HALF_UP).floatValue();
			DB_sanityrate = new BigDecimal(String.valueOf(tracker.sanity - tracker.prevSanity)).setScale(3, RoundingMode.HALF_UP).floatValue();
			DB_airquality = new BigDecimal(String.valueOf(tracker.airQuality - tracker.prevAirQuality)).setScale(3, RoundingMode.HALF_UP).floatValue();
			DB_dehydrateRate = new BigDecimal(String.valueOf(tracker.hydration - tracker.prevHydration)).setScale(3, RoundingMode.HALF_UP).floatValue();
			
			if(EM_Settings.useFarenheit == true)
			{
				Minecraft.getMinecraft().fontRenderer.drawString("Body Temp: " + ((tracker.bodyTemp * 1.8) + 32F) + "F", 10, 10, 16777215);
				Minecraft.getMinecraft().fontRenderer.drawString("Ambient Temp: " + ((DB_abientTemp * 1.8) + 32F) + "F | Cur Biome: " + DB_biomeName, 10, 10 * 2, 16777215);
				Minecraft.getMinecraft().fontRenderer.drawString("Temp Rate: " + ((DB_tempchange * 1.8) + 32F) + "F", 10, 10 * 3, 16777215);
				
			} else
			{
				Minecraft.getMinecraft().fontRenderer.drawString("Body Temp: " + tracker.bodyTemp + "C", 10, 10, 16777215);
				Minecraft.getMinecraft().fontRenderer.drawString("Ambient Temp: " + DB_abientTemp + "C | Cur Biome: " + DB_biomeName, 10, 10 * 2, 16777215);
				Minecraft.getMinecraft().fontRenderer.drawString("Temp Rate: " + DB_tempchange + "C", 10, 10 * 3, 16777215);
			}
			
			Minecraft.getMinecraft().fontRenderer.drawString("Sanity Rate: " + DB_sanityrate + "%", 10, 10 * 4, 16777215);
			Minecraft.getMinecraft().fontRenderer.drawString("Air Quality Rate: " + DB_airquality + "%", 10, 10 * 5, 16777215);
			Minecraft.getMinecraft().fontRenderer.drawString("Dehydration Rate: " + DB_dehydrateRate + "%", 10, 10 * 6, 16777215);
			Minecraft.getMinecraft().fontRenderer.drawString("Status Update Speed: " + DB_timer, 10, 10 * 8, 16777215);
			Minecraft.getMinecraft().fontRenderer.drawString("The Thing: " + tracker.trackedEntity.getEntityData().getInteger("EM_THING"), 10, 10 * 12, 16777215);
		} catch(NullPointerException e)
		{
			
		}
		
		if(EM_Settings.enablePhysics)
		{
			Minecraft.getMinecraft().fontRenderer.drawString("Physics Update Speed: " + DB_physTimer, 10, 10 * 9, 16777215);
			Minecraft.getMinecraft().fontRenderer.drawString("No. Physics Updates: " + DB_physUpdates, 10, 10 * 10, 16777215);
			Minecraft.getMinecraft().fontRenderer.drawString("No. Buffered Updates: " + DB_physBuffer, 10, 10 * 11, 16777215);
		}
	}
	
	public void RenderOverlays(int width, int height)
	{
		if(tracker != null)
		{
			this.mc.renderEngine.bindTexture(blurOverlayResource);
			
			if(tracker.bodyTemp >= 39)
			{
				int grad = 0;
				if(tracker.bodyTemp >= 41F)
				{
					grad = 210;
				} else
				{
					grad = (int)((1F - (Math.abs(3 - (tracker.bodyTemp - 39)) / 3)) * 96);
				}
				EnviroUtils.drawScreenOverlay(width, height, EnviroUtils.getColorFromRGBA(255, 255, 255, grad));
				
			} else if(tracker.bodyTemp <= 35)
			{
				int grad = 0;
				if(tracker.bodyTemp <= 32F)
				{
					grad = 210;
				} else
				{
					grad = (int)((Math.abs(3 - (tracker.bodyTemp - 32)))) * 64;
				}
				EnviroUtils.drawScreenOverlay(width, height, EnviroUtils.getColorFromRGBA(125, 255, 255, grad));
			}
			if(tracker.airQuality < 50F)
			{
				int grad = (int)((50 - tracker.airQuality) / 15 * 64);
				EnviroUtils.drawScreenOverlay(width, height, EnviroUtils.getColorFromRGBA(32, 96, 0, grad));
			}
			if(tracker.sanity < 50F)
			{
				int grad = (int)((50 - tracker.sanity) / 15 * 64);
				EnviroUtils.drawScreenOverlay(width, height, EnviroUtils.getColorFromRGBA(200, 0, 249, grad));
			}
		}
		
		boolean infection = false;
		if(infection && this.mc.gameSettings.thirdPersonView == 0)
		{
			int A = (int) RenderPulse();
			EnviroUtils.drawScreenOverlay(width, height, EnviroUtils.getColorFromRGBA(220, 3, 3, A));
			
			//this.mc.renderEngine.bindTexture(bloodshotResource);
			//EnviroUtils.drawScreenOverlay(width, height, EnviroUtils.getColorFromRGBA(255, 255, 255, 100));
		}
		
		ItemStack itemstack = this.mc.thePlayer.inventory.armorItemInSlot(3);
		
		if(itemstack != null && itemstack.getItem() != null)
		{
			if(itemstack.getItem() == ObjectHandler.gasMask)
			{
				
				Renderbreath(width, height, itemstack);
				
				if(this.mc.gameSettings.thirdPersonView == 0)
				{
					this.mc.renderEngine.bindTexture(gasMaskResource);
					//Draw gasMask Overlay
					EnviroUtils.drawScreenOverlay(width, height, EnviroUtils.getColorFromRGBA(255, 255, 255, 255));
				}
			}
		}
		
	}
	
	
	int pulseStart = 0;
	boolean pulseDown = true;
	float pulseAlpha = 0;
	
	
	public float RenderPulse()
	{
		if(tracker == null)
		{
			return 0;
		} else
		{
			if(pulseStart <= 100)
			{
				pulseStart++;
				
			} else if(pulseDown)
			{
				pulseAlpha += 2.5F;
			} else
				//Exhale
			{
				pulseAlpha -= 2.01F;
			}
		
			if(pulseAlpha >= 250)
			{
				pulseDown = false;
				pulseAlpha = 250;
			} else if(pulseAlpha < 0F)
			{
				pulseStart = 0;
				pulseDown = true;
				pulseAlpha = 0F;
			}
	
		}
		return pulseAlpha;
	}	
	
	
	
	
	
	
	boolean exhale = false;
	float alpha = 0;
	boolean pause = false;
	int pauseCnt = 0;
	
	public void Renderbreath(int k, int l, ItemStack itemstack)
	{
		
		if(tracker == null)
		{
			return;
		} else
		{
			
			if(pause)
			{
				pauseCnt++;
				if(pauseCnt >= EM_Settings.breathPause)
				{
					pauseCnt = 0;
					pause = false;
					
					if(EM_Settings.breathSound == true)
					{
						//ISound sound = null; //TODO ("enviromine:gasmask", (float)player.posX, (float)player.posY, (float)player.posZ, EM_Settings.breathVolume, 1.0F)
						//mc.getSoundHandler().playSound(sound);
					}
				}
				return;
			} else if(exhale)
			{
				alpha += 2.5F;
			} else
			//Exhale
			{
				alpha -= 2.01F;
			}
			
			if(alpha >= 191)
			{
				exhale = false;
				alpha = 191F;
			} else if(alpha <= 0F)
			{
				pause = true;
				exhale = true;
				alpha = 0F;
			}
			
			// If Item is Damaged Render Breath onscreen
			if(itemstack.getItemDamage() >= itemstack.getMaxDamage() - 1 && this.mc.gameSettings.thirdPersonView == 0)
			{
				this.mc.renderEngine.bindTexture(breathMaskResource);
				enviromine.EnviroUtils.drawScreenOverlay(k, l, EnviroUtils.getColorFromRGBA(255, 255, 255, (int)alpha));
			}
		}
	}
	
}
