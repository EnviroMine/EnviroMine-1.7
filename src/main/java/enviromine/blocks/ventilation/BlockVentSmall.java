package enviromine.blocks.ventilation;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import enviromine.blocks.tiles.ventilation.TileEntityVentSmall;

public class BlockVentSmall extends BlockVentBase
{
	public BlockVentSmall(Material p_i45394_1_)
	{
		super(p_i45394_1_);
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityVentSmall();
	}
	
	@Override
	public void updateBounds(TileEntity te)
	{
		ForgeDirection[] dirs = ((TileEntityVentSmall)te).getHandler().getConnections();
		
		boolean up = false;
		boolean down = false;
		boolean north = false;
		boolean south = false;
		boolean east = false;
		boolean west = false;
		for (ForgeDirection dir : dirs)
		{
			switch (dir) {
				case UP:
					up = true;
					break;
				case DOWN:
					down = true;
					break;
				case NORTH:
					north = true;
					break;
				case SOUTH:
					south = true;
					break;
				case EAST:
					east = true;
					break;
				case WEST:
					west = true;
					break;
				case UNKNOWN:
					break;
			}
		}
		
		float min1 = 0.125F; //Not connected
		float min2 = 0F; //Connected
		float max1 = 0.875F; //Not connected
		float max2 = 1F; //Connected
		this.setBlockBounds(west ? min2 : min1, down ? min2 : min1, north ? min2 : min1, east ? max2 : max1, up ? max2 : max1, south ? max2 : max1);
	}
}