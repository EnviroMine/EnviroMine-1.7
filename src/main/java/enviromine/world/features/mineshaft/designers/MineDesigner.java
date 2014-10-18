package enviromine.world.features.mineshaft.designers;

import enviromine.world.features.mineshaft.MineshaftBuilder;

public abstract class MineDesigner
{
	public abstract void StartDesign(MineshaftBuilder builder, int size, int depth, int auxSpacing);
}
