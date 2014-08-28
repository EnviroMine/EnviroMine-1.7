package enviromine.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.EnviroUtils;
import enviromine.blocks.tiles.TileEntityGas;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.gases.EnviroGasDictionary;
import enviromine.handlers.ObjectHandler;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import org.apache.logging.log4j.Level;

public class BlockGas extends Block implements ITileEntityProvider
{
	public IIcon gasIcon;
	public ArrayList<String> igniteList = new ArrayList<String>();
	
	public BlockGas(Material par2Material)
	{
		super(par2Material);
		this.setTickRandomly(true);
		igniteList.add("" + Block.blockRegistry.getNameForObject(Blocks.flowing_lava));
		igniteList.add("" + Block.blockRegistry.getNameForObject(Blocks.lava));
		igniteList.add("" + Block.blockRegistry.getNameForObject(Blocks.torch));
		igniteList.add("" + Block.blockRegistry.getNameForObject(Blocks.lit_furnace));
		igniteList.add("" + Block.blockRegistry.getNameForObject(Blocks.fire));
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public void onBlockAdded(World world, int i, int j, int k)
	{
		super.onBlockAdded(world, i, j, k);
		
		TileEntity tile = world.getTileEntity(i, j, k);
		
		if(tile == null)
		{
			tile = new TileEntityGas(world);
			world.setTileEntity(i, j, k, tile);
		}
		
		if(world.scheduledUpdatesAreImmediate)
		{
			world.scheduleBlockUpdateWithPriority(i, j, k, this, this.tickRate(world), 0);
		} else
		{
			world.scheduleBlockUpdate(i, j, k, this, this.tickRate(world));
		}
	}
	
	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityLiving, ItemStack itemStack)
	{
		TileEntity tile = world.getTileEntity(i, j, k);
		
		if(tile != null && tile instanceof TileEntityGas)
		{
			TileEntityGas gasTile = (TileEntityGas)tile;
			
			//EnviroGasDictionary.gasFire.setDecayRates(1, 1, 100).setDensity(-1F);
			//EnviroGasDictionary.methane.setVolitility(10F);
			//EnviroGasDictionary.hydrogenSulfide.setVolitility(100F);
			//EnviroGasDictionary.carbonDioxide.setDecayRates(1, 1, 100, 5);
			//EnviroGasDictionary.carbonMonoxide.setDecayRates(1, 0, 100, 1);
			
			//gasTile.addGas(1, 10);
			//gasTile.addGas(3, 50);
			gasTile.addGas(4, 100); // METHANE
			//gasTile.addGas(0, 2000); // FIRE
			//gasTile.addGas(7, 100); // NUKE
			gasTile.updateRender();
		}
	}
	
	@Override
	public int colorMultiplier(IBlockAccess blockAccess, int i, int j, int k)
	{
		TileEntity tile = blockAccess.getTileEntity(i, j, k);
		
		if(tile != null && tile instanceof TileEntityGas)
		{
			TileEntityGas gasTile = (TileEntityGas)tile;
			return gasTile.color.getRGB();
		} else
		{
			return Color.WHITE.getRGB();
		}
	}
	
	public float getOpacity(IBlockAccess blockAccess, int i, int j, int k)
	{
		if(blockAccess.getBlock(i, k, k) == ObjectHandler.gasBlock && !EM_Settings.renderGases)
		{
			return 0;
		} else
		{
			TileEntity tile = blockAccess.getTileEntity(i, j, k);
			
			if(tile != null && tile instanceof TileEntityGas)
			{
				float maxOpacity = ((TileEntityGas)tile).opacity;
				return maxOpacity;
			} else
			{
				return 0F;
			}
		}
	}
	
	public void swtichIgnitionState(World world, int i, int j, int k)
	{
        TileEntity tile = world.getTileEntity(i, j, k);
        Block newBlock = this;
        
        if(this == ObjectHandler.gasBlock)
        {
            world.setBlock(i, j, k, ObjectHandler.fireGasBlock);
            newBlock = ObjectHandler.fireGasBlock;
        } else
        {
            world.setBlock(i, j, k, ObjectHandler.gasBlock);
            newBlock = ObjectHandler.gasBlock;
        }
        
        if (tile != null)
        {
            tile.validate();
            world.setTileEntity(i, j, k, tile);
            tile.blockType = newBlock;
            
            if(tile instanceof TileEntityGas)
            {
            	((TileEntityGas)tile).updateRender();
            }
        }
	}
	
	@Override
	public int tickRate(World world)
	{
		if(this  == ObjectHandler.fireGasBlock)
		{
			return EM_Settings.gasTickRate/4;
		} else
		{
			return EM_Settings.gasTickRate;
		}
	}
	
	@Override
	public int getRenderColor(int meta)
	{
		return 16777215;
	}
	
	@Override
	public int getRenderType()
	{
		return 0;//ObjectHandler.renderGasID;
	}
	
