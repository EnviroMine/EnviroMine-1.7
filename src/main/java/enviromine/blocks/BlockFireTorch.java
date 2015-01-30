package enviromine.blocks;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;
import java.util.Random;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.core.EM_Settings;
import enviromine.handlers.ObjectHandler;
import net.minecraft.block.BlockTorch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFireTorch extends BlockTorch
{
	boolean isLit = false;
	
	public BlockFireTorch(boolean lit)
	{
		super();
		this.isLit = lit;
	}
	
	@Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
		return this.isLit? Item.getItemFromBlock(Blocks.torch) : Items.stick;
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
	@Override
    public void onBlockAdded(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
    {
        p_149726_1_.scheduleBlockUpdate(p_149726_2_, p_149726_3_, p_149726_4_, this, this.tickRate(p_149726_1_) + p_149726_1_.rand.nextInt(10));
    }
	
	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		ItemStack stack = player.getEquipmentInSlot(0);
		
		if (stack != null && stack.getItem() == Items.flint_and_steel)
		{
			stack.damageItem(1, player);
			world.setBlock(i, j, k, ObjectHandler.fireTorch, world.getBlockMetadata(i, j, k), 3);
		}
		
		return true;
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		// Reset the torch back to vanilla
		if(!EM_Settings.torchesBurn && !EM_Settings.torchesGoOut)
		{
			world.setBlock(x, y, z, Blocks.torch, world.getBlockMetadata(x, y, z), 3);
			return;
		}
		
		if((world.rand.nextInt(1000) == 0 || (world.isRaining() && world.canBlockSeeTheSky(x, y, z))) && EM_Settings.torchesGoOut)
		{
			world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
			world.setBlock(x, y, z, ObjectHandler.offTorch, world.getBlockMetadata(x, y, z), 3);
			return;
		}
		
		super.updateTick(world, x, y, z, rand);
		
		// Don't go any further unless this torch is allowed to burn stuff
		if(!EM_Settings.torchesBurn || !isLit)
		{
			return;
		}

        world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world) + rand.nextInt(10));

        int l = world.getBlockMetadata(x, y, z);
		boolean flag1 = world.isBlockHighHumidity(x, y, z);
        byte b0 = 0;

        if (flag1)
        {
            b0 = -50;
        }

        this.tryCatchFire(world, x + 1, y, z, 300 + b0, rand, l, WEST );
        this.tryCatchFire(world, x - 1, y, z, 300 + b0, rand, l, EAST );
        this.tryCatchFire(world, x, y - 1, z, 250 + b0, rand, l, UP   );
        this.tryCatchFire(world, x, y + 1, z, 250 + b0, rand, l, DOWN );
        this.tryCatchFire(world, x, y, z - 1, 300 + b0, rand, l, SOUTH);
        this.tryCatchFire(world, x, y, z + 1, 300 + b0, rand, l, NORTH);

        for (int i1 = x - 1; i1 <= x + 1; ++i1)
        {
            for (int j1 = z - 1; j1 <= z + 1; ++j1)
            {
                for (int k1 = y - 1; k1 <= y + 4; ++k1)
                {
                    if (i1 != x || k1 != y || j1 != z)
                    {
                        int l1 = 100;

                        if (k1 > y + 1)
                        {
                            l1 += (k1 - (y + 1)) * 100;
                        }

                        int i2 = this.getChanceOfNeighborsEncouragingFire(world, i1, k1, j1);

                        if (i2 > 0)
                        {
                            int j2 = (i2 + 40 + world.difficultySetting.getDifficultyId() * 7) / (l + 30);

                            if (flag1)
                            {
                                j2 /= 2;
                            }

                            if (j2 > 0 && rand.nextInt(l1) <= j2 && (!world.isRaining() || !world.canLightningStrikeAt(i1, k1, j1)) && !world.canLightningStrikeAt(i1 - 1, k1, z) && !world.canLightningStrikeAt(i1 + 1, k1, j1) && !world.canLightningStrikeAt(i1, k1, j1 - 1) && !world.canLightningStrikeAt(i1, k1, j1 + 1))
                            {
                                int k2 = l + rand.nextInt(5) / 4;

                                if (k2 > 15)
                                {
                                    k2 = 15;
                                }

                                world.setBlock(i1, k1, j1, Blocks.fire, k2, 3);
                            }
                        }
                    }
                }
            }
        }
	}

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand)
    {
    	if(isLit)
    	{
    		super.randomDisplayTick(world, x, y, z, rand);
    	}
    }
	
    private void tryCatchFire(World p_149841_1_, int p_149841_2_, int p_149841_3_, int p_149841_4_, int p_149841_5_, Random p_149841_6_, int p_149841_7_, ForgeDirection face)
    {
        int j1 = p_149841_1_.getBlock(p_149841_2_, p_149841_3_, p_149841_4_).getFlammability(p_149841_1_, p_149841_2_, p_149841_3_, p_149841_4_, face);

        if (p_149841_6_.nextInt(p_149841_5_) < j1)
        {
            boolean flag = p_149841_1_.getBlock(p_149841_2_, p_149841_3_, p_149841_4_) == Blocks.tnt;

            if (p_149841_6_.nextInt(p_149841_7_ + 10) < 5 && !p_149841_1_.canLightningStrikeAt(p_149841_2_, p_149841_3_, p_149841_4_))
            {
                int k1 = p_149841_7_ + p_149841_6_.nextInt(5) / 4;

                if (k1 > 15)
                {
                    k1 = 15;
                }

                p_149841_1_.setBlock(p_149841_2_, p_149841_3_, p_149841_4_, Blocks.fire, k1, 3);
            }
            else
            {
                p_149841_1_.setBlockToAir(p_149841_2_, p_149841_3_, p_149841_4_);
            }

            if (flag)
            {
                Blocks.tnt.onBlockDestroyedByPlayer(p_149841_1_, p_149841_2_, p_149841_3_, p_149841_4_, 1);
            }
        }
    }

    /**
     * Gets the highest chance of a neighbor block encouraging this block to catch fire
     */
    private int getChanceOfNeighborsEncouragingFire(World p_149845_1_, int p_149845_2_, int p_149845_3_, int p_149845_4_)
    {
        byte b0 = 0;

        if (!p_149845_1_.isAirBlock(p_149845_2_, p_149845_3_, p_149845_4_))
        {
            return 0;
        }
        else
        {
            int l = b0;
            l = this.getChanceToEncourageFire(p_149845_1_, p_149845_2_ + 1, p_149845_3_, p_149845_4_, l, WEST );
            l = this.getChanceToEncourageFire(p_149845_1_, p_149845_2_ - 1, p_149845_3_, p_149845_4_, l, EAST );
            l = this.getChanceToEncourageFire(p_149845_1_, p_149845_2_, p_149845_3_ - 1, p_149845_4_, l, UP   );
            l = this.getChanceToEncourageFire(p_149845_1_, p_149845_2_, p_149845_3_ + 1, p_149845_4_, l, DOWN );
            l = this.getChanceToEncourageFire(p_149845_1_, p_149845_2_, p_149845_3_, p_149845_4_ - 1, l, SOUTH);
            l = this.getChanceToEncourageFire(p_149845_1_, p_149845_2_, p_149845_3_, p_149845_4_ + 1, l, NORTH);
            return l;
        }
    }

    /**
     * Side sensitive version that calls the block function.
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param oldChance The previous maximum chance.
     * @param face The side the fire is coming from
     * @return The chance of the block catching fire, or oldChance if it is higher
     */
    public int getChanceToEncourageFire(IBlockAccess world, int x, int y, int z, int oldChance, ForgeDirection face)
    {
        int newChance = world.getBlock(x, y, z).getFireSpreadSpeed(world, x, y, z, face);
        return (newChance > oldChance ? newChance : oldChance);
    }
}