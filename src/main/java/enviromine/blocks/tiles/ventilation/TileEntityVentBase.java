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

import enviromine.util.Coords;
import enviromine.util.Utils;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;

public class TileEntityVentBase extends TileEntity implements IVentTileBase
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
	private ForgeDirection[] connections = new ForgeDirection[0];
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
	}
	
	@Override
	public int getTemp()
	{
		return this.airTemp;
	}
	
	@Override
	public Coords getCoords()
	{
		return new Coords(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
	}
	
	@Override
	public ForgeDirection[] getConnections()
	{
		return this.connections;
	}
	
	@Override
	public void calculateConnections()
	{
		this.calculateConnections(ForgeDirection.UNKNOWN);
	}
	
	@Override
	public void calculateConnections(ForgeDirection removeDir)
	{
		Coords coords = this.getCoords();
		
		this.connections = new ForgeDirection[0];
		
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			Coords pos = coords.getCoordsInDir(dir);
			
			if (dir == removeDir.getOpposite() || coords.isBlockSideSolid(dir) || pos.isBlockSideSolid(dir.getOpposite()))
			{
				continue;
			}
			
			if (pos.hasTileEntity())
			{
				TileEntity tileEntity = pos.getTileEntity();
				
				if (tileEntity instanceof TileEntityVentBase) {
					this.connections = Utils.append(this.connections, dir);
				} else if (tileEntity instanceof TileMultipart) {
					TMultiPart part = ((TileMultipart)tileEntity).jPartList().get(0);
					if (part instanceof IVentTileBase) {
						this.connections = Utils.append(this.connections, dir);
					}
				}
			}
		}
		
		coords.markForUpdate(true);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		
		int[] conns = tag.getIntArray("Connections");
		
		this.connections = new ForgeDirection[conns.length];
		
		for (int i = 0; i < conns.length; i++)
		{
			this.connections[i] = ForgeDirection.getOrientation(conns[i]);
		}
		
		this.airSpeed = tag.getFloat("speed");
		this.airTemp = tag.getInteger("temp");
		this.insulation = tag.getFloat("insulation");
		this.leakageMultiplier = tag.getFloat("leakage");
		this.repair = tag.getFloat("repair");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		
		int[] conns = new int[this.connections.length];
		
		for (int i = 0; i < connections.length; i++)
		{
			conns[i] = this.connections[i].ordinal();
		}
		
		tag.setIntArray("Connections", conns);
		
		tag.setFloat("speed", this.airSpeed);
		tag.setInteger("temp", this.airTemp);
		tag.setFloat("insulation", this.insulation);
		tag.setFloat("leakage", this.leakageMultiplier);
		tag.setFloat("repair", this.repair);
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