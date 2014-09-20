package enviromine.world.features.mineshaft.designers;

import enviromine.world.features.mineshaft.MineSegment;
import enviromine.world.features.mineshaft.MineSegmentNormal;
import enviromine.world.features.mineshaft.MineshaftBuilder;

public class MineDesignerFeather extends MineDesigner
{
	@Override
	public void StartDesign(MineshaftBuilder builder, int size, int depth, int auxSpacing)
	{
		int length = builder.rand.nextInt(16) + 16;
		
		for(int i = 0; i <= length; i++)
		{
			MineSegment basSeg = new MineSegmentNormal(builder.world, builder.xOffset(i * 4, 0), depth, builder.zOffset(i * 4, 0), builder.rot, builder, true);
			basSeg.setDecay(builder.decayAmount);
			basSeg.linkChunksToBuilder();
			
			if(i%(auxSpacing + 1) == 0)
			{
				int lWidth = builder.rand.nextInt(4) + 4;
				int rWidth = (builder.rand.nextInt(4) + 4) * -1;
				
				for(int il = 1; il <= lWidth; il++)
				{
					MineSegment segment = new MineSegmentNormal(builder.world, builder.xOffset(i * 4, il * 4), depth, builder.zOffset(i * 4, il * 4), builder.rot, builder, true);
					segment.setDecay(builder.decayAmount);
					segment.linkChunksToBuilder();
				}
				
				for(int ir = -1; ir >= rWidth; ir--)
				{
					MineSegment segment = new MineSegmentNormal(builder.world, builder.xOffset(i * 4, ir * 4), depth, builder.zOffset(i * 4, ir * 4), builder.rot, builder, true);
					segment.setDecay(builder.decayAmount);
					segment.linkChunksToBuilder();
				}
			}
		}
	}
}
