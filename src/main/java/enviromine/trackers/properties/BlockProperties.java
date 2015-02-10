package enviromine.trackers.properties;

import java.io.File;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;

import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.handlers.ObjectHandler;
import enviromine.trackers.properties.helpers.PropertyBase;
import enviromine.trackers.properties.helpers.SerialisableProperty;
import enviromine.utils.EnviroUtils;

public class BlockProperties implements SerialisableProperty, PropertyBase
{
	public static final BlockProperties base = new BlockProperties();
	static String[] BPName;
	
	public boolean hasPhys;
	
	public String name;
	public int meta;
	
	public String stability;
	public int minFall;
	public int maxFall;
	public int supportDist;
	
	public String dropName;
	public int dropMeta;
	public int dropNum;
	
	public boolean enableTemp;
	
	public float temp;
	public float air;
	public float sanity;
	
	public boolean holdsOthers;
	public boolean slides;
	public boolean canHang;
	public boolean wetSlide;
	
	public BlockProperties(NBTTagCompound tags)
	{
		this.ReadFromNBT(tags);
	}
	
	public BlockProperties()
	{
		// THIS CONSTRUCTOR IS FOR STATIC PURPOSES ONLY!
		
		if(base != null && base != this)
		{
			throw new IllegalStateException();
		}
	}
	
	public BlockProperties(String name, int meta, boolean hasPhys, int minFall, int maxFall, int supportDist, String dropName, int dropMeta, int dropNum, boolean enableTemp, float temp, float air, float sanity, boolean holdOther, boolean slides, boolean canHang, boolean wetSlide, String stability)
	{
		this.name = name;
		this.meta = meta;
		this.hasPhys = hasPhys;
		this.minFall = minFall;
		this.maxFall = maxFall;
		this.supportDist = supportDist;
		this.dropName = dropName;
		this.dropMeta = dropMeta;
		this.dropNum = dropNum;
		this.enableTemp = enableTemp;
		this.temp = temp;
		this.air = air;
		this.sanity = sanity;
		this.holdsOthers = holdOther;
		this.slides = slides;
		this.canHang = canHang;
		this.wetSlide = wetSlide;
		this.stability = stability;
	}

	/**
	 * <b>hasProperty(Block block, int meta)</b><bR><br>
	 * Checks if contains custom properties for Block and metaData.
	 * @param block
	 * @param meta - Block MetaData<br>
	 * @return true if has custom properties
	 */
	
	
	public boolean hasProperty(Block block, int meta)
	{
		return EM_Settings.blockProperties.containsKey("" + Block.blockRegistry.getNameForObject(block) + "," + meta) || EM_Settings.blockProperties.containsKey("" + Block.blockRegistry.getNameForObject(block));
	}
	/** 
	 * 	<b>getProperty(Block block, int meta)</b><bR><br>
	 * Gets Property form Block and metaData.
	 * @param block
	 * @param meta - Block MetaData<br>
	 * @return BlockProperties for block
	 */
	public BlockProperties getProperty(Block block, int meta)
	{
		BlockProperties blockProps = null;
		
		if(EM_Settings.blockProperties.containsKey("" + Block.blockRegistry.getNameForObject(block) + "," + meta))
		{
			blockProps = EM_Settings.blockProperties.get("" + Block.blockRegistry.getNameForObject(block) + "," + meta);
		} else
		{
			blockProps = EM_Settings.blockProperties.get("" + Block.blockRegistry.getNameForObject(block));
		}
		return blockProps;
	}
	
	@Override
	public NBTTagCompound WriteToNBT()
	{
		NBTTagCompound tags = new NBTTagCompound();
		tags.setString("name", this.name);
		tags.setInteger("metaData", this.meta);
		tags.setString("dropName", this.dropName);
		tags.setInteger("dropMeta", this.dropMeta);
		tags.setInteger("dropNum", this.dropNum);
		tags.setBoolean("enableTemp", this.enableTemp);
		tags.setFloat("temp", this.temp);
		tags.setFloat("air", this.air);
		tags.setFloat("sanity", this.sanity);
		tags.setBoolean("holdsOthers", this.holdsOthers);
		tags.setBoolean("slides", this.slides);
		tags.setBoolean("canHang", this.canHang);
		tags.setBoolean("wetSlide", this.wetSlide);
		return tags;
	}

