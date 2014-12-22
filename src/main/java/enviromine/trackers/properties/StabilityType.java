package enviromine.trackers.properties;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;
import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.helpers.PropertyBase;

public class StabilityType implements PropertyBase
{
	public static final StabilityType base = new StabilityType();
	static String[] SPName;
	
	public String name;
	public boolean enablePhysics;
	public int supportDist;
	public int minFall;
	public int maxFall;
	public boolean canHang;
	public boolean holdOther;
	
	public StabilityType()
	{
		// THIS CONSTRUCTOR IS FOR STATIC PURPOSES ONLY!
		
		if(base != null && base != this)
		{
			throw new IllegalStateException();
		}
	}
	
	public StabilityType(String name, boolean enablePhysics, int supportDist, int minFall, int maxFall, boolean canHang, boolean holdOther)
	{
		this.name = name;
		this.enablePhysics = enablePhysics;
		this.supportDist = supportDist;
		this.minFall = minFall;
		this.maxFall = maxFall;
		this.canHang = canHang;
		this.holdOther = holdOther;
	}

	@Override
	public String categoryName()
	{
		return "stability";
	}

	@Override
	public String categoryDescription()
	{
		return "Custom stability types for block physics";
	}

	@Override
	public void LoadProperty(Configuration config, String category)
	{
		config.setCategoryComment(this.categoryName(), this.categoryDescription());
		boolean physEnable = config.get(category, SPName[0], true).getBoolean(true);
		int supportDist = config.get(category, SPName[1], 0).getInt(0);
		int minFall = config.get(category, SPName[2], -1).getInt(-1);
		int maxFall = config.get(category, SPName[3], -1).getInt(-1);
		boolean canHang = config.get(category, SPName[4], false).getBoolean(false);
		boolean holdOther = config.get(category, SPName[5], false).getBoolean(false);
		
		String name = category.replaceFirst(this.categoryName() + ".", "");
		EM_Settings.stabilityTypes.put(name, new StabilityType(name, physEnable, supportDist, minFall, maxFall, canHang, holdOther));
	}

	@Override
	public void SaveProperty(Configuration config, String category)
	{
		config.get(category, SPName[0], enablePhysics).getBoolean(enablePhysics);
		config.get(category, SPName[1], supportDist).getInt(supportDist);
		config.get(category, SPName[2], minFall).getInt(minFall);
		config.get(category, SPName[3], maxFall).getInt(maxFall);
		config.get(category, SPName[4], canHang).getBoolean(canHang);
		config.get(category, SPName[5], holdOther).getBoolean(holdOther);
	}

