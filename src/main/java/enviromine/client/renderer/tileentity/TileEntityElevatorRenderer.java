package enviromine.client.renderer.tileentity;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class TileEntityElevatorRenderer extends TileEntitySpecialRenderer
{
	IModelCustom modelTop;
	IModelCustom modelBottom;
	ResourceLocation mainTexture;
	ResourceLocation recallTexture;
	
	public TileEntityElevatorRenderer()
	{
		modelTop = AdvancedModelLoader.loadModel(new ResourceLocation("enviromine", "models/topblockelevator.obj"));
		modelBottom = AdvancedModelLoader.loadModel(new ResourceLocation("enviromine", "models/bottomblockelevator.obj"));
		mainTexture = new ResourceLocation("enviromine", "textures/models/blocks/elevator_model.png");
		recallTexture = new ResourceLocation("enviromine", "textures/models/blocks/recall_model.png");
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f)
	{
		GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        if(tileentity.getBlockMetadata() >= 2)
        {
        	Minecraft.getMinecraft().renderEngine.bindTexture(recallTexture);
        } else
        {
        	Minecraft.getMinecraft().renderEngine.bindTexture(mainTexture);
        }
        if(tileentity.getBlockMetadata()%2 == 0)
        {
        	modelTop.renderAll();
        } else
        {
        	modelBottom.renderAll();
        }
        GL11.glPopMatrix();
	}
}
