package enviromine.blocks.tiles.ventilation;

import enviromine.util.Coords;

import net.minecraftforge.common.util.ForgeDirection;

public interface IPosProvider
{
	public Coords getCoords();
	public boolean allowConnect(ForgeDirection dir);
}