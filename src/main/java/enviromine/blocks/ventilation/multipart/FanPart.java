package enviromine.blocks.ventilation.multipart;

import enviromine.blocks.IRotatable;
import enviromine.blocks.tiles.ventilation.TileEntityFan;
import enviromine.client.renderer.tileentity.ventilation.TileEntityFanRenderer;
import enviromine.handlers.ObjectHandler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import codechicken.lib.vec.Cuboid6;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author thislooksfun
 */
public class FanPart extends VentBasePart implements IRotatable
{
	private ForgeDirection facing = ForgeDirection.UNKNOWN;
	
	public FanPart()
	{
		super(ObjectHandler.fan, new TileEntityFanRenderer(), "fan");
	}
	
	@Override
	public Cuboid6 getBounds()
	{
		if (this.facing == ForgeDirection.UNKNOWN)
		{
			TileEntity te = this.getCoords().getTileEntity();
			if (te instanceof TileEntityFan)
			{
				this.facing = ((TileEntityFan)te).facing();
			}
		}
		boolean faceX = this.facing.offsetX != 0;
		boolean faceY = this.facing.offsetY != 0;
		boolean faceZ = this.facing.offsetZ != 0;
		double min = 0.125;
		double max = 0.875;
		return new Cuboid6(faceX ? 0F : min, faceY ? 0F : min, faceZ ? 0F : min, faceX ? 1F : max, faceY ? 1F : max, faceZ ? 1F : max);
	}
	
	@Override
	public Cuboid6 getCollision(ForgeDirection dir)
	{
		return this.getBounds();
	}
	
	@Override
	public boolean allowConnect(ForgeDirection dir)
	{
		return dir == this.facing || dir.getOpposite() == this.facing;
	}
	
	@Override
	public void customSave(NBTTagCompound tag)
	{
		super.customSave(tag);
		
		tag.setInteger("facing", this.facing.ordinal());
	}
	
	@Override
	public void customLoad(NBTTagCompound tag)
	{
		super.customLoad(tag);
		
		this.facing = ForgeDirection.getOrientation(tag.getInteger("facing"));
	}
	
	@Override
	public ForgeDirection facing()
	{
		return this.facing;
	}
	
	@Override
	public void setFacing(ForgeDirection dir)
	{
		this.facing = dir;
	}
	
	@Override
	public void rotate()
	{
		this.facing = ForgeDirection.getOrientation(this.facing == ForgeDirection.EAST ? 0 : this.facing.ordinal() + 1);
		this.getCoords().notifyNeighbors();
	}
}