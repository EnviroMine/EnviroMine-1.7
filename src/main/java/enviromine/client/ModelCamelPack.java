package enviromine.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCamelPack extends ModelBase
{
	private ModelRenderer pack;
	
	private final ResourceLocation texture = new ResourceLocation("enviromine:textures/models/armor/camelpack.png");
	
	public ModelCamelPack()
	{
		textureWidth = 64;
		textureHeight = 32;
		
		pack = new ModelRenderer(this, 0, 0);
		pack.addBox(-4F, 1F, 1F, 7, 10, 3);
		pack.setRotationPoint(0F, 0F, 0F);
		pack.setTextureSize(64, 32);
		pack.mirror = true;
		setRotation(pack, 0F, 0F, 0F);
	}
	
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);	
		pack.render(f5);
	}
	
	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
	{
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}
	
}