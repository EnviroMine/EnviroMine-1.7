package enviromine.blocks;

import enviromine.handlers.ObjectHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFlammableCoal extends BlockOre
{
	public BlockFlammableCoal()
	{
		this.setHardness(3.0F).setResistance(5.0F).setStepSound(Block.soundTypePiston).setBlockName("oreCoal").setBlockTextureName("coal_ore");
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block nBlock)
	{
		for(int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++)
		{
			int xOff = ForgeDirection.VALID_DIRECTIONS[i].offsetX + x;
			int yOff = ForgeDirection.VALID_DIRECTIONS[i].offsetY + y;
			int zOff = ForgeDirection.VALID_DIRECTIONS[i].offsetZ + z;
			Block block = world.getBlock(xOff, yOff, zOff);
			int meta = world.getBlockMetadata(xOff, yOff, zOff);
			
			if(ObjectHandler.igniteList.containsKey(block) && (ObjectHandler.igniteList.get(block).isEmpty() || ObjectHandler.igniteList.get(block).contains(meta)))
			{
				world.setBlock(x, y, z, ObjectHandler.burningCoal);
				return;
			}
		}
	}
}
