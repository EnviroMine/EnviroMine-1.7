package enviromine.blocks;

import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import enviromine.blocks.tiles.TileEntityElevator;
import enviromine.core.EM_Settings;
import enviromine.handlers.EnviroAchievements;
import enviromine.handlers.ObjectHandler;
import enviromine.handlers.TeleportHandler;

public class BlockElevator extends Block implements ITileEntityProvider
{
	public BlockElevator(Material par2Material)
	{
		super(par2Material);
		this.setHardness(3.0F);
		this.setStepSound(Block.soundTypeMetal);
		this.setLightLevel(1F);
	}
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
    	return null;
    }
	
	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		if(player.isSneaking())
		{
			return true;
		}
		
		EntityPlayerMP playerMP = null;
		
		if(player instanceof EntityPlayerMP)
		{
			playerMP = (EntityPlayerMP)player;
		} else
		{
			return true;
		}
		
		if(EM_Settings.disableCaves)
		{
			player.addChatMessage(new ChatComponentText("The cave dimension has been disabled by the server owner!"));
			return true;
		}
		
		int meta = world.getBlockMetadata(i, j, k)%2;
		
		if(!(meta == 0 && world.getBlock(i, j - 1, k) == ObjectHandler.elevator && world.getBlockMetadata(i, j - 1, k) == 1) && !(meta == 1 && world.getBlock(i, j + 1, k) == ObjectHandler.elevator && world.getBlockMetadata(i, j + 1, k) == 0))
		{
			player.addChatMessage(new ChatComponentText("Elevator is incomplete!"));
			return true;
		}
		
		if(j > 10 - meta && player.dimension == 0)
		{
			player.addChatMessage(new ChatComponentText("Elevator must be built near bedrock."));
			return true;
		}
		
		if(player.timeUntilPortal > 0)
		{
			player.addChatMessage(new ChatComponentText("Please wait before attempting to teleport again."));
			return true;
		} else
		{
			player.timeUntilPortal = 100;
		}
		
		if(player.dimension == -3)
		{
			player.setLocationAndAngles((double)i + 0.5D, j - 1 + meta, (double)k + 0.5D, player.rotationYaw, player.rotationPitch);
			player.addStat(EnviroAchievements.intoTheDarkness, 1);
			
			if(player.getEntityData().hasKey("EM_CAVE_DIST"))
			{
				if(player.getEntityData().getIntArray("EM_CAVE_DIST")[3] >= 1000)
				{
					player.addStat(EnviroAchievements.intoTheDarkness, 1);
				}
				
				player.getEntityData().removeTag("EM_CAVE_DIST");
			}
			
			playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, 0, new TeleportHandler(playerMP.mcServer.worldServerForDimension(0)));
			world.setBlockToAir(i, j, k);
			if(meta == 0)
			{
				world.setBlockToAir(i, j - 1, k);
			} else
			{
				world.setBlockToAir(i, j + 1, k);
			}
		} else if(player.dimension == 0)
		{
			player.setLocationAndAngles((double)i + 0.5D, j - 1 + meta, (double)k + 0.5D, player.rotationYaw, player.rotationPitch);
			player.addStat(EnviroAchievements.boreToTheCore, 1);
			player.getEntityData().setIntArray("EM_CAVE_DIST", new int[]{i, j, k, 0});
			playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, -3, new TeleportHandler(playerMP.mcServer.worldServerForDimension(-3)));
			world.setBlockToAir(i, j, k);
			if(meta == 0)
			{
				world.setBlockToAir(i, j - 1, k);
			} else
			{
				world.setBlockToAir(i, j + 1, k);
			}
		} else
		{
			player.addChatMessage(new ChatComponentText("You cannot use the elevator from here!"));
		}
		return true;
	}
	
	//Make sure you set this as your TileEntity class relevant for the block!
	@Override
	public TileEntity createNewTileEntity(World world, int i)
	{
		return new TileEntityElevator();
	}
	
	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void getSubBlocks(Item item, CreativeTabs tab, List tabList)
	{
		for (int i = 0; i < 2; ++i)
		{
			tabList.add(new ItemStack(item, 1, i));
		}
	}
	
	@Override
	public int damageDropped(int meta)
	{
		return meta;
	}
	
	//You don't want the normal render type, or it wont render properly.
	@Override
	public int getRenderType()
	{
        return ObjectHandler.renderSpecialID;
	}
	
	//It's not an opaque cube, so you need this.
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	//It's not a normal block, so you need this too.
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	//This is the icon to use for showing the block in your hand.
	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		this.blockIcon = register.registerIcon("iron_block");
	}
}
