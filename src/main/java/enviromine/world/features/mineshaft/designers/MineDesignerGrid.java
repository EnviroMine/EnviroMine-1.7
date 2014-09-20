package enviromine.world.features.mineshaft.designers;

import enviromine.world.features.mineshaft.MineSegment;
import enviromine.world.features.mineshaft.MineSegmentNormal;
import enviromine.world.features.mineshaft.MineshaftBuilder;

public class MineDesignerGrid extends MineDesigner
{
	@Override
	public void StartDesign(MineshaftBuilder builder, int size, int depth, int auxSpacing)
	{
		if(auxSpacing < 0)
		{
			return;
		}
		
		int skew = 0;
		int gridL = builder.rand.nextInt(64) + 64;
		int gridW = builder.rand.nextInt(64) + 64;
		
		for(int i = -((gridL/2) - (gridL/2)%(auxSpacing+1)); i < gridL/2; i += auxSpacing + 1)
		{
			for(int j = -(gridW/2); j < gridW/2; j++)
			{
				if(i == 0 && j == 0)
				{
					continue;
				}
				
				if(i == 0)
				{
					skew = 0;
				} else if(j%(auxSpacing+1) == 0)
				{
					skew = builder.rand.nextInt(3) - 1;
				}
				
				MineSegment segment = new MineSegmentNormal(builder.world, builder.xOffset(i * 4 + skew * 4, j * 4), depth, builder.zOffset(i * 4 + skew * 4, j * 4), builder.rot, builder, true);
				segment.setDecay(builder.decayAmount);
				segment.linkChunksToBuilder();
			}
		}
		
		for(int i = -((gridW/2) - (gridW/2)%(auxSpacing+1)); i < gridW/2; i += auxSpacing + 1)
		{
			for(int j = -(gridL/2); j < gridL/2; j++)
			{
				if(i == 0 && j == 0)
				{
					continue;
				}
				
				if(i == 0)
				{
					skew = 0;
				} else if(j%(auxSpacing+1) == 0)
				{
					skew = builder.rand.nextInt(3) - 1;
				}
				
				MineSegment segment = new MineSegmentNormal(builder.world, builder.xOffset(j * 4 + skew * 4, i * 4), depth, builder.zOffset(j * 4 + skew * 4, i * 4), builder.rot, builder, true);
				segment.setDecay(builder.decayAmount);
				segment.linkChunksToBuilder();
			}
		}
	}
}
