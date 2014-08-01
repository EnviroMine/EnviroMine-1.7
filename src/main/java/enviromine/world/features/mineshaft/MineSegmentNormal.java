package enviromine.world.features.mineshaft;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class MineSegmentNormal extends MineSegment
{
	boolean hasFloor = true;
	
	public MineSegmentNormal(World world, int x, int y, int z, int rotation, MineshaftBuilder builder, boolean hasFloor)
	{
		super(world, x, y, z, rotation, builder);
		this.setChunkBounds(-1, -1, 5, 5);
		this.setBlockBounds(0, -1, 0, 4, 4, 4);
		this.hasFloor = hasFloor;
	}

	@Override
	public boolean build()
	{
		boolean containsLoot = false;
		
		if(getBlockID(2, 0, 2) == Block.chest.blockID)
		{
			containsLoot = true;
		}
		
		if(!containsLoot)
		{
			this.fillArea(1, 0, 1, 3, 2, 3, 0, 0);
		} else
		{
			this.fillArea(1, 1, 1, 3, 2, 3, 0, 0);
			
			this.fillArea(1, 0, 1, 3, 0, 1, 0, 0);
			this.fillArea(1, 0, 1, 1, 0, 3, 0, 0);
			this.fillArea(3, 0, 3, 3, 0, 1, 0, 0);
			this.fillArea(3, 0, 3, 1, 0, 3, 0, 0);
		}
		
		if((getBlockID(2, 1, 0) == Block.fence.blockID && getBlockID(2, 1, -1) == 0) || getBlockID(2, 1, -1) == 0)
		{
			this.fillArea(1, 0, 0, 3, 2, 0, 0, 0);
			
			for(int i = 1; i <= 3; i++)
			{
				if(!Block.isNormalCube(getBlockID(i, -1, -1)) && !Block.isNormalCube(getBlockID(i, 0, -1)))
				{
					setBlock(i, 0, 0, Block.fence.blockID, 0);
				}
			}
		} else if(getBlockID(2, 1, -1) != Block.ladder.blockID)
		{
			this.fillArea(1, 0, 0, 3, 2, 0, Block.fence.blockID, 0);
		}
		
		if((getBlockID(0, 1, 2) == Block.fence.blockID && getBlockID(-1, 1, 2) == 0) || getBlockID(-1, 1, 2) == 0)
		{
			this.fillArea(0, 0, 1, 0, 2, 3, 0, 0);
			
			for(int i = 1; i <= 3; i++)
			{
				if(!Block.isNormalCube(getBlockID(-1, -1, i)) && !Block.isNormalCube(getBlockID(-1, 0, i)))
				{
					setBlock(0, 0, i, Block.fence.blockID, 0);
				}
			}
		} else if(getBlockID(-1, 1, 2) != Block.ladder.blockID)
		{
			this.fillArea(0, 0, 1, 0, 2, 3, Block.fence.blockID, 0);
		}
		
		if((getBlockID(2, 1, 4) == Block.fence.blockID && getBlockID(2, 1, 5) == 0) || getBlockID(2, 1, 5) == 0)
		{
			this.fillArea(1, 0, 4, 3, 2, 4, 0, 0);
			
			for(int i = 1; i <= 3; i++)
			{
				if(!Block.isNormalCube(getBlockID(i, -1, 5)) && !Block.isNormalCube(getBlockID(i, 0, 5)))
				{
					setBlock(i, 0, 4, Block.fence.blockID, 0);
				}
			}
		} else if(getBlockID(2, 1, 5) != Block.ladder.blockID)
		{
			this.fillArea(1, 0, 4, 3, 2, 4, Block.fence.blockID, 0);
		}
		
		if((getBlockID(4, 1, 2) == Block.fence.blockID && getBlockID(5, 1, 2) == 0) || getBlockID(5, 1, 2) == 0)
		{
			this.fillArea(4, 0, 1, 4, 2, 3, 0, 0);
			
			for(int i = 1; i <= 3; i++)
			{
				if(!Block.isNormalCube(getBlockID(5, -1, i)) && !Block.isNormalCube(getBlockID(5, 0, i)))
				{
					setBlock(4, 0, i, Block.fence.blockID, 0);
				}
			}
		} else if(getBlockID(5, 1, 2) != Block.ladder.blockID)
		{
			this.fillArea(4, 0, 1, 4, 2, 3, Block.fence.blockID, 0);
		}
		
		this.fillArea(0, 3, 0, 4, 3, 4, Block.planks.blockID, 0);
		this.fillArea(1, 3, 1, 3, 3, 3, 0, 0);
		
		this.fillArea(0, 0, 0, 0, 3, 0, Block.wood.blockID, 0);
		this.fillArea(4, 0, 0, 4, 3, 0, Block.wood.blockID, 0);
		this.fillArea(0, 0, 4, 0, 3, 4, Block.wood.blockID, 0);
		this.fillArea(4, 0, 4, 4, 3, 4, Block.wood.blockID, 0);
		
		if(this.hasFloor)
		{
			for(int i = 0; i <= 4; i++)
			{
				for(int k = 0; k <= 4; k++)
				{
					int id = this.getBlockID(i, -1, k);
					
					if(!Block.isNormalCube(id))
					{
						this.setBlock(i, -1, k, Block.planks.blockID, 0);
					}
				}
			}
		}
		
		if(builder.rand.nextInt(250) == 0 && !containsLoot)
		{
			this.addLootChest(2, 0, 2, 3 + builder.rand.nextInt(4));
		}
		
		return true;
	}
	
	@Override
	public boolean canBuild()
	{
		if(this.getBlockID(2, 0, 1) == Block.ladder.blockID || this.getBlockID(1, 0, 2) == Block.ladder.blockID || this.getBlockID(2, 0, 3) == Block.ladder.blockID || this.getBlockID(3, 0, 2) == Block.ladder.blockID)
		{
			return false;
		} else
		{
			return super.canBuild();
		}
	}
	
	public int[] getExitPoint(int rotation)
	{
		int[] exitPoint = new int[3];
		
		exitPoint[0] = this.xOffset(this.xOffset(2, 2), rotation, 2, -2);
		exitPoint[1] = this.yOffset(0);
		exitPoint[2] = this.zOffset(this.zOffset(2, 2), rotation, 2, -2);
		
		return exitPoint;
	}
}
