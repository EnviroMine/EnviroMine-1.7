package enviromine.world.features.mineshaft;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class MineSegmentShaft extends MineSegment
{
	boolean hasFloor = false;
	
	public MineSegmentShaft(World world, int x, int y, int z, int rotation, MineshaftBuilder builder, boolean hasFloor)
	{
		super(world, x, y, z, rotation, builder);
		this.setChunkBounds(-1, -1, 5, 5);
		this.setBlockBounds(0, -1, 0, 4, 4, 4);
	}

	@Override
	public boolean build()
	{
		this.fillArea(1, 0, 1, 3, 2, 3, Blocks.air, 0);
		
		if(hasFloor && ((getBlock(2, 1, 0) == Blocks.fence && getBlock(2, 1, -1) == Blocks.air) || getBlock(2, 1, -1) == Blocks.air))
		{
			this.fillArea(1, 0, 0, 3, 2, 0, Blocks.air, 0);
			
			for(int i = 1; i <= 3; i++)
			{
				if(!getBlock(i, -1, -1).isNormalCube() && !getBlock(i, 0, -1).isNormalCube())
				{
					setBlock(i, 0, 0, Blocks.fence, 0);
				}
			}
		} else if(getBlock(2, 1, -1) != Blocks.ladder)
		{
			this.fillArea(1, 0, 0, 3, 2, 0, Blocks.fence, 0);
		}
		
		if(hasFloor && ((getBlock(0, 1, 2) == Blocks.fence && getBlock(-1, 1, 2) == Blocks.air) || getBlock(-1, 1, 2) == Blocks.air))
		{
			this.fillArea(0, 0, 1, 0, 2, 3, Blocks.air, 0);
			
			for(int i = 1; i <= 3; i++)
			{
				if(!getBlock(-1, -1, i).isNormalCube() && !getBlock(-1, 0, i).isNormalCube())
				{
					setBlock(0, 0, i, Blocks.fence, 0);
				}
			}
		} else if(getBlock(-1, 1, 2) != Blocks.ladder)
		{
			this.fillArea(0, 0, 1, 0, 2, 3, Blocks.fence, 0);
		}
		
		if(hasFloor && ((getBlock(2, 1, 4) == Blocks.fence && getBlock(2, 1, 5) == Blocks.air) || getBlock(2, 1, 5) == Blocks.air))
		{
			this.fillArea(1, 0, 4, 3, 2, 4, Blocks.air, 0);
			
			for(int i = 1; i <= 3; i++)
			{
				if(!getBlock(i, -1, 5).isNormalCube() && !getBlock(i, 0, 5).isNormalCube())
				{
					setBlock(i, 0, 4, Blocks.fence, 0);
				}
			}
		} else if(getBlock(2, 1, 5) != Blocks.ladder)
		{
			this.fillArea(1, 0, 4, 3, 2, 4, Blocks.fence, 0);
		}
		
		if(hasFloor && ((getBlock(4, 1, 2) == Blocks.fence && getBlock(5, 1, 2) == Blocks.air) || getBlock(5, 1, 2) == Blocks.air))
		{
			this.fillArea(4, 0, 1, 4, 2, 3, Blocks.air, 0);
			
			for(int i = 1; i <= 3; i++)
			{
				if(!getBlock(5, -1, i).isNormalCube() && !getBlock(5, 0, i).isNormalCube())
				{
					setBlock(4, 0, i, Blocks.fence, 0);
				}
			}
		} else if(getBlock(5, 1, 2) != Blocks.ladder)
		{
			this.fillArea(4, 0, 1, 4, 2, 3, Blocks.fence, 0);
		}
		
		this.fillArea(0, 3, 0, 4, 3, 4, Blocks.planks, 0);
		this.fillArea(1, 3, 1, 3, 3, 3, Blocks.air, 0);
		
		this.fillArea(0, 0, 0, 0, 3, 0, Blocks.log, 0);
		this.fillArea(4, 0, 0, 4, 3, 0, Blocks.log, 0);
		this.fillArea(0, 0, 4, 0, 3, 4, Blocks.log, 0);
		this.fillArea(4, 0, 4, 4, 3, 4, Blocks.log, 0);
		
		this.fillArea(0, 0, 2, 0, 3, 2, Blocks.planks, 0);
		this.fillAndRotate(1, 0, 2, 1, 3, 2, Blocks.ladder, 5);
		
		if(this.hasFloor)
		{
			for(int i = 0; i <= 4; i++)
			{
				for(int k = 0; k <= 4; k++)
				{
					if(!this.getBlock(i, -1, k).isNormalCube())
					{
						this.setBlock(i, -1, k, Blocks.planks, 0);
					}
				}
			}
		}
		
		return true;
	}
	
	@Override
	public boolean canBuild()
	{
		return !(this.getBlock(2, 0, 1) == Blocks.ladder || this.getBlock(1, 0, 2) == Blocks.ladder || this.getBlock(2, 0, 3) == Blocks.ladder || this.getBlock(3, 0, 2) == Blocks.ladder) && super.canBuild();
	}
	
	@Override
	public int[] getExitPoint(int rotation)
	{
		int[] exitPoint = new int[3];
		
		exitPoint[0] = this.xOffset(this.xOffset(2, 2), rotation, 2, -2);
		exitPoint[1] = this.yOffset(0);
		exitPoint[2] = this.zOffset(this.zOffset(2, 2), rotation, 2, -2);
		
		return exitPoint;
	}
	
	@Override
	public int[] getExitPoint(int rotation, int yDir)
	{
		int[] point = this.getExitPoint(rotation);
		point[1] += (int)(Math.signum(yDir)*4);
		return point;
	}
}
