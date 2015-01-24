package enviromine.blocks;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import enviromine.core.EnviroMine;
import enviromine.handlers.EM_PhysManager;

public class BlockNoPhysics extends Block
{
	public BlockNoPhysics()
	{
		super(Material.iron);
		this.setBlockUnbreakable();
		this.setBlockName("enviromine.nophysblock");
		this.setBlockTextureName("enviromine:no_phys_block");
		this.setCreativeTab(EnviroMine.enviroTab);
		this.setTickRandomly(true);
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		EM_PhysManager.chunkDelay.put(world.provider.dimensionId + "" + (x >> 4) + "," + (z >> 4), Long.MAX_VALUE);
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		EM_PhysManager.chunkDelay.put(world.provider.dimensionId + "" + (x >> 4) + "," + (z >> 4), Long.MAX_VALUE);
	}
}
