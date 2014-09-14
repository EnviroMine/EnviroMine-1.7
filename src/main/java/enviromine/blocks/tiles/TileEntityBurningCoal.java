package enviromine.blocks.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBurningCoal extends TileEntity
{
	public int fuel = 10;
	
	public TileEntityBurningCoal()
	{
		this.fuel = 10;
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

    /**
     * Overriden in a sign to provide the text.
     */
    public Packet getDescriptionPacket()
    {
        return null;
    }
}
