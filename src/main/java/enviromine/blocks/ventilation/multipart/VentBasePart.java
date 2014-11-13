package enviromine.blocks.ventilation.multipart;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import enviromine.blocks.tiles.ventilation.IVentTileBase;
import enviromine.blocks.tiles.ventilation.TileEntityVentBase;
import enviromine.core.EM_Settings;
import enviromine.util.Coords;
import enviromine.util.Utils;

import java.util.Arrays;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import codechicken.multipart.minecraft.McMetaPart;

public abstract class VentBasePart extends McMetaPart implements IVentTileBase
{
	private final Block block;
	private final TileEntitySpecialRenderer renderer;
	private final String type;
	
	public VentBasePart(Block block, String type)
	{
		this(block, 0, null, type);
	}
	
	public VentBasePart(Block block, TileEntitySpecialRenderer render, String type)
	{
		this(block, 0, render, type);
	}
	
	public VentBasePart(Block block, int meta, TileEntitySpecialRenderer render, String type)
	{
		this.block = block;
		this.renderer = render;
		this.type = EM_Settings.ModID + "|" + type;
	}
	
	@Override
	public void invalidateConvertedTile()
	{
		NBTTagCompound tag = new NBTTagCompound();
		this.world().getTileEntity(x(), y(), z()).writeToNBT(tag);
		this.customLoadNBT(tag);
		
		super.invalidateConvertedTile();
	}
	
	@Override
	public void save(NBTTagCompound tag)
	{
		super.save(tag);
		tag.setTag("TileEntityData", this.customSaveNBT());
	}
	
	@Override
	public void load(NBTTagCompound tag)
	{
		super.load(tag);
		this.customLoadNBT(tag.getCompoundTag("TileEntityData"));
	}
	
	@Override
	public void onWorldJoin()
	{
		if (this.renderer != null)
		{
			this.renderer.func_147497_a(TileEntityRendererDispatcher.instance);
		}
		
		this.calculateConnections();
	}
	
	@Override
	public void renderDynamic(Vector3 pos, float frame, int pass)
	{
		if (this.renderer != null)
		{
			this.renderer.renderTileEntityAt(tile(), pos.x, pos.y, pos.z, 0);
		}
	}
	
	@Override
	public void onPartChanged(TMultiPart changedPart)
	{
		this.calculateConnections();
		
		Coords coords = this.getCoords();
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			Coords pos = coords.getCoordsInDir(dir);
			
			if (pos.hasTileEntity())
			{
				TileEntity tileEntity = pos.getTileEntity();
				
				if (tileEntity instanceof TileEntityVentBase) {
					((IVentTileBase)tileEntity).calculateConnections();
				} else if (tileEntity instanceof TileMultipart) {
					TMultiPart part = ((TileMultipart)tileEntity).jPartList().get(0);
					if (part instanceof IVentTileBase) {
						((IVentTileBase)part).calculateConnections();
					}
				}
			}
		}
	}
	
	@Override
	public void onNeighborChanged()
	{
		this.calculateConnections();
	}
	
	/***********************/
	/** Tile Entity Stuff **/
	/***********************/
	
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
	
	
	/** Reads the data from the tileentity format */
	public void customLoadNBT(NBTTagCompound tag)
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
	public NBTTagCompound customSaveNBT()
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
	
	@Override
	public Block getBlock()
	{
		return this.block;
	}
	
	@Override
	public String getType()
	{
		return this.type;
	}
	
	@Override
	public boolean renderStatic(Vector3 pos, int pass)
	{
		return this.renderer == null;
	}
	
	@Override
	public int getTemp()
	{
		return this.airTemp;
	}
	
	@Override
	public Coords getCoords()
	{
		return new Coords(this.world(), this.x(), this.y(), this.z());
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
		
		System.out.println(String.format("Calculating at (%s, %s, %s)", coords.x, coords.y, coords.z));
		
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
	public Iterable<Cuboid6> getCollisionBoxes()
	{
		return Arrays.asList(this.getBounds());
	}
	
	@Override
	public boolean hasWorldObj()
	{
		return this.world() != null;
	}
	
	@Override
	public int getBlockMetadata()
	{
		return this.getMetadata();
	}
}