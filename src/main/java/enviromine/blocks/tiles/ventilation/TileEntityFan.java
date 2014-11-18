package enviromine.blocks.tiles.ventilation;

import enviromine.blocks.IRotatable;
import enviromine.util.Coords;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author thislooksfun
 */
public class TileEntityFan extends TileEntityVentBase implements IRotatable
{
	private ForgeDirection facing = ForgeDirection.UNKNOWN;
	
	@Override
	public void updateEntity()
	{
		this.handler.airSpeed = 1;
		super.updateEntity();
		
		Coords intake = this.getCoords().getCoordsOppositeDir(facing);
		if (intake.hasTileEntity() && intake.getTileEntity() instanceof TileEntityVentBase)
		{
			this.handler.airTemp = ((TileEntityVentBase)intake.getTileEntity()).getHandler().airTemp;
		}
	}
	
	@Override
	public boolean allowConnect(ForgeDirection dir)
	{
		return dir == this.facing || dir.getOpposite() == this.facing;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		
		tag.setInteger("facing", this.facing.ordinal());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		
		this.facing = ForgeDirection.getOrientation(tag.getInteger("facing"));
	}
	
	@Override
	public ForgeDirection facing()
	{
		return this.facing;
	}
	
	@Override
	public void setFacing(ForgeDirection dir)
	{
		this.facing = dir;
	}
	
	@Override
	public void rotate()
	{
		this.facing = ForgeDirection.getOrientation(this.facing == ForgeDirection.EAST ? 0 : this.facing.ordinal()+1);
		this.getCoords().notifyNeighbors();
	}
}