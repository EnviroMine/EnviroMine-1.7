package enviromine.blocks.tiles;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import enviromine.EnviroUtils;
import enviromine.blocks.BlockGas;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.gases.EnviroGas;
import enviromine.gases.EnviroGasDictionary;
import enviromine.handlers.ObjectHandler;
import java.awt.Color;
import java.util.ArrayList;
import org.apache.logging.log4j.Level;

public class TileEntityGas extends TileEntity
{
	public ArrayList<int[]> gases = new ArrayList<int[]>();
	public Color color = Color.WHITE;
	public float opacity = 1.0F;
	public float yMax = 1.0F;
	public float yMin = 0.0F;
	public int amount = 0;
	
	public int firePressure = 0;
	
	public boolean preReqRender = true;
	public boolean curReqRender = true;
	
	public TileEntityGas()
	{
	}
	
	public TileEntityGas(World world)
	{
		this.worldObj = world;
	}
	
	public void doAllEffects(EntityLivingBase entityLiving)
	{
		if(gases.size() <= 0)
		{
			return;
		}
		
		for(int i = 0; i < gases.size(); i++)
		{
			int[] gasArray = gases.get(i);
			EnviroGasDictionary.gasList[gasArray[0]].applyEffects(entityLiving, gasArray[1]);
		}
	}
	
	public void updateColor()
	{
		if(gases.size() <= 0)
		{
			this.color = Color.WHITE;
			return;
		}
		
		Color fCol = null;
		
		for(int i = 0; i < gases.size(); i++)
		{
			if(fCol == null)
			{
				fCol = EnviroGasDictionary.gasList[gases.get(i)[0]].color;
			} else
			{
				int[] gasArray = gases.get(i);
				EnviroGas gas = EnviroGasDictionary.gasList[gasArray[0]];
				float opacity =  gas.getOpacity()*gasArray[1];
				opacity = opacity >= 1.0F? 1.0F : opacity;
				fCol = EnviroUtils.blendColors(fCol.getRGB(), gas.color.getRGB(), opacity / 0.5F);
			}
		}
		
		this.color = fCol;
	}
	
