package enviromine.client.renderer.tileentity.ventilation;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import enviromine.blocks.tiles.ventilation.TileEntityVentSmall;
import enviromine.client.model.tileentity.ventilation.ModelVentSmall;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class TileEntityVentSmallRenderer extends TileEntitySpecialRenderer
{
	private static final ResourceLocation texture = new ResourceLocation("enviromine", "textures/models/blocks/yellow.png"); //TODO
	private ModelVentSmall model = new ModelVentSmall();
	
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float scale)
	{
		TileEntityVentSmall te = (TileEntityVentSmall)tileEntity;
		
		int i = 0;
		
		if (te.hasWorldObj())
        {
            i = te.getBlockMetadata();
        }
		
		this.bindTexture(texture);
		
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)x, (float)y + 1.0F, (float)z + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		
		short rotateX = (short)(i == 2 ? 180 : (i == 4 ? 90 : (i == 5 ? -90 : 0))); //TODO
		
		GL11.glRotatef((float)rotateX, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		
		this.model.renderAll(te, 0.0625F);
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}