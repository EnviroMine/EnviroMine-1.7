package enviromine.blocks;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author thislooksfun
 */
public interface IRotatable
{
	public ForgeDirection facing();
	public void setFacing(ForgeDirection dir);
	public void rotate();
}
