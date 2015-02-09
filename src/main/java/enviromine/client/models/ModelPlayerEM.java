package enviromine.client.models;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import enviromine.handlers.ObjectHandler;
import enviromine.items.EnviroArmor;

public class ModelPlayerEM extends ModelBiped
{

		private ModelRenderer camelPack;
		
		
		private final ResourceLocation texture = new ResourceLocation("enviromine:textures/models/armor/camelpack.png");
		
		public ModelPlayerEM(float var1)
		{
			super(var1);
			
			textureWidth = 20;
			textureHeight = 13;
			
			camelPack = new ModelRenderer(this, 0, 0);
			camelPack.addBox(-3F, 2F, 2F, 6, 9, 4);
			camelPack.setRotationPoint(0F, 0F, 0F);
			camelPack.setTextureSize(64, 32);
			camelPack.mirror = true;

		}
		
	
		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
		{
			 final Class<?> entityClass = EntityClientPlayerMP.class;
			 final Render render = RenderManager.instance.getEntityClassRenderObject(entityClass);
			 final ModelBiped modelBipedMain = ((RenderPlayer) render).modelBipedMain;
		
			 final EntityPlayer player = (EntityPlayer) entity;
			 
			 ItemStack plate = player.getEquipmentInSlot(3);
			 
			 super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

			 if (entity instanceof AbstractClientPlayer && this.equals(modelBipedMain))
			 {
				 if(plate != null)
				 {
					 // CamelPack
					 if(plate.hasTagCompound() && plate.getTagCompound().hasKey("camelPackFill"))
					 {
						 Minecraft.getMinecraft().renderEngine.bindTexture(texture);
						 GL11.glPushMatrix();
						 	// If Armor offset item
						 	//if(plate.getItem() != ObjectHandler.camelPack) { 	this.camelPack.offsetZ = .1f; }
				 			this.camelPack.render(f5);
				 		 GL11.glPopMatrix();
					 }
				 }
	
				 FMLClientHandler.instance().getClient().renderEngine.bindTexture(((AbstractClientPlayer) player).getLocationSkin());
			 }
			 super.render(entity, f, f1, f2, f3, f4, f5);

		}
		
		@Override
		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
		{
			super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
			
			this.camelPack.rotateAngleX = this.bipedBody.rotateAngleX;
			this.camelPack.rotateAngleY = this.bipedBody.rotateAngleY;
			this.camelPack.rotateAngleZ = this.bipedBody.rotateAngleZ;
		}
}
