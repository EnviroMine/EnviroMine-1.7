package enviromine.blocks.ventilation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import enviromine.blocks.tiles.ventilation.IPosProvider;
import enviromine.blocks.tiles.ventilation.TileEntityVentBase;
import enviromine.blocks.ventilation.multipart.VentBasePart;
import enviromine.util.Coords;
import enviromine.util.Utils;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;

public class VentDataHandler
{
	/** The air temperature in this pipe */
	public int airTemp = 0;
	/** The air speed in this pipe (0-1) */
	public float airSpeed = 0F;
	/** The insulation of this pipe (0-1)<br>0 will be instantly set to the ambient temp<br>1 will never be affected by external forces */
	public float insulation = 0F;
	/** The percentage of air that will leak out every tick (0-1)<br>0 is no leakage<br>1 is full leakage - none transmitted */
	public float leakageMultiplier = 0F;
	/** The state this pipe is in (0-1)<br>0 is completely broken, and will be removed next tick<br>1 is fully repaired */
	public float repair = 1F;
	
	/** The sides that are connected to other pipes */
	private ForgeDirection[] connections = new ForgeDirection[0];
	
	private final IPosProvider provider;
	
	public VentDataHandler(IPosProvider provider)
	{
		this.provider = provider;
	}
	
	/** Reads the data from the tileentity format */
	public void load(NBTTagCompound tag)
	{
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
	
	/** Saves the data in the tileentity format */
	public NBTTagCompound save()
	{
		NBTTagCompound tag = new NBTTagCompound();
		
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
		
		return tag;
	}
	
	public ForgeDirection[] getConnections()
	{
		return this.connections;
	}
	
	public void calculateConnections()
	{
		this.calculateConnections(ForgeDirection.UNKNOWN);
	}
	
	public void calculateConnections(ForgeDirection removeDir)
	{
		this.connections = new ForgeDirection[0];
		
		Coords coords = this.provider.getCoords();
		
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
				
				if (tileEntity instanceof TileEntityVentBase)
				{
					this.connections = Utils.append(this.connections, dir);
				} else if (tileEntity instanceof TileMultipart)
				{
					TMultiPart part = ((TileMultipart)tileEntity).jPartList().get(0);
					if (part instanceof VentBasePart)
					{
						this.connections = Utils.append(this.connections, dir);
					}
				}
			}
		}
		
		coords.markForUpdate(true);
	}
}