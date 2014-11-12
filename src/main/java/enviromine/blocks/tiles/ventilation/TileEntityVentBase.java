/**
 * @author thislooksfun
 */

package enviromine.blocks.tiles.ventilation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import enviromine.blocks.ventilation.BlockVentBase;
import enviromine.util.Coords;
import enviromine.util.Utils;

public class TileEntityVentBase extends TileEntity
{
	/** The air temperature in this pipe */
	protected int airTemp = 12;
	/** The air speed in this pipe (0-1) */
	protected float airSpeed = 0F;
	/** The insulation of this pipe (0-1)<br>0 will be instantly set to the ambient temp<br>1 will never be affected by external forces */
	protected float insulation = 0F;
	/** The percentage of air that will leak out every tick (0-1)<br>0 is no leakage<br>1 is full leakage - none transmitted */
	protected float leakageMultiplier = 0F;
	/** The state this pipe is in (0-1)<br>0 is completely broken, and will be removed next tick<br>1 is fully repaired */
	protected float repair;
	
	/** The sides that are connected to other pipes */
	private ForgeDirection[] connections;
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
	}
	
	public int getTemp()
	{
		return this.airTemp;
	}
	
	public Coords getCoords()
	{
		return new Coords(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
	}
	
	public ForgeDirection[] getConnections()
	{
		if (this.connections != null)
		{
			return this.connections;
		}
		
		return new ForgeDirection[0];
	}
	
	public void calculateConnections()
	{
		this.calculateConnections(ForgeDirection.UNKNOWN);
	}
	
	public void calculateConnections(ForgeDirection removeDir)
	{
		Coords coords = this.getCoords();
		
		this.connections = new ForgeDirection[0];
		
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			if (dir != removeDir.getOpposite() && coords.getCoordsInDir(dir).getBlock() instanceof BlockVentBase)
			{
				this.connections = Utils.append(this.connections, dir);
			}
		}
		
		if (!this.worldObj.isRemote) {
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		
		int[] conns = nbt.getIntArray("Connections");
		
		this.connections = new ForgeDirection[conns.length];
		
		for (int i = 0; i < conns.length; i++)
		{
			this.connections[i] = ForgeDirection.getOrientation(conns[i]);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		
		int[] conns = new int[this.connections.length];
		
		for (int i = 0; i < connections.length; i++)
		{
			conns[i] = this.connections[i].ordinal();
		}
		
		nbt.setIntArray("Connections", conns);
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