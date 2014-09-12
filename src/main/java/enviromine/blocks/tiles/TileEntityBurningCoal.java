package enviromine.blocks.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBurningCoal extends TileEntity
{
	public int fuel = 10;
	
	public TileEntityBurningCoal()
	{
	}
	
	public void readFromNBT(NBTTagCompound tags)
    {
		super.readFromNBT(tags);
		
		tags.setInteger("Fuel", fuel);
    }
	
	public void writeToNBT(NBTTagCompound tags)
    {
		super.writeToNBT(tags);
		
		fuel = tags.getInteger("Fuel");
    }
}
