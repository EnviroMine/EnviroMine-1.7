package enviromine.client;

import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCamelPack extends ModelBase
{
	private ModelRenderer pack;
	//private static HashMap<Entity, ModelCamelPack> modelMap = new HashMap<Entity, ModelCamelPack>();
	static ModelCamelPack model;
	
	private final ResourceLocation texture = new ResourceLocation("enviromine:textures/models/armor/camelpack.png");
	
	public ModelCamelPack()
	{
		textureWidth = 20;
		textureHeight = 13;
		
		pack = new ModelRenderer(this, 0, 0);
		pack.addBox(-3F, 2F, 2F, 6, 9, 4);
		pack.setRotationPoint(0F, 0F, 0F);
		pack.setTextureSize(64, 32);
		pack.mirror = true;
		setRotation(pack, 0F, 0F, 0F);
	}
	
	public static void RenderPack(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		/*ModelCamelPack model;
		if (!modelMap.containsKey(entity))
		{
			model = new ModelCamelPack();
			modelMap.put(entity, model);
		} else
		{
			model = modelMap.get(entity);
		}*/
		
		if(model == null)
		{
			model = new ModelCamelPack();
		}
		
		model.render(entity, f, f1, f2, f3, f4, f5);
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