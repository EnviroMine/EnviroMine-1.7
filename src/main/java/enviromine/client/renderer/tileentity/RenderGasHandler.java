package enviromine.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.blocks.BlockGas;
import enviromine.core.EnviroMine;
import enviromine.handlers.ObjectHandler;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderGasHandler implements ISimpleBlockRenderingHandler
{
	private IIcon icon;
	private Tessellator tessellator;
	private int verts;
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		Tessellator tessellator = Tessellator.instance;
		
		float red = 1.0F;
		float green = 1.0F;
		float blue = 1.0F;
		
		if(renderer.useInventoryTint)
		{
			int var6 = block.getRenderColor(metadata);
			
			red = (float)(var6 >> 16 & 255) / 255.0F;
			green = (float)(var6 >> 8 & 255) / 255.0F;
			blue = (float)(var6 & 255) / 255.0F;
		}
		
		renderer.setRenderBoundsFromBlock(block);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		GL11.glColor4f(red, green, blue, 1.0F);
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
		tessellator.draw();
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
		tessellator.draw();
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
		tessellator.draw();
		
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int i, int j, int k, Block oBlock, int modelId, RenderBlocks renderer)
	{
		BlockGas block = (BlockGas)oBlock;
		
		Block sideBlock;
		float sideAlpha;
		
		if(!(blockAccess.getBlock(i, j, k) instanceof BlockGas))
		{
			EnviroMine.logger.log(Level.ERROR, "Trying to render gas without block at position!");
			return false;
		} else if(blockAccess.getTileEntity(i, j, k) == null)
		{
			EnviroMine.logger.log(Level.ERROR, "Trying to render gas without tile at position!");
			return false;
		}
		
		icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : renderer.getBlockIcon(block);
		int brightness = block.getMixedBrightnessForBlock(blockAccess, i, j, k);
		int color = block.colorMultiplier(blockAccess, i, j, k);
		
		float red = (float)((color >> 16) & 0xFF) / 255.0F;
		float green = (float)((color >> 8) & 0xFF) / 255.0F;
		float blue = (float)(color & 0xFF) / 255.0F;
		float alpha = block.getOpacity(blockAccess, i, j, k);
		
		if(alpha <= 0.1F)
		{
			return false;
		}
		
		double minX = 0D;
		double maxX = 1.0D;
		
		double minY = block.getMinY(blockAccess, i, j, k);
		double maxY = block.getMaxY(blockAccess, i, j, k);
		
		double minZ = 0D;
		double maxZ = 1.0D;
		
		double sideMinY;
		double sideMaxY;

		verts = 0;
		tessellator = Tessellator.instance;
		
		tessellator.setBrightness(brightness);
		tessellator.addTranslation((float)i, (float)j, (float)k);
		
		tessellator.setColorRGBA_F(red * 0.9F, green * 0.9F, blue * 0.9F, alpha * 0.9F);
		if(block.shouldSideBeRendered(blockAccess, i, j, k, 2))
		{
			sideBlock = blockAccess.getBlock(i, j, k - 1);
			sideAlpha = sideBlock instanceof BlockGas? ((BlockGas)sideBlock).getOpacity(blockAccess, i, j, k - 1) : 0F;
			
			if(sideAlpha > 0.1F)
			{
				sideMinY = ((BlockGas)sideBlock).getMinY(blockAccess, i, j, k - 1);
				sideMaxY = ((BlockGas)sideBlock).getMaxY(blockAccess, i, j, k - 1);
				
				if((minY <= sideMinY & minY <= sideMaxY & maxY <= sideMinY & maxY <= sideMinY) | (minY >= sideMinY & minY >= sideMaxY & maxY >= sideMinY & maxY >= sideMinY))
				{
					vertexAutoMap(minX, minY, minZ, maxX, minY);
					vertexAutoMap(minX, maxY, minZ, maxX, maxY);
					vertexAutoMap(maxX, maxY, minZ, minX, maxY);
					vertexAutoMap(maxX, minY, minZ, minX, minY);
				} else
				{
					if(minY < sideMinY)
					{
						vertexAutoMap(minX, minY, minZ, maxX, minY);
						vertexAutoMap(minX, sideMinY, minZ, maxX, sideMinY);
						vertexAutoMap(maxX, sideMinY, minZ, minX, sideMinY);
						vertexAutoMap(maxX, minY, minZ, minX, minY);
					}
					
					if(maxY > sideMaxY)
					{
						vertexAutoMap(minX, sideMaxY, minZ, maxX, sideMaxY);
						vertexAutoMap(minX, maxY, minZ, maxX, maxY);
						vertexAutoMap(maxX, maxY, minZ, minX, maxY);
						vertexAutoMap(maxX, sideMaxY, minZ, minX, sideMaxY);
					}
				}
			} else
			{
				vertexAutoMap(minX, minY, minZ, maxX, minY);
				vertexAutoMap(minX, maxY, minZ, maxX, maxY);
				vertexAutoMap(maxX, maxY, minZ, minX, maxY);
				vertexAutoMap(maxX, minY, minZ, minX, minY);
			}
			
		}
		
		if(block.shouldSideBeRendered(blockAccess, i, j, k, 3))
		{
			sideBlock = blockAccess.getBlock(i, j, k + 1);
			sideAlpha = sideBlock instanceof BlockGas? ((BlockGas)sideBlock).getOpacity(blockAccess, i, j, k + 1) : 0F;
			
			if(sideAlpha > 0.1F)
			{
				sideMinY = ((BlockGas)sideBlock).getMinY(blockAccess, i, j, k + 1);
				sideMaxY = ((BlockGas)sideBlock).getMaxY(blockAccess, i, j, k + 1);
				
				if((minY <= sideMinY & minY <= sideMaxY & maxY <= sideMinY & maxY <= sideMinY) | (minY >= sideMinY & minY >= sideMaxY & maxY >= sideMinY & maxY >= sideMinY))
				{
					vertexAutoMap(maxX, minY, maxZ, maxX, minY);
					vertexAutoMap(maxX, maxY, maxZ, maxX, maxY);
					vertexAutoMap(minX, maxY, maxZ, minX, maxY);
					vertexAutoMap(minX, minY, maxZ, minX, minY);
				} else
				{
					if(minY < sideMinY)
					{
						vertexAutoMap(maxX, minY, maxZ, maxX, minY);
						vertexAutoMap(maxX, sideMinY, maxZ, maxX, sideMinY);
						vertexAutoMap(minX, sideMinY, maxZ, minX, sideMinY);
						vertexAutoMap(minX, minY, maxZ, minX, minY);
					}
					
					if(maxY > sideMaxY)
					{
						vertexAutoMap(maxX, sideMaxY, maxZ, maxX, sideMaxY);
						vertexAutoMap(maxX, maxY, maxZ, maxX, maxY);
						vertexAutoMap(minX, maxY, maxZ, minX, maxY);
						vertexAutoMap(minX, sideMaxY, maxZ, minX, sideMaxY);
					}
				}
			} else
			{
				vertexAutoMap(maxX, minY, maxZ, maxX, minY);
				vertexAutoMap(maxX, maxY, maxZ, maxX, maxY);
				vertexAutoMap(minX, maxY, maxZ, minX, maxY);
				vertexAutoMap(minX, minY, maxZ, minX, minY);
			}
		}
		
		if(block.shouldSideBeRendered(blockAccess, i, j, k, 4))
		{
			sideBlock = blockAccess.getBlock(i - 1, j, k);
			sideAlpha = sideBlock instanceof BlockGas? ((BlockGas)sideBlock).getOpacity(blockAccess, i - 1, j, k) : 0F;
			
			if(sideAlpha > 0.1F)
			{
				sideMinY = ((BlockGas)sideBlock).getMinY(blockAccess, i - 1, j, k);
				sideMaxY = ((BlockGas)sideBlock).getMaxY(blockAccess, i - 1, j, k);
				
				if((minY <= sideMinY & minY <= sideMaxY & maxY <= sideMinY & maxY <= sideMinY) | (minY >= sideMinY & minY >= sideMaxY & maxY >= sideMinY & maxY >= sideMinY))
				{
					vertexAutoMap(minX, minY, maxZ, minZ, maxY);
					vertexAutoMap(minX, maxY, maxZ, minZ, minY);
					vertexAutoMap(minX, maxY, minZ, maxZ, minY);
					vertexAutoMap(minX, minY, minZ, maxZ, maxY);
				} else
				{
					if(minY < sideMinY)
					{
						vertexAutoMap(minX, minY, maxZ, minZ, sideMinY);
						vertexAutoMap(minX, sideMinY, maxZ, minZ, minY);
						vertexAutoMap(minX, sideMinY, minZ, maxZ, minY);
						vertexAutoMap(minX, minY, minZ, maxZ, sideMinY);
					}
					
					if(maxY > sideMaxY)
					{
						vertexAutoMap(minX, sideMaxY, maxZ, minZ, maxY);
						vertexAutoMap(minX, maxY, maxZ, minZ, sideMaxY);
						vertexAutoMap(minX, maxY, minZ, maxZ, sideMaxY);
						vertexAutoMap(minX, sideMaxY, minZ, maxZ, maxY);
					}
				}
			} else
			{
				vertexAutoMap(minX, minY, maxZ, minZ, maxY);
				vertexAutoMap(minX, maxY, maxZ, minZ, minY);
				vertexAutoMap(minX, maxY, minZ, maxZ, minY);
				vertexAutoMap(minX, minY, minZ, maxZ, maxY);
			}
		}
		
		if(block.shouldSideBeRendered(blockAccess, i, j, k, 5))
		{
			sideBlock = blockAccess.getBlock(i + 1, j, k);
			sideAlpha = sideBlock instanceof BlockGas? ((BlockGas)sideBlock).getOpacity(blockAccess, i + 1, j, k) : 0F;
			
			if(sideAlpha > 0.1F)
			{
				sideMinY = ((BlockGas)sideBlock).getMinY(blockAccess, i + 1, j, k);
				sideMaxY = ((BlockGas)sideBlock).getMaxY(blockAccess, i + 1, j, k);
				
				if((minY <= sideMinY & minY <= sideMaxY & maxY <= sideMinY & maxY <= sideMinY) | (minY >= sideMinY & minY >= sideMaxY & maxY >= sideMinY & maxY >= sideMinY))
				{
					vertexAutoMap(maxX, minY, minZ, minZ, maxY);
					vertexAutoMap(maxX, maxY, minZ, minZ, minY);
					vertexAutoMap(maxX, maxY, maxZ, maxZ, minY);
					vertexAutoMap(maxX, minY, maxZ, maxZ, maxY);
				} else
				{
					if(minY < sideMinY)
					{
						vertexAutoMap(maxX, minY, minZ, minZ, sideMinY);
						vertexAutoMap(maxX, sideMinY, minZ, minZ, minY);
						vertexAutoMap(maxX, sideMinY, maxZ, maxZ, minY);
						vertexAutoMap(maxX, minY, maxZ, maxZ, sideMinY);
					}
					
					if(maxY > sideMaxY)
					{
						vertexAutoMap(maxX, sideMaxY, minZ, minZ, maxY);
						vertexAutoMap(maxX, maxY, minZ, minZ, sideMaxY);
						vertexAutoMap(maxX, maxY, maxZ, maxZ, sideMaxY);
						vertexAutoMap(maxX, sideMaxY, maxZ, maxZ, maxY);
					}
				}
			} else
			{
				vertexAutoMap(maxX, minY, minZ, minZ, maxY);
				vertexAutoMap(maxX, maxY, minZ, minZ, minY);
				vertexAutoMap(maxX, maxY, maxZ, maxZ, minY);
				vertexAutoMap(maxX, minY, maxZ, maxZ, maxY);
			}
		}
		
		tessellator.setColorRGBA_F(red * 0.8F, green * 0.8F, blue * 0.8F, alpha * 0.9F);
		if(block.shouldSideBeRendered(blockAccess, i, j, k, 0))
		{
			vertexAutoMap(maxX, minY, minZ, maxX, maxZ);
			vertexAutoMap(maxX, minY, maxZ, maxX, minZ);
			vertexAutoMap(minX, minY, maxZ, minX, minZ);
			vertexAutoMap(minX, minY, minZ, minX, maxZ);
		}
		
		tessellator.setColorRGBA_F(red, green, blue, alpha * 0.9F);
		if(block.shouldSideBeRendered(blockAccess, i, j, k, 1))
		{
			vertexAutoMap(maxX, maxY, maxZ, maxX, maxZ);
			vertexAutoMap(maxX, maxY, minZ, maxX, minZ);
			vertexAutoMap(minX, maxY, minZ, minX, minZ);
			vertexAutoMap(minX, maxY, maxZ, minX, maxZ);
		}
		
		tessellator.addTranslation((float)-i, (float)-j, (float)-k);

		if(verts <= 0)
		{
			return false;
		} else
		{
			return true;
		}
	}
	
	private void vertexAutoMap(double x, double y, double z, double u, double v)
	{
		tessellator.addVertexWithUV(x, y, z, icon.getInterpolatedU(u * 16.0D), icon.getInterpolatedV(v * 16.0D));
		verts += 1;
	}
	
	@Override
	public boolean shouldRender3DInInventory(int i)
	{
		return true;
	}
	
	@Override
	public int getRenderId()
	{
		return ObjectHandler.renderGasID;
	}
	
}