	@Override
	public void ReadFromNBT(NBTTagCompound tags)
	{
		this.name = tags.getString("name");
		this.meta = tags.getInteger("metaData");
		this.dropName = tags.getString("dropName");
		this.dropMeta = tags.getInteger("dropMeta");
		this.dropNum = tags.getInteger("dropNum");
		this.enableTemp = tags.getBoolean("enableTemp");
		this.temp = tags.getFloat("temp");
		this.air = tags.getFloat("air");
		this.sanity = tags.getFloat("sanity");
		this.holdsOthers = tags.getBoolean("holdsOthers");
		this.slides = tags.getBoolean("slides");
		this.canHang = tags.getBoolean("canHang");
		this.wetSlide = tags.getBoolean("wetSlide");
	}

	@Override
	public String categoryName()
	{
		return "blocks";
	}

	@Override
	public String categoryDescription()
	{
		return "Customise the physical properties and environmental effects of blocks";
	}

	@Override
	public void LoadProperty(Configuration config, String category)
	{
		config.setCategoryComment(this.categoryName(), this.categoryDescription());
		String name = config.get(category, BPName[0], "").getString();
		int metaData = config.get(category, BPName[1], 0).getInt(0);
		String dropName = config.get(category, BPName[2], "").getString();
		int dropMeta = config.get(category, BPName[3], 0).getInt(0);
		int dropNum = config.get(category, BPName[4], -1).getInt(-1);
		boolean enableTemp = config.get(category, BPName[5], false).getBoolean(false);
		float temperature = (float)config.get(category, BPName[6], 0.00).getDouble(0.00);
		float airQuality = (float)config.get(category, BPName[7], 0.00).getDouble(0.00);
		float sanity = (float)config.get(category, BPName[8], 0.00).getDouble(0.00);
		String stability = config.get(category, BPName[9], "loose").getString();
		boolean slides = config.get(category, BPName[10], false).getBoolean(false);
		boolean wetSlides = config.get(category, BPName[11], false).getBoolean(false);
		
		// 	Get Stability Options
		int minFall = 99;
		int maxFall = 99;
		int supportDist = 5;
		boolean holdOther = false;
		boolean canHang = true;
		boolean hasPhys = false;
		
		if(EM_Settings.stabilityTypes.containsKey(stability))
		{
			StabilityType stabType = EM_Settings.stabilityTypes.get(stability);
			
			minFall = stabType.minFall;
			maxFall = stabType.maxFall;
			supportDist = stabType.supportDist;
			hasPhys = stabType.enablePhysics;
			holdOther = stabType.holdOther;
			canHang = stabType.canHang;
		} else
		{
			EnviroMine.logger.log(Level.WARN, "Stability type '" + stability + "' not found.");
			minFall = 99;
			maxFall = 99;
			supportDist = 9;
			hasPhys = false;
			holdOther = false;
			canHang = true;
		}
		
		BlockProperties entry = new BlockProperties(name, metaData, hasPhys, minFall, maxFall, supportDist, dropName, dropMeta, dropNum, enableTemp, temperature, airQuality, sanity, holdOther, slides, canHang, wetSlides, stability);
		
		if(metaData < 0)
		{
			EM_Settings.blockProperties.put("" + name, entry);
		} else
		{
			EM_Settings.blockProperties.put("" + name + "," + metaData, entry);
		}
	}