	@Override
	public void GenDefaults()
	{
		File file = this.GetDefaultFile();

		if(!file.exists())
		{
			try
			{
				file.createNewFile();
			} catch(Exception e)
			{
				EnviroMine.logger.log(Level.ERROR, "Failed to create file for StabilityTypes", e);
				return;
			}
		}
		
		Configuration config = new Configuration(file, true);
		
		config.load();
		
		String catName = this.categoryName() + ".sand-like";
		config.get(catName, SPName[0], true).getBoolean(true);
		config.get(catName, SPName[1], 0).getInt(0);
		config.get(catName, SPName[2], -1).getInt(-1);
		config.get(catName, SPName[3], -1).getInt(-1);
		config.get(catName, SPName[4], false).getBoolean(false);
		config.get(catName, SPName[5], false).getBoolean(false);
		
		catName = this.categoryName() + ".loose";
		config.get(catName, SPName[0], true).getBoolean(true);
		config.get(catName, SPName[1], 1).getInt(1);
		config.get(catName, SPName[2], 10).getInt(10);
		config.get(catName, SPName[3], 15).getInt(15);
		config.get(catName, SPName[4], false).getBoolean(false);
		config.get(catName, SPName[5], false).getBoolean(false);

		catName = this.categoryName() + ".average";
		config.get(catName, SPName[0], true).getBoolean(true);
		config.get(catName, SPName[1], 2).getInt(2);
		config.get(catName, SPName[2], 15).getInt(15);
		config.get(catName, SPName[3], 22).getInt(22);
		config.get(catName, SPName[4], true).getBoolean(true);
		config.get(catName, SPName[5], false).getBoolean(false);

		catName = this.categoryName() + ".strong";
		config.get(catName, SPName[0], true).getBoolean(true);
		config.get(catName, SPName[1], 3).getInt(3);
		config.get(catName, SPName[2], 22).getInt(22);
		config.get(catName, SPName[3], 25).getInt(25);
		config.get(catName, SPName[4], true).getBoolean(true);
		config.get(catName, SPName[5], false).getBoolean(false);

		catName = this.categoryName() + ".none";
		config.get(catName, SPName[0], false).getBoolean(false);
		config.get(catName, SPName[1], 3).getInt(3);
		config.get(catName, SPName[2], 0).getInt(0);
		config.get(catName, SPName[3], 0).getInt(0);
		config.get(catName, SPName[4], true).getBoolean(true);
		config.get(catName, SPName[5], false).getBoolean(false);

		catName = this.categoryName() + ".glowstone";
		config.get(catName, SPName[0], false).getBoolean(false);
		config.get(catName, SPName[1], 3).getInt(3);
		config.get(catName, SPName[2], 0).getInt(0);
		config.get(catName, SPName[3], 0).getInt(0);
		config.get(catName, SPName[4], true).getBoolean(true);
		config.get(catName, SPName[5], true).getBoolean(true);
		
		config.save();
	}

	@Override
	public File GetDefaultFile()
	{
		return new File(EM_ConfigHandler.configPath + "StabilityTypes.cfg");
	}

	@Override
	public void generateEmpty(Configuration config, Object obj)
	{
		if(obj == null || !(obj instanceof String))
		{
			EnviroMine.logger.log(Level.ERROR, "Tried to register config with non string object!", new Exception());
			return;
		}
		
		String catName = this.categoryName() + "." + (String)obj;
		
		config.get(catName, SPName[0], true).getBoolean(true);
		config.get(catName, SPName[1], 1).getInt(1);
		config.get(catName, SPName[2], 10).getInt(10);
		config.get(catName, SPName[3], 15).getInt(15);
		config.get(catName, SPName[4], false).getBoolean(false);
		config.get(catName, SPName[5], false).getBoolean(false);
	}

	@Override
	public boolean useCustomConfigs()
	{
		return false;
	}

	@Override
	public void customLoad()
	{
		File file = this.GetDefaultFile();

		if(!file.exists())
		{
			try
			{
				file.createNewFile();
			} catch(Exception e)
			{
				EnviroMine.logger.log(Level.ERROR, "Failed to create file for StabilityTypes", e);
				return;
			}
		}
		
		Configuration config;
		
		try
		{
			config = new Configuration(file, true);
		} catch(Exception e)
		{
			EnviroMine.logger.log(Level.ERROR, "Failed to load stability types!", e);
			return;
		}
		
		config.load();
		
		// 	Grab all Categories in File
		List<String> catagory = new ArrayList<String>();
		Set<String> nameList = config.getCategoryNames();
		Iterator<String> nameListData = nameList.iterator();
		
		// add Categories to a List 
		while(nameListData.hasNext())
		{
			String catName = nameListData.next();
			
			if(catName.startsWith(this.categoryName() + "."))
			{
				catagory.add(catName);
			}
		}
		
		// Now Read/Save Each Category And Add into Proper Hash Maps
		
		for(int x = 0; x <= (catagory.size() - 1); x++)
		{
			String currentCat = catagory.get(x);
			
			this.LoadProperty(config, currentCat);
		}
		
		config.save();
	}
	
	static
	{
		SPName = new String[6];
		SPName[0] = "01.Enable Physics";
		SPName[1] = "02.Max Support Distance";
		SPName[2] = "03.Min Missing Blocks To Fall";
		SPName[3] = "04.Max Missing Blocks To Fall";
		SPName[4] = "05.Can Hang";
		SPName[5] = "06.Holds Others Up";
	}
}
