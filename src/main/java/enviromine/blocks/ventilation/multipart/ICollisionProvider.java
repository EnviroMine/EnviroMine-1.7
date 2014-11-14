package enviromine.blocks.ventilation.multipart;

import net.minecraftforge.common.util.ForgeDirection;

import enviromine.blocks.tiles.ventilation.IPosProvider;

import codechicken.lib.vec.Cuboid6;

public interface ICollisionProvider extends IPosProvider
{
	public Cuboid6 getCollision(ForgeDirection dir);
}