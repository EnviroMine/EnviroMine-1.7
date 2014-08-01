package enviromine;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.ComponentVillage;
import net.minecraft.world.gen.structure.ComponentVillageStartPiece;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureMineshaftPieces;

public class EM_VillageMineshaft extends ComponentVillage
{
	private int averageGroundLevel = -1;
	private int maxDistance = 120;
	
	public EM_VillageMineshaft()
	{
	}
	
	public EM_VillageMineshaft(ComponentVillageStartPiece par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
	{
		super(par1ComponentVillageStartPiece, par2);
		this.coordBaseMode = par5;
		this.boundingBox = par4StructureBoundingBox;
	}
	
	public static EM_VillageMineshaft buildComponent(ComponentVillageStartPiece par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
	{
		StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 4, 4, 4, par6);
		return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par1List, structureboundingbox) == null ? new EM_VillageMineshaft(par0ComponentVillageStartPiece, par7, par2Random, structureboundingbox, par6) : null;
	}
	
	/**
	 * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
	 * the end, it adds Fences...
	 */
	public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
	{
		if(par1World.isRemote)
		{
			return true;
		}
		// --- Calibrate Depth --- //
		
		if(this.averageGroundLevel < 0)
		{
			this.averageGroundLevel = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);
			
			if(this.averageGroundLevel < 16)
			{
				return true;
			}
		}
		
		boundingBox.minY = averageGroundLevel;
		
		while(boundingBox.minY > 12)
		{
			boundingBox.minY -= 4;
		}
		
		int shaftTop = averageGroundLevel - boundingBox.minY;
		
		// --- Generate Shaft --- //
		
