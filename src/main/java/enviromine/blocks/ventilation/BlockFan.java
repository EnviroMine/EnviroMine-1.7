package enviromine.blocks.ventilation;

import enviromine.blocks.tiles.ventilation.TileEntityFan;
import enviromine.util.Coords;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author thislooksfun
 */
public class BlockFan extends BlockVentBase
{
	public BlockFan(Material mat)
	{
		super(mat);

		//Set bounds slightly farther out than model so you can see the wireframe
		float min = 0.125F;
		float max = 0.875F;
		this.setBlockBounds(min, min, 0F, max, max, 1F);
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
	{
		ForgeDirection facing = ((TileEntityFan)blockAccess.getTileEntity(x, y, z)).facing();
		boolean faceZ = facing.offsetZ != 0;
		boolean faceX = facing.offsetX != 0;
		boolean faceY = facing.offsetY != 0;
		float min = 0.125F;
		float max = 0.875F;
		this.setBlockBounds(faceX ? 0F : min, faceY ? 0F : min, faceZ ? 0F : min, faceX ? 1F : max, faceY ? 1F : max, faceZ ? 1F : max);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item)
	{
		super.onBlockPlacedBy(world, x, y, z, entity, item);
		
		Coords coords = new Coords(world, x, y, z);
		
		((TileEntityFan)coords.getTileEntity()).setFacing(this.getFacing(coords, entity));
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityFan();
	}
}