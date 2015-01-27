package enviromine.client.renderer.tileentity;

import java.util.HashMap;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.blocks.tiles.*;
import enviromine.handlers.ObjectHandler;

@SideOnly(Side.CLIENT)
public class RenderSpecialHandler implements ISimpleBlockRenderingHandler
{
	static HashMap<Block, TileEntity> blockToTile = new HashMap<Block, TileEntity>();
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
		if(blockToTile.containsKey(block))
		{
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            if(!blockToTile.get(block).hasWorldObj())
            {
            	blockToTile.get(block).setWorldObj(renderer.minecraftRB.theWorld);
            }
            
            if(blockToTile.get(block).blockType == null)
            {
            	blockToTile.get(block).blockType = block;
            }
            blockToTile.get(block).blockMetadata = metadata;
			TileEntityRendererDispatcher.instance.renderTileEntityAt(blockToTile.get(block), 0.0D, 0.0D, 0.0D, 0.0F);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return ObjectHandler.renderSpecialID;
	}
	
	{
		blockToTile.put(ObjectHandler.esky, new TileEntityEsky());
		blockToTile.put(ObjectHandler.freezer, new TileEntityFreezer());
		blockToTile.put(ObjectHandler.elevator, new TileEntityElevator());
	}
}
