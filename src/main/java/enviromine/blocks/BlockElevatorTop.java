package enviromine.blocks;

import enviromine.blocks.tiles.TileEntityElevatorTop;
import enviromine.handlers.ObjectHandler;
import enviromine.handlers.TeleportHandler;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;

public class BlockElevatorTop extends Block implements ITileEntityProvider
{
	public BlockElevatorTop(int par1, Material par2Material)
	{
		super(par1, par2Material);
		this.setHardness(5.0F);
		this.setStepSound(Block.soundMetalFootstep);
		this.setLightValue(1F);
	}
	
	/**
	 * Called upon block activation (right click on the block.)
	 */
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
		
		if(world.getBlockId(i, j - 1, k) != ObjectHandler.elevatorBottom.blockID)
		{
			player.sendChatToPlayer(ChatMessageComponent.createFromText("Elevator is incomplete!"));
			return true;
		}
		
		if(j > 10 && player.dimension == 0)
		{
			player.sendChatToPlayer(ChatMessageComponent.createFromText("Elevator must be built near bedrock."));
			return true;
		}
		
		if(player.timeUntilPortal > 0)
		{
			player.sendChatToPlayer(ChatMessageComponent.createFromText("Please wait before attempting to teleport again."));
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
			player.sendChatToPlayer(ChatMessageComponent.createFromText("You cannot use the elevator from here!"));
		}
		return true;
	}
	
	//Make sure you set this as your TileEntity class relevant for the block!
	@Override
	public TileEntity createNewTileEntity(World world)
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
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	//This is the icon to use for showing the block in your hand.
	public void registerIcons(IconRegister icon)
	{
		this.blockIcon = icon.registerIcon("enviromine:elevator_top_icon");
	}
}