	@Override
	public void SaveProperty(Configuration config, String category)
	{
		config.get(category, BPName[0], name).getString();
		config.get(category, BPName[1], meta).getInt(0);
		config.get(category, BPName[2], dropName).getString();
		config.get(category, BPName[3], dropMeta).getInt(0);
		config.get(category, BPName[4], dropNum).getInt(-1);
		config.get(category, BPName[5], enableTemp).getBoolean(false);
		config.get(category, BPName[6], temp).getDouble(0.00);
		config.get(category, BPName[7], air).getDouble(0.00);
		config.get(category, BPName[8], sanity).getDouble(0.00);
		config.get(category, BPName[9], stability).getString();
		config.get(category, BPName[10], slides).getBoolean(false);
		config.get(category, BPName[11], wetSlide).getBoolean(false);
	}

	@Override
	public void GenDefaults()
	{
		@SuppressWarnings("unchecked")
		Iterator<Block> iterator = Block.blockRegistry.iterator();
		
		while(iterator.hasNext())
		{
			Block block = iterator.next();
			
			if(block == null || block == Blocks.air)
			{
				continue;
			}

			String[] regName = Block.blockRegistry.getNameForObject(block).split(":");
			
			if(regName.length <= 0)
			{
				EnviroMine.logger.log(Level.ERROR, "Failed to get correctly formatted object name for " + block.getUnlocalizedName());
				continue;
			}
			
			File blockFile = new File(EM_ConfigHandler.customPath + EnviroUtils.SafeFilename(regName[0]) + ".cfg");
			
			if(!blockFile.exists())
			{
				try
				{
					blockFile.createNewFile();
				} catch(Exception e)
				{
					EnviroMine.logger.log(Level.ERROR, "Failed to create file for " + block.getUnlocalizedName(), e);
					continue;
				}
			}
			
			Configuration config = new Configuration(blockFile, true);
			
			config.load();
			
			String category = this.categoryName() + "." + EnviroUtils.replaceULN(block.getUnlocalizedName());
			StabilityType defStability = EnviroUtils.getDefaultStabilityType(block);
			
			if(block == Blocks.lava || block == Blocks.flowing_lava || block == ObjectHandler.fireGasBlock || (EM_Settings.genConfigs && block.getMaterial() == Material.lava))
			{
				config.get(category, BPName[0], Block.blockRegistry.getNameForObject(block)).getString();
				config.get(category, BPName[1], -1).getInt(-1);
				config.get(category, BPName[2], "").getString();
				config.get(category, BPName[3], -1).getInt(-1);
				config.get(category, BPName[4], -1).getInt(-1);
				config.get(category, BPName[5], true).getBoolean(true);
				config.get(category, BPName[6], 200.0D).getDouble(200.0D);
				config.get(category, BPName[7], -1D).getDouble(-1D);
				config.get(category, BPName[8], 0.0D).getDouble(0.0D);
				config.get(category, BPName[9], defStability.name).getString();
				config.get(category, BPName[10], false).getBoolean(false);
				config.get(category, BPName[11], false).getBoolean(false);
			} else if(block == Blocks.fire || block == ObjectHandler.burningCoal || block == ObjectHandler.fireTorch || (EM_Settings.genConfigs && block.getMaterial() == Material.fire))
			{
				config.get(category, BPName[0], Block.blockRegistry.getNameForObject(block)).getString();
				config.get(category, BPName[1], -1).getInt(-1);
				config.get(category, BPName[2], "").getString();
				config.get(category, BPName[3], -1).getInt(-1);
				config.get(category, BPName[4], -1).getInt(-1);
				config.get(category, BPName[5], true).getBoolean(true);
				config.get(category, BPName[6], 75.0D).getDouble(75.0D);
				config.get(category, BPName[7], -0.25D).getDouble(-0.25D);
				config.get(category, BPName[8], 0.0D).getDouble(0.0D);
				config.get(category, BPName[9], defStability.name).getString();
				config.get(category, BPName[10], false).getBoolean(false);
				config.get(category, BPName[11], false).getBoolean(false);
			} else if(block == Blocks.torch || block == Blocks.lit_furnace || block == ObjectHandler.fireTorch)
			{
				config.get(category, BPName[0], Block.blockRegistry.getNameForObject(block)).getString();
				config.get(category, BPName[1], -1).getInt(-1);
				config.get(category, BPName[2], "").getString();
				config.get(category, BPName[3], -1).getInt(-1);
				config.get(category, BPName[4], -1).getInt(-1);
				config.get(category, BPName[5], true).getBoolean(true);
				config.get(category, BPName[6], 75.0D).getDouble(75.0D);
				config.get(category, BPName[7], -0.25D).getDouble(-0.25D);
				config.get(category, BPName[8], 0.0D).getDouble(0.0D);
				config.get(category, BPName[9], defStability.name).getString();
				config.get(category, BPName[10], false).getBoolean(false);
				config.get(category, BPName[11], false).getBoolean(false);
			} else if(block == Blocks.netherrack || block == Blocks.nether_brick || block == Blocks.nether_brick_fence || block == Blocks.nether_brick_stairs)
			{
				config.get(category, BPName[0], Block.blockRegistry.getNameForObject(block)).getString();
				config.get(category, BPName[1], -1).getInt(-1);
				config.get(category, BPName[2], "").getString();
				config.get(category, BPName[3], -1).getInt(-1);
				config.get(category, BPName[4], -1).getInt(-1);
				config.get(category, BPName[5], true).getBoolean(true);
				config.get(category, BPName[6], 50.0D).getDouble(50.0D);
				config.get(category, BPName[7], 0.0D).getDouble(0.0D);
				config.get(category, BPName[8], 0.0D).getDouble(0.0D);
				config.get(category, BPName[9], defStability.name).getString();
				config.get(category, BPName[10], false).getBoolean(false);
				config.get(category, BPName[11], false).getBoolean(false);
			} else if((block == Blocks.flower_pot || block == Blocks.grass || block instanceof BlockLeavesBase || block instanceof BlockFlower || block instanceof BlockBush || block.getMaterial() == Material.grass || block.getMaterial() == Material.leaves || block.getMaterial() == Material.vine || block.getMaterial() == Material.plants) && (regName[0].equals("minecraft") || EM_Settings.genConfigs))
			{
				config.get(category, BPName[0], Block.blockRegistry.getNameForObject(block)).getString();
				config.get(category, BPName[1], -1).getInt(-1);
				config.get(category, BPName[2], block == Blocks.grass? Block.blockRegistry.getNameForObject(Blocks.dirt) : "").getString();
				config.get(category, BPName[3], -1).getInt(-1);
				config.get(category, BPName[4], -1).getInt(-1);
				config.get(category, BPName[5], false).getBoolean(false);
				config.get(category, BPName[6], 0.0D).getDouble(0.0D);
				config.get(category, BPName[7], 1.0D).getDouble(1.0D);
				config.get(category, BPName[8], 0.1D).getDouble(0.1D);
				config.get(category, BPName[9], defStability.name).getString();
				config.get(category, BPName[10], false).getBoolean(false);
				config.get(category, BPName[11], false).getBoolean(false);
			} else if((block.getMaterial() == Material.snow || block.getMaterial() == Material.ice || block.getMaterial() == Material.packedIce) && (regName[0].equals("minecraft") || EM_Settings.genConfigs))
			{
				config.get(category, BPName[0], Block.blockRegistry.getNameForObject(block)).getString();
				config.get(category, BPName[1], -1).getInt(-1);
				config.get(category, BPName[2], "").getString();
				config.get(category, BPName[3], -1).getInt(-1);
				config.get(category, BPName[4], -1).getInt(-1);
				config.get(category, BPName[5], true).getBoolean(true);
				config.get(category, BPName[6], -0.02D).getDouble(-0.02D);
				config.get(category, BPName[7], 0.0D).getDouble(0.0D);
				config.get(category, BPName[8], 0.0D).getDouble(0.0D);
				config.get(category, BPName[9], defStability.name).getString();
				config.get(category, BPName[10], false).getBoolean(false);
				config.get(category, BPName[11], (block == Blocks.snow || block == Blocks.snow_layer)).getBoolean((block == Blocks.snow || block == Blocks.snow_layer));
			} else if((block == Blocks.skull || block == Blocks.soul_sand) && (regName[0].equals("minecraft") || EM_Settings.genConfigs))
			{
				config.get(category, BPName[0], Block.blockRegistry.getNameForObject(block)).getString();
				config.get(category, BPName[1], -1).getInt(-1);
				config.get(category, BPName[2], "").getString();
				config.get(category, BPName[3], -1).getInt(-1);
				config.get(category, BPName[4], -1).getInt(-1);
				config.get(category, BPName[5], false).getBoolean(false);
				config.get(category, BPName[6], 0.0D).getDouble(0.0D);
				config.get(category, BPName[7], 0.0D).getDouble(0.0D);
				config.get(category, BPName[8], -0.1D).getDouble(-0.1D);
				config.get(category, BPName[9], defStability.name).getString();
				config.get(category, BPName[10], false).getBoolean(false);
				config.get(category, BPName[11], false).getBoolean(false);
			} else if(block.getMaterial() == Material.web && (regName[0].equals("minecraft") || EM_Settings.genConfigs))
			{
				config.get(category, BPName[0], Block.blockRegistry.getNameForObject(block)).getString();
				config.get(category, BPName[1], -1).getInt(-1);
				config.get(category, BPName[2], "").getString();
				config.get(category, BPName[3], -1).getInt(-1);
				config.get(category, BPName[4], -1).getInt(-1);
				config.get(category, BPName[5], false).getBoolean(false);
				config.get(category, BPName[6], 0.0D).getDouble(0.0D);
				config.get(category, BPName[7], 0.0D).getDouble(0.0D);
				config.get(category, BPName[8], -0.01D).getDouble(-0.01D);
				config.get(category, BPName[9], defStability.name).getString();
				config.get(category, BPName[10], false).getBoolean(false);
				config.get(category, BPName[11], false).getBoolean(false);
			} else if(block.getMaterial() == Material.dragonEgg && (regName[0].equals("minecraft") || EM_Settings.genConfigs))
			{
				config.get(category, BPName[0], Block.blockRegistry.getNameForObject(block)).getString();
				config.get(category, BPName[1], -1).getInt(-1);
				config.get(category, BPName[2], "").getString();
				config.get(category, BPName[3], -1).getInt(-1);
				config.get(category, BPName[4], -1).getInt(-1);
				config.get(category, BPName[5], false).getBoolean(false);
				config.get(category, BPName[6], 0.0D).getDouble(0.0D);
				config.get(category, BPName[7], 0.0D).getDouble(0.0D);
				config.get(category, BPName[8], 1.0D).getDouble(1.0D);
				config.get(category, BPName[9], defStability.name).getString();
				config.get(category, BPName[10], false).getBoolean(false);
				config.get(category, BPName[11], false).getBoolean(false);
			} else if(block instanceof BlockFalling && (regName[0].equals("minecraft") || EM_Settings.genConfigs))
			{
				config.get(category, BPName[0], Block.blockRegistry.getNameForObject(block)).getString();
				config.get(category, BPName[1], -1).getInt(-1);
				config.get(category, BPName[2], "").getString();
				config.get(category, BPName[3], -1).getInt(-1);
				config.get(category, BPName[4], -1).getInt(-1);
				config.get(category, BPName[5], false).getBoolean(false);
				config.get(category, BPName[6], 0.0D).getDouble(0.0D);
				config.get(category, BPName[7], 0.0D).getDouble(0.0D);
				config.get(category, BPName[8], 0.0D).getDouble(0.0D);
				config.get(category, BPName[9], defStability.name).getString();
				config.get(category, BPName[10], true).getBoolean(true);
				config.get(category, BPName[11], false).getBoolean(false);
			} else if(block == Blocks.dirt)
			{
				config.get(category, BPName[0], Block.blockRegistry.getNameForObject(block)).getString();
				config.get(category, BPName[1], -1).getInt(-1);
				config.get(category, BPName[2], "").getString();
				config.get(category, BPName[3], -1).getInt(-1);
				config.get(category, BPName[4], -1).getInt(-1);
				config.get(category, BPName[5], false).getBoolean(false);
				config.get(category, BPName[6], 0.0D).getDouble(0.0D);
				config.get(category, BPName[7], 0.0D).getDouble(0.0D);
				config.get(category, BPName[8], 0.0D).getDouble(0.0D);
				config.get(category, BPName[9], defStability.name).getString();
				config.get(category, BPName[10], false).getBoolean(false);
				config.get(category, BPName[11], true).getBoolean(true);
			} else if(EM_Settings.genConfigs)
			{
				this.generateEmpty(config, block);
				/*config.get(category, BPName[0], Block.blockRegistry.getNameForObject(block)).getString();
				config.get(category, BPName[1], -1).getInt(0);
				config.get(category, BPName[2], block == Blocks.stone? Block.blockRegistry.getNameForObject(Blocks.cobblestone) : "").getString();
				config.get(category, BPName[3], -1).getInt(0);
				config.get(category, BPName[4], 1).getInt(0);
				config.get(category, BPName[5], false).getBoolean(false);
				config.get(category, BPName[6], 0.0D).getDouble(0.0D);
				config.get(category, BPName[7], 0.0D).getDouble(0.0D);
				config.get(category, BPName[8], 0.0D).getDouble(0.0D);
				config.get(category, BPName[9], defStability.name).getString();
				config.get(category, BPName[10], false).getBoolean(false);
				config.get(category, BPName[11], false).getBoolean(false);*/
			}
			
			config.save();
		}
	}

