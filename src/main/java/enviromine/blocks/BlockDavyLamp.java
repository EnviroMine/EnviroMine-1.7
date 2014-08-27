package enviromine.blocks;

import enviromine.blocks.tiles.TileEntityDavyLamp;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDavyLamp extends Block implements ITileEntityProvider
{
	public BlockDavyLamp(Material material)
	{
		super(material);
		this.setHardness(5.0F);
		this.setStepSound(Block.soundTypeMetal);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityDavyLamp();
	}
	
	@Override
	public int getRenderType()
	{
		return -1;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
}
