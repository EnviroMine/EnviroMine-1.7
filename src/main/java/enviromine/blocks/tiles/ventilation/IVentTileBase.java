package enviromine.blocks.tiles.ventilation;

import net.minecraftforge.common.util.ForgeDirection;

import enviromine.util.Coords;

public interface IVentTileBase
{
	public boolean hasWorldObj();
	public int getBlockMetadata();
	public int getTemp(); //TODO expand
	public Coords getCoords();
	public ForgeDirection[] getConnections();
	public void calculateConnections();
	public void calculateConnections(ForgeDirection removeDir);
}