package enviromine.client.model.tileentity.ventilation;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import enviromine.blocks.ventilation.VentDataHandler;

public class ModelVentSmall extends ModelBase
{
	public ModelRenderer pipeBox;
	
	public ModelRenderer[] connections = new ModelRenderer[6];
	
	public ModelVentSmall()
	{
		this.textureWidth = 64;
		this.textureHeight = 64;

		pipeBox = new ModelRenderer(this, 0, 0);
		pipeBox.setTextureSize(textureWidth, textureHeight);
		pipeBox.addBox(2F, 2F, 2F, 12, 12, 12);
		pipeBox.setRotationPoint(0F, 0F, 0F);
		setRotation(pipeBox, 0F, 0F, 0F);

		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			ModelRenderer tmp = new ModelRenderer(this, 0, 24);
			tmp.setTextureSize(textureWidth, textureHeight);

			int connectSize = 12;
			float offSize = (14F - connectSize);

			int sizeX = dir.offsetX == 0 ? connectSize : 2;
			int sizeY = dir.offsetY == 0 ? connectSize : 2;
			int sizeZ = dir.offsetZ == 0 ? connectSize : 2;

			float offX = dir.offsetX == 0 ? offSize+(-7F*dir.offsetX) : 7F+(-7F*dir.offsetX);
			float offY = dir.offsetY == 0 ? offSize+(7F*dir.offsetY) : 7F+(7F*dir.offsetY);
			float offZ = dir.offsetZ == 0 ? offSize+(7F*dir.offsetZ) : 7F+(7F*dir.offsetZ);

			tmp.addBox(offX, offY, offZ, sizeX, sizeY, sizeZ);

			tmp.setRotationPoint(0F, 0F, 0F);
			setRotation(tmp, 0F, 0F, 0F);

			connections[dir.getOpposite().ordinal()] = tmp;
		}
	}
	
	public void renderAll(VentDataHandler handler, float scale)
	{
		ForgeDirection[] connections = handler.getConnections();
		
		if (connections.length < 6) {
			this.pipeBox.render(scale); //Don't bother to render if covered up by extensions
		}
		
		for (ForgeDirection dir : connections)
		{
			this.connections[dir.ordinal()].render(scale);
		}
	}
	
	private static void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}