package enviromine.client.renderer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import org.lwjgl.opengl.GL11;

import enviromine.client.models.ModelPlayerEM;

public class RenderPlayerEM extends RenderPlayer {

	public RenderPlayerEM() 
	{
		super();
		
		this.mainModel = new ModelPlayerEM(0.0F);
		this.modelBipedMain = (ModelPlayerEM) this.mainModel;
		this.modelArmorChestplate = new ModelPlayerEM(1.0F);
		this.modelArmor = new ModelPlayerEM(0.5F);
	
	}

	@Override
	protected void renderModel(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4, float par5, float par6, float par7) 
	{
		super.renderModel(par1EntityLivingBase, par2, par3, par4, par5, par6, par7);

	}

	/*
	@Override
	protected void rotateCorpse(AbstractClientPlayer par1AbstractClientPlayer,	float par2, float par3, float par4) 
	{
		if (par1AbstractClientPlayer.isEntityAlive() && par1AbstractClientPlayer.isPlayerSleeping()) 
		{
			RotatePlayerEvent event = new RotatePlayerEvent(par1AbstractClientPlayer);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.shouldRotate == null || event.shouldRotate) 
			{
				GL11.glRotatef(	par1AbstractClientPlayer.getBedOrientationInDegrees(),	0.0F, 1.0F, 0.0F);
			}
		} else 
		{
			super.rotateCorpse(par1AbstractClientPlayer, par2, par3, par4);
		}
	}

	public static class RotatePlayerEvent extends PlayerEvent 
	{
		public Boolean shouldRotate = null;

		public RotatePlayerEvent(AbstractClientPlayer player) 
		{
			super(player);
		}
	}
*/
}
