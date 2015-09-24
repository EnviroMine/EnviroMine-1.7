package enviromine.trackers.properties;

import java.io.File;
import java.util.Iterator;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;

import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.helpers.PropertyBase;
import enviromine.trackers.properties.helpers.SerialisableProperty;
import enviromine.utils.EnviroUtils;

public class ArmorProperties implements SerialisableProperty, PropertyBase
{
	public static final ArmorProperties base = new ArmorProperties();
	static String[] APName;
	
	public Item item;
	public String name;
	public float nightTemp;
	public float shadeTemp;
	public float sunTemp;
	public float nightMult;
	public float shadeMult;
	public float sunMult;
	public float sanity;
	public float air;
	public boolean allowCamelPack;
	public String loadedFrom;
	
	public ArmorProperties(NBTTagCompound tags)
	{
		this.ReadFromNBT(tags);
	}
	
	public ArmorProperties()
	{
		// THIS CONSTRUCTOR IS FOR STATIC PURPOSES ONLY!
		
		if(base != null && base != this)
		{
			throw new IllegalStateException();
		}
	}
	
	public ArmorProperties(Item item, String name, float nightTemp, float shadeTemp, float sunTemp, float nightMult, float shadeMult, float sunMult, float sanity, float air, boolean allowCamelPack, String filename)
	{
		this.item = item;
		this.name = name;
		this.nightTemp = nightTemp;
		this.shadeTemp = shadeTemp;
		this.sunTemp = sunTemp;
		this.nightMult = nightMult;
		this.shadeMult = shadeMult;
		this.sunMult = sunMult;
		this.sanity = sanity;
		this.air = air;
		this.allowCamelPack = allowCamelPack;
		this.loadedFrom = filename;
	}

	/**
	 * <b>hasProperty(ItemStack stack)</b><bR><br>
	 * Checks if ArmorProperty contains custom properties from ItemStack.
	 * @param stack
	 * @return true if has custom properties
	 */
	public boolean hasProperty(ItemStack stack)
	{
		return EM_Settings.armorProperties.containsKey(Item.itemRegistry.getNameForObject(stack.getItem()));
	}
	/** 
	 * 	<b>getProperty(ItemStack stack)</b><bR><br>
	 * Gets ItemProperty from ItemStack.
	 * @param stack
	 * @return ItemProperties
	 */
	public ArmorProperties getProperty(ItemStack stack)
	{
		return EM_Settings.armorProperties.get(Item.itemRegistry.getNameForObject(stack.getItem()));
	}
	
	@Override
	public NBTTagCompound WriteToNBT()
	{
		NBTTagCompound tags = new NBTTagCompound();
		tags.setString("name", Item.itemRegistry.getNameForObject(item));
		tags.setFloat("nightTemp", nightTemp);
		tags.setFloat("shadeTemp", shadeTemp);
		tags.setFloat("sunTemp", sunTemp);
		tags.setFloat("nightMult", nightMult);
		tags.setFloat("shadeMult", shadeMult);
		tags.setFloat("sunMult", sunMult);
		tags.setFloat("sanity", sanity);
		tags.setFloat("air", air);
		tags.setBoolean("allowCamelPack", allowCamelPack);
		return tags;
	}

	@Override
	public void ReadFromNBT(NBTTagCompound tags)
	{
		this.name = tags.getString("name");
		item = (Item)Item.itemRegistry.getObject(this.name);
		this.nightTemp = tags.getFloat("nightTemp");
		this.shadeTemp = tags.getFloat("shadeTemp");
		this.sunTemp = tags.getFloat("sunTemp");
		this.nightMult = tags.getFloat("nightMult");
		this.shadeMult = tags.getFloat("shadeMult");
		this.sunMult = tags.getFloat("sunMult");
		this.sanity = tags.getFloat("sanity");
		this.air = tags.getFloat("air");
		this.allowCamelPack = tags.getBoolean("allowCamelPack");
	}

