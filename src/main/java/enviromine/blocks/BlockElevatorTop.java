package enviromine.blocks;

import org.apache.logging.log4j.Level;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import enviromine.blocks.tiles.TileEntityElevatorTop;
import enviromine.core.EnviroMine;
import enviromine.handlers.ObjectHandler;
import enviromine.handlers.TeleportHandler;
import enviromine.world.Earthquake;
import enviromine.world.features.mineshaft.MineshaftBuilder;

public class BlockElevatorTop extends Block implements ITileEntityProvider
{
	public BlockElevatorTop(Material par2Material)
	{
		super(par2Material);
		this.setHardness(5.0F);
		this.setStepSound(Block.soundTypeMetal);
		this.setLightLevel(1F);
	}
	
	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		if(player.isSneaking())
		{
			if(!world.isRemote)
			{
				EnviroMine.logger.log(Level.INFO, "Building mine...");
				MineshaftBuilder tmpBuilder = new MineshaftBuilder(world, i, k, 0);
				tmpBuilder.decayAmount = world.rand.nextInt(10);
				tmpBuilder.setRandom(world.rand);
				tmpBuilder.BuildAbandonedMine();
				if(tmpBuilder.segmentMap.size() <= 0)
				{
					EnviroMine.logger.log(Level.WARN, "Nothing was built!");
				} else
				{
					MineshaftBuilder.pendingBuilders.add(tmpBuilder);
					tmpBuilder.CheckAndBuildAll();
				}
			}
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
		
		if(world.getBlock(i, j - 1, k) != ObjectHandler.elevatorBottom)
		{
			player.addChatMessage(new ChatComponentText("Elevator is incomplete!"));
			return true;
		}
		
		if(j > 10 && player.dimension == 0)
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
			player.setPosition(i + 0.5D, j - 1, k + 0.5D);
			playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, 0, new TeleportHandler(playerMP.mcServer.worldServerForDimension(0)));
			world.setBlockToAir(i, j, k);
			world.setBlockToAir(i, j - 1, k);
		} else if(player.dimension == 0)
		{
			player.setPosition(i + 0.5D, j - 1, k + 0.5D);
			playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, -3, new TeleportHandler(playerMP.mcServer.worldServerForDimension(-3)));
			world.setBlockToAir(i, j, k);
			world.setBlockToAir(i, j - 1, k);
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
		return new TileEntityElevatorTop();
	}
	
	//You don't want the normal render type, or it wont render properly.
	@Override
	public int getRenderType()
	{
		return -1;
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
		this.blockIcon = register.registerIcon("enviromine:elevator_top_icon");
	}
}
