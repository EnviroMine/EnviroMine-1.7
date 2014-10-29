package enviromine.blocks;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.Random;

import enviromine.blocks.tiles.TileEntityBurningCoal;
import enviromine.blocks.tiles.TileEntityGas;
import enviromine.gases.EnviroGasDictionary;
import enviromine.handlers.ObjectHandler;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockBurningCoal extends Block implements ITileEntityProvider
{
	public BlockBurningCoal(Material mat)
	{
		super(mat);
		this.setHardness(3.0F).setResistance(5.0F).setStepSound(Block.soundTypePiston).setBlockTextureName("redstone_ore").setLightLevel(0.625F).setTickRandomly(true);
	}

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
	@Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        world.scheduleBlockUpdateWithPriority(x, y, z, this, this.tickRate(world) + world.rand.nextInt(10), 0);
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
    	double f = 0.125;
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox((double)x + this.minX, (double)y + this.minY, (double)z + this.minZ, (double)x + this.maxX, (double)y + this.maxY, (double)z + this.maxZ);
        
        if(bounds != null)
        {
        	return bounds.contract(f, f, f);
        } else
        {
        	return null;
        }
    }
    
    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
    {
    	entity.setFire(10);
    }
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		super.updateTick(world, x, y, z, rand);
		
		if(!world.scheduledUpdatesAreImmediate)
		{
			world.scheduleBlockUpdateWithPriority(x, y, z, this, this.tickRate(world) + rand.nextInt(10), 0);
		}
        
        TileEntityBurningCoal coalTile = (TileEntityBurningCoal)world.getTileEntity(x, y, z);

        int l = world.getBlockMetadata(x, y, z);
		boolean flag1 = world.isBlockHighHumidity(x, y, z);
        byte b0 = 0;

        if (flag1)
        {
            b0 = -50;
        }
        
        for(int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++)
        {
        	ForgeDirection fDir = ForgeDirection.VALID_DIRECTIONS[i];
        	int xOff = fDir.offsetX + x;
        	int yOff = fDir.offsetY + y;
        	int zOff = fDir.offsetZ + z;
        	int enco = 300 + b0 - (fDir == UP || fDir == DOWN? 50 : 0);
        	
        	this.tryCatchFire(world, xOff, yOff, zOff, enco, rand, l, fDir.getOpposite());
        	
        	if(world.rand.nextInt(5) == 0 && (world.getBlock(xOff, yOff, zOff) == Blocks.air || world.getBlock(xOff, yOff, zOff) instanceof BlockGas))
        	{
        		if(world.getBlock(xOff, yOff, zOff) == Blocks.air)
        		{
        			world.setBlock(xOff, yOff, zOff, ObjectHandler.gasBlock);
        		}
        		TileEntity tile = world.getTileEntity(xOff, yOff, zOff);
        		
        		if(tile != null && tile instanceof TileEntityGas)
        		{
        			TileEntityGas gasTile = (TileEntityGas)tile;
        			
        			if(gasTile.amount < 10)
        			{
        				gasTile.addGas(EnviroGasDictionary.carbonMonoxide.gasID, 1);
        				coalTile.fuel -= 1;
        			}
        		}
        	}
        	
        	if(coalTile.fuel <= 0)
        	{
        		world.setBlock(x, y, z, Blocks.air, 0, 2);
        		return;
        	}
        }
        /*this.tryCatchFire(world, x + 1, y, z, 300 + b0, rand, l, WEST );
        this.tryCatchFire(world, x - 1, y, z, 300 + b0, rand, l, EAST );
        this.tryCatchFire(world, x, y - 1, z, 250 + b0, rand, l, UP   );
        this.tryCatchFire(world, x, y + 1, z, 250 + b0, rand, l, DOWN );
        this.tryCatchFire(world, x, y, z - 1, 300 + b0, rand, l, SOUTH);
        this.tryCatchFire(world, x, y, z + 1, 300 + b0, rand, l, NORTH);*/

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
	
    private void tryCatchFire(World world, int x, int y, int z, int p_149841_5_, Random random, int chance, ForgeDirection face)
    {
        int j1 = world.getBlock(x, y, z).getFlammability(world, x, y, z, face);

        if (random.nextInt(p_149841_5_) < j1)
        {
            boolean flag = world.getBlock(x, y, z) == Blocks.tnt;

            if (random.nextInt(chance + 10) < 5 && !world.canLightningStrikeAt(x, y, z))
            {
                int k1 = chance + random.nextInt(5) / 4;

                if (k1 > 15)
                {
                    k1 = 15;
                }

                world.setBlock(x, y, z, Blocks.fire, k1, 3);
            }
            else
            {
                world.setBlockToAir(x, y, z);
            }

            if (flag)
            {
                Blocks.tnt.onBlockDestroyedByPlayer(world, x, y, z, 1);
            }
        }
    }

    /**
     * Gets the highest chance of a neighbor block encouraging this block to catch fire
     */
    private int getChanceOfNeighborsEncouragingFire(World world, int x, int y, int z)
    {
        byte b0 = 0;

        if (!world.isAirBlock(x, y, z))
        {
            return 0;
        }
        else
        {
            int l = b0;
            l = this.getChanceToEncourageFire(world, x + 1, y, z, l, WEST );
            l = this.getChanceToEncourageFire(world, x - 1, y, z, l, EAST );
            l = this.getChanceToEncourageFire(world, x, y - 1, z, l, UP   );
            l = this.getChanceToEncourageFire(world, x, y + 1, z, l, DOWN );
            l = this.getChanceToEncourageFire(world, x, y, z - 1, l, SOUTH);
            l = this.getChanceToEncourageFire(world, x, y, z + 1, l, NORTH);
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

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityBurningCoal();
	}
}
