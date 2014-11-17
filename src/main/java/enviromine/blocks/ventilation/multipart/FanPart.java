package enviromine.blocks.ventilation.multipart;

import enviromine.client.renderer.tileentity.ventilation.TileEntityFanRenderer;
import enviromine.handlers.ObjectHandler;

import codechicken.lib.vec.Cuboid6;
import net.minecraftforge.common.util.ForgeDirection;

public class FanPart extends VentBasePart
{
	private Cuboid6 bounds = new Cuboid6(0.125, 0.125, 0.125, 0.875, 0.875, 0.875);
	
	public FanPart()
	{
		super(ObjectHandler.fan, new TileEntityFanRenderer(), "fan");
	}
	
	@Override
	public Cuboid6 getBounds()
	{
		return this.bounds.set(0.125, 0.125, 0.125, 0.875, 0.875, 0.875); //TODO rotate
	}
	
	@Override
	public Cuboid6 getCollision(ForgeDirection dir)
	{
		return null;
	}
	
	@Override
	public boolean allowConnect(ForgeDirection dir)
	{
		return true; //TODO
	}
}