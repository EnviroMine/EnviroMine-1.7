package enviromine.client.renderer.tileentity.ventilation;

import enviromine.blocks.tiles.ventilation.TileEntityVentSmall;
import enviromine.blocks.ventilation.VentDataHandler;
import enviromine.blocks.ventilation.multipart.VentBasePart;
import enviromine.client.model.tileentity.ventilation.ModelVentSmall;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import codechicken.multipart.TileMultipart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class TileEntityVentSmallRenderer extends TileEntitySpecialRenderer
{
	private static final ResourceLocation texture = new ResourceLocation("enviromine", "textures/models/blocks/vent_small.png");
	private ModelVentSmall model = new ModelVentSmall();
	
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float scale)
	{
		VentDataHandler handler;
		if (tileEntity instanceof TileEntityVentSmall) {
			handler = ((TileEntityVentSmall)tileEntity).getHandler();
		} else if (tileEntity instanceof TileMultipart) {
			handler = ((VentBasePart)((TileMultipart)tileEntity).jPartList().get(0)).getHandler();
		} else {
			return;
		}
		
		this.bindTexture(texture);
		
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)x, (float)y + 1.0F, (float)z + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		
		this.model.renderAll(handler, 0.0625F);
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}