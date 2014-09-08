package enviromine.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

public class TileEntityDavyLampRenderer extends TileEntitySpecialRenderer
{
	IModelCustom model;
	ResourceLocation texOff;
	ResourceLocation[] texLit;
	ResourceLocation[] texGas;
	
	public TileEntityDavyLampRenderer()
	{
		model = AdvancedModelLoader.loadModel(new ResourceLocation("enviromine", "models/davy_lamp.obj"));
		texOff = new ResourceLocation("enviromine", "textures/models/blocks/davy_lamp_model_off.png");
		texLit = new ResourceLocation[]{new ResourceLocation("enviromine", "textures/models/blocks/davy_lamp_model_lit_0.png"), new ResourceLocation("enviromine", "textures/models/blocks/davy_lamp_model_lit_1.png")};
		texGas = new ResourceLocation[]{new ResourceLocation("enviromine", "textures/models/blocks/davy_lamp_model_gas_0.png"), new ResourceLocation("enviromine", "textures/models/blocks/davy_lamp_model_gas_1.png")};
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f)
	{
		int pass = (int)(tileEntity.getWorldObj().getTotalWorldTime()/2%2);
		GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        if(tileEntity.getBlockMetadata() == 1)
        {
        	Minecraft.getMinecraft().renderEngine.bindTexture(texLit[pass]);
        } else if(tileEntity.getBlockMetadata() == 2)
        {
        	Minecraft.getMinecraft().renderEngine.bindTexture(texGas[pass]);
        } else
        {
        	Minecraft.getMinecraft().renderEngine.bindTexture(texOff);
        }
        model.renderAll();
        GL11.glPopMatrix();
	}
}