	@Override
	public String categoryName()
	{
		return "armor";
	}

	@Override
	public String categoryDescription()
	{
		return "Modify the effects armor has on entities when worn";
	}

	@Override
	public void LoadProperty(Configuration config, String category)
	{
		config.setCategoryComment(this.categoryName(), this.categoryDescription());
		String name = config.get(category, APName[0], "").getString();
		float nightTemp = (float)config.get(category, APName[1], 0.00).getDouble(0.00);
		float shadeTemp = (float)config.get(category, APName[2], 0.00).getDouble(0.00);
		float sunTemp = (float)config.get(category, APName[3], 0.00).getDouble(0.00);
		float nightMult = (float)config.get(category, APName[4], 1.00).getDouble(1.00);
		float shadeMult = (float)config.get(category, APName[5], 1.00).getDouble(1.00);
		float sunMult = (float)config.get(category, APName[6], 1.00).getDouble(1.00);
		float sanity = (float)config.get(category, APName[7], 0.00).getDouble(0.00);
		float air = (float)config.get(category, APName[8], 0.00).getDouble(0.00);
		String filename = config.getConfigFile().getName();
		
		Object item = Item.itemRegistry.getObject(name);
		boolean allowCamelPack = true;
		if (item instanceof ItemArmor && ((ItemArmor)item).armorType == 1)
		{
			allowCamelPack = config.get(category, APName[9], true).getBoolean(true);
		}
		
		ArmorProperties entry = new ArmorProperties((Item)item, name, nightTemp, shadeTemp, sunTemp, nightMult, shadeMult, sunMult, sanity, air, allowCamelPack, filename);

		// If item already exist and current file hasn't completely been loaded do this
		if(EM_Settings.armorProperties.containsKey(name) && !EM_ConfigHandler.loadedConfigs.contains(filename)) EnviroMine.logger.log(Level.ERROR, "CONFIG DUPLICATE: Armor - "+ name.toUpperCase() +" was already added from "+ EM_Settings.armorProperties.get(name).loadedFrom.toUpperCase() +" and will be overriden by "+ filename.toUpperCase());
		
		EM_Settings.armorProperties.put(name, entry);
	}

	@Override
	public void SaveProperty(Configuration config, String category)
	{
		config.get(category, APName[0], name).getString();
		config.get(category, APName[1], nightTemp).getDouble(nightTemp);
		config.get(category, APName[2], shadeTemp).getDouble(shadeTemp);
		config.get(category, APName[3], sunTemp).getDouble(sunTemp);
		config.get(category, APName[4], nightMult).getDouble(nightMult);
		config.get(category, APName[5], shadeMult).getDouble(shadeMult);
		config.get(category, APName[6], sunMult).getDouble(sunMult);
		config.get(category, APName[7], sanity).getDouble(sanity);
		config.get(category, APName[8], air).getDouble(air);
	}
	
