package enviromine.blocks;

import java.util.List;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.blocks.tiles.TileEntityDavyLamp;
import enviromine.client.TextureDavyLamp;
import enviromine.core.EnviroMine;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockDavyLamp extends Block implements ITileEntityProvider
{
	public IIcon litIcon;
	public IIcon gasIcon;
	
	public BlockDavyLamp(Material material)
	{
		super(material);
		this.setHardness(5.0F);
		this.setStepSound(Block.soundTypeMetal);
	}
	
	@Override
	public int damageDropped(int meta)
	{
		return meta;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityDavyLamp();
	}
	
	@Override
	public int getRenderType()
	{
		return -1;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List tabList)
    {
        for (int i = 0; i < 3; ++i)
        {
        	tabList.add(new ItemStack(item, 1, i));
        }
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
    	if(meta == 1)
    	{
    		return this.litIcon;
    	} else if(meta == 2)
    	{
    		return this.gasIcon;
    	} else
    	{
            return this.blockIcon;
    	}
    }
	
	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		this.blockIcon = register.registerIcon("enviromine:davy_lamp_off");
		this.litIcon = register.registerIcon("enviromine:davy_lamp_lit");
		this.gasIcon = register.registerIcon("enviromine:davy_lamp_gas");
	}
}
