package enviromine.blocks.ventilation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import enviromine.blocks.tiles.ventilation.IPosProvider;
import enviromine.blocks.tiles.ventilation.TileEntityVentBase;
import enviromine.blocks.ventilation.multipart.ICollisionProvider;
import enviromine.blocks.ventilation.multipart.VentBasePart;
import enviromine.util.Coords;
import enviromine.util.Utils;

import java.util.List;

import codechicken.lib.vec.Cuboid6;
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
		
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			if (dir != removeDir.getOpposite() && checkConnection(dir))
			{
				this.connections = Utils.append(this.connections, dir);
			}
		}
		
		this.provider.getCoords().markForUpdate(true);
	}
	
	/** Checks if there is any reason why we can't connect in this direction. For example, a multipart in the way, or there being nothing in this direction. */
	public boolean checkConnection(ForgeDirection dir)
	{
		Coords pos = this.provider.getCoords();
		Coords pos2 = pos.getCoordsInDir(dir);
		
		if (!pos.hasTileEntity() || !pos2.hasTileEntity()) {
			return false;
		}
		
		return isValidConnection(dir);
	}
	
	public boolean isValidConnection(ForgeDirection dir)
	{
		Coords pos = this.provider.getCoords();
		
		boolean valid1 = isValidBlock(pos.getTileEntity()) && (!(this.provider instanceof ICollisionProvider) || !doesSideClip((ICollisionProvider)this.provider, dir));
		
		Coords pos2 = pos.getCoordsInDir(dir);
		VentDataHandler handler = getHandler(pos2.getTileEntity());
		
		boolean valid2 = isValidBlock(pos2.getTileEntity()) && (handler == null || !(handler.provider instanceof ICollisionProvider) || !doesSideClip((ICollisionProvider)handler.provider, dir.getOpposite()));
		
		return valid1 && valid2;
	}
	
	public static boolean doesSideClip(ICollisionProvider provider, ForgeDirection dir)
	{
		TileEntity te = provider.getCoords().getTileEntity();
		
		if (!(te instanceof TileMultipart)) {
			return false;
		}
		
		TileMultipart tilemp = (TileMultipart)te;
		
		if (tilemp.isSolid(dir.ordinal())) {
			return true;
		}
		
		TMultiPart part = tilemp.partMap(dir.ordinal());
		if (part == null) {
			return false;
		}
		
		Iterable<Cuboid6> colls = part.getCollisionBoxes();
		Cuboid6 baseColl = provider.getCollision(dir);
		for (Cuboid6 coll : colls) {
			if (baseColl.intersects(coll)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isValidBlock(TileEntity te)
	{
		if (te instanceof TileEntityVentBase) {
			return true;
		} else if (getMultiPart(te) != null) {
			return true;
		}
		
		return false;
	}
	
	public static VentBasePart getMultiPart(TileEntity te)
	{
		if (te instanceof TileMultipart) {
			List<TMultiPart> parts = ((TileMultipart)te).jPartList();
			if (!parts.isEmpty()) {
				TMultiPart part = parts.get(0);
				if (part instanceof VentBasePart) {
					return (VentBasePart)part;
				}
			}
		}
		
		return null;
	}
	
	public static VentDataHandler getHandler(TileEntity te)
	{
		if (te instanceof TileEntityVentBase) {
			return ((TileEntityVentBase)te).getHandler();
		} else if (getMultiPart(te) != null) {
			return getMultiPart(te).getHandler();
		}
		
		return null;
	}
}