	@Override
	public void GenDefaults()
	{
		@SuppressWarnings("unchecked")
		Iterator<Item> itemList = Item.itemRegistry.iterator();
		
		while(itemList.hasNext())
		{
			Item regItem = itemList.next();
			
			if(!(regItem instanceof ItemArmor))
			{
				continue;
			}
			
			ItemArmor armor = (ItemArmor)regItem;
			String[] regName = Item.itemRegistry.getNameForObject(armor).split(":");
			
			if(regName.length <= 0)
			{
				EnviroMine.logger.log(Level.ERROR, "Failed to get correctly formatted object name for " + armor.getUnlocalizedName());
				continue;
			}
			
			File armorFile = new File(EM_ConfigHandler.loadedProfile + EM_ConfigHandler.customPath + EnviroUtils.SafeFilename(regName[0]) + ".cfg");
			
			if(!armorFile.exists())
			{
				try
				{
					armorFile.createNewFile();
				} catch(Exception e)
				{
					EnviroMine.logger.log(Level.ERROR, "Failed to create file for " + armor.getUnlocalizedName(), e);
					continue;
				}
			}
			
			Configuration config = new Configuration(armorFile, true);
			config.load();
			
			String catName = this.categoryName() + "." + EnviroUtils.replaceULN(armor.getUnlocalizedName() +"_"+ regName[1]);
			
			if(armor == Items.diamond_helmet || armor == Items.diamond_chestplate || armor == Items.diamond_leggings || armor == Items.diamond_boots || (armor.getArmorMaterial() == ArmorMaterial.DIAMOND && EM_Settings.genConfigs))
			{
				config.get(catName, APName[0], Item.itemRegistry.getNameForObject(armor)).getString();
				config.get(catName, APName[1], 0.0D).getDouble(0.0D);
				config.get(catName, APName[2], 0.0D).getDouble(0.0D);
				config.get(catName, APName[3], 0.0D).getDouble(0.0D);
				config.get(catName, APName[4], 1.0D).getDouble(1.0D);
				config.get(catName, APName[5], 1.0D).getDouble(1.0D);
				config.get(catName, APName[6], 0.9D).getDouble(0.9D);
				config.get(catName, APName[7], 0.0D).getDouble(0.0D);
				config.get(catName, APName[8], 0.0D).getDouble(0.0D);
				
				if(armor.armorType == 1)
				{
					config.get(catName, APName[9], true).getBoolean(true);
				}
			} else if(armor == Items.iron_helmet || armor == Items.iron_chestplate || armor == Items.iron_leggings || armor == Items.iron_boots || (armor.getArmorMaterial() == ArmorMaterial.IRON && EM_Settings.genConfigs))
			{
				config.get(catName, APName[0], Item.itemRegistry.getNameForObject(armor)).getString();
				config.get(catName, APName[1], -1.0D).getDouble(-1.0D);
				config.get(catName, APName[2], 0.0D).getDouble(0.0D);
				config.get(catName, APName[3], 2.0D).getDouble(2.0D);
				config.get(catName, APName[4], 1.0D).getDouble(1.0D);
				config.get(catName, APName[5], 1.0D).getDouble(1.0D);
				config.get(catName, APName[6], 1.1D).getDouble(1.1D);
				config.get(catName, APName[7], 0.0D).getDouble(0.0D);
				config.get(catName, APName[8], 0.0D).getDouble(0.0D);
				
				if(armor.armorType == 1)
				{
					config.get(catName, APName[9], true).getBoolean(true);
				}
			} else if(armor == Items.golden_helmet || armor == Items.golden_chestplate || armor == Items.golden_leggings || armor == Items.golden_boots || (armor.getArmorMaterial() == ArmorMaterial.GOLD && EM_Settings.genConfigs))
			{
				config.get(catName, APName[0], Item.itemRegistry.getNameForObject(armor)).getString();
				config.get(catName, APName[1], 0.0D).getDouble(0.0D);
				config.get(catName, APName[2], 0.0D).getDouble(0.0D);
				config.get(catName, APName[3], 2.5D).getDouble(2.5D);
				config.get(catName, APName[4], 1.0D).getDouble(1.0D);
				config.get(catName, APName[5], 1.0D).getDouble(1.0D);
				config.get(catName, APName[6], 1.2D).getDouble(1.2D);
				config.get(catName, APName[7], 0.0D).getDouble(0.0D);
				config.get(catName, APName[8], 0.0D).getDouble(0.0D);
				
				if(armor.armorType == 1)
				{
					config.get(catName, APName[9], true).getBoolean(true);
				}
			} else if(armor == Items.leather_helmet || armor == Items.leather_chestplate || armor == Items.leather_leggings || armor == Items.leather_boots || (armor.getArmorMaterial() == ArmorMaterial.CLOTH && EM_Settings.genConfigs))
			{
				config.get(catName, APName[0], Item.itemRegistry.getNameForObject(armor)).getString();
				config.get(catName, APName[1], 1.0D).getDouble(1.0D);
				config.get(catName, APName[2], 1.0D).getDouble(1.0D);
				config.get(catName, APName[3], 1.0D).getDouble(1.0D);
				config.get(catName, APName[4], 1.0D).getDouble(1.0D);
				config.get(catName, APName[5], 1.0D).getDouble(1.0D);
				config.get(catName, APName[6], 1.0D).getDouble(1.0D);
				config.get(catName, APName[7], 0.0D).getDouble(0.0D);
				config.get(catName, APName[8], 0.0D).getDouble(0.0D);
				
				if(armor.armorType == 1)
				{
					config.get(catName, APName[9], true).getBoolean(true);
				}
			} else if(EM_Settings.genConfigs)
			{
				this.generateEmpty(config, armor);
				/*config.get(catName, APName[0], Item.itemRegistry.getNameForObject(armor)).getString();
				config.get(catName, APName[1], 0.0D).getDouble(0.0D);
				config.get(catName, APName[2], 0.0D).getDouble(0.0D);
				config.get(catName, APName[3], 0.0D).getDouble(0.0D);
				config.get(catName, APName[4], 1.0D).getDouble(1.0D);
				config.get(catName, APName[5], 1.0D).getDouble(1.0D);
				config.get(catName, APName[6], 1.0D).getDouble(1.0D);
				config.get(catName, APName[7], 0.0D).getDouble(0.0D);
				config.get(catName, APName[8], 0.0D).getDouble(0.0D);
				
				if(armor.armorType == 1)
				{
					config.get(catName, APName[9], true).getBoolean(true);
				}*/
			}
			
			config.save();
		}
	}

