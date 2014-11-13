package enviromine.blocks.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBurningCoal extends TileEntity
{
	public int fuel = 100000;
	
	public TileEntityBurningCoal()
	{
		this.fuel = 100000;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tags)
    {
		super.readFromNBT(tags);
		
		tags.setInteger("Fuel", fuel);
    }
	
	@Override
	public void writeToNBT(NBTTagCompound tags)
    {
		super.writeToNBT(tags);
		
		if(tags.hasKey("Fuel"))
		{
			fuel = tags.getInteger("Fuel");
		}
    }

    /**
     * Overridden in a sign to provide the text.
     */
	@Override
    public Packet getDescriptionPacket()
    {
    	NBTTagCompound tags = new NBTTagCompound();
    	this.writeToNBT(tags);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tags);
    }
	
	@Override
	public void onDataPacket(NetworkManager netManager, S35PacketUpdateTileEntity packet)
	{
		this.readFromNBT(packet.func_148857_g());
	}
}
