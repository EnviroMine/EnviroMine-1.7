package enviromine.blocks.ventilation;

import enviromine.blocks.tiles.ventilation.TileEntityFan;
import enviromine.handlers.ObjectHandler;
import enviromine.util.Coords;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockFan extends BlockVentBase
{
	public BlockFan(Material mat)
	{
		super(mat);

		//Set bounds slightly farther out than model so you can see the wireframe
		float min = 0.125F;
		float max = 0.875F;
		this.setBlockBounds(min, min, min, max, max, max);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item)
	{
		super.onBlockPlacedBy(world, x, y, z, entity, item);
		
		Coords coords = new Coords(world, x, y, z);
		
		coords.setBlockWithMetadata(ObjectHandler.fan, this.getFacing(coords, entity));
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityFan();
	}
}