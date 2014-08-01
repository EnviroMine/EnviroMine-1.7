package enviromine.blocks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.EnviroUtils;
import enviromine.blocks.tiles.TileEntityGas;
import enviromine.core.EM_Settings;
import enviromine.gases.EnviroGasDictionary;
import enviromine.handlers.ObjectHandler;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGas extends Block implements ITileEntityProvider
{
	public Icon gasIcon;
	public ArrayList<String> igniteList = new ArrayList<String>();
	
	public BlockGas(int par1, Material par2Material)
	{
		super(par1, par2Material);
		this.setTickRandomly(true);
		igniteList.add("" + Block.lavaMoving.blockID);
		igniteList.add("" + Block.lavaStill.blockID);
		igniteList.add("" + Block.torchWood.blockID);
		igniteList.add("" + Block.furnaceBurning.blockID);
		igniteList.add("" + Block.fire.blockID);
	}
	
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	public void onBlockAdded(World world, int i, int j, int k)
	{
		super.onBlockAdded(world, i, j, k);
		
		TileEntity tile = world.getBlockTileEntity(i, j, k);
		
		if(tile == null)
		{
			tile = new TileEntityGas(world);
			world.setBlockTileEntity(i, j, k, tile);
		}
		
		if(world.scheduledUpdatesAreImmediate)
		{
			world.scheduleBlockUpdateFromLoad(i, j, k, this.blockID, this.tickRate(world), 0);
		} else
		{
			world.scheduleBlockUpdate(i, j, k, this.blockID, this.tickRate(world));
		}
	}
	
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityLiving, ItemStack itemStack)
	{
		TileEntity tile = world.getBlockTileEntity(i, j, k);
		
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
			//gasTile.addGas(4, 100);
			//gasTile.addGas(0, 2000);
			gasTile.addGas(7, 100);
		}
	}
	
	@Override
	public int colorMultiplier(IBlockAccess blockAccess, int i, int j, int k)
	{
		TileEntity tile = blockAccess.getBlockTileEntity(i, j, k);
		
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
		if(blockAccess.getBlockId(i, k, k) == ObjectHandler.gasBlock.blockID && !EM_Settings.renderGases)
		{
			return 0;
		} else
		{
			TileEntity tile = blockAccess.getBlockTileEntity(i, j, k);
			
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
        TileEntity tile = world.getBlockTileEntity(i, j, k);
        int newID = this.blockID;
        
        if(this.blockID == ObjectHandler.gasBlock.blockID)
        {
            world.setBlock(i, j, k, ObjectHandler.fireGasBlock.blockID);
            newID = ObjectHandler.fireGasBlock.blockID;
        } else
        {
            world.setBlock(i, j, k, ObjectHandler.gasBlock.blockID);
            newID = ObjectHandler.gasBlock.blockID;
        }
        
        if (tile != null)
        {
            tile.validate();
            world.setBlockTileEntity(i, j, k, tile);
            tile.blockType = Block.blocksList[newID];
            
            if(tile instanceof TileEntityGas)
            {
            	((TileEntityGas)tile).updateRender();
            }
        }
	}
	
	public int tickRate(World world)
	{
		if(this.blockID  == ObjectHandler.fireGasBlock.blockID)
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
	
	public int getRenderType()
	{
		return ObjectHandler.renderGasID;
	}
	
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	public boolean canCollideCheck(int par1, boolean par2)
	{
		return false;
	}
	
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
		if(blockAccess.getBlockId(sideCoord[0], sideCoord[1], sideCoord[2]) == ObjectHandler.gasBlock.blockID || blockAccess.getBlockId(sideCoord[0], sideCoord[1], sideCoord[2]) == ObjectHandler.fireGasBlock.blockID)
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
				return !blockAccess.isBlockOpaqueCube(sideCoord[0], sideCoord[1], sideCoord[2]);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return 0;
	}
	
	public int getRenderBlockPass()
	{
		return 1;
	}
	
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		if(world.isRemote)
		{
			return;
		}
		
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		
		if(tile == null || !(tile instanceof TileEntityGas))
		{
			world.setBlockToAir(x, y, z);
			return;
		} else
		{
			TileEntityGas gasTile = (TileEntityGas)tile;
			
			/*if(gasTile.amount > 2048)
			{
				EnviroMine.logger.log(Level.SEVERE, "Too many gases inside one block! (amount > 1024)");
				world.setBlockToAir(x, y, z);
				return;
			}*/
			
			if(isTouchingIgnition(world, x, y, z) && this.blockID == ObjectHandler.gasBlock.blockID)
			{
				if(gasTile.burnGases())
				{
					this.swtichIgnitionState(world, x, y, z);
				}
			} else if(gasTile.getGasQuantity(0) >= 1 && this.blockID == ObjectHandler.gasBlock.blockID)
			{
				gasTile.burnGases();
				this.swtichIgnitionState(world, x, y, z);
			} else if(gasTile.getGasQuantity(0) <= 0 && this.blockID == ObjectHandler.fireGasBlock.blockID)
			{
				this.swtichIgnitionState(world, x, y, z);
			}
			
			if(gasTile.gases.size() <= 0 || gasTile.amount <= 0)
			{
				world.setBlockToAir(x, y, z);
				return;
			} else if(gasTile.spreadGas())
			{
				world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
			} else if(gasTile.amount > 10)
			{
				if(world.scheduledUpdatesAreImmediate)
				{
					world.scheduleBlockUpdateFromLoad(x, y, z, this.blockID, this.tickRate(world), 0);
				} else
				{
					world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(world));
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
			if(igniteList.contains("" + world.getBlockId(x + pos[0], y + pos[1], z + pos[2])))
			{
				return true;
			} else
			{
				TileEntity tile = world.getBlockTileEntity(x + pos[0], y + pos[1], z + pos[2]);
				
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

	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
	{
		if(world.scheduledUpdatesAreImmediate)
		{
			world.scheduleBlockUpdateFromLoad(x, y, z, this.blockID, this.tickRate(world), 0);
		} else
		{
			world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(world));
		}
		
		if(world.isRemote && (blockID == ObjectHandler.gasBlock.blockID || blockID == ObjectHandler.fireGasBlock.blockID))
		{
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			
			if(tile != null && tile instanceof TileEntityGas)
			{
				TileEntityGas gasTile = (TileEntityGas)tile;
				
				gasTile.updateOpacity();
				gasTile.updateSize();
				Minecraft.getMinecraft().renderGlobal.markBlockForRenderUpdate(x, y, z);
			}
		}
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		
		if(tile instanceof TileEntityGas && entity instanceof EntityLivingBase)
		{
			TileEntityGas gasTile = (TileEntityGas)tile;
			gasTile.doAllEffects((EntityLivingBase)entity);
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World world)
	{
		TileEntityGas tile = new TileEntityGas(world);
		return tile;
	}
	
	public Icon getIcon(int par1, int par2)
	{
		return gasIcon;
	}
	
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.gasIcon = par1IconRegister.registerIcon("enviromine:block_gas");
	}
	
	/**
	 * Return whether this block can drop from an explosion.
	 */
	public boolean canDropFromExplosion(Explosion par1Explosion)
	{
		return false;
	}
	
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return 0;
    }
    
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }
    
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion)
    {
    	if(world.isBlockNormalCubeDefault(x, y - 1, z, false) && this.blockID == ObjectHandler.fireGasBlock.blockID)
    	{
    		world.setBlock(x, y, z, Block.fire.blockID);
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
			
			TileEntity tile = world.getBlockTileEntity(x + pos[0], y + pos[1], z + pos[2]);
			
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
		TileEntity tile = blockAccess.getBlockTileEntity(i, j, k);
		
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
		TileEntity tile = blockAccess.getBlockTileEntity(i, j, k);
		
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
