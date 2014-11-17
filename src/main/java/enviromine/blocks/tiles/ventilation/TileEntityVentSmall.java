package enviromine.blocks.tiles.ventilation;

import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityVentSmall extends TileEntityVentBase
{
	@Override
	public boolean allowConnect(ForgeDirection dir)
	{
		return true;
	}
}