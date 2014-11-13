package enviromine.blocks.ventilation.multipart;

import enviromine.client.renderer.tileentity.ventilation.TileEntityVentSmallRenderer;
import enviromine.handlers.ObjectHandler;

import codechicken.lib.vec.Cuboid6;

public class VentSmallPart extends VentBasePart
{
	public VentSmallPart()
	{
		super(ObjectHandler.ventSmall, new TileEntityVentSmallRenderer(), "ventSmall");
	}

	@Override
	public Cuboid6 getBounds()
	{
		return new Cuboid6(0.125, 0.125, 0.125, 0.875, 0.875, 0.875); //TODO
	}
}