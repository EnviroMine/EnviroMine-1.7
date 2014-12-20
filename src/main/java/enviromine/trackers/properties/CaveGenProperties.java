package enviromine.trackers.properties;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;
import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.handlers.ObjectHandler;
import enviromine.trackers.properties.helpers.PropertyBase;
import enviromine.utils.EnviroUtils;

public class CaveGenProperties implements PropertyBase
{
	public static final CaveGenProperties base = new CaveGenProperties();
	static String[] CGPNames;
	
	public Block ore;
	public int oreMeta;
	public Block source;
	public int srcMeta;
	public int veins;
	public int size;
	public int minY;
	public int maxY;
	
	public CaveGenProperties()
	{
		// THIS CONSTRUCTOR IS FOR STATIC PURPOSES ONLY!
		
		if(base != null && base != this)
		{
			throw new IllegalStateException();
		}
	}
	
	public CaveGenProperties(String id, int oreMeta, String source, int srcMeta, int veins, int size, int minY, int maxY)
	{
		this.ore = (Block)Block.blockRegistry.getObject(id);
		this.oreMeta = oreMeta;
		this.source = (Block)Block.blockRegistry.getObject(source);
		this.srcMeta = srcMeta;
		this.veins = veins;
		this.size = size;
		this.minY = minY;
		this.maxY = maxY;
	}

	@Override
	public String categoryName()
	{
		return "Cave Ores";
	}

	@Override
	public String categoryDescription()
	{
		return "Changes how ores generate in the cave dimension";
	}

	@Override
	public void LoadProperty(Configuration config, String category)
	{
		config.setCategoryComment(this.categoryName(), this.categoryDescription());
		String nID = config.get(category, CGPNames[0], "minecraft:stone").getString();
		int nOreM = MathHelper.clamp_int(config.get(category, CGPNames[1], 0).getInt(), 0, 15);
		String nSource = config.get(category, CGPNames[2], "minecraft:stone").getString();
		int nSrcM = config.get(category, CGPNames[3], 0).getInt();
		int nVeins = config.get(category, CGPNames[4], 4).getInt();
		int nSize = config.get(category, CGPNames[5], 4).getInt();
		int nMinY = config.get(category, CGPNames[6], 10).getInt();
		int nMaxY = config.get(category, CGPNames[7], 246).getInt();
		
		CaveGenProperties entry = new CaveGenProperties(nID, nOreM, nSource, nSrcM, nVeins, nSize, nMinY, nMaxY);
		EM_Settings.caveGenProperties.add(entry);
	}

	@Override
	public void SaveProperty(Configuration config, String category)
	{
		config.get(category, CGPNames[0], Block.blockRegistry.getIDForObject(ore));
		config.get(category, CGPNames[1], oreMeta);
		config.get(category, CGPNames[2], Block.blockRegistry.getNameForObject(source));
		config.get(category, CGPNames[3], srcMeta);
		config.get(category, CGPNames[4], veins);
		config.get(category, CGPNames[5], size);
		config.get(category, CGPNames[6], minY);
		config.get(category, CGPNames[7], maxY);
	}