	public void updateOpacity()
	{
		if(gases.size() <= 0 || this.amount == 0)
		{
			this.opacity = 1F;
			return;
		}
		
		float alpha = 0F;
		
		for(int i = 0; i < gases.size(); i++)
		{
			int[] gasArray = gases.get(i);
			alpha += EnviroGasDictionary.gasList[gasArray[0]].getOpacity()*gasArray[1];
		}
		
		if(alpha >= 1F)
		{
			this.opacity = 1F;
		} else
		{
			this.opacity = alpha;
		}
		
		if(this.worldObj != null)
		{
			TileEntity tile1 = this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord);
			TileEntity tile2 = this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord);
			TileEntity tile3 = this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord);
			TileEntity tile4 = this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);
			TileEntity tile5 = this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1);
			TileEntity tile6 = this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);
			
			if(tile1 != null && tile1 instanceof TileEntityGas)
			{
				TileEntityGas gasTile1 = (TileEntityGas)tile1;
				
				if(gasTile1.opacity > 0.1F && this.opacity > 0.1F && gasTile1.opacity > this.opacity)
				{
					this.opacity = gasTile1.opacity;
				}
			}
			if(tile2 != null && tile2 instanceof TileEntityGas)
			{
				TileEntityGas gasTile2 = (TileEntityGas)tile2;
				
				if(gasTile2.opacity > 0.1F && this.opacity > 0.1F && gasTile2.opacity > this.opacity)
				{
					this.opacity = gasTile2.opacity;
				}
			}
			if(tile3 != null && tile3 instanceof TileEntityGas)
			{
				TileEntityGas gasTile3 = (TileEntityGas)tile3;
				
				if(gasTile3.opacity > 0.1F && this.opacity > 0.1F && gasTile3.opacity > this.opacity)
				{
					this.opacity = gasTile3.opacity;
				}
			}
			if(tile4 != null && tile4 instanceof TileEntityGas)
			{
				TileEntityGas gasTile4 = (TileEntityGas)tile4;
				
				if(gasTile4.opacity > 0.1F && this.opacity > 0.1F && gasTile4.opacity > this.opacity)
				{
					this.opacity = gasTile4.opacity;
				}
			}
			if(tile5 != null && tile5 instanceof TileEntityGas)
			{
				TileEntityGas gasTile5 = (TileEntityGas)tile5;
				
				if(gasTile5.opacity > 0.1F && this.opacity > 0.1F && gasTile5.opacity > this.opacity)
				{
					this.opacity = gasTile5.opacity;
				}
			}
			if(tile6 != null && tile6 instanceof TileEntityGas)
			{
				TileEntityGas gasTile6 = (TileEntityGas)tile6;
				
				if(gasTile6.opacity > 0.1F && this.opacity > 0.1F && gasTile6.opacity > this.opacity)
				{
					this.opacity = gasTile6.opacity;
				}
			}
		}
	}
	
	public void updateSize()
	{
		if(this.amount >= 10)
		{
			yMax = 1.0F;
			yMin = 0.0F;
			return;
		} else if(this.amount <= 0)
		{
			yMax = 0.0F;
			yMin = 0.0F;
			return;
		}
		
		boolean lightGas = false;
		boolean heavyGas = false;
		
		for(int i = 0; i < gases.size(); i++)
		{
			int[] gasArray = gases.get(i);
			float density = EnviroGasDictionary.gasList[gasArray[0]].density;
			
			if(density >= 1F)
			{
				heavyGas = true;
			} else if(density <= -1F)
			{
				lightGas = true;
			}
			
			if(lightGas && heavyGas)
			{
				yMax = 1.0F;
				yMin = 0.0F;
				return;
			}
		}
		
		if(this.amount >= 10)
		{
			yMax = 1.0F;
			yMin = 0.0F;
			return;
		} else if(lightGas)
		{
			yMax = 1.0F;
			yMin = 1.0F - (this.amount/10F);
		} else if(heavyGas)
		{
			yMax = this.amount/10F;
			yMin = 0.0F;
		} else
		{
			yMax = 0.5F + (this.amount/20F);
			yMin = 0.5F - (this.amount/20F);
		}
		
		if(this.worldObj != null)
		{
			TileEntity tile1 = this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord);
			TileEntity tile2 = this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord);
			
			if(tile1 != null && tile1 instanceof TileEntityGas)
			{
				TileEntityGas gasTile1 = (TileEntityGas)tile1;
				
				if(gasTile1.opacity > 0.1F)
				{
					yMax = 1F;
				}
			}
			if(tile2 != null && tile2 instanceof TileEntityGas)
			{
				TileEntityGas gasTile2 = (TileEntityGas)tile2;
				
				if(gasTile2.opacity > 0.1F)
				{
					yMin = 0F;
				}
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		
		if(!par1NBTTagCompound.hasKey("GasArray"))
		{
			return;
		}
		
		int[] savedGases = par1NBTTagCompound.getIntArray("GasArray");
		
		if(savedGases.length > 0)
		{
			gases = new ArrayList<int[]>();
		} else
		{
			EnviroMine.logger.log(Level.ERROR, "GasTile loaded 0 gases, this should not happen!");
		}
		
		for(int i = 0; i < savedGases.length; i++)
		{
			this.addGas(savedGases[i], 1);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		
		this.updateAmount();
		
		int[] savedGases = new int[this.amount];
		int index = 0;
		
		for(int i = 0; i < gases.size(); i++)
		{
			int[] gasArray = gases.get(i);
			
			for(int j = 0; j < gasArray[1]; j++)
			{
				savedGases[index] = gasArray[0];
				index++;
			}
		}
		
		par1NBTTagCompound.setIntArray("GasArray", savedGases);
	}
	
	public void updateAmount()
	{
		this.amount = getGasQuantity(-1);
	}
	
	public int getGasQuantity(int id)
	{
		int total = 0;
		for(int i = 0; i < gases.size(); i++)
		{
			int[] gasArray = gases.get(i);
			if(gasArray[0] == id || id <= -1)
			{
				total += gasArray[1];
			}
		}
		
		return total;
	}
	
	public void updateRender()
	{
		if(this.worldObj == null)
		{
			return;
		}
		
		if(!this.worldObj.isRemote)
		{
			this.checkNeedsReRender();
			
			if(!preReqRender && !curReqRender)
			{
				return;
			} else
			{
				Packet packet = this.getDescriptionPacket();
				
				MinecraftServer.getServer().getConfigurationManager().sendToAllNear(this.xCoord, this.yCoord, this.zCoord, 128, this.worldObj.provider.dimensionId, packet);
			}
		} else
		{
			Minecraft.getMinecraft().renderGlobal.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
		}
	}
	
	public void checkNeedsReRender()
	{
		boolean shouldRender = false;
		
		for(int i = 0; i < 6; i++)
		{
			if(((BlockGas)this.getBlockType()).shouldSideBeRendered(this.worldObj, this.xCoord, this.yCoord, this.zCoord, i))
			{
				shouldRender = true;
			}
		}
		
		preReqRender = curReqRender;
		curReqRender = shouldRender;
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbttagcompound);
	}
	
	public void addGas(int id, int addNum)
	{
		if(addNum <= 0 || !this.hasWorldObj())
		{
			return;
		}
		
		for(int i = 0; i < gases.size(); i++)
		{
			int[] gasArray = gases.get(i);
			if(gasArray[0] == id)
			{
				gases.set(i, new int[]{id, gasArray[1] + addNum});
				this.updateAmount();
				this.updateColor();
				this.updateOpacity();
				this.updateSize();
				this.sortGasesByDensity();
				return;
			}
		}
		gases.add(new int[]{id, addNum});
		
		if(id == 0)
		{
			if(this.getBlockType() == ObjectHandler.gasBlock)
			{
				this.burnGases();
				((BlockGas)this.getBlockType()).swtichIgnitionState(this.getWorldObj(), this.xCoord, this.yCoord, this.zCoord);
			}
		}
		
		this.updateAmount();
		this.updateColor();
		this.updateOpacity();
		this.updateSize();
		this.sortGasesByDensity();
	}
	
	public void subtractGas(int id, int takeNum)
	{
		if(takeNum <= 0)
		{
			return;
		}
		
		for(int i = 0; i < gases.size(); i++)
		{
			int[] gasArray = gases.get(i);
			if(gasArray[0] == id)
			{
				if(gasArray[1] <= takeNum)
				{
					gases.remove(i);
					break;
				} else
				{
					gases.set(i, new int[]{id, gasArray[1] - takeNum});
					break;
				}
			}
		}
		
		this.updateAmount();
		this.updateColor();
		this.updateOpacity();
		this.updateSize();
	}
	
	public boolean burnGases()
	{
		boolean didBurn = false;
		int fireSize = 0;
		ArrayList<int[]> burntGases = new ArrayList<int[]>();
		
		for(int i = 0; i < gases.size(); i ++)
		{
			int[] gasArray = gases.get(i);
			int fire = EnviroGasDictionary.gasList[gasArray[0]].getFire(gasArray[1], this.amount >= 10? 0 : 10 - this.amount);
			float vol = EnviroGasDictionary.gasList[gasArray[0]].volitility;
			if(vol > 0)
			{
				burntGases.add(gasArray);
				fireSize += fire;
				didBurn = true;
			}
		}
		
		if(burntGases.size() >= 1)
		{
			for(int i = 0; i < burntGases.size(); i++)
			{
				int[] burntArray = burntGases.get(i);
				this.subtractGas(burntArray[0], burntArray[1]);
			}
		}
		
		if(fireSize >= 1)
		{
			this.addGas(EnviroGasDictionary.gasFire.gasID, fireSize);
		}
		
		return didBurn;
	}
	
	public void sortGasesByDensity()
	{
		if(gases.size() <= 1)
		{
			return;
		}
		
		for(int i = 1; i < gases.size(); i++)
		{
			EnviroGas gasA = EnviroGasDictionary.gasList[gases.get(i)[0]];
			EnviroGas gasB = EnviroGasDictionary.gasList[gases.get(i-1)[0]];
			if(gasA.density < gasB.density)
			{
				for(int j = i - 1; j >= 0; j--)
				{
					EnviroGas gasC = EnviroGasDictionary.gasList[gases.get(j)[0]];
					EnviroGas gasD = j == 0? gasA : EnviroGasDictionary.gasList[gases.get(j-1)[0]];
					if(j == 0 || (gasA.density < gasC.density && gasA.density >= gasD.density))
					{
						int[] tmpGas = gases.get(i);
						gases.remove(i);
						gases.add(j, tmpGas);
						break;
					}
				}
			}
		}
	}
	
	public boolean spreadGas()
	{
		if(this.gases.size() <= 0 || this.amount == 0)
		{
			return false;
		}
		
		if(this.getBlockType() == Blocks.air)
		{
			EnviroMine.logger.log(Level.ERROR, "TileEntityGas has null block type!");
			return false;
		} else if(!(this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) instanceof BlockGas))
		{
			EnviroMine.logger.log(Level.ERROR, "TileEntityGas has no block at position!");
			return false;
		}
		
		boolean changed = false;
		
		ArrayList<int[]> gDir = new ArrayList<int[]>();
		int[] dArray = this.getGasDistribution();
		
		gDir.add(new int[]{-1,0,0,dArray[0]});
		gDir.add(new int[]{1,0,0,dArray[1]});
		gDir.add(new int[]{0,-1,0,dArray[2]});
		gDir.add(new int[]{0,1,0,dArray[3]});
		gDir.add(new int[]{0,0,-1,dArray[4]});
		gDir.add(new int[]{0,0,1,dArray[5]});
		
		if(doDecay())
		{
			changed = true;
		}
		
		for(int i = gDir.size(); i > 0; i--)
		{
			int index = this.worldObj.rand.nextInt(gDir.size());
			
			int[] rDir = gDir.get(index);
			
			if(rDir[1] == 0 && this.amount <= 1 || !this.worldObj.getChunkProvider().chunkExists((this.xCoord + rDir[0])/16, (this.zCoord + rDir[2])/16))
			{
				gDir.remove(index);
				continue;
			}
			
			TileEntity tile = this.worldObj.getTileEntity(this.xCoord + rDir[0], this.yCoord + rDir[1], this.zCoord + rDir[2]);
			
			if((tile != null && tile instanceof TileEntityGas) || this.worldObj.getBlock(this.xCoord + rDir[0], this.yCoord + rDir[1], this.zCoord + rDir[2]) == Blocks.air)
			{
				if(this.offLoadGas(this.xCoord + rDir[0], this.yCoord + rDir[1], this.zCoord + rDir[2], rDir[3]))
				{
					changed = true;
				}
			}
			
			tile = this.worldObj.getTileEntity(this.xCoord + rDir[0], this.yCoord + rDir[1], this.zCoord + rDir[2]);
			
			if(tile != null && tile instanceof TileEntityGas)
			{
				TileEntityGas gasTile = (TileEntityGas)tile;
				
				if(gasTile.gases.size() <= 0 || gasTile.amount <= 0)
				{
					this.worldObj.setBlockToAir(this.xCoord + rDir[0], this.yCoord + rDir[1], this.zCoord + rDir[2]);
				}
			}
			
			gDir.remove(index);
		}
		
		if(changed)
		{
			/*this.updateColor();
			this.updateAmount();
			this.updateOpacity();
			this.updateSize();*/
		}
		return changed;
	}
	
	public boolean offLoadGas(int i, int j, int k, int offLoadNum)
	{
		if(gases.size() <= 0 || this.amount <= 0 || j < 0 || j > 255 || offLoadNum <= 0)
		{
			return false;
		}
		
		int gasMode = EM_Settings.gasWaterLike? 1 : 0;
		
		int vDir = j - this.yCoord;
		
		TileEntity tile = this.worldObj.getTileEntity(i, j, k);
		if(tile == null)
		{
			if(this.worldObj.getBlock(i, j, k) == Blocks.air && this.getBlockType() != Blocks.air)
			{
				this.worldObj.setBlock(i, j, k, this.getBlockType());
				
				if(this.worldObj.getTileEntity(i, j, k) == null)
				{
					return false;
				} else
				{
					return this.offLoadGas(i, j, k, offLoadNum);
				}
			} else
			{
				return false;
			}
		} else if(!(tile instanceof TileEntityGas))
		{
			return false;
		} else
		{
			TileEntityGas gasTile = (TileEntityGas)tile;
			
			if(gasTile.amount + gasMode >= this.amount && this.amount <= 10 && vDir == 0 && this.getBlockType() != ObjectHandler.fireGasBlock)
			{
				return false;
			} else if(vDir != 0 && this.amount <= 10 && gasTile.amount >= 10 && this.getBlockType() != ObjectHandler.fireGasBlock)
			{
				return false;
			}
			
			int[] selGas = null;
			
			if(EnviroGasDictionary.gasList[gases.get(0)[0]].density < 0F && vDir == 1 && (gasTile.amount < 10 || this.amount > 10))
			{
				selGas = gases.get(0);
			} else if(EnviroGasDictionary.gasList[gases.get(gases.size()-1)[0]].density > 0F && vDir == -1 && (gasTile.amount < 10 || this.amount > 10))
			{
				selGas = gases.get(gases.size() -1);
			} else
			{
				if(this.getBlockType() == ObjectHandler.fireGasBlock && this.getGasQuantity(0) > 0)
				{
					selGas = new int[]{0, this.getGasQuantity(0)};
				} else
				{
					for(int index = 0; index < gases.size(); index++)
					{
						EnviroGas gasType = EnviroGasDictionary.gasList[gases.get(index)[0]];
						
						if(gasType.density < 0F && vDir == -1 && this.amount <= 10)
						{
							continue;
						} else if(gasType.density > 0F && vDir == 1 && this.amount <= 10)
						{
							continue;
						} else
						{
							selGas = gases.get(index);
							break;
						}
					}
				}
			}
			
			//selGas = gases.get(this.worldObj.rand.nextInt(gases.size()));
			
			if(selGas == null)
			{
				return false;
			}
			
			int gasDiff = selGas[1] < offLoadNum? selGas[1] : offLoadNum;
			gasTile.addGas(selGas[0], gasDiff);
			this.subtractGas(selGas[0], gasDiff);
			gasTile.updateRender();
			return true;
		}
	}
	
	public boolean doDecay()
	{
		boolean decayed = false;
		
		int skyLight = 0;
		
		Chunk chunk = this.worldObj.getChunkFromBlockCoords(this.xCoord, this.zCoord);
		
		if(this.yCoord > 0 && chunk != null)
		{
			if(this.yCoord >= 256)
			{
				skyLight = 15;
			} else
			{
				skyLight = chunk.getSavedLightValue(EnumSkyBlock.Sky, this.xCoord & 0xf, this.yCoord, this.zCoord & 0xf);
			}
		}
		
		for(int i = gases.size() - 1; i >= 0; i--)
		{
			int[] gasArray = gases.get(i);
			EnviroGas gasType = EnviroGasDictionary.gasList[gasArray[0]];
			int decayGasID = gasType.getGasOnDeath(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			
			if(gasType.normDecay > 0 && gasType.normDecayThresh >= gasArray[1])
			{
				decayed = true;
				this.subtractGas(gasArray[0], gasType.normDecay);
				
				if(decayGasID >= 0)
				{
					int decayNum = gasArray[1] < gasType.normDecay? gasArray[1] : gasType.normDecay;
					
					this.addGas(decayGasID, decayNum);
				}
			} else if(skyLight >= 5 && gasType.airDecay > 0 && gasType.airDecayThresh >= gasArray[1])
			{
				decayed = true;
				this.subtractGas(gasArray[0], gasType.airDecay);
				
				if(decayGasID >= 0)
				{
					int decayNum = gasArray[1] < gasType.airDecay? gasArray[1] : gasType.airDecay;
					
					this.addGas(decayGasID, decayNum);
				}
			} else if(this.worldObj.rand.nextInt(100) == 0 && gasType.randDecay > 0 && gasType.randDecayThresh >= gasArray[1])
			{
				decayed = true;
				this.subtractGas(gasArray[0], gasType.randDecay);
				
				if(decayGasID >= 0)
				{
					int decayNum = gasArray[1] < gasType.randDecay? gasArray[1] : gasType.randDecay;
					
					this.addGas(decayGasID, decayNum);
				}
			}
		}
		return decayed;
	}
	
	public int[] getGasDistribution()
	{
		int[] dArray = new int[]{0,0,0,0,0,0};
		
		ArrayList<int[]> gDir = new ArrayList<int[]>();
		
		gDir.add(new int[]{-1,0,0});
		gDir.add(new int[]{1,0,0});
		gDir.add(new int[]{0,-1,0});
		gDir.add(new int[]{0,1,0});
		gDir.add(new int[]{0,0,-1});
		gDir.add(new int[]{0,0,1});
		
		for(int i = 0; i < gDir.size(); i++)
		{
			int[] rDir = gDir.get(i);
			dArray[i] = this.getGasCapactiy(this.xCoord + rDir[0], this.yCoord + rDir[1], this.zCoord + rDir[2]);
		}
		
		int totalSpace = 0;
		int largestSpace = 0;
		
		for(int i = 0; i < 6; i++)
		{
			if(dArray[i] > 0)
			{
				totalSpace += dArray[i];
				
				if(dArray[i] > largestSpace)
				{
					largestSpace = dArray[i];
				}
			}
		}
		
		float gasFactor;
		if(this.getBlockType() == ObjectHandler.fireGasBlock)
		{
			gasFactor = this.amount/(float)(totalSpace + 1F);
		} else
		{
			gasFactor = this.amount/(float)(totalSpace + 10F);
		}
		
		for(int i = 0; i < 6; i++)
		{
			if(dArray[i] > 0)
			{
				dArray[i] = MathHelper.ceiling_float_int(dArray[i] * gasFactor);
			}
		}
		
		return dArray;
	}
	
	public int getGasCapactiy(int i, int j, int k)
	{
		if(!worldObj.getChunkProvider().chunkExists(i/16, k/16))
		{
			return 0;
		}
		
		int fireAmount = this.getGasQuantity(0);
		TileEntity tile = this.worldObj.getTileEntity(i, j, k);
		
		if(tile != null && tile instanceof TileEntityGas)
		{
			TileEntityGas gasTile = (TileEntityGas)tile;
			
			int gasSpace = 10 - gasTile.amount;
			gasSpace = gasSpace < 0? (this.amount > 10? 10 : 0) : gasSpace;
			
			if(this.getBlockType() == ObjectHandler.fireGasBlock)
			{
				if(j < this.yCoord)
				{
					return 1;
				} else if(gasTile.getGasQuantity(0) > fireAmount)
				{
					return 5;
				} else if(gasTile.getGasQuantity(0) > 0)
				{
					return 10;
				} else
				{
					return 20;
				}
			} else
			{
				return gasSpace;
			}
		} else if(this.worldObj.getBlock(i, j, k) == Blocks.air)
		{
			return 10;
		}
		return 0;
	}
	
	@Override
	public void onDataPacket(NetworkManager netManager, S35PacketUpdateTileEntity packet)
	{
		if(packet.func_148853_f() == 0)
		{
			this.readFromNBT(packet.func_148857_g());
		}
	}
}
