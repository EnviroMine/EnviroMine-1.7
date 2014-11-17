package enviromine.blocks.tiles.ventilation;

import enviromine.util.Coords;

import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityFan extends TileEntityVentBase
{
	private int facing = -1;
	
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
	
	@Override
	public boolean allowConnect(ForgeDirection dir)
	{
		return dir.ordinal() == facing; //TODO
	}
}