	@Override
	public void GenDefaults()
	{
		File file = GetDefaultFile();
		
		try
		{
			if(file.createNewFile())
			{
				Configuration config = new Configuration(file, true);
				
				config.load();
				
				if(!config.hasCategory(this.categoryName() + ".Coal"))
				{
					String catName = this.categoryName() + ".Coal";
					
					config.get(catName, CGPNames[0], Block.blockRegistry.getNameForObject(ObjectHandler.flammableCoal));
					config.get(catName, CGPNames[1], 0);
					config.get(catName, CGPNames[2], Block.blockRegistry.getNameForObject(Blocks.stone));
					config.get(catName, CGPNames[3], 0);
					config.get(catName, CGPNames[4], 32);
					config.get(catName, CGPNames[5], 16);
					config.get(catName, CGPNames[6], 10);
					config.get(catName, CGPNames[7], 246);
				}
				
				if(!config.hasCategory(this.categoryName() + ".Iron"))
				{
					String catName = this.categoryName() + ".Iron";
					
					config.get(catName, CGPNames[0], Block.blockRegistry.getNameForObject(Blocks.iron_ore));
					config.get(catName, CGPNames[1], 0);
					config.get(catName, CGPNames[2], Block.blockRegistry.getNameForObject(Blocks.stone));
					config.get(catName, CGPNames[3], 0);
					config.get(catName, CGPNames[4], 24);
					config.get(catName, CGPNames[5], 16);
					config.get(catName, CGPNames[6], 10);
					config.get(catName, CGPNames[7], 246);
				}
				
				if(!config.hasCategory(this.categoryName() + ".Lapis"))
				{
					String catName = this.categoryName() + ".Lapis";
					
					config.get(catName, CGPNames[0], Block.blockRegistry.getNameForObject(Blocks.redstone_ore));
					config.get(catName, CGPNames[1], 0);
					config.get(catName, CGPNames[2], Block.blockRegistry.getNameForObject(Blocks.stone));
					config.get(catName, CGPNames[3], 0);
					config.get(catName, CGPNames[4], 12);
					config.get(catName, CGPNames[5], 8);
					config.get(catName, CGPNames[6], 10);
					config.get(catName, CGPNames[7], 246);
				}
				
				if(!config.hasCategory(this.categoryName() + ".Redstone"))
				{
					String catName = this.categoryName() + ".Redstone";
					
					config.get(catName, CGPNames[0], Block.blockRegistry.getNameForObject(Blocks.redstone_ore));
					config.get(catName, CGPNames[1], 0);
					config.get(catName, CGPNames[2], Block.blockRegistry.getNameForObject(Blocks.stone));
					config.get(catName, CGPNames[3], 0);
					config.get(catName, CGPNames[4], 12);
					config.get(catName, CGPNames[5], 12);
					config.get(catName, CGPNames[6], 10);
					config.get(catName, CGPNames[7], 246);
				}
				
				if(!config.hasCategory(this.categoryName() + ".Gold"))
				{
					String catName = this.categoryName() + ".Gold";
					
					config.get(catName, CGPNames[0], Block.blockRegistry.getNameForObject(Blocks.gold_ore));
					config.get(catName, CGPNames[1], 0);
					config.get(catName, CGPNames[2], Block.blockRegistry.getNameForObject(Blocks.stone));
					config.get(catName, CGPNames[3], 0);
					config.get(catName, CGPNames[4], 8);
					config.get(catName, CGPNames[5], 8);
					config.get(catName, CGPNames[6], 10);
					config.get(catName, CGPNames[7], 246);
				}
				
				if(!config.hasCategory(this.categoryName() + ".Diamonds"))
				{
					String catName = this.categoryName() + ".Diamonds";
					
					config.get(catName, CGPNames[0], Block.blockRegistry.getNameForObject(Blocks.diamond_ore));
					config.get(catName, CGPNames[1], 0);
					config.get(catName, CGPNames[2], Block.blockRegistry.getNameForObject(Blocks.stone));
					config.get(catName, CGPNames[3], 0);
					config.get(catName, CGPNames[4], 4);
					config.get(catName, CGPNames[5], 8);
					config.get(catName, CGPNames[6], 10);
					config.get(catName, CGPNames[7], 246);
				}
				
				if(!config.hasCategory(this.categoryName() + ".Emeralds"))
				{
					String catName = this.categoryName() + ".Emeralds";
					
					config.get(catName, CGPNames[0], Block.blockRegistry.getNameForObject(Blocks.emerald_ore));
					config.get(catName, CGPNames[1], 0);
					config.get(catName, CGPNames[2], Block.blockRegistry.getNameForObject(Blocks.stone));
					config.get(catName, CGPNames[3], 0);
					config.get(catName, CGPNames[4], 2);
					config.get(catName, CGPNames[5], 4);
					config.get(catName, CGPNames[6], 10);
					config.get(catName, CGPNames[7], 246);
				}
				
				if(!config.hasCategory(this.categoryName() + ".Silverfish")) // Must be last so as to not interfere with ores
				{
					String catName = this.categoryName() + ".Silverfish";
					
					config.get(catName, CGPNames[0], Block.blockRegistry.getNameForObject(Blocks.monster_egg));
					config.get(catName, CGPNames[1], 0);
					config.get(catName, CGPNames[2], Block.blockRegistry.getNameForObject(Blocks.stone));
					config.get(catName, CGPNames[3], 0);
					config.get(catName, CGPNames[4], 48);
					config.get(catName, CGPNames[5], 24);
					config.get(catName, CGPNames[6], 10);
					config.get(catName, CGPNames[7], 246);
				}
				
				config.save();
			}
		} catch(Exception e)
		{
			EnviroMine.logger.log(Level.ERROR, "An error occured while generating defaults for " + this.getClass().getSimpleName(), e);
			return;
		}
	}

	@Override
	public File GetDefaultFile()
	{
		return new File(EM_ConfigHandler.configPath + "/CaveDimension.cfg");
	}
	
	@Override
	public boolean hasDefault(Object obj)
	{
		return false;
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
		
		String catName = this.categoryName() + "." + EnviroUtils.replaceULN(block.getUnlocalizedName());
		
		config.get(catName, CGPNames[0], Block.blockRegistry.getNameForObject(Blocks.iron_ore));
		config.get(catName, CGPNames[1], 0);
		config.get(catName, CGPNames[2], Block.blockRegistry.getNameForObject(Blocks.stone));
		config.get(catName, CGPNames[3], 0);
		config.get(catName, CGPNames[4], 24);
		config.get(catName, CGPNames[5], 16);
		config.get(catName, CGPNames[6], 10);
		config.get(catName, CGPNames[7], 246);
	}

	@Override
	public boolean useCustomConfigs()
	{
		return false;
	}
	
	@Override
	public void customLoad()
	{
		Configuration config;
		
		try
		{
			config = new Configuration(this.GetDefaultFile(), true);
			
		} catch(Exception e)
		{
			EnviroMine.logger.log(Level.ERROR, "Failed to load custom configuration for " + this.getClass().getSimpleName(), e);
			return;
		} 
		
		config.load();

		ArrayList<String> entries = EM_ConfigHandler.getSubCategories(config, this.categoryName());
		Iterator<String> iterator = entries.iterator();
		
		while(iterator.hasNext())
		{
			this.LoadProperty(config, iterator.next());
		}
		
		config.save();
	}
	
	/**
	 * Sets up all of the static information about this property type
	 */
	static
	{
		EM_Settings.caveGenProperties = new ArrayList<CaveGenProperties>();
		
		CGPNames = new String[8];
		CGPNames[0] = "01.Ore Name";
		CGPNames[1] = "02.Ore Metadata";
		CGPNames[2] = "03.Source Block";
		CGPNames[3] = "04.Source Metadata";
		CGPNames[4] = "03.Veins Per Chunk";
		CGPNames[5] = "04.Veins Size";
		CGPNames[6] = "05.Min Y";
		CGPNames[7] = "06.Max Y";
	}
}
