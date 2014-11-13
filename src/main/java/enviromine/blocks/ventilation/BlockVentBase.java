package enviromine.blocks.ventilation;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import enviromine.blocks.tiles.ventilation.IVentTileBase;
import enviromine.blocks.tiles.ventilation.TileEntityVentBase;
import enviromine.util.Coords;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;

public abstract class BlockVentBase extends Block implements ITileEntityProvider
{
	protected BlockVentBase(Material mat)
	{
		super(mat);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item)
	{
		//When block is placed by player - instant visual update
		super.onBlockPlacedBy(world, x, y, z, entity, item);
		this.onChanged(false, new Coords(world, x, y, z));
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		//When the block is placed at all - small pause before visual update
		super.onBlockAdded(world, x, y, z);
		this.onChanged(false, new Coords(world, x, y, z));
	}
	
	protected void onChanged(boolean removed, Coords coords)
	{
		TileEntityVentBase te = (TileEntityVentBase)coords.getTileEntity();
		te.calculateConnections();
		
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			Coords pos = coords.getCoordsInDir(dir);
			if (pos.hasTileEntity())
			{
				TileEntity tileEntity = pos.getTileEntity();
				
				IVentTileBase ivtb = null;
				
				if (tileEntity instanceof IVentTileBase) {
					ivtb = (IVentTileBase)tileEntity;
				} else if (tileEntity instanceof TileMultipart) {
					TMultiPart part = ((TileMultipart)tileEntity).jPartList().get(0);
					if (part instanceof IVentTileBase) {
						ivtb = (IVentTileBase)part;
					}
				}
				
				if (ivtb == null) {
					continue;
				}
				
				if (removed) {
					ivtb.calculateConnections(dir);
				} else {
					ivtb.calculateConnections();
				}
			}
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z)
	{
		this.onChanged(true, new Coords(world, x, y, z));
		return super.removedByPlayer(world, player, x, y, z);
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		((TileEntityVentBase)new Coords(world, x, y, z).getTileEntity()).calculateConnections();
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
	{
		this.updateBounds(blockAccess.getTileEntity(x, y, z));
	}
	
	public abstract void updateBounds(TileEntity te);
	
	public int getFacing(Coords coords, EntityLivingBase entity)
	{
		return BlockPistonBase.determineOrientation(coords.world, coords.x, coords.y, coords.z, entity);
	}
	
	public int translateSideAroundMeta(int side, int meta)
	{
		if (meta == 0)
		{
			//Pointing down
			return side;
		} else if (meta == 1)
		{
			//Pointing up
			return (side < 4) ? getOppSide(side) : side;
		} else if (meta == side)
		{
			//Front
			return 0;
		} else if (getOppSide(meta) == side)
		{
			//Back
			return 1;
		} else if (side == 0)
		{
			//Top
			return 3;
		} else if (side == 1)
		{
			//Bottom
			return 2;
		} else
		{
			int diff = side - meta;
			return (diff > 0) ? ((diff % 2 == 0) ? 4 : 5) : ((diff % 2 == 0) ? 5 : 4);
		}
	}
	
	public int getOppSide(int side)
	{
		return (side % 2 == 0 ? side + 1 : side - 1);
	}
	
	@Override
	public int getRenderType() {
		return -1;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
}