package enviromine.blocks.ventilation;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import enviromine.blocks.tiles.ventilation.TileEntityVentSmall;

public class BlockVentSmall extends BlockVentBase
{
	public BlockVentSmall(Material p_i45394_1_)
	{
		super(p_i45394_1_);
		
		//Set bounds slightly farther out than model so you can see the wireframe
		float min = 0.125F;
		float max = 0.875F;
		this.setBlockBounds(min, min, min, max, max, max);
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityVentSmall();
	}
}