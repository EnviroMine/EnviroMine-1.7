package enviromine.world.features.mineshaft.designers;

import enviromine.world.features.mineshaft.MineSegment;
import enviromine.world.features.mineshaft.MineSegmentNormal;
import enviromine.world.features.mineshaft.MineshaftBuilder;

public class MineDesignerRandomized extends MineDesigner
{
	@Override
	public void StartDesign(MineshaftBuilder builder, int size, int depth, int auxSpacing)
	{
		int[][] segMap = new int[size][size];
		
		//Pass limit whether the target room count has been reached or not
		int maxAttempts = size * size * 10;
		
		//Target number of rooms in the dungeon
		int rooms = (size/2) * (size/2);
		
		segMap[size/2][size/2] = 1;
		
		for(int attempts = 0; attempts < maxAttempts && rooms > 0; attempts++)
		{
			int x = builder.rand.nextInt(size);
			int y = builder.rand.nextInt(size);
			
			if(segMap[x][y] == 0 && GetNeighbours(segMap, x, y) == 1)
			{
				segMap[x][y] = 1;
				rooms--;
			}
		}
		
		for(int i = 0; i < size; i++)
		{
			for(int j = 0; j < size; j++)
			{
				if(segMap[i][j] == 1)
				{
					int posX = (i - (size/2))*4;
					int posZ = (j - (size/2))*4;
					MineSegment segment = new MineSegmentNormal(builder.world, builder.xOffset(posX, posZ), depth, builder.zOffset(posX, posZ), builder.rot, builder, true);
					segment.setDecay(builder.decayAmount);
					segment.linkChunksToBuilder();
				}
			}
		}
	}
	
	public int GetNeighbours(int[][] map, int posX, int posY)
	{
		int count = 0;
		count += posX + 1 >= map.length? 0 : map[posX + 1][posY];
		count += posX - 1 < 0? 0 : map[posX - 1][posY];
		count += posY + 1 >= map[posX].length? 0 : map[posX][posY + 1];
		count += posY - 1 < 0? 0 : map[posX][posY - 1];
		return count;
	}
}
