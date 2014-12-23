package enviromine.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.Level;
import com.google.common.base.Stopwatch;
import enviromine.EntityPhysicsBlock;
import enviromine.client.gui.hud.items.Debug_Info;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.BlockProperties;
import enviromine.trackers.properties.StabilityType;
import enviromine.utils.EnviroUtils;

public class EM_PhysManager
{
	public static ArrayList<String> usedSlidePositions = new ArrayList<String>();
	public static HashMap<String, String> excluded = new HashMap<String, String>();
	public static ArrayList<Object[]> physSchedule = new ArrayList<Object[]>();
	public static HashMap<String, Long> chunkDelay = new HashMap<String, Long>();
	
	public static int currentTime = 0;
	public static int debugInterval = 30;
	public static int debugTime = 0;
	public static int debugUpdatesCaptured = 0;
	private static Stopwatch timer = Stopwatch.createUnstarted();
	
	public static long worldStartTime = -1;
	
	public static void schedulePhysUpdate(World world, int x, int y, int z, boolean updateSelf, String type)
	{
		if(world.isRemote || world.getTotalWorldTime() < worldStartTime + EM_Settings.worldDelay)
		{
			return;
		} else if(chunkDelay.containsKey("" + (x >> 4) + "," + (z >> 4)))
		{
			if(chunkDelay.get("" + (x >> 4) + "," + (z >> 4)) > world.getTotalWorldTime())
			{
				return;
			}
		}
		
		Object[] entry = new Object[6];
		entry[0] = world;
		entry[1] = x;
		entry[2] = y;
		entry[3] = z;
		entry[4] = updateSelf;
		entry[5] = type;
		
		physSchedule.add(entry);
	}
	
	public static void scheduleSlideUpdate(World world, int x, int y, int z)
	{
		if(world.isRemote || world.getTotalWorldTime() < worldStartTime + EM_Settings.worldDelay)
		{
			return;
		} else if(chunkDelay.containsKey("" + (x >> 4) + "," + (z >> 4)))
		{
			if(chunkDelay.get("" + (x >> 4) + "," + (z >> 4)) > world.getTotalWorldTime())
			{
				return;
			}
		}
		
		if(world.isAirBlock(x, y, z))
		{
			return;
		}
		
		Object[] entry = new Object[6];
		entry[0] = world;
		entry[1] = x;
		entry[2] = y;
		entry[3] = z;
		entry[4] = true;
		entry[5] = "Slide";
		
		physSchedule.add(entry);
	}
	
	public static void updateSurroundingWithExclusions(World world, int x, int y, int z, boolean updateSelf, String type)
	{
		if(world.isRemote)
		{
			return;
		}
		
		for(int i = -1; i <= 1; i++)
		{
			for(int j = -1; j <= 1; j++)
			{
				for(int k = -1; k <= 1; k++)
				{
					if(timer.elapsed(TimeUnit.SECONDS) > 2)
					{
						return;
					}
					
					String position = (new StringBuilder()).append(x + i).append(",").append(y + j).append(",").append(z + k).toString();
					if(i == 0 && j == 0 && k == 0)
					{
						if(updateSelf)
						{
							callPhysUpdate(world, x, y, z, type);
						} else
						{
							excluded.put(position, type);
							continue;
						}
					} else
					{
						callPhysUpdate(world, x + i, y + j, k + z, type);
					}
					
					if(physSchedule.size() <= 0)
					{
						return;
					}
				}
			}
		}
	}
	
