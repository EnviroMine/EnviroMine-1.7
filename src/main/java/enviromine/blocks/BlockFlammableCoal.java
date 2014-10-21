package enviromine.blocks;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import enviromine.blocks.tiles.TileEntityGas;
import enviromine.core.EM_Settings;
import enviromine.gases.EnviroGasDictionary;
import enviromine.handlers.ObjectHandler;

public class BlockFlammableCoal extends BlockOre
{
	public BlockFlammableCoal()
	{
		this.setHardness(3.0F).setResistance(5.0F).setStepSound(Block.soundTypePiston).setBlockName("oreCoal").setBlockTextureName("coal_ore");
	}
	
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Items.coal;
    }
	
	@Override
	public ItemStack createStackedBlock(int meta)
	{
        int j = 0;
        Item item = Item.getItemFromBlock(Blocks.coal_ore);

        if (item != null && item.getHasSubtypes())
        {
            j = meta;
        }

        return new ItemStack(item, 1, j);
	}

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random p_149745_1_)
    {
        return 1;
    }

    private Random rand = new Random();
    @Override
    public int getExpDrop(IBlockAccess p_149690_1_, int p_149690_5_, int p_149690_7_)
    {
        if (this.getItemDropped(p_149690_5_, rand, p_149690_7_) != Item.getItemFromBlock(this))
        {
        	return MathHelper.getRandomIntegerInRange(rand, 0, 2);
        } else
        {
        	return 0;
        }
    }
    
    /**
     * Called when the player destroys a block with an item that can harvest it. (i, j, k) are the coordinates of the
     * block and meta is the block's subtype/damage.
     */
    @Override
    public void harvestBlock(World world, EntityPlayer player, int i, int j, int k, int meta)
    {
    	super.harvestBlock(world, player, i, j, k, meta);
    	
    	if(EM_Settings.gasGen && world.rand.nextBoolean())
    	{
    		world.setBlock(i, j, k, ObjectHandler.gasBlock);
    		TileEntity tile = world.getTileEntity(i, j, k);
    		
    		if(tile != null && tile instanceof TileEntityGas)
    		{
    			TileEntityGas gasTile = (TileEntityGas)tile;
    			gasTile.addGas(EnviroGasDictionary.carbonDioxide.gasID, 1 + world.rand.nextInt(5));
    			gasTile.addGas(EnviroGasDictionary.methane.gasID, 1 + world.rand.nextInt(5));
    		}
    	}
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
				world.setBlock(x, y, z, ObjectHandler.burningCoal, 0, 2);
				return;
			}
		}
	}
	
    public int damageDropped(int p_149692_1_)
    {
        return 0;
    }
}
