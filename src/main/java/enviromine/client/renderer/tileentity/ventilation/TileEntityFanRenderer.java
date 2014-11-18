package enviromine.client.renderer.tileentity.ventilation;

import enviromine.blocks.IRotatable;
import enviromine.blocks.tiles.ventilation.TileEntityFan;
import enviromine.blocks.ventilation.VentDataHandler;
import enviromine.blocks.ventilation.multipart.VentBasePart;
import enviromine.client.model.tileentity.ventilation.ModelFan;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import codechicken.multipart.TileMultipart;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * @author thislooksfun
 */
public class TileEntityFanRenderer extends TileEntitySpecialRenderer
{
	private static final ResourceLocation texture = new ResourceLocation("enviromine", "textures/models/blocks/fan.png");
	private ModelFan model = new ModelFan();
	
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float scale)
	{
		VentDataHandler handler;
		if (tileEntity instanceof TileEntityFan)
		{
			handler = ((TileEntityFan)tileEntity).getHandler();
		} else if (tileEntity instanceof TileMultipart)
		{
			handler = ((VentBasePart)((TileMultipart)tileEntity).jPartList().get(0)).getHandler();
		} else
		{
			return;
		}
		
		this.bindTexture(texture);
		
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)x, (float)y + 1.0F, (float)z + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		
		//Rotation start
		ForgeDirection dir = ((IRotatable)handler.provider()).facing();
		
		float rotateX = (dir == ForgeDirection.UP ? -90 : dir == ForgeDirection.DOWN ? 90 : 0);
		float rotateY = (dir == ForgeDirection.NORTH ? 180 : dir == ForgeDirection.EAST ? -90 : (dir == ForgeDirection.WEST ? 90 : 0));
		
		GL11.glRotatef(rotateX, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(rotateY, 0.0F, 1.0F, 0.0F);
		//Rotation end
		
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		
		this.model.renderAll(handler, 0.0625F);
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}