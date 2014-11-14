package enviromine.blocks.ventilation.multipart;

import net.minecraftforge.common.util.ForgeDirection;

import enviromine.client.renderer.tileentity.ventilation.TileEntityVentSmallRenderer;
import enviromine.handlers.ObjectHandler;

import codechicken.lib.vec.Cuboid6;

public class VentSmallPart extends VentBasePart
{
	private Cuboid6 bounds = new Cuboid6(0.125, 0.125, 0.125, 0.875, 0.875, 0.875);
	private Cuboid6[] sidedBounds;
	
	public VentSmallPart()
	{
		super(ObjectHandler.ventSmall, new TileEntityVentSmallRenderer(), "ventSmall");
		
		float min = 0.125F;
		float max = 0.876F;
		
		this.sidedBounds = new Cuboid6[] {
					new Cuboid6(min , max , min , max , 1.0F, max ), //UP
					new Cuboid6(min , 0.0F, min , max , min , max ), //DOWN
					new Cuboid6(min , min , 0.0F, max , max , min), //NORTH
					new Cuboid6(min , min , max , max , max , 1.0F), //SOUTH
					new Cuboid6(0.0F, min , min , min , max , max ), //WEST
					new Cuboid6(max , min , min , 1.0F, max , max ), //EAST
					null //UNKNOWN
				};
	}

	@Override
	public Cuboid6 getBounds()
	{
		return this.bounds.copy();
	}

	@Override
	public Cuboid6 getCollision(ForgeDirection dir)
	{
		return this.sidedBounds[dir.ordinal()].copy();
	}
}