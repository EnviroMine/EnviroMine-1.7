package enviromine.blocks.materials;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLogic;

public class MaterialGas extends MaterialLogic
{
	public MaterialGas(MapColor p_i2112_1_)
	{
		super(p_i2112_1_);
		this.setBurning();
		this.setNoPushMobility();
		this.setReplaceable();
	}
	
	@Override
	public boolean isOpaque()
	{
		return false;
	}
}