	@Override
	public File GetDefaultFile()
	{
		return new File(EM_ConfigHandler.loadedProfile + EM_ConfigHandler.customPath + "Armor.cfg");
	}

	@Override
	public void generateEmpty(Configuration config, Object obj)
	{
		if(obj == null || !(obj instanceof ItemArmor))
		{
			EnviroMine.logger.log(Level.ERROR, "Tried to register config with non armor object!", new Exception());
			return;
		}
		
		ItemArmor armor = (ItemArmor)obj;
		
		String[] regName = Item.itemRegistry.getNameForObject(armor).split(":");
		
		if(regName.length <= 0)
		{
			EnviroMine.logger.log(Level.ERROR, "Failed to get correctly formatted object name for " + armor.getUnlocalizedName());
			return;
		}
		
		String catName = this.categoryName() + "." + EnviroUtils.replaceULN(armor.getUnlocalizedName() +"_"+ regName);
		
		config.addCustomCategoryComment(catName, "");
		config.get(catName, APName[0], Item.itemRegistry.getNameForObject(armor)).getString();
		config.get(catName, APName[1], 0.0D).getDouble(0.0D);
		config.get(catName, APName[2], 0.0D).getDouble(0.0D);
		config.get(catName, APName[3], 0.0D).getDouble(0.0D);
		config.get(catName, APName[4], 1.0D).getDouble(1.0D);
		config.get(catName, APName[5], 1.0D).getDouble(1.0D);
		config.get(catName, APName[6], 1.0D).getDouble(1.0D);
		config.get(catName, APName[7], 0.0D).getDouble(0.0D);
		config.get(catName, APName[8], 0.0D).getDouble(0.0D);
		
		if(armor.armorType == 1)
		{
			config.get(catName, APName[9], true).getBoolean(true);
		}
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
		APName = new String[10];
		APName[0] = "01.ID";
		APName[1] = "02.Temp Add - Night";
		APName[2] = "03.Temp Add - Shade";
		APName[3] = "04.Temp Add - Sun";
		APName[4] = "05.Temp Multiplier - Night";
		APName[5] = "06.Temp Multiplier - Shade";
		APName[6] = "07.Temp Multiplier - Sun";
		APName[7] = "08.Sanity";
		APName[8] = "09.Air";
		APName[9] = "10.Allow Camel Pack";
	}
}
