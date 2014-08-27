package enviromine.client.renderer.tileentity;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class TileEntityDavyLampRenderer extends TileEntitySpecialRenderer
{
	IModelCustom model;
	ResourceLocation texture;
	
	public TileEntityDavyLampRenderer()
	{
		model = AdvancedModelLoader.loadModel(new ResourceLocation("enviromine", "models/davy_lamp.obj"));
		texture = new ResourceLocation("enviromine", "textures/models/blocks/davy_lamp_model.png");
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f)
	{
		GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        model.renderAll();
        GL11.glPopMatrix();
	}
}
