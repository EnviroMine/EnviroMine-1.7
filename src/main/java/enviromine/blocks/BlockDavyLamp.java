package enviromine.blocks;

import java.util.ArrayList;
import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.blocks.tiles.TileEntityDavyLamp;
import enviromine.gases.EnviroGas;
import enviromine.gases.EnviroGasDictionary;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
	
	@Override
	public void onBlockAdded(World world, int i, int j, int k)
	{
		super.onBlockAdded(world, i, j, k);
		this.onNeighborBlockChange(world, i, j, k, this);
	}
	
	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		ItemStack stack = player.getEquipmentInSlot(0);
		
		if (stack != null && stack.getItem() == Items.flint_and_steel && world.getBlockMetadata(i, j, k) == 0)
		{
			stack.damageItem(1, player);
			world.setBlockMetadataWithNotify(i, j, k, 1, 2);
			this.onNeighborBlockChange(world, i, j, k, this);
		}
		
		return true;
	}
	
	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block block)
	{
		int state = world.getBlockMetadata(i, j, k);
		int origState = state;
		
		if (origState > 0)
		{
			for (int ii = 0; ii < 4; ii++)
			{
				int x = i + ForgeDirection.VALID_DIRECTIONS[ii].offsetX;
				int y = j + ForgeDirection.VALID_DIRECTIONS[ii].offsetY;
				int z = k + ForgeDirection.VALID_DIRECTIONS[ii].offsetZ;
				
				Block bDir = world.getBlock(x, y, z);
				
				if (bDir instanceof BlockGas)
				{
					BlockGas bGas = (BlockGas)bDir;
					ArrayList<int[]> gasList = bGas.getGasInfo(world, x, y, z);
					
					for (int jj = 0; jj < gasList.size(); jj++)
					{
						EnviroGas gasInfo = EnviroGasDictionary.gasList[gasList.get(jj)[0]];
						if (gasInfo.volitility > 0)
						{
							state = 2;
							break;
						} else if (gasInfo.suffocation > 0)
						{
							state = 0;
						}
					}
					
					if (state == 2)
					{
						break;
					}
				}
			}
			
			if (state != origState)
			{
				world.setBlockMetadataWithNotify(i, j, k, state, 2);
			}
		}
	}
	
	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"unchecked", "rawtypes"})
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
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if (meta == 1)
		{
			return this.litIcon;
		} else if (meta == 2)
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
