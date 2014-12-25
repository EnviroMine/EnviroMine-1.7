package enviromine.blocks.materials;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MaterialElevator extends Material
{
	public MaterialElevator(MapColor colour)
	{
		super(colour);
		this.setRequiresTool();
	}
	
	@Override
	public boolean isSolid()
	{
		return false;
	}
}