	@Override
	public boolean renderAsNormalBlock()
	{
		return true;//false;
	}
	
	@Override
	public boolean canCollideCheck(int par1, boolean par2)
	{
		return false;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	{
		return null;
	}
	
	public boolean isBlockReplaceable(World world, int x, int y, int z)
	{
		return true;
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int i, int j, int k, int side)
	{
		double yMax = this.getMaxY(blockAccess, i, j, k);
		double yMin = this.getMinY(blockAccess, i, j, k);
		float opacity = this.getOpacity(blockAccess, i, j, k);
		
		if(opacity <= 0.1F)
		{
			return false;
		}
		
		int[] sideCoord = EnviroUtils.getAdjacentBlockCoordsFromSide(i, j, k, side);
		if(blockAccess.getBlock(sideCoord[0], sideCoord[1], sideCoord[2]) == ObjectHandler.gasBlock || blockAccess.getBlock(sideCoord[0], sideCoord[1], sideCoord[2]) == ObjectHandler.fireGasBlock)
		{
			double sideYMax = this.getMaxY(blockAccess, sideCoord[0], sideCoord[1], sideCoord[2]);
			double sideYMin = this.getMinY(blockAccess, sideCoord[0], sideCoord[1], sideCoord[2]);
			
			if(this.getOpacity(blockAccess, sideCoord[0], sideCoord[1], sideCoord[2]) <= 0.1F)
			{
				return true;
			} else if(side > 1) // Sides
			{
				
				if(sideYMin > yMin || sideYMax < yMax)
				{
					return true;
				} else
				{
					return false;
				}
			} else if(side == 0) // Bottom
			{
				if(sideYMax != 1.0F || yMin != 0.0F)
				{
					return true;
				} else
				{
					return false;
				}
			} else if(side == 1) // Top
			{
				if(yMax != 1.0F || sideYMin != 0.0F)
				{
					return true;
				} else
				{
					return false;
				}
			} else
			{
				return true;
			}
		} else
		{
			if(side == 0 && yMin != 0.0F)
			{
				return true;
			} else if(side == 1 && yMax != 1.0F)
			{
				return true;
			} else
			{
				return !blockAccess.getBlock(sideCoord[0], sideCoord[1], sideCoord[2]).isOpaqueCube();
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return 0;
	}
	
	@Override
	public int getRenderBlockPass()
	{
		return 1;
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		if(world.isRemote)
		{
			return;
		}
		
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if(tile == null || !(tile instanceof TileEntityGas))
		{
			world.setBlockToAir(x, y, z);
			return;
		} else
		{
			TileEntityGas gasTile = (TileEntityGas)tile;
			
			if(gasTile.amount >= 1000)
			{
				EnviroMine.logger.log(Level.ERROR, "Too many gases inside one block! (" + gasTile.amount + " / 1000)");
				world.removeTileEntity(x, y, z);
				world.setBlockToAir(x, y, z);
				return;
			}
			
			if(isTouchingIgnition(world, x, y, z) && this == ObjectHandler.gasBlock)
			{
				if(gasTile.burnGases())
				{
					this.swtichIgnitionState(world, x, y, z);
				}
			} else if(gasTile.getGasQuantity(0) >= 1 && this == ObjectHandler.gasBlock)
			{
				gasTile.burnGases();
				this.swtichIgnitionState(world, x, y, z);
			} else if(gasTile.getGasQuantity(0) <= 0 && this == ObjectHandler.fireGasBlock)
			{
				this.swtichIgnitionState(world, x, y, z);
			}
			
			if(gasTile.gases.size() <= 0 || gasTile.amount <= 0)
			{
				world.setBlockToAir(x, y, z);
				return;
			} else if(gasTile.spreadGas())
			{
				world.notifyBlocksOfNeighborChange(x, y, z, this);
			} else if(gasTile.amount > 10)
			{
				if(world.scheduledUpdatesAreImmediate)
				{
					world.scheduleBlockUpdateWithPriority(x, y, z, this, this.tickRate(world), 0);
				} else
				{
					world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
				}
			}
			
			if(gasTile.getGasQuantity(0) > 20)
			{
				if(gasTile.firePressure >= 10)
				{
					world.setBlockToAir(x, y, z);
					if(gasTile.getGasQuantity(0) > 80)
					{
						world.newExplosion(null, x, y, z, 16F, true, true);
					} else
					{
						world.newExplosion(null, x, y, z, gasTile.getGasQuantity(0)/5F, true, true);
					}
				} else
				{
					gasTile.firePressure += 1;
				}
			} else
			{
				gasTile.firePressure = 0;
			}
			
			if(gasTile.gases.size() <= 0 || gasTile.amount <= 0)
			{
				world.setBlockToAir(x, y, z);
			} else
			{
				gasTile.updateRender();
			}
		}
	}
	
	public boolean isTouchingIgnition(World world, int x, int y, int z)
	{
		ArrayList<int[]> dir = new ArrayList<int[]>();
		
		dir.add(new int[]{-1,0,0});
		dir.add(new int[]{1,0,0});
		dir.add(new int[]{0,-1,0});
		dir.add(new int[]{0,1,0});
		dir.add(new int[]{0,0,-1});
		dir.add(new int[]{0,0,1});
		
		for(int i = 0; i < dir.size(); i++)
		{
			int[] pos = dir.get(i);
			Block block = world.getBlock(x + pos[0], y + pos[1], z + pos[2]);
			int meta = world.getBlockMetadata(x + pos[0], y + pos[1], z + pos[2]);
			
			if(igniteList.contains("" + Block.blockRegistry.getNameForObject(block)) || igniteList.contains("" + Block.blockRegistry.getNameForObject(block) + "," + meta))
			{
				return true;
			} else
			{
				TileEntity tile = world.getTileEntity(x + pos[0], y + pos[1], z + pos[2]);
				
				if(tile != null && tile instanceof TileEntityGas)
				{
					TileEntityGas gasTile = (TileEntityGas)tile;
					
					if(gasTile.getGasQuantity(EnviroGasDictionary.gasFire.gasID) > 0)
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		if(world.scheduledUpdatesAreImmediate)
		{
			world.scheduleBlockUpdateWithPriority(x, y, z, this, this.tickRate(world), 0);
		} else
		{
			world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
		}
		
		if(world.isRemote && (block == ObjectHandler.gasBlock || block == ObjectHandler.fireGasBlock))
		{
			TileEntity tile = world.getTileEntity(x, y, z);
			
			if(tile != null && tile instanceof TileEntityGas)
			{
				TileEntityGas gasTile = (TileEntityGas)tile;
				
				gasTile.updateOpacity();
				gasTile.updateColor();
				gasTile.updateSize();
				gasTile.updateRender();
				
				if(gasTile.amount > 1000)
				{
					EnviroMine.logger.log(Level.ERROR, "Too many gases inside one block! (EARLY PASS) (" + gasTile.amount + " / 1000)");
					world.removeTileEntity(x, y, z);
					world.setBlock(x, y, z, Blocks.air);
					return;
				}
			}
		}
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if(tile instanceof TileEntityGas && entity instanceof EntityLivingBase)
		{
			TileEntityGas gasTile = (TileEntityGas)tile;
			gasTile.doAllEffects((EntityLivingBase)entity);
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int i)
	{
		TileEntityGas tile = new TileEntityGas(world);
		return tile;
	}
	
	@Override
	public IIcon getIcon(int par1, int par2)
	{
		return gasIcon;
	}
	
	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		this.gasIcon = register.registerIcon("enviromine:block_gas");
		this.blockIcon = register.registerIcon("enviromine:block_gas");
	}
	
	/**
	 * Return whether this block can drop from an explosion.
	 */
	@Override
	public boolean canDropFromExplosion(Explosion par1Explosion)
	{
		return false;
	}

    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return null;
    }
    
    @Override
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }
    
    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion)
    {
    	if(world.isBlockNormalCubeDefault(x, y - 1, z, false) && this == ObjectHandler.fireGasBlock)
    	{
    		world.setBlock(x, y, z, Blocks.fire);
    	}
    	

		ArrayList<int[]> dir = new ArrayList<int[]>();
		
		dir.add(new int[]{-1,0,0});
		dir.add(new int[]{1,0,0});
		dir.add(new int[]{0,-1,0});
		dir.add(new int[]{0,1,0});
		dir.add(new int[]{0,0,-1});
		dir.add(new int[]{0,0,1});
		
		for(int i = 0; i < dir.size(); i++)
		{
			int[] pos = dir.get(i);
			
			TileEntity tile = world.getTileEntity(x + pos[0], y + pos[1], z + pos[2]);
			
			if(tile != null && tile instanceof TileEntityGas)
			{
				TileEntityGas gasTile = (TileEntityGas)tile;
				
				if(gasTile.burnGases())
				{
					if(gasTile.getBlockType() == ObjectHandler.fireGasBlock)
					{
						((BlockGas)gasTile.getBlockType()).swtichIgnitionState(world, x + pos[0], y + pos[1], z + pos[2]);
					}
				}
			}
		}
    }
	
	public double getMinY(IBlockAccess blockAccess, int i, int j, int k)
	{
		TileEntity tile = blockAccess.getTileEntity(i, j, k);
		
		if(tile != null && tile instanceof TileEntityGas)
		{
			TileEntityGas gasTile = (TileEntityGas)tile;
			return (double)gasTile.yMin;
		} else
		{
			return 0D;
		}
	}
	
	public double getMaxY(IBlockAccess blockAccess, int i, int j, int k)
	{
		TileEntity tile = blockAccess.getTileEntity(i, j, k);
		
		if(tile instanceof TileEntityGas)
		{
			TileEntityGas gasTile = (TileEntityGas)tile;
			return (double)gasTile.yMax;
		} else
		{
			return 1D;
		}
	
	}
}
