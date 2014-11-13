package enviromine.blocks.tiles.ventilation;

import net.minecraftforge.common.util.ForgeDirection;

import enviromine.util.Coords;

public class TileEntityFan extends TileEntityVentBase
{
	private int facing;
	
	@Override
	public void updateEntity()
	{
		this.handler.airSpeed = 1;
		super.updateEntity();
		
		Coords intake = this.getCoords().getCoordsOppositeDir(ForgeDirection.getOrientation(facing));
		if (intake.hasTileEntity() && intake.getTileEntity() instanceof TileEntityVentBase)
		{
			this.handler.airTemp = ((TileEntityVentBase)intake.getTileEntity()).getHandler().airTemp;
		}
	}
}