		this.customFillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 4, shaftTop, 4, Block.fence.blockID, Block.fence.blockID, false);
		this.customFillWithBlocks(par1World, par3StructureBoundingBox, 0, shaftTop + 1, 0, 4, shaftTop + 2, 4, 0, 0, false);
		
		for(int depth = 3; depth < shaftTop; depth += 4)
		{
			this.customFillWithBlocks(par1World, par3StructureBoundingBox, 0, depth, 0, 4, depth, 4, Block.planks.blockID, Block.planks.blockID, false);
		}
		
		this.customFillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 0, shaftTop - 2, 0, Block.wood.blockID, Block.wood.blockID, false);
		this.customFillWithBlocks(par1World, par3StructureBoundingBox, 4, 0, 0, 4, shaftTop - 2, 0, Block.wood.blockID, Block.wood.blockID, false);
		this.customFillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, 4, 0, shaftTop - 2, 4, Block.wood.blockID, Block.wood.blockID, false);
		this.customFillWithBlocks(par1World, par3StructureBoundingBox, 4, 0, 4, 4, shaftTop - 2, 4, Block.wood.blockID, Block.wood.blockID, false);
		
		this.customFillWithBlocks(par1World, par3StructureBoundingBox, 2, 0, 0, 2, shaftTop - 2, 0, Block.planks.blockID, Block.planks.blockID, false);
		
		this.customPlaceBlockAtCurrentPosition(par1World, 0, 0, 2, shaftTop, 0, par3StructureBoundingBox);
		
		this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 1, 3, shaftTop, 3, 0, 0, false);
		
		int var4 = this.getMetadataWithOffset(Block.ladder.blockID, 2);
		
		for(int ladderDepth = 0; ladderDepth < shaftTop; ladderDepth += 1)
		{
			this.customPlaceBlockAtCurrentPosition(par1World, Block.ladder.blockID, var4, 2, ladderDepth, 1, par3StructureBoundingBox);
		}
		
		for(int sectionHeight = 0; sectionHeight < shaftTop - 8; sectionHeight += 8)
		{
			if(par2Random.nextInt(10) == 0 || sectionHeight == 0)
			{
				if(sectionHeight != 0)
				{
					this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1, sectionHeight - 1, 1, 3, sectionHeight - 1, 3, Block.planks.blockID, Block.planks.blockID, false);
					this.customPlaceBlockAtCurrentPosition(par1World, Block.ladder.blockID, var4, 2, sectionHeight - 1, 1, par3StructureBoundingBox);
				}
				
				genSegmentsAtCurrentHeight(par1World, par2Random, par3StructureBoundingBox, 0, sectionHeight, 0);
			}
		}
		
		return true;
	}
	
	protected void genSegmentsAtCurrentHeight(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4, int par5, int par6)
	{
		// --- Generate Segments --- //
		
		int curX = par4;
		int curY = par5;
		int curZ = par6;
		
		int preX = curX;
		int preZ = curZ;
		
		boolean containsLoot = false;
		boolean ventDivider = false;
		
		switch(par2Random.nextInt(3))
		{
			case 0:
			{
				curX += 4;
				break;
			}
			
			case 1:
			{
				curX -= 4;
				break;
			}
			
			case 2:
			{
				curZ += 4;
				break;
			}
			
			case 3:
			{
				curZ -= 4;
				break;
			}
		}
		
		int segmentCount = 32;
		
		for(int loop = segmentCount; loop > 0; loop -= 1)
		{
			containsLoot = false;
			ventDivider = false;
			
			if(curX == 0 && curZ == 0)
			{
				switch(par2Random.nextInt(3))
				{
					case 0:
					{
						curX += 4;
						break;
					}
					
					case 1:
					{
						curX -= 4;
						break;
					}
					
					case 2:
					{
						curZ += 4;
						break;
					}
					
					case 3:
					{
						curZ -= 4;
						break;
					}
				}
				
				loop += 1;
				continue;
			} else if(curX >= maxDistance || curZ >= maxDistance)
			{
				curX = preX;
				curZ = preZ;
				
				switch(par2Random.nextInt(3))
				{
					case 0:
					{
						curX += 4;
						break;
					}
					
					case 1:
					{
						curX -= 4;
						break;
					}
					
					case 2:
					{
						curZ += 4;
						break;
					}
					
					case 3:
					{
						curZ -= 4;
						break;
					}
				}
				
				loop += 1;
				continue;
			}
			
			// --- Pre Checks --- //
			
			if(customGetBlockIdAtCurrentPosition(par1World, 2 + curX, 0 + curY, 2 + curZ, par3StructureBoundingBox) == Block.chest.blockID)
			{
				containsLoot = true;
			}
			
			if((customGetBlockIdAtCurrentPosition(par1World, 2 + curX, -1 + curY, 2 + curZ, par3StructureBoundingBox) == 0 || customGetBlockIdAtCurrentPosition(par1World, 2 + curX, -1 + curY, 2 + curZ, par3StructureBoundingBox) == Block.trapdoor.blockID) && customGetBlockSkyLightAtCurrentPosition(par1World, 2 + curX, -1 + curY, 2 + curZ, par3StructureBoundingBox) >= 15)
			{
				ventDivider = true;
			}
			
			if(!containsLoot)
			{
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1 + curX, 0 + curY, 1 + curZ, 3 + curX, 2 + curY, 3 + curZ, 0, 0, false);
			} else
			{
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1 + curX, 1 + curY, 1 + curZ, 3 + curX, 2 + curY, 3 + curZ, 0, 0, false);
				
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1 + curX, 0 + curY, 1 + curZ, 3 + curX, 0 + curY, 1 + curZ, 0, 0, false);
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1 + curX, 0 + curY, 1 + curZ, 1 + curX, 0 + curY, 3 + curZ, 0, 0, false);
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 3 + curX, 0 + curY, 3 + curZ, 3 + curX, 0 + curY, 1 + curZ, 0, 0, false);
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 3 + curX, 0 + curY, 3 + curZ, 1 + curX, 0 + curY, 3 + curZ, 0, 0, false);
			}
			
			// --- Base Segment --- //
			if((customGetBlockIdAtCurrentPosition(par1World, 2 + curX, 1 + curY, 0 + curZ, par3StructureBoundingBox) == Block.fence.blockID && customGetBlockIdAtCurrentPosition(par1World, 2 + curX, 1 + curY, -1 + curZ, par3StructureBoundingBox) == 0) || customGetBlockIdAtCurrentPosition(par1World, 2 + curX, 1 + curY, 0 + curZ, par3StructureBoundingBox) == 0)
			{
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1 + curX, 0 + curY, 0 + curZ, 3 + curX, 2 + curY, 0 + curZ, 0, 0, false);
			} else if(customGetBlockIdAtCurrentPosition(par1World, 2 + curX, 1 + curY, -1 + curZ, par3StructureBoundingBox) != Block.ladder.blockID)
			{
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1 + curX, 0 + curY, 0 + curZ, 3 + curX, 2 + curY, 0 + curZ, Block.fence.blockID, Block.fence.blockID, false);
			}
			
			if((customGetBlockIdAtCurrentPosition(par1World, 0 + curX, 1 + curY, 2 + curZ, par3StructureBoundingBox) == Block.fence.blockID && customGetBlockIdAtCurrentPosition(par1World, -1 + curX, 1 + curY, 2 + curZ, par3StructureBoundingBox) == 0) || customGetBlockIdAtCurrentPosition(par1World, 0 + curX, 1 + curY, 2 + curZ, par3StructureBoundingBox) == 0)
			{
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 0 + curX, 0 + curY, 1 + curZ, 0 + curX, 2 + curY, 3 + curZ, 0, 0, false);
			} else if(customGetBlockIdAtCurrentPosition(par1World, -1 + curX, 1 + curY, 2 + curZ, par3StructureBoundingBox) != Block.ladder.blockID)
			{
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 0 + curX, 0 + curY, 1 + curZ, 0 + curX, 2 + curY, 3 + curZ, Block.fence.blockID, Block.fence.blockID, false);
			}
			
			if((customGetBlockIdAtCurrentPosition(par1World, 2 + curX, 1 + curY, 4 + curZ, par3StructureBoundingBox) == Block.fence.blockID && customGetBlockIdAtCurrentPosition(par1World, 2 + curX, 1 + curY, 5 + curZ, par3StructureBoundingBox) == 0) || customGetBlockIdAtCurrentPosition(par1World, 2 + curX, 1 + curY, 4 + curZ, par3StructureBoundingBox) == 0)
			{
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1 + curX, 0 + curY, 4 + curZ, 3 + curX, 2 + curY, 4 + curZ, 0, 0, false);
			} else if(customGetBlockIdAtCurrentPosition(par1World, 2 + curX, 1 + curY, 5 + curZ, par3StructureBoundingBox) != Block.ladder.blockID)
			{
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1 + curX, 0 + curY, 4 + curZ, 3 + curX, 2 + curY, 4 + curZ, Block.fence.blockID, Block.fence.blockID, false);
			}
			
			if((customGetBlockIdAtCurrentPosition(par1World, 4 + curX, 1 + curY, 2 + curZ, par3StructureBoundingBox) == Block.fence.blockID && customGetBlockIdAtCurrentPosition(par1World, 5 + curX, 1 + curY, 2 + curZ, par3StructureBoundingBox) == 0) || customGetBlockIdAtCurrentPosition(par1World, 4 + curX, 1 + curY, 2 + curZ, par3StructureBoundingBox) == 0)
			{
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 4 + curX, 0 + curY, 1 + curZ, 4 + curX, 2 + curY, 3 + curZ, 0, 0, false);
			} else if(customGetBlockIdAtCurrentPosition(par1World, 5 + curX, 1 + curY, 2 + curZ, par3StructureBoundingBox) != Block.ladder.blockID)
			{
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 4 + curX, 0 + curY, 1 + curZ, 4 + curX, 2 + curY, 3 + curZ, Block.fence.blockID, Block.fence.blockID, false);
			}
			
			if(customGetBlockIdAtCurrentPosition(par1World, 2 + curX, 3 + curY, 2 + curZ, par3StructureBoundingBox) != Block.trapdoor.blockID)
			{
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 0 + curX, 3 + curY, 0 + curZ, 4 + curX, 3 + curY, 4 + curZ, Block.planks.blockID, Block.planks.blockID, false);
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1 + curX, 3 + curY, 1 + curZ, 3 + curX, 3 + curY, 3 + curZ, 0, 0, false);
			}
			
			this.customFillWithBlocks(par1World, par3StructureBoundingBox, 0 + curX, 0 + curY, 0 + curZ, 0 + curX, 3 + curY, 0 + curZ, Block.wood.blockID, Block.wood.blockID, false);
			this.customFillWithBlocks(par1World, par3StructureBoundingBox, 4 + curX, 0 + curY, 0 + curZ, 4 + curX, 3 + curY, 0 + curZ, Block.wood.blockID, Block.wood.blockID, false);
			this.customFillWithBlocks(par1World, par3StructureBoundingBox, 0 + curX, 0 + curY, 4 + curZ, 0 + curX, 3 + curY, 4 + curZ, Block.wood.blockID, Block.wood.blockID, false);
			this.customFillWithBlocks(par1World, par3StructureBoundingBox, 4 + curX, 0 + curY, 4 + curZ, 4 + curX, 3 + curY, 4 + curZ, Block.wood.blockID, Block.wood.blockID, false);
			
			int tempBlock;
			
			for(int i = 0; i <= 4; i++)
			{
				for(int k = 0; k <= 4; k++)
				{
					tempBlock = customGetBlockIdAtCurrentPosition(par1World, i + curX, -1 + curY, k + curZ, par3StructureBoundingBox);
					if(tempBlock == 0 || tempBlock == Block.lavaMoving.blockID || tempBlock == Block.lavaStill.blockID || tempBlock == Block.waterMoving.blockID || tempBlock == Block.waterStill.blockID)
					{
						//if(!(i == 2 && k == 2 && ventDivider))
						{
							customPlaceBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, i + curX, -1 + curY, k + curZ, par3StructureBoundingBox);
						}
					}
				}
			}
			
			// --- Air-Vent --- //
			
			if(par2Random.nextInt(50) == 0 && !ventDivider)
			{
				int ventGroundLevel = 3 + curY;
				
				while(customGetBlockSkyLightAtCurrentPosition(par1World, 2 + curX, ventGroundLevel + 1, 2 + curZ, par3StructureBoundingBox) < 15)
				{
					ventGroundLevel += 4;
				}
				
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1 + curX, 3 + curY, 1 + curZ, 3 + curX, ventGroundLevel, 3 + curZ, Block.fence.blockID, Block.fence.blockID, false);
				
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1 + curX, 3 + curY, 1 + curZ, 1 + curX, ventGroundLevel, 1 + curZ, Block.wood.blockID, Block.wood.blockID, false);
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 3 + curX, 3 + curY, 1 + curZ, 3 + curX, ventGroundLevel, 1 + curZ, Block.wood.blockID, Block.wood.blockID, false);
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1 + curX, 3 + curY, 3 + curZ, 1 + curX, ventGroundLevel, 3 + curZ, Block.wood.blockID, Block.wood.blockID, false);
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 3 + curX, 3 + curY, 3 + curZ, 3 + curX, ventGroundLevel, 3 + curZ, Block.wood.blockID, Block.wood.blockID, false);
				
				for(int ventHeight = 3 + curY; ventHeight <= ventGroundLevel; ventHeight += 4)
				{
					this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1 + curX, ventHeight, 1 + curZ, 3 + curX, ventHeight, 3 + curZ, Block.planks.blockID, Block.planks.blockID, false);
				}
				
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 2 + curX, 3 + curY, 2 + curZ, 2 + curX, ventGroundLevel, 2 + curZ, 0, 0, false);
				
				this.customPlaceBlockAtCurrentPosition(par1World, Block.trapdoor.blockID, 0 | 8, 2 + curX, ventGroundLevel, 2 + curZ, par3StructureBoundingBox);
				this.customPlaceBlockAtCurrentPosition(par1World, Block.trapdoor.blockID, 0, 2 + curX, 3 + curY, 2 + curZ, par3StructureBoundingBox);
			} else if(par2Random.nextInt(20) == 0 && !ventDivider)
			{
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1 + curX, 3 + curY, 1 + curZ, 3 + curX, 3 + curY, 3 + curZ, Block.leaves.blockID, Block.leaves.blockID, false);
				this.customPlaceBlockAtCurrentPosition(par1World, Block.wood.blockID, 0, 2 + curX, 3 + curY, 2 + curZ, par3StructureBoundingBox);
			}
			
			if(ventDivider)
			{
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1 + curX, -1 + curY, 1 + curZ, 3 + curX, -1 + curY, 3 + curZ, Block.planks.blockID, Block.planks.blockID, false);
				this.customFillWithBlocks(par1World, par3StructureBoundingBox, 1 + curX, 3 + curY, 1 + curZ, 3 + curX, 3 + curY, 3 + curZ, Block.planks.blockID, Block.planks.blockID, false);
				this.customPlaceBlockAtCurrentPosition(par1World, Block.trapdoor.blockID, 0, 2 + curX, 3 + curY, 2 + curZ, par3StructureBoundingBox);
				this.customPlaceBlockAtCurrentPosition(par1World, Block.trapdoor.blockID, 0 | 8, 2 + curX, -1 + curY, 2 + curZ, par3StructureBoundingBox);
			}
			
			// --- Place Loot --- //
			
			if(par2Random.nextInt(100) == 0 && !containsLoot && !ventDivider)
			{
				this.customGenerateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 2 + curX, 0 + curY, 2 + curZ, StructureMineshaftPieces.mineshaftChestContents, 3 + par2Random.nextInt(4));
			}
			
			preX = curX;
			preZ = curZ;
			
			switch(par2Random.nextInt(3))
			{
				case 0:
				{
					curX += 4;
					break;
				}
				
				case 1:
				{
					curX -= 4;
					break;
				}
				
				case 2:
				{
					curZ += 4;
					break;
				}
				
				case 3:
				{
					curZ -= 4;
					break;
				}
			}
		}
	}
	
	protected void customFillWithBlocks(World par1World, StructureBoundingBox par2StructureBoundingBox, int par3, int par4, int par5, int par6, int par7, int par8, int par9, int par10, boolean par11)
	{
		for(int var12 = par4; var12 <= par7; ++var12)
		{
			for(int var13 = par3; var13 <= par6; ++var13)
			{
				for(int var14 = par5; var14 <= par8; ++var14)
				{
					if(!par11 || this.getBlockIdAtCurrentPosition(par1World, var13, var12, var14, par2StructureBoundingBox) != 0)
					{
						if(var12 != par4 && var12 != par7 && var13 != par3 && var13 != par6 && var14 != par5 && var14 != par8)
						{
							this.customPlaceBlockAtCurrentPosition(par1World, par10, 0, var13, var12, var14, par2StructureBoundingBox);
						} else
						{
							this.customPlaceBlockAtCurrentPosition(par1World, par9, 0, var13, var12, var14, par2StructureBoundingBox);
						}
					}
				}
			}
		}
	}
	
	protected void customPlaceBlockAtCurrentPosition(World par1World, int par2, int par3, int par4, int par5, int par6, StructureBoundingBox par7StructureBoundingBox)
	{
		int var8 = this.getXWithOffset(par4, par6);
		int var9 = this.getYWithOffset(par5);
		int var10 = this.getZWithOffset(par4, par6);
		int var11 = this.getBiomeSpecificBlockMetadata(par2, par3);
		
		if(par1World.getChunkFromBlockCoords(var8, var10) == null)
		{
			return;
		}
		
		par1World.setBlock(var8, var9, var10, par2, var11, 2);
	}
	
	protected int customGetBlockIdAtCurrentPosition(World par1World, int par2, int par3, int par4, StructureBoundingBox par5StructureBoundingBox)
	{
		int var6 = this.getXWithOffset(par2, par4);
		int var7 = this.getYWithOffset(par3);
		int var8 = this.getZWithOffset(par2, par4);
		
		if(par1World.getChunkFromBlockCoords(var6, var8) == null)
		{
			return 0;
		} else
		{
			return par1World.getBlockId(var6, var7, var8);
		}
	}
	
	protected int customGetBlockSkyLightAtCurrentPosition(World par1World, int par2, int par3, int par4, StructureBoundingBox par5StructureBoundingBox)
	{
		int var6 = this.getXWithOffset(par2, par4);
		int var7 = this.getYWithOffset(par3);
		int var8 = this.getZWithOffset(par2, par4);
		Chunk chunk = par1World.getChunkFromBlockCoords(var6, var8);
		return chunk.getSavedLightValue(EnumSkyBlock.Sky, var6 & 0xf, var7, var8 & 0xf);
	}
	
	protected boolean customGenerateStructureChestContents(World par1World, StructureBoundingBox par2StructureBoundingBox, Random par3Random, int par4, int par5, int par6, WeightedRandomChestContent[] par7ArrayOfWeightedRandomChestContent, int par8)
	{
		int var9 = this.getXWithOffset(par4, par6);
		int var10 = this.getYWithOffset(par5);
		int var11 = this.getZWithOffset(par4, par6);
		
		if(par1World.getChunkFromBlockCoords(var9, var11) == null)
		{
			return false;
		}
		
		par1World.setBlock(var9, var10, var11, Block.chest.blockID);
		TileEntityChest var12 = (TileEntityChest)par1World.getBlockTileEntity(var9, var10, var11);
		
		if(var12 != null)
		{
			WeightedRandomChestContent.generateChestContents(par3Random, par7ArrayOfWeightedRandomChestContent, var12, par8);
		}
		
		return true;
	}
}
