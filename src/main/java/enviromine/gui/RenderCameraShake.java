package enviromine.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

public class RenderCameraShake extends EntityRenderer
{
	
	private final Minecraft mc;
	private float offsetY = 0.25F; // just for testing, should be based on actual render size
	private double shakeSpeed = 10D;
	
	public RenderCameraShake(Minecraft mc)
	{
		super(mc); //, mc.getResourceManager());
		this.mc = mc;
	}
	
	@Override
	public void updateCameraAndRender(float partialTick)
	{
		if(mc.thePlayer == null || mc.thePlayer.isPlayerSleeping() || !mc.thePlayer.onGround || (mc.currentScreen != null && mc.currentScreen.doesGuiPauseGame()))
		{
			super.updateCameraAndRender(partialTick);
			return;
		}
		
		shakeSpeed = 2D;
		offsetY = 0.2F;
		
		double shake = (int)(mc.theWorld.getTotalWorldTime()%24000L) * shakeSpeed;
		float tempPitch = mc.thePlayer.cameraPitch;
		float tempYaw = mc.thePlayer.cameraYaw;
		
		mc.thePlayer.yOffset -= (Math.sin(shake) * (offsetY/2F)) + (offsetY/2F);
		mc.thePlayer.cameraPitch = (float)(Math.sin(shake) * offsetY/4F);
		mc.thePlayer.cameraYaw = (float)(Math.sin(shake) * offsetY/4F);
		
		super.updateCameraAndRender(partialTick);
		mc.thePlayer.yOffset = 1.62F;
		
	}
	
	@Override
	public void getMouseOver(float partialTick)
	{
		if(mc.thePlayer == null || mc.thePlayer.isPlayerSleeping())
		{
			super.getMouseOver(partialTick);
			return;
		}
		// adjust the y position to get a mouseover at eye-level
		// not perfect, as the server posY does not match, meaning
		// that some block clicks do not process correctly
		// (distance check or something like that)
		/*mc.thePlayer.posY += offsetY;
		mc.thePlayer.prevPosY += offsetY;
		mc.thePlayer.lastTickPosY += offsetY;*/
		super.getMouseOver(partialTick);
		/*mc.thePlayer.posY -= offsetY;
		mc.thePlayer.prevPosY -= offsetY;
		mc.thePlayer.lastTickPosY -= offsetY;*/
	}
	
}