	@Override
	public File GetDefaultFile()
	{
		return new File(EM_ConfigHandler.customPath + "Blocks.cfg");
	}

	@Override
	public void generateEmpty(Configuration config, Object obj)
	{
		if(obj == null || !(obj instanceof Block))
		{
			EnviroMine.logger.log(Level.ERROR, "Tried to register config with non block object!", new Exception());
			return;
		}
		
		Block block = (Block)obj;
		
		String category = this.categoryName() + "." + EnviroUtils.replaceULN(block.getUnlocalizedName());
		StabilityType defStability = EnviroUtils.getDefaultStabilityType(block);
		
		config.get(category, BPName[0], Block.blockRegistry.getNameForObject(block)).getString();
		config.get(category, BPName[1], -1).getInt(0);
		config.get(category, BPName[2], block == Blocks.stone? Block.blockRegistry.getNameForObject(Blocks.cobblestone) : (block == Blocks.grass? Block.blockRegistry.getNameForObject(Blocks.dirt) : "")).getString();
		config.get(category, BPName[3], -1).getInt(0);
		config.get(category, BPName[4], -1).getInt(0);
		config.get(category, BPName[5], false).getBoolean(false);
		config.get(category, BPName[6], 0.0D).getDouble(0.0D);
		config.get(category, BPName[7], 0.0D).getDouble(0.0D);
		config.get(category, BPName[8], 0.0D).getDouble(0.0D);
		config.get(category, BPName[9], defStability.name).getString();
		config.get(category, BPName[10], false).getBoolean(false);
		config.get(category, BPName[11], false).getBoolean(false);
	}

	@Override
	public boolean useCustomConfigs()
	{
		return true;
	}

	@Override
	public void customLoad()
	{
	}
	
	static
	{
		BPName = new String[12];
		BPName[0] = "01.Name";
		BPName[1] = "02.MetaID";
		BPName[2] = "03.DropName";
		BPName[3] = "04.DropMetaID";
		BPName[4] = "05.DropNumber";
		BPName[5] = "06.Enable Temperature";
		BPName[6] = "07.Temperature";
		BPName[7] = "08.Air Quality";
		BPName[8] = "09.Sanity";
		BPName[9] = "10.Stability";
		BPName[10] = "11.Slides";
		BPName[11] = "12.Slides When Wet";
	}

}
