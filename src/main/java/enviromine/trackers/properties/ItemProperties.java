package enviromine.trackers.properties;

import java.io.File;
import java.util.Iterator;
import org.apache.logging.log4j.Level;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.helpers.PropertyBase;
import enviromine.trackers.properties.helpers.SerialisableProperty;
import enviromine.utils.EnviroUtils;

public class ItemProperties implements SerialisableProperty, PropertyBase
{
	public static final ItemProperties base = new ItemProperties();
	static String[] IPName;
	
	public String name;
	public int meta;
	
	public boolean enableTemp;
	
	public float ambTemp;
	public float ambAir;
	public float ambSanity;
	
	public float effTemp;
	public float effAir;
	public float effSanity;
	public float effHydration;
	
	public float effTempCap;
	
	public ItemProperties(NBTTagCompound tags)
	{
		this.ReadFromNBT(tags);
	}
	
	public ItemProperties()
	{
		// THIS CONSTRUCTOR IS FOR STATIC PURPOSES ONLY!
		
		if(base != null && base != this)
		{
			throw new IllegalStateException();
		}
	}
	
	public ItemProperties(String name, int meta, boolean enableTemp, float ambTemp, float ambAir, float ambSanity, float effTemp, float effAir, float effSanity, float effHydration, float effTempCap)
	{
		this.name = name;
		this.meta = meta;
		this.enableTemp = enableTemp;
		
		this.ambTemp = ambTemp;
		this.ambAir = ambAir;
		this.ambSanity = ambSanity;
		
		this.effTemp = effTemp;
		this.effAir = effAir;
		this.effSanity = effSanity;
		this.effHydration = effHydration;
		
		this.effTempCap = effTempCap;
	}

	@Override
	public NBTTagCompound WriteToNBT()
	{
		NBTTagCompound tags = new NBTTagCompound();
		tags.setString("name", this.name);
		tags.setBoolean("enableTemp", this.enableTemp);
		tags.setFloat("ambTemp", this.ambTemp);
		tags.setFloat("ambAir", this.ambAir);
		tags.setFloat("ambSanity", this.ambSanity);
		tags.setFloat("effTemp", this.effTemp);
		tags.setFloat("effAir", this.effAir);
		tags.setFloat("effHydration", this.effHydration);
		tags.setFloat("effTempCap", this.effTempCap);
		return tags;
	}

	@Override
	public void ReadFromNBT(NBTTagCompound tags)
	{
		this.name = tags.getString("name");
		this.enableTemp = tags.getBoolean("enableTemp");
		this.ambTemp = tags.getFloat("ambTemp");
		this.ambAir = tags.getFloat("ambAir");
		this.ambSanity = tags.getFloat("ambSanity");
		this.effTemp = tags.getFloat("effTemp");
		this.effAir = tags.getFloat("effAir");
		this.effHydration = tags.getFloat("effHydration");
		this.effTempCap = tags.getFloat("effTempCap");
	}

	@Override
	public String categoryName()
	{
		return "items";
	}

	@Override
	public String categoryDescription()
	{
		return "Custom effects for items";
	}

	@Override
	public void LoadProperty(Configuration config, String category)
	{
		config.setCategoryComment(this.categoryName(), this.categoryDescription());
		String name = config.get(category, IPName[0], "").getString();
		int meta = config.get(category, IPName[1], 0).getInt(0);
		boolean enableTemp = config.get(category, IPName[2], false).getBoolean(false);
		float ambTemp = (float)config.get(category, IPName[3], 0D).getDouble(0D);
		float ambAir = (float)config.get(category, IPName[4], 0D).getDouble(0D);
		float ambSanity = (float)config.get(category, IPName[5], 0D).getDouble(0D);
		float effTemp = (float)config.get(category, IPName[6], 0D).getDouble(0D);
		float effAir = (float)config.get(category, IPName[7], 0D).getDouble(0D);
		float effSanity = (float)config.get(category, IPName[8], 0D).getDouble(0D);
		float effHydration = (float)config.get(category, IPName[9], 0D).getDouble(0D);
		float effTempCap = (float)config.get(category, IPName[10], 37D).getDouble(37D);
		
		ItemProperties entry = new ItemProperties(name, meta, enableTemp, ambTemp, ambAir, ambSanity, effTemp, effAir, effSanity, effHydration, effTempCap);
		
		if(meta < 0)
		{
			EM_Settings.itemProperties.put("" + name, entry);
		} else
		{
			EM_Settings.itemProperties.put("" + name + "," + meta, entry);
		}
	}

	@Override
	public void SaveProperty(Configuration config, String category)
	{
		config.get(category, IPName[0], name).getString();
		config.get(category, IPName[1], meta).getInt(meta);
		config.get(category, IPName[2], enableTemp).getBoolean(enableTemp);
		config.get(category, IPName[3], ambTemp).getDouble(ambTemp);
		config.get(category, IPName[4], ambAir).getDouble(ambAir);
		config.get(category, IPName[5], ambSanity).getDouble(ambSanity);
		config.get(category, IPName[6], effTemp).getDouble(effTemp);
		config.get(category, IPName[7], effAir).getDouble(effAir);
		config.get(category, IPName[8], effSanity).getDouble(effSanity);
		config.get(category, IPName[9], effHydration).getDouble(effHydration);
		config.get(category, IPName[10], effTempCap).getDouble(effTempCap);
	}

	@Override
	public void GenDefaults()
	{
		Iterator<Item> iterator = Item.itemRegistry.iterator();
		
		while(iterator.hasNext())
		{
			Item item = iterator.next();
			Block block = Blocks.air;
			
			if(item == null)
			{
				continue;
			}
			
			if(item instanceof ItemBlock)
			{
				block = ((ItemBlock)item).field_150939_a;
			}
			
			String[] regName = Item.itemRegistry.getNameForObject(item).split(":");
			
			if(regName.length <= 0)
			{
				EnviroMine.logger.log(Level.ERROR, "Failed to get correctly formatted object name for " + item.getUnlocalizedName());
				continue;
			}
			
			File itemFile = new File(EM_ConfigHandler.customPath + EnviroUtils.SafeFilename(regName[0]) + ".cfg");
			
			if(!itemFile.exists())
			{
				try
				{
					itemFile.createNewFile();
				} catch(Exception e)
				{
					EnviroMine.logger.log(Level.ERROR, "Failed to create file for " + item.getUnlocalizedName(), e);
					continue;
				}
			}
			
			Configuration config = new Configuration(itemFile, true);
			
			String category = this.categoryName() + "." + EnviroUtils.replaceULN(item.getUnlocalizedName());
			
			config.load();
			
			if(item == Items.potionitem)
			{
				config.get(category, IPName[0], Item.itemRegistry.getNameForObject(item)).getString();
				config.get(category, IPName[1], -1).getInt(-1);
				config.get(category, IPName[2], false).getBoolean(false);
				config.get(category, IPName[3], 0D).getDouble(0D);
				config.get(category, IPName[4], 0D).getDouble(0D);
				config.get(category, IPName[5], 0D).getDouble(0D);
				config.get(category, IPName[6], -0.1D).getDouble(-0.1D);
				config.get(category, IPName[7], 0D).getDouble(0D);
				config.get(category, IPName[8], 0D).getDouble(0D);
				config.get(category, IPName[9], 25D).getDouble(25D);
				config.get(category, IPName[10], 37D).getDouble(37D);
			} else if(item == Items.melon || item == Items.carrot || item == Items.apple)
			{
				config.get(category, IPName[0], Item.itemRegistry.getNameForObject(item)).getString();
				config.get(category, IPName[1], -1).getInt(-1);
				config.get(category, IPName[2], false).getBoolean(false);
				config.get(category, IPName[3], 0D).getDouble(0D);
				config.get(category, IPName[4], 0D).getDouble(0D);
				config.get(category, IPName[5], 0D).getDouble(0D);
				config.get(category, IPName[6], -0.025D).getDouble(-0.025D);
				config.get(category, IPName[7], 0D).getDouble(0D);
				config.get(category, IPName[8], 0D).getDouble(0D);
				config.get(category, IPName[9], 5D).getDouble(5D);
				config.get(category, IPName[10], 37D).getDouble(37D);
			} else if(block == Blocks.snow || block == Blocks.snow_layer)
			{
				config.get(category, IPName[0], Item.itemRegistry.getNameForObject(item)).getString();
				config.get(category, IPName[1], -1).getInt(-1);
				config.get(category, IPName[2], true).getBoolean(true);
				config.get(category, IPName[3], -0.1D).getDouble(-0.1D);
				config.get(category, IPName[4], 0D).getDouble(0D);
				config.get(category, IPName[5], 0D).getDouble(0D);
				config.get(category, IPName[6], 0D).getDouble(0D);
				config.get(category, IPName[7], 0D).getDouble(0D);
				config.get(category, IPName[8], 0D).getDouble(0D);
				config.get(category, IPName[9], 0D).getDouble(0D);
				config.get(category, IPName[10], 37D).getDouble(37D);
			} else if(block == Blocks.ice || block == Blocks.packed_ice)
			{
				config.get(category, IPName[0], Item.itemRegistry.getNameForObject(item)).getString();
				config.get(category, IPName[1], -1).getInt(-1);
				config.get(category, IPName[2], true).getBoolean(true);
				config.get(category, IPName[3], -0.5D).getDouble(-0.5D);
				config.get(category, IPName[4], 0D).getDouble(0D);
				config.get(category, IPName[5], 0D).getDouble(0D);
				config.get(category, IPName[6], 0D).getDouble(0D);
				config.get(category, IPName[7], 0D).getDouble(0D);
				config.get(category, IPName[8], 0D).getDouble(0D);
				config.get(category, IPName[9], 0D).getDouble(0D);
				config.get(category, IPName[10], 37D).getDouble(37D);
			} else if(block == Blocks.netherrack || block == Blocks.nether_brick || block == Blocks.nether_brick_fence || block == Blocks.nether_brick_stairs)
			{
				config.get(category, IPName[0], Item.itemRegistry.getNameForObject(item)).getString();
				config.get(category, IPName[1], -1).getInt(-1);
				config.get(category, IPName[2], true).getBoolean(true);
				config.get(category, IPName[3], 50D).getDouble(50D);
				config.get(category, IPName[4], 0D).getDouble(0D);
				config.get(category, IPName[5], 0D).getDouble(0D);
				config.get(category, IPName[6], 0D).getDouble(0D);
				config.get(category, IPName[7], 0D).getDouble(0D);
				config.get(category, IPName[8], 0D).getDouble(0D);
				config.get(category, IPName[9], 0D).getDouble(0D);
				config.get(category, IPName[10], 37D).getDouble(37D);
			} else if(block == Blocks.soul_sand || item == Items.skull || block == Blocks.skull)
			{
				config.get(category, IPName[0], Item.itemRegistry.getNameForObject(item)).getString();
				config.get(category, IPName[1], -1).getInt(-1);
				config.get(category, IPName[2], false).getBoolean(false);
				config.get(category, IPName[3], 0D).getDouble(0D);
				config.get(category, IPName[4], 0D).getDouble(0D);
				config.get(category, IPName[5], -1D).getDouble(-1D);
				config.get(category, IPName[6], 0D).getDouble(0D);
				config.get(category, IPName[7], 0D).getDouble(0D);
				config.get(category, IPName[8], 0D).getDouble(0D);
				config.get(category, IPName[9], 0D).getDouble(0D);
				config.get(category, IPName[10], 37D).getDouble(37D);
			} else if((block == Blocks.flower_pot || block == Blocks.grass || block instanceof BlockLeavesBase || block instanceof BlockFlower || block instanceof BlockBush || block.getMaterial() == Material.grass || block.getMaterial() == Material.leaves || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine) && (regName[0].equals("minecraft") || EM_Settings.genConfigs))
			{
				config.get(category, IPName[0], Item.itemRegistry.getNameForObject(item)).getString();
				config.get(category, IPName[1], -1).getInt(-1);
				config.get(category, IPName[2], false).getBoolean(false);
				config.get(category, IPName[3], 0D).getDouble(0D);
				config.get(category, IPName[4], 0.025D).getDouble(0.025D);
				config.get(category, IPName[5], 0.1D).getDouble(0.1D);
				config.get(category, IPName[6], 0D).getDouble(0D);
				config.get(category, IPName[7], 0D).getDouble(0D);
				config.get(category, IPName[8], 0D).getDouble(0D);
				config.get(category, IPName[9], 0D).getDouble(0D);
				config.get(category, IPName[10], 37D).getDouble(37D);
			} else if(item == Items.lava_bucket)
			{
				config.get(category, IPName[0], Item.itemRegistry.getNameForObject(item)).getString();
				config.get(category, IPName[1], -1).getInt(-1);
				config.get(category, IPName[2], true).getBoolean(true);
				config.get(category, IPName[3], 100D).getDouble(100D);
				config.get(category, IPName[4], 0D).getDouble(0D);
				config.get(category, IPName[5], 0D).getDouble(0D);
				config.get(category, IPName[6], 0D).getDouble(0D);
				config.get(category, IPName[7], 0D).getDouble(0D);
				config.get(category, IPName[8], 0D).getDouble(0D);
				config.get(category, IPName[9], 0D).getDouble(0D);
				config.get(category, IPName[10], 37D).getDouble(37D);
			} else if(EM_Settings.genConfigs)
			{
				this.generateEmpty(config, item);
			}
			
			config.save();
		}
	}

	@Override
	public File GetDefaultFile()
	{
		return new File(EM_ConfigHandler.customPath + "Items.cfg");
	}

	@Override
	public void generateEmpty(Configuration config, Object obj)
	{
		if(obj == null || !(obj instanceof Item))
		{
			EnviroMine.logger.log(Level.ERROR, "Tried to register config with non item object!", new Exception());
			return;
		}
		
		Item item = (Item)obj;
		
		String category = this.categoryName() + "." + EnviroUtils.replaceULN(item.getUnlocalizedName());
		
		config.get(category, IPName[0], Item.itemRegistry.getNameForObject(item)).getString();
		config.get(category, IPName[1], -1).getInt(-1);
		config.get(category, IPName[2], false).getBoolean(false);
		config.get(category, IPName[3], 37D).getDouble(37D);
		config.get(category, IPName[4], 0D).getDouble(0D);
		config.get(category, IPName[5], 0D).getDouble(0D);
		config.get(category, IPName[6], 0D).getDouble(0D);
		config.get(category, IPName[7], 0D).getDouble(0D);
		config.get(category, IPName[8], 0D).getDouble(0D);
		config.get(category, IPName[9], 0D).getDouble(0D);
		config.get(category, IPName[10], 37D).getDouble(37D);
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
		IPName = new String[11];
		IPName[0] = "01.Name";
		IPName[1] = "02.Damage";
		IPName[2] = "03.Enable Ambient Temperature";
		IPName[3] = "04.Ambient Temperature";
		IPName[4] = "05.Ambient Air Quality";
		IPName[5] = "06.Ambient Santity";
		IPName[6] = "07.Effect Temperature";
		IPName[7] = "08.Effect Air Quality";
		IPName[8] = "09.Effect Sanity";
		IPName[9] = "10.Effect Hydration";
		IPName[10] = "11.Effect Temperature Cap";
	}
}
