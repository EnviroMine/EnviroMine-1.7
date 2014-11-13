/**
 * @author thislooksfun
 */

package enviromine.blocks.tiles.ventilation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import enviromine.blocks.ventilation.VentDataHandler;
import enviromine.util.Coords;

public class TileEntityVentBase extends TileEntity implements IPosProvider
{
	protected VentDataHandler handler;
	
	@Override
	public void setWorldObj(World world)
	{
		super.setWorldObj(world);
		if (this.handler == null) {
			this.handler = new VentDataHandler(this);
		}
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
	}
	
	public VentDataHandler getHandler()
	{
		return this.handler;
	}
	
	@Override
	public Coords getCoords()
	{
		return new Coords(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		
		this.handler.load(tag.getCompoundTag("TileEntityData"));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		
		tag.setTag("TileEntityData", this.handler.save());
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tagCompound = new NBTTagCompound();
		this.writeToNBT(tagCompound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tagCompound);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
	{
        this.readFromNBT(packet.func_148857_g());
	}
}