	public static void callPhysUpdate(World world, int x, int y, int z, String type)
	{
		if(world.isRemote)
		{
			return;
		}
		
		callPhysUpdate(world, x, y, z, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z), type);
	}
	
	public static void callPhysUpdate(World world, int x, int y, int z, Block block, int meta, String type)
	{
		String position = (new StringBuilder()).append(x).append(",").append(y).append(",").append(z).toString();
		
		if(excluded.containsKey(position))
		{
			if(!excluded.get(position).equals("Collapse") && type.equals("Collapse"))
			{
				excluded.put(position, type);
			} else
			{
				return;
			}
		} else
		{
			excluded.put(position, type);
		}
		
		boolean locLoaded = false;
		
		if(world.getChunkProvider().chunkExists(x >> 4, z >> 4))
		{
			locLoaded = world.getChunkFromChunkCoords(x >> 4, z >> 4).isChunkLoaded;
		} else
		{
			locLoaded = false;
		}
		
		if(world.isRemote || block == null || !locLoaded)
		{
			return;
		}
		
		if(EnviroMine.proxy.isClient())
		{
			debugUpdatesCaptured += 1;
		}
		
		int[] blockData = getSurroundingBlockData(world, x, y, z);
		
		boolean waterLogged = false;
		//boolean isMuddy = false;
		boolean touchingWaterDirect = blockData[2] > 0;//isTouchingLiquid(world, x, y, z, true);
		boolean touchingWater = blockData[3] > 0;//isTouchingLiquid(world, x, y, z, false);
		
		BlockProperties blockProps = null;
		
		Chunk chunk = world.getChunkFromBlockCoords(x, z);
		if(chunk != null)
		{
			waterLogged = (chunk.getBiomeGenForWorldCoords(x & 15, z & 15, world.getWorldChunkManager()).rainfall > 0 && world.isRaining() && world.canBlockSeeTheSky(x, y + 1, z)) || touchingWater;
		}
		
		boolean validSlideType = false;
		boolean emptyBelow = BlockFalling.func_149831_e(world, x, y - 1, z);
		
		if(EM_Settings.blockProperties.containsKey("" + Block.blockRegistry.getNameForObject(block) + "," + meta) || EM_Settings.blockProperties.containsKey("" + Block.blockRegistry.getNameForObject(block)))
		{
			if(EM_Settings.blockProperties.containsKey("" + Block.blockRegistry.getNameForObject(block) + "," + meta))
			{
				blockProps = EM_Settings.blockProperties.get("" + Block.blockRegistry.getNameForObject(block) + "," + meta);
			} else
			{
				blockProps = EM_Settings.blockProperties.get("" + Block.blockRegistry.getNameForObject(block));
			}
			
			validSlideType = blockProps.slides || ((waterLogged || touchingWater) && blockProps.wetSlide);
			//isMuddy = ((waterLogged || touchingWater) && blockProps.wetSlide);
		}/* else if(block instanceof BlockFalling || ((block == Blocks.dirt || block == Blocks.snow || block == Blocks.snow_layer) && (waterLogged || touchingWater)))
		{
			if(block instanceof BlockAnvil)
			{
				validSlideType = false;
			} else
			{
				validSlideType = true;
				isMuddy = (block == Blocks.dirt || block == Blocks.snow || block == Blocks.snow_layer);
			}
		}*/
		
		if(validSlideType && EM_Settings.enableLandslide)
		{
			if(!(block instanceof BlockFalling) && blockData[4] >= 1)
			{
				return;
			}
			Block slideBlock = block;
			int slideMeta = meta;
			
			int[] pos = new int[]{x, y, z};
			int[] npos = slideDirection(world, pos, true);
			int[] ppos = slideDirection(world, pos, false);
			
			TileEntity tile = world.getTileEntity(x, y, z);
			NBTTagCompound nbtTC = new NBTTagCompound();
			
			if(tile != null)
			{
				tile.writeToNBT(nbtTC);
			}
			
			if(emptyBelow)
			{
				if(!(block instanceof BlockFalling) && !usedSlidePositions.contains("" + pos[0] + "," + pos[2]))
				{
					//usedSlidePositions.add("" + pos[0] + "," + pos[2]);
					EntityPhysicsBlock physBlock = new EntityPhysicsBlock(world, pos[0] + 0.5, pos[1] + 0.5, pos[2] + 0.5, slideBlock, slideMeta, false);
					if(tile != null)
					{
						physBlock.field_145810_d = nbtTC;
					}
					world.setBlock(x, y, z, Blocks.air);
					physBlock.isLandSlide = true;
					world.spawnEntityInWorld(physBlock);
					EM_PhysManager.schedulePhysUpdate(world, x, y, z, true, "Collapse");
					return;
				}
			} else if(!(pos[0] == npos[0] && pos[1] == npos[1] && pos[2] == npos[2]) && !usedSlidePositions.contains("" + npos[0] + "," + npos[2]))
			{
				//world.setBlock(npos[0], npos[1], npos[2], slideID, slideMeta, 2);
				//usedSlidePositions.add("" + npos[0] + "," + npos[2]);
				
				EntityPhysicsBlock physBlock = new EntityPhysicsBlock(world, npos[0] + 0.5, npos[1] + 0.5, npos[2] + 0.5, slideBlock, slideMeta, false);
				if(tile != null)
				{
					physBlock.field_145810_d = nbtTC;
				}
				world.setBlock(x, y, z, Blocks.air);
				physBlock.isLandSlide = true;
				world.spawnEntityInWorld(physBlock);
				EM_PhysManager.schedulePhysUpdate(world, x, y, z, true, "Collapse");
				return;
			} else if(!(pos[0] == ppos[0] && pos[1] == ppos[1] && pos[2] == ppos[2]))
			{
				EM_PhysManager.scheduleSlideUpdate(world, x, y, z);
			}
		}
		
		if(isLegalType(world, x, y, z) && blockNotSolid(world, x, y - 1, z, false) && blockData[4] <= 0)
		{
			Object dropBlock = block;
			int dropMeta = -1;
			int dropNum = -1;
			int dropType = 0;
			
			boolean isCustom = false;
			boolean defaultDrop = true;
			
			if(blockProps != null)
			{
				isCustom = true;
				defaultDrop = false;
				
				if(blockProps.dropName.equals(""))
				{
					dropType = -1;
					defaultDrop = true;
					dropNum = blockProps.dropNum;
				} else if(Block.getBlockFromName(blockProps.dropName) != null && blockProps.dropNum <= 0)
				{
					dropType = 1;
					dropBlock = Block.getBlockFromName(blockProps.dropName);
					if(blockProps.dropMeta <= -1)
					{
						dropMeta = -1;
					} else
					{
						dropMeta = blockProps.dropMeta;
					}
					dropNum = 0;
				} else if(Item.getItemFromBlock(Block.getBlockFromName(blockProps.dropName)) != null && blockProps.dropNum > 0)
				{
					dropType = 2;
					dropBlock = Item.getItemFromBlock(Block.getBlockFromName(blockProps.dropName));
					if(blockProps.dropMeta <= -1)
					{
						dropMeta = -1;
					} else
					{
						dropMeta = blockProps.dropMeta;
					}
					dropNum = blockProps.dropNum;
				} else
				{
					dropType = 0;
					dropBlock = null;
					dropMeta = -1;
					dropNum = -1;
				}
			}
			
			if(!defaultDrop)
			{
			} else if(dropBlock == null || dropBlock == Blocks.air || block.getMaterial() == Material.glass || block.getMaterial() == Material.ice)
			{
				dropType = 0;
			} else if(block instanceof BlockLeavesBase)
			{
				dropType = -1;
			} else if(dropBlock instanceof Block)
			{
				dropType = 1;
			} else if(dropBlock instanceof Item)
			{
				dropType = 2;
			} else
			{
				dropType = -1;
			}
			
			int minThreshold = 10;
			int maxThreshold = 15;
			int supportDist = 1;
			int yMax = 1;
			
			StabilityType stabType = EnviroUtils.getDefaultStabilityType(block);
			
			if(isCustom)
			{
				minThreshold = blockProps.minFall;
				maxThreshold = blockProps.maxFall;
				supportDist = blockProps.supportDist;
				if(blockProps.canHang)
				{
					yMax = 2;
				} else
				{
					yMax = 1;
				}
			} else if(stabType != null)
			{
				minThreshold = stabType.minFall;
				maxThreshold = stabType.maxFall;
				supportDist = stabType.supportDist;
				if(stabType.canHang)
				{
					yMax = 2;
				} else
				{
					yMax = 1;
				}
			}
			
			int missingBlocks = 0;
			
			if(yMax >= 2)
			{
				missingBlocks = blockData[0];
			} else
			{
				missingBlocks = blockData[1];
			}
			
			int dropChance = maxThreshold - missingBlocks;
			
			if(dropChance <= 0)
			{
				dropChance = 1;
			}
			
			boolean supported = hasSupports(world, x, y, z, touchingWaterDirect? MathHelper.floor_double(supportDist/2D) : supportDist);
			//missingBlocks total = 25 - 26
			
			if(missingBlocks > 0 && blockNotSolid(world, x, y - 1, z, false) && !supported)
			{
				if(!world.isRemote && ((missingBlocks > minThreshold && (world.rand.nextInt(dropChance) == 0 || type.equals("Collapse"))) || missingBlocks >= maxThreshold || (touchingWaterDirect && world.rand.nextBoolean())))
				{
					if(dropType == -1)
					{
						world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (world.getBlockMetadata(x, y, z) << 12));
						block.dropBlockAsItem(world, x, y, z, meta, 0);
						world.setBlock(x, y, z, Blocks.air);
						return;
					} else if(dropType == 2)
					{
						world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (world.getBlockMetadata(x, y, z) << 12));
						if(isCustom && dropMeta > -1)
						{
							if(dropNum >= 1)
							{
								if(dropBlock instanceof Item)
								{
									dropItemstack(world, x, y, z, new ItemStack((Item)dropBlock, dropNum, dropMeta));
								} else if(dropBlock instanceof Block)
								{
									dropItemstack(world, x, y, z, new ItemStack((Block)dropBlock, dropNum, dropMeta));
								}
							}
						} else if(isCustom && dropNum >= 1)
						{
							if(dropBlock instanceof Item)
							{
								dropItemstack(world, x, y, z, new ItemStack((Item)dropBlock, dropNum, meta));
							} else if(dropBlock instanceof Block)
							{
								dropItemstack(world, x, y, z, new ItemStack((Block)dropBlock, dropNum, meta));
							}
						} else if(!isCustom)
						{
							block.dropBlockAsItem(world, x, y, z, meta, 0);
						}
						world.setBlock(x, y, z, Blocks.air);
						schedulePhysUpdate(world, x, y, z, true, "Normal");
						return;
					} else if(dropType == 0)
					{
						world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (world.getBlockMetadata(x, y, z) << 12));
						
						if(block == Blocks.ice)
						{
							Material mat = world.getBlock(x, y - 1, z).getMaterial();
							
							if((mat.blocksMovement() || mat.isLiquid()) && !world.provider.isHellWorld)
							{
								world.setBlock(x, y, z, Blocks.flowing_water);
							} else
							{
								world.setBlock(x, y, z, Blocks.air);
							}
						} else
						{
							world.setBlock(x, y, z, Blocks.air);
						}
						
						if(block.getMaterial() != Material.ice || EM_Settings.spreadIce)
						{
							schedulePhysUpdate(world, x, y, z, true, "Break");
						}
						return;
					}
					
					if(dropType != 1)
					{
						return;
					}
					if(block == Blocks.stone && EM_Settings.stoneCracks && !isCustom)
					{
						world.setBlock(x, y, z, Blocks.cobblestone);
						dropBlock = Blocks.cobblestone;
					} else if(block == Blocks.grass && !isCustom)
					{
						world.setBlock(x, y, z, Blocks.dirt);
						dropBlock = Blocks.dirt;
					} else
					{
						if(world.getBlock(x, y, z) != dropBlock)
						{
							world.setBlock(x, y, z, (Block)dropBlock, world.getBlockMetadata(x, y, z), 2);
						}
					}
					
					TileEntity tile = world.getTileEntity(x, y, z);
					NBTTagCompound nbtTC = new NBTTagCompound();
					
					if(tile != null)
					{
						tile.writeToNBT(nbtTC);
					}
					
					EntityPhysicsBlock entityphysblock;
					if(isCustom && dropMeta > -1)
					{
						entityphysblock = new EntityPhysicsBlock(world, (float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, (Block)dropBlock, dropMeta, true);
					} else
					{
						entityphysblock = new EntityPhysicsBlock(world, (float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, (Block)dropBlock, world.getBlockMetadata(x, y, z), true);
					}
					
					if(tile != null)
					{
						entityphysblock.field_145810_d = nbtTC;
					}
					//world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(entityphysblock.block) + (entityphysblock.meta << 12));
					world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), entityphysblock.block.stepSound.func_150496_b(), (entityphysblock.block.stepSound.getVolume() + 1.0F) / 2.0F, entityphysblock.block.stepSound.getPitch() * 1.2F);
					world.spawnEntityInWorld(entityphysblock);
					
				} else if(missingBlocks > minThreshold && !world.isRemote && EM_Settings.stoneCracks)
				{
					if(block == Blocks.stone && !isCustom)
					{
						world.setBlock(x, y, z, Blocks.cobblestone);
					} else if(block == Blocks.grass && !isCustom)
					{
						world.setBlock(x, y, z, Blocks.dirt);
					}
				}
			}
		}
	}
	
	/**
	 * 0 - missing solidBlocks<br/>
	 * 1 - missing solidBlocks level or below<br/>
	 * 2 - contactLiquids<br/>
	 * 3 - allLiquids<br/>
	 * 4 - heldUpByOther (0/1)
	 */
	public static int[] getSurroundingBlockData(World world, int x, int y, int z)
	{
		int[] data = new int[]{0,0,0,0,0};
		
		for(int i = x - 1; i <= x + 1; i++)
		{
			for(int j = y - 1; j <= y + 1; j++)
			{
				for(int k = z - 1; k <= z + 1; k++)
				{
					Block block = world.getBlock(i, j, k);
					Material material = block.getMaterial();
					int metaID = world.getBlockMetadata(i, j, k);
					String name = Block.blockRegistry.getNameForObject(block);
					
					if(block == Blocks.air)
					{
						if(j < y + 1)
						{
							data[1] += 1;
						}
						
						data[0] += 1;
						continue;
					}
					
					StabilityType stabType = EnviroUtils.getDefaultStabilityType(block);
					
					if(material != null && material.isLiquid())
					{
						if(j >= y && (i == x || k == z))
						{
							data[2] += 1;
							data[3] += 1;
						} else
						{
							data[3] += 1;
						}
					}
					
					BlockProperties blockProps = null;
					
					if(EM_Settings.blockProperties.containsKey("" + name) || EM_Settings.blockProperties.containsKey("" + name + "," + metaID))
					{
						if(EM_Settings.blockProperties.containsKey("" + name + "," + metaID))
						{
							blockProps = EM_Settings.blockProperties.get("" + name + "," + metaID);
						} else if(EM_Settings.blockProperties.containsKey("" + name))
						{
							blockProps = EM_Settings.blockProperties.get("" + name);
						}
					}
					
					if(blockProps != null)
					{
						if(blockProps.holdsOthers)
						{
							data[4] = 1;
						}
					} else if(stabType != null)
					{
						if(stabType != null && stabType.holdOther)
						{
							data[4] = 1;
						}
					}
					
					if(world.getEntitiesWithinAABB(EntityPhysicsBlock.class, AxisAlignedBB.getBoundingBox(i, j, k, i + 1, j + 1, k + 1)).size() > 0)
					{
						if(j < y + 1)
						{
							data[1] += 1;
						}
						
						data[0] += 1;
					} else if((blockNotSolid(world, i, j, k, false) || (world.getBlock(x, y, z).getMaterial() != Material.leaves && material == Material.leaves)) && !(i == x && j < y + 1 && k == z))
					{
						if(j < y + 1)
						{
							data[1] += 1;
						}
						data[0] += 1;
					}
				}
			}
		}
		
		data[1] += 9;
		
		return data;
	}

	public static boolean hasSupports(World world, int x, int y, int z, int dist)
	{
		if(dist <= 0)
		{
			return false;
		}
		
		Block baseBlock = world.getBlock(x, y, z);
		Material baseMat = baseBlock == null? Material.air : baseBlock.getMaterial();
		
		boolean isLeaves = baseMat == Material.leaves;
		
		for(int i = x - 1; i <= x + 1; i++)
		{
			for(int k = z - 1; k <= z + 1; k++)
			{
				int j = y - 1;
				Block block = world.getBlock(i, j, k);
				Material material = block == null? Material.air : block.getMaterial();
				
				
				if(!(blockNotSolid(world, i, j, k, false) || (material == Material.leaves && !isLeaves)))
				{
					return true;
				}
			}
		}
		for(int i = x + 1; i <= x + dist; i++)
		{
			int k = z;
			
			boolean cancel = false;
			
			for(int j = y - 1; j <= y; j++)
			{
				Block block = world.getBlock(i, j, k);
				Material material = block == null? Material.air : block.getMaterial();
				
				if(j == y)
				{
					if(blockNotSolid(world, i, j, k, false) || (material == Material.leaves && !isLeaves))
					{
						cancel = true;
						break;
					} else
					{
						continue;
					}
				} else
				{
					if(blockNotSolid(world, i, j, k, false) || (material == Material.leaves && !isLeaves))
					{
						continue;
					} else
					{
						return true;
					}
				}
			}
			
			if(cancel)
			{
				break;
			}
		}
		
		for(int i = x - 1; i >= x - dist; i--)
		{
			int k = z;
			
			boolean cancel = false;
			
			for(int j = y - 1; j <= y; j++)
			{
				Block block = world.getBlock(i, j, k);
				Material material = block == null? Material.air : block.getMaterial();
				
				if(j == y)
				{
					if(blockNotSolid(world, i, j, k, false) || (material == Material.leaves && !isLeaves))
					{
						cancel = true;
						break;
					} else
					{
						continue;
					}
				} else
				{
					if(blockNotSolid(world, i, j, k, false) || (material == Material.leaves && !isLeaves))
					{
						continue;
					} else
					{
						return true;
					}
				}
			}
			
			if(cancel)
			{
				break;
			}
		}
		
		for(int k = z + 1; k <= z + dist; k++)
		{
			int i = x;
			
			boolean cancel = false;
			
			for(int j = y - 1; j <= y; j++)
			{
				Block block = world.getBlock(i, j, k);
				Material material = block == null? Material.air : block.getMaterial();
				
				if(j == y)
				{
					if(blockNotSolid(world, i, j, k, false) || (material == Material.leaves && !isLeaves))
					{
						cancel = true;
						break;
					} else
					{
						continue;
					}
				} else
				{
					if(blockNotSolid(world, i, j, k, false) || (material == Material.leaves && !isLeaves))
					{
						continue;
					} else
					{
						return true;
					}
				}
			}
			
			if(cancel)
			{
				break;
			}
		}
		
		for(int k = z - 1; k >= z - dist; k--)
		{
			int i = x;
			
			boolean cancel = false;
			
			for(int j = y - 1; j <= y; j++)
			{
				Block block = world.getBlock(i, j, k);
				Material material = block == null? Material.air : block.getMaterial();
				
				if(j == y)
				{
					if(blockNotSolid(world, i, j, k, false) || (material == Material.leaves && !isLeaves))
					{
						cancel = true;
						break;
					} else
					{
						continue;
					}
				} else
				{
					if(blockNotSolid(world, i, j, k, false) || (material == Material.leaves && !isLeaves))
					{
						continue;
					} else
					{
						return true;
					}
				}
			}
			
			if(cancel)
			{
				break;
			}
		}
		
		return false;
	}
	
	protected static void dropItemstack(World par1World, int par2, int par3, int par4, ItemStack par5ItemStack)
	{
		if(!par1World.isRemote && par1World.getGameRules().getGameRuleBooleanValue("doTileDrops"))
		{
			float f = 0.7F;
			double d0 = (double)(par1World.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
			double d1 = (double)(par1World.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
			double d2 = (double)(par1World.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
			EntityItem entityitem = new EntityItem(par1World, (double)par2 + d0, (double)par3 + d1, (double)par4 + d2, par5ItemStack);
			entityitem.delayBeforeCanPickup = 10;
			par1World.spawnEntityInWorld(entityitem);
		}
	}
	
	public static boolean isLegalType(World world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);
		
		if(block == null)
		{
			return false;
		}
		
		String name = Block.blockRegistry.getNameForObject(block);
		int meta = world.getBlockMetadata(x, y, z);
		
		if(EM_Settings.blockProperties.containsKey("" + name + "," + meta) || EM_Settings.blockProperties.containsKey("" + name))
		{
			if(EM_Settings.blockProperties.containsKey("" + name + "," + meta))
			{
				BlockProperties blockProps = EM_Settings.blockProperties.get("" + name + "," + meta);
				return(blockProps.hasPhys && !blockProps.holdsOthers);
			} else
			{
				BlockProperties blockProps = EM_Settings.blockProperties.get("" + name);
				return(blockProps.hasPhys && !blockProps.holdsOthers);
			}
		} else
		{
			if(block.getBlockHardness(world, x, y, z) >= 0)
			{
				StabilityType stabType = EnviroUtils.getDefaultStabilityType(block);
				
				if(stabType != null)
				{
					return stabType.enablePhysics && !stabType.holdOther;
				} else
				{
					return false;
				}
			} else
			{
				return false;
			}
		}
	}
	
	public static boolean blockNotSolid(World world, int x, int y, int z, boolean isSliding)
	{
		if(world.isAirBlock(x, y, z))
		{
			return true;
		}
		
		Block block = world.getBlock(x, y, z);
		Material material = block.getMaterial();
		
		if(block == Blocks.fire)
		{
			return true;
		} else if(material.isLiquid())
		{
			return !isSliding;
		} else if(block.getCollisionBoundingBoxFromPool(world, x, y, z) == null || !material.blocksMovement())
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	public static void updateSchedule()
	{
		if(MinecraftServer.getServer() == null)
		{
			return;
		}
		
		if(physSchedule.size() >= 4096 && EM_Settings.updateCap <= -1)
		{
			EnviroMine.logger.log(Level.ERROR, "Physics updates exeeded 4096! Dumping update schedule");
			physSchedule.clear();
			return;
		}
		
		if(EnviroMine.proxy.isClient())
		{
			if(debugTime == 0)
			{
				if(!timer.isRunning())
				{
					timer.reset();
					timer.start();
				}
				debugUpdatesCaptured = 0;
			}
		}
		
		boolean canClear = true;
		if(currentTime >= EM_Settings.physInterval)
		{
			int updateNum = 0;
			
			if(physSchedule.size() <= EM_Settings.updateCap || EM_Settings.updateCap < 0)
			{
				updateNum = physSchedule.size();
			} else
			{
				updateNum = EM_Settings.updateCap;
			}
			
			int updateRem = physSchedule.size();
			
			for(int i = updateNum - 1; i >= 0; i -= 1)
			{
				if(!MinecraftServer.getServer().isServerRunning())
				{
					physSchedule.clear();
					physSchedule = new ArrayList<Object[]>();
					canClear = true;
					break;
				}
				
				if(EnviroMine.proxy.isClient() && Minecraft.getMinecraft().isIntegratedServerRunning())
				{
					if(Minecraft.getMinecraft().isGamePaused() && !EnviroMine.proxy.isOpenToLAN())
					{
						if(timer.isRunning())
						{
							timer.stop();
							debugTime = 0;
						}
						break;
					} else
					{
						if(!timer.isRunning())
						{
							timer.start();
						}
					}
				}
				
				if(timer.elapsed(TimeUnit.SECONDS) > 2)
				{
					EnviroMine.logger.log(Level.ERROR, "Physics updates are taking too long! Dumping schedule!");
					physSchedule.clear();
					canClear = false;
					break;
				}
				
				if(physSchedule.size() - 1 < i)
				{
					EnviroMine.logger.log(Level.ERROR, "Unable to get physcis schedule entry, index out of bounds! (Size: " + physSchedule.size() + ", Index: " + i +")");
					canClear = false;
					break;
				}
				
				Object[] entry = physSchedule.get(i);
				
				boolean locLoaded = false;
				
				if(((World)entry[0]).getChunkProvider().chunkExists((Integer)entry[1] >> 4, (Integer)entry[3] >> 4))
				{
					locLoaded = ((World)entry[0]).getChunkFromChunkCoords((Integer)entry[1] >> 4, (Integer)entry[3] >> 4).isChunkLoaded;
				} else
				{
					locLoaded = false;
				}
				
				if(locLoaded)
				{
					canClear = false;
					if(((String)entry[5]).equalsIgnoreCase("Slide"))
					{
						String position = (new StringBuilder()).append((Integer)entry[1]).append(",").append((Integer)entry[2]).append(",").append((Integer)entry[3]).toString();
						if(!excluded.containsKey(position))
						{
							excluded.put(position, (String)entry[5]);
							callPhysUpdate((World)entry[0], (Integer)entry[1], (Integer)entry[2], (Integer)entry[3], (String)entry[5]);
						}
					} else
					{
						updateSurroundingWithExclusions((World)entry[0], (Integer)entry[1], (Integer)entry[2], (Integer)entry[3], (Boolean)entry[4], (String)entry[5]);
					}
				}
				
				if(physSchedule.size() < updateRem)
				{
					EnviroMine.logger.log(Level.ERROR, "Physics schedule dumped early! Resetting scheduler...");
					physSchedule.clear();
					canClear = false;
					break;
				}
				
				if(physSchedule.size() - 1 >= i)
				{
					physSchedule.remove(i);
				} else
				{
					EnviroMine.logger.log(Level.ERROR, "Failed to remove entry from physics schedule. Things may break badly!");
				}
				
				updateRem = physSchedule.size();
			}
			currentTime = 0;
		} else
		{
			currentTime += 1;
		}
		
		if(canClear)
		{
			excluded.clear();
			usedSlidePositions.clear();
		}
		
		if(EnviroMine.proxy.isClient() && debugTime >= debugInterval && timer.isRunning())
		{
			timer.stop();
			Debug_Info.DB_physTimer = timer.toString();
			Debug_Info.DB_physUpdates = debugUpdatesCaptured;
			Debug_Info.DB_physBuffer = physSchedule.size();
			timer.reset();
			debugTime = 0;
		} else if(EnviroMine.proxy.isClient())
		{
			debugTime += 1;
		}
	}
	
	public static int[] slideDirection(World world, int[] pos, boolean checkEntities)
	{
		if(pos.length != 3)
		{
			return pos;
		}
		
		int[] npos = new int[3];
		
		int x = pos[0];
		int y = pos[1];
		int z = pos[2];
		
		npos[0] = x;
		npos[1] = y;
		npos[2] = z;
		
		ArrayList<String> canSlideDir = new ArrayList<String>();
		
		if(blockNotSolid(world, x + 1, y, z, true) && blockNotSolid(world, x + 1, y - 1, z, false) && (!checkEntities || world.getEntitiesWithinAABB(EntityPhysicsBlock.class, AxisAlignedBB.getBoundingBox(x + 1, y - 2, z, x + 2, y, z + 1)).size() <= 0))
		{
			canSlideDir.add("X+");
		}
		if(blockNotSolid(world, x - 1, y, z, true) && blockNotSolid(world, x - 1, y - 1, z, false) && (!checkEntities || world.getEntitiesWithinAABB(EntityPhysicsBlock.class, AxisAlignedBB.getBoundingBox(x - 1, y - 2, z, x, y, z + 1)).size() <= 0))
		{
			canSlideDir.add("X-");
		}
		if(blockNotSolid(world, x, y, z + 1, true) && blockNotSolid(world, x, y - 1, z + 1, false) && (!checkEntities || world.getEntitiesWithinAABB(EntityPhysicsBlock.class, AxisAlignedBB.getBoundingBox(x, y - 2, z + 1, x + 1, y, z + 2)).size() <= 0))
		{
			canSlideDir.add("Z+");
		}
		if(blockNotSolid(world, x, y, z - 1, true) && blockNotSolid(world, x, y - 1, z - 1, false) && (!checkEntities || world.getEntitiesWithinAABB(EntityPhysicsBlock.class, AxisAlignedBB.getBoundingBox(x, y - 2, z - 1, x + 1, y, z)).size() <= 0))
		{
			canSlideDir.add("Z-");
		}
		
		if(canSlideDir.size() >= 1)
		{
			String slideDir = "";
			
			slideDir = canSlideDir.get(world.rand.nextInt(canSlideDir.size()));
			
			if(slideDir == "X+")
			{
				npos[0] = x + 1;
			} else if(slideDir == "X-")
			{
				npos[0] = x - 1;
			} else if(slideDir == "Z+")
			{
				npos[2] = z + 1;
			} else if(slideDir == "Z-")
			{
				npos[2] = z - 1;
			}
		}
		
		return npos;
	}
}