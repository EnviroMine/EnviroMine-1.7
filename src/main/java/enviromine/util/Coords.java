/**
 * @author thislooksfun
 */

package enviromine.util;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/** Provides easy XYZ coord handling, as well as some methods to make world manipulation easier */
public class Coords
{
	/** The world object */
	public final World world;
	/** The x coord */
	public final int x;
	/** The y coord */
	public final int y;
	/** The z coord */
	public final int z;
	
	public Coords(World world, int x, int y, int z)
	{
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/** Returns the block at this location */
	public Block getBlock() {
		return this.world.getBlock(this.x, this.y, this.z);
	}
	/** Returns the neighbors of this block */
	public Block[][][] getNeighbors()
	{
		Block[][][] blocks = new Block[3][3][3];
		
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				for (int k = -1; k < 2; k++) {
					blocks[i+1][j+1][k+1] = this.world.getBlock(this.x + i, this.y + j, this.z + k);
				}
			}
		}
		
		return blocks;
	}
	/** Sets the block at this location to the specified block */
	public void setBlock(Block block) {
		this.setBlockWithMetadata(block, 0);
	}
	/** Sets the block and metadata at this location to the specified values */
	public void setBlockWithMetadata(Block block, int meta) {
		this.world.setBlock(this.x, this.y, this.z, block, meta, 2);
	}
	/** */
	public boolean isBlockSideSolid(ForgeDirection dir) {
		return this.getBlock().isSideSolid(this.world, this.x, this.y, this.z, dir);
	}
	/** Sets the block at this location to air */
	public void setAir() {
		this.world.setBlockToAir(this.x, this.y, this.z);
	}
	/** Returns the metadata at this location */
	public int getMetadata() {
		return this.world.getBlockMetadata(this.x, this.y, this.z);
	}
	/** Returns the TileEntity at this location */
	public TileEntity getTileEntity() {
		return this.world.getTileEntity(this.x, this.y, this.z);
	}
	/** Returns true if this location has a tileentity */
	public boolean hasTileEntity() {
		return this.getTileEntity() != null;
	}
	/** Removes the TileEntity at this location */
	public void removeTileEntity() {
		this.world.removeTileEntity(this.x, this.y, this.z);
	}
	/** Removes the TileEntity at this location, and returns it */
	public TileEntity getAndRemoveTileEntity()
	{
		TileEntity te = this.getTileEntity();
		this.removeTileEntity();
		te.validate();
		return te;
	}
	/** Marks the block as requiring an update */
	public void markForUpdate(boolean requireServer) {
		if (!requireServer || !this.world.isRemote) {
			this.world.markBlockForUpdate(this.x, this.y, this.z);
		}
	}
	
	public Coords getCoordsInDir(ForgeDirection dir)
	{
		return new Coords(this.world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
	}
	
	public Coords getCoordsOppositeDir(ForgeDirection dir)
	{
		return this.getCoordsInDir(dir.getOpposite());
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Coords) {
			Coords compare = (Coords)obj;
			if (compare.world.provider.dimensionId == this.world.provider.dimensionId && compare.x == this.x && compare.y == this.y && compare.z == this.z) {
				return true;
			}
		}
		
		return false;
	}
	
	/** Returns a copy of this object */
	public Coords copy() {
		return new Coords(this.world, this.x, this.y, this.z);
	}
}