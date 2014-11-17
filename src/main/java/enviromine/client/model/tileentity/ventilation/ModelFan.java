package enviromine.client.model.tileentity.ventilation;

import enviromine.blocks.ventilation.VentDataHandler;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelFan extends ModelBase
{
	public ModelRenderer pipeBox;

	public ModelFan()
	{
		this.initModel();
	}

	//TODO REMOVE
	private void initModel()
	{
		this.textureWidth = 64;
		this.textureHeight = 64;

		pipeBox = new ModelRenderer(this, 0, 0);
		pipeBox.setTextureSize(textureWidth, textureHeight);
		pipeBox.addBox(2F, 2F, 0F, 12, 12, 16);
		pipeBox.setRotationPoint(0F, 0F, 0F);
		setRotation(pipeBox, 0F, 0F, 0F);
	}

	public void renderAll(VentDataHandler handler, float scale)
	{
//		this.initModel();

		this.pipeBox.render(scale);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}