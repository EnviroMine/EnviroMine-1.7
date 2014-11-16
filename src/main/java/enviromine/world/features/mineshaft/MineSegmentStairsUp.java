package enviromine.world.features.mineshaft;

import net.minecraft.world.World;

public class MineSegmentStairsUp extends MineSegment
{
	public MineSegmentStairsUp(World world, int x, int y, int z, int rotation, MineshaftBuilder builder)
	{
		super(world, x, y, z, rotation, builder);
		this.setBlockBounds(0, -1, 0, 8, 8, 4);
		this.setChunkBounds(-1, -1, 9, 5);
	}

	@Override
	public boolean build()
	{
		return this.canBuild();
	}
}
