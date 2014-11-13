package enviromine.blocks.ventilation;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import enviromine.blocks.tiles.ventilation.TileEntityFan;
import enviromine.handlers.ObjectHandler;
import enviromine.util.Coords;

public class BlockFan extends BlockVentBase
{
	public BlockFan()
	{
		super(Material.iron);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		side = translateSideAroundMeta(side, meta);
		
		switch (side)
		{
			case 0:
				//Top (front)
				return Blocks.sponge.getIcon(0, 0);
			case 1:
				//Bottom (back)
				return Blocks.cobblestone.getIcon(0, 0);
			case 2:
				//Side (North)
				return Blocks.diamond_block.getIcon(0, 0);
			case 3:
				//Side (South)
				return Blocks.emerald_block.getIcon(0, 0);
			case 4:
				//Side (West)
				return Blocks.lapis_ore.getIcon(0, 0);
			case 5:
				//Side (East)
				return Blocks.coal_ore.getIcon(0, 0);
		}
		
		return Blocks.obsidian.getIcon(0, 0);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item)
	{
		super.onBlockPlacedBy(world, x, y, z, entity, item);
		
		Coords coords = new Coords(world, x, y, z);
		
		coords.setBlockWithMetadata(ObjectHandler.fan, this.getFacing(coords, entity));
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityFan();
	}

	@Override
	public void updateBounds(TileEntity te)
	{
		//TODO
	}
}