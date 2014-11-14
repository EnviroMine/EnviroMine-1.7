package enviromine.blocks.ventilation.multipart;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import enviromine.blocks.ventilation.VentDataHandler;
import enviromine.core.EM_Settings;
import enviromine.util.Coords;

import java.util.Arrays;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.minecraft.McMetaPart;

public abstract class VentBasePart extends McMetaPart implements ICollisionProvider
{
	private final Block block;
	private final TileEntitySpecialRenderer renderer;
	private final String type;
	
	private final VentDataHandler handler = new VentDataHandler(this);
	
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
	public Coords getCoords()
	{
		return new Coords(this.world(), this.x(), this.y(), this.z());
	}
	
	public VentDataHandler getHandler()
	{
		return this.handler;
	}
	
	@Override
	public Iterable<Cuboid6> getCollisionBoxes()
	{
		return Arrays.asList(this.getBounds());
	}
	
	@Override
	public void invalidateConvertedTile()
	{
		NBTTagCompound tag = new NBTTagCompound();
		this.world().getTileEntity(x(), y(), z()).writeToNBT(tag);
		this.handler.load(tag);
		
		super.invalidateConvertedTile();
	}
	
	@Override
	public void save(NBTTagCompound tag)
	{
		super.save(tag);
		tag.setTag("TileEntityData", this.handler.save());
	}
	
	@Override
	public void load(NBTTagCompound tag)
	{
		super.load(tag);
		this.handler.load(tag.getCompoundTag("TileEntityData"));
	}
	
	@Override
	public void onWorldJoin()
	{
		if (this.renderer != null)
		{
			this.renderer.func_147497_a(TileEntityRendererDispatcher.instance);
		}
		
		this.handler.calculateConnections();
	}
	
	@Override
	public void onRemoved()
	{
		Coords coords = this.getCoords();
		
		this.handler.calculateConnections();
		
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			Coords pos = coords.getCoordsInDir(dir);
			if (pos.hasTileEntity())
			{
				VentDataHandler handler = VentDataHandler.getHandler(pos.getTileEntity());
				
				if (handler == null) {
					continue;
				}
				
				handler.calculateConnections(dir);
			}
		}
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
		this.handler.calculateConnections();
		
		Coords coords = this.getCoords();
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			Coords pos = coords.getCoordsInDir(dir);
			
			if (pos.hasTileEntity())
			{
				VentDataHandler handler = VentDataHandler.getHandler(pos.getTileEntity());
				if (handler == null) {
					continue;
				}
				
				handler.calculateConnections();
			}
		}
	}
	
	@Override
	public void onNeighborChanged()
	{
		this.handler.calculateConnections();
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
}