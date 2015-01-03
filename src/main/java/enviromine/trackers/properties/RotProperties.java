package enviromine.trackers.properties;

import java.io.File;
import java.util.Iterator;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
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

public class RotProperties implements SerialisableProperty, PropertyBase
{
	public static final RotProperties base = new RotProperties();
	static String[] RPName;
	
	public String name;
	public int meta;
	public String rotID;
	public int rotMeta;
	public int days;
	
	public RotProperties(NBTTagCompound tags)
	{
		this.ReadFromNBT(tags);
	}
	
	public RotProperties()
	{
		// THIS CONSTRUCTOR IS FOR STATIC PURPOSES ONLY!
		
		if(base != null && base != this)
		{
			throw new IllegalStateException();
		}
	}
	
	public RotProperties(String name, int meta, String rotID, int rotMeta, int days)
	{
		this.name = name;
		this.meta = meta;
		this.rotID = rotID;
		this.rotMeta = rotMeta;
		this.days = days;
	}

	@Override
	public NBTTagCompound WriteToNBT()
	{
		NBTTagCompound tags = new NBTTagCompound();
		tags.setString("name", this.name);
		tags.setInteger("meta", this.meta);
		tags.setString("rotID", this.rotID);
		tags.setInteger("rotMeta", this.rotMeta);
		tags.setInteger("days", this.days);
		return tags;
	}

	@Override
	public void ReadFromNBT(NBTTagCompound tags)
	{
		this.name = tags.getString("name");
		this.meta = tags.getInteger("meta");
		this.rotID = tags.getString("rotID");
		this.rotMeta = tags.getInteger("rotMeta");
		this.days = tags.getInteger("days");
	}

	@Override
	public String categoryName()
	{
		return "spoiling";
	}

	@Override
	public String categoryDescription()
	{
		return "Set the properties of spoliable items";
	}

	@Override
	public void LoadProperty(Configuration config, String category)
	{
		config.addCustomCategoryComment(this.categoryName(), this.categoryDescription());
		String name = config.get(category, RPName[0], "").getString();
		int meta = config.get(category, RPName[1], -1).getInt(-1);
		String rotID = config.get(category, RPName[2], "", "Set blank to rot into nothing").getString();
		int rotMeta = config.get(category, RPName[3], 0).getInt(0);
		int DTR = config.get(category, RPName[4], 0, "Set this to -1 to disable rotting on this item").getInt(0);
		
		RotProperties entry = new RotProperties(name, meta, rotID, rotMeta, DTR);
		
		if(meta < 0)
		{
			EM_Settings.rotProperties.put("" + name, entry);
		} else
		{
			EM_Settings.rotProperties.put("" + name + "," + meta, entry);
		}
	}

	@Override
	public void SaveProperty(Configuration config, String category)
	{
		config.get(category, RPName[0], name).getString();
		config.get(category, RPName[1], meta).getInt(-1);
		config.get(category, RPName[2], rotID, "Set blank to rot into nothing").getString();
		config.get(category, RPName[3], rotMeta).getInt(0);
		config.get(category, RPName[4], days, "Set this to -1 to disable rotting on this item").getInt(7);
	}

	@Override
	public void GenDefaults()
	{
		Iterator<Item> iterator = Item.itemRegistry.iterator();
		
		while(iterator.hasNext())
		{
			Item item = iterator.next();
			
			if(item == null)
			{
				continue;
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
			
			if(item == Items.rotten_flesh || item == ObjectHandler.rottenFood)
			{
				config.get(category, RPName[0], Item.itemRegistry.getNameForObject(item)).getString();
				config.get(category, RPName[1], -1).getInt(-1);
				config.get(category, RPName[2], "", "Set blank to rot into nothing").getString();
				config.get(category, RPName[3], 0).getInt(0);
				config.get(category, RPName[4], -1, "Set this to -1 to disable rotting on this item").getInt(-1);
			} else if(item == Items.milk_bucket)
			{
				config.get(category, RPName[0], Item.itemRegistry.getNameForObject(item)).getString();
				config.get(category, RPName[1], -1).getInt(-1);
				config.get(category, RPName[2], Item.itemRegistry.getNameForObject(ObjectHandler.spoiledMilk), "Set blank to rot into nothing").getString();
				config.get(category, RPName[3], 0).getInt(0);
				config.get(category, RPName[4], 7, "Set this to -1 to disable rotting on this item").getInt(7);
			} else if(item == Items.spider_eye)
			{
				config.get(category, RPName[0], Item.itemRegistry.getNameForObject(item)).getString();
				config.get(category, RPName[1], -1).getInt(-1);
				config.get(category, RPName[2], Item.itemRegistry.getNameForObject(Items.fermented_spider_eye), "Set blank to rot into nothing").getString();
				config.get(category, RPName[3], 0).getInt(0);
				config.get(category, RPName[4], 7, "Set this to -1 to disable rotting on this item").getInt(7);
			} else if(item == Items.fermented_spider_eye || item == Items.beef || item == Items.chicken || item == Items.porkchop || item == Items.fish || item == Items.cooked_beef || item == Items.cooked_chicken || item == Items.cooked_porkchop || item == Items.cooked_fished)
			{
				config.get(category, RPName[0], Item.itemRegistry.getNameForObject(item)).getString();
				config.get(category, RPName[1], -1).getInt(-1);
				config.get(category, RPName[2], Item.itemRegistry.getNameForObject(Items.rotten_flesh), "Set blank to rot into nothing").getString();
				config.get(category, RPName[3], 0).getInt(0);
				config.get(category, RPName[4], 7, "Set this to -1 to disable rotting on this item").getInt(7);
			} else if(item instanceof ItemFood && (regName[0].equals("minecraft") || EM_Settings.genConfigs))
			{
				config.get(category, RPName[0], Item.itemRegistry.getNameForObject(item)).getString();
				config.get(category, RPName[1], -1).getInt(-1);
				config.get(category, RPName[2], Item.itemRegistry.getNameForObject(ObjectHandler.rottenFood), "Set blank to rot into nothing").getString();
				config.get(category, RPName[3], 0).getInt(0);
				config.get(category, RPName[4], 7, "Set this to -1 to disable rotting on this item").getInt(7);
			}
			
			config.save();
		}
	}

	@Override
	public File GetDefaultFile()
	{
		return new File(EM_ConfigHandler.customPath + "Spoiling.cfg");
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
		
		config.get(category, RPName[0], Item.itemRegistry.getNameForObject(item)).getString();
		config.get(category, RPName[1], -1).getInt(-1);
		config.get(category, RPName[2], Item.itemRegistry.getNameForObject(ObjectHandler.rottenFood), "Set blank to rot into nothing").getString();
		config.get(category, RPName[3], 0).getInt(0);
		config.get(category, RPName[4], 7, "Set this to -1 to disable rotting on this item").getInt(7);
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
		RPName = new String[5];
		RPName[0] = "01.ID";
		RPName[1] = "02.Damage";
		RPName[2] = "03.Rotten ID";
		RPName[3] = "04.Rotten Damage";
		RPName[4] = "05.Days To Rot";
	}
}
