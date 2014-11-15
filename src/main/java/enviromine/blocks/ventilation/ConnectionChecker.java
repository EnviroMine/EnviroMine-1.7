package enviromine.blocks.ventilation;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import enviromine.blocks.tiles.ventilation.IPosProvider;
import enviromine.blocks.tiles.ventilation.TileEntityVentBase;
import enviromine.blocks.ventilation.multipart.ICollisionProvider;
import enviromine.util.Coords;

import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;

public class ConnectionChecker
{
	/** Checks if there is any reason why we can't connect in this direction. For example, a multipart in the way, or there being nothing in this direction. */
	public static boolean checkConnection(IPosProvider provider, ForgeDirection dir)
	{
		Coords pos = provider.getCoords();
		Coords pos2 = pos.getCoordsInDir(dir);
		
		if (!pos.hasTileEntity() || !pos2.hasTileEntity()) {
			return false;
		}
		
		return isValidConnection(provider, dir);
	}
	
	public static boolean isValidConnection(IPosProvider provider, ForgeDirection dir)
	{
		Coords pos = provider.getCoords();
		
		boolean valid1 = isValidBlock(pos.getTileEntity()) && (!(provider instanceof ICollisionProvider) || !doesSideClip((ICollisionProvider)provider, dir));
		
		Coords pos2 = pos.getCoordsInDir(dir);
		VentDataHandler handler = VentDataHandler.getHandler(pos2.getTileEntity());
		
		boolean valid2 = isValidBlock(pos2.getTileEntity()) && (handler == null || !(handler.provider() instanceof ICollisionProvider) || !doesSideClip((ICollisionProvider)handler.provider(), dir.getOpposite()));
		
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
		} else if (VentDataHandler.getMultiPart(te) != null) {
			return true;
		}
		
		return false;
	}
}