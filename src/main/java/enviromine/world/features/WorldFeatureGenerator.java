package enviromine.world.features;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;
import enviromine.blocks.tiles.TileEntityGas;
import enviromine.core.EM_Settings;
import enviromine.gases.EnviroGasDictionary;
import enviromine.handlers.ObjectHandler;
import enviromine.world.features.mineshaft.MineshaftBuilder;

public class WorldFeatureGenerator implements IWorldGenerator
{
	public static ArrayList<int[]> pendingMines = new ArrayList<int[]>();
	public static boolean disableMineScan = false;
	
	public WorldFeatureGenerator()
	{
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		if(world.isRemote)
		{
			return;
		}
		
		if(EM_Settings.oldMineGen)
		{
			if(!disableMineScan)
			{
				MineshaftBuilder.scanGrids(world, chunkX, chunkZ, random);
			}
			
			for(int i = MineshaftBuilder.pendingBuilders.size() - 1; i >= 0; i--)
			{
				MineshaftBuilder builder = MineshaftBuilder.pendingBuilders.get(i);
				
				if(builder.checkAndBuildSegments(chunkX, chunkZ))
				{
					MineshaftBuilder.pendingBuilders.remove(i);
					if(MineshaftBuilder.scannedGrids.containsKey("" + (chunkX/64) + "," + (chunkZ/64) + "," + world.provider.dimensionId))
					{
						int remBuilders = MineshaftBuilder.scannedGrids.get("" + (chunkX/64) + "," + (chunkZ/64) + "," + world.provider.dimensionId);
						MineshaftBuilder.scannedGrids.put("" + (chunkX/64) + "," + (chunkZ/64) + "," + world.provider.dimensionId, remBuilders - 1);
					}
				}
			}
		}
		
		if(EM_Settings.gasGen)
		{
			for(int i = 25; i >= 0; i--)
			{
				GenGasPocket(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
			}
		}
	}
	
	public void GenGasPocket(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		int rX = (chunkX * 16) + random.nextInt(16);
		int rY = 1 + random.nextInt(32);
		int rZ = (chunkZ * 16) + random.nextInt(16);
		
		while(world.getBlockId(rX, rY - 1, rZ) == 0 && rY > 1)
		{
			rY -= 1;
		}
		
		if(world.getBlockId(rX, rY, rZ) == 0)
		{
			Block bBlock = Block.blocksList[world.getBlockId(rX, rY - 1, rZ)];
			if(rY < 16 && rY > 0)
			{
				if(bBlock != null && bBlock.blockMaterial == Material.water)
				{
					world.setBlock(rX, rY, rZ, ObjectHandler.gasBlock.blockID, 0, 2);
					TileEntity tile = world.getBlockTileEntity(rX, rY, rZ);
					
					if(tile instanceof TileEntityGas)
					{
						TileEntityGas gasTile = (TileEntityGas)tile;
						gasTile.addGas(EnviroGasDictionary.hydrogenSulfide.gasID, 10);
						//EnviroMine.logger.log(Level.INFO, "Generating hydrogen sulfide at (" + rX + "," + rY + "," + rZ + ")");
					}
				} else if(bBlock != null && (bBlock.blockMaterial == Material.lava || bBlock.blockMaterial == Material.fire))
				{
					world.setBlock(rX, rY, rZ, ObjectHandler.gasBlock.blockID, 0, 2);
					TileEntity tile = world.getBlockTileEntity(rX, rY, rZ);
					
					if(tile instanceof TileEntityGas)
					{
						TileEntityGas gasTile = (TileEntityGas)tile;
						gasTile.addGas(EnviroGasDictionary.carbonMonoxide.gasID, 25);
						gasTile.addGas(EnviroGasDictionary.sulfurDioxide.gasID, 25);
						//EnviroMine.logger.log(Level.INFO, "Generating carbon monoxide at (" + rX + "," + rY + "," + rZ + ")");
					}
				} else
				{
					world.setBlock(rX, rY, rZ, ObjectHandler.gasBlock.blockID, 0, 2);
					TileEntity tile = world.getBlockTileEntity(rX, rY, rZ);
					
					if(tile instanceof TileEntityGas)
					{
						TileEntityGas gasTile = (TileEntityGas)tile;
						gasTile.addGas(EnviroGasDictionary.sulfurDioxide.gasID, 20);
						gasTile.addGas(EnviroGasDictionary.carbonDioxide.gasID, 30);
						//EnviroMine.logger.log(Level.INFO, "Generating sulfur dioxide at (" + rX + "," + rY + "," + rZ + ")");
					}
				}
			} else
			{
				world.setBlock(rX, rY, rZ, ObjectHandler.gasBlock.blockID, 0, 2);
				TileEntity tile = world.getBlockTileEntity(rX, rY, rZ);
				
				if(tile instanceof TileEntityGas)
				{
					TileEntityGas gasTile = (TileEntityGas)tile;
					gasTile.addGas(EnviroGasDictionary.carbonDioxide.gasID, 50);
					//EnviroMine.logger.log(Level.INFO, "Generating carbon dioxide at (" + rX + "," + rY + "," + rZ + ")");
				}
			}
		}
	}
	
	public void SavePendingMines()
	{
	}
	
	public void LoadPendingMines()
	{
	}
}
