package enviromine.client.gui.menu.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import enviromine.core.EM_ConfigHandler;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.CaveBaseProperties;
import enviromine.trackers.properties.StabilityType;

public class EM_ConfigMenu extends GuiConfig
{
	public static ArrayList<Configuration> tempConfigs = new ArrayList<Configuration>();
	
	public EM_ConfigMenu(GuiScreen parentScreen)
	{
		super(parentScreen, getMainElements(), EnviroMine.ModID, false, false, EnviroMine.Name);
	}
	
    /*@Override
    protected void actionPerformed(GuiButton button)
    {
    	if(!Keyboard.isRepeatEvent())
    	{
    		super.actionPerformed(button);
    	}
    }*/
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private static List<IConfigElement> getMainElements()
	{
		tempConfigs.clear();
		List<IConfigElement> list = new ArrayList<IConfigElement>();
		
		File mainFile = new File(EM_ConfigHandler.configPath + "EnviroMine.cfg");
		list.add(new DummyCategoryElement("Main Config", "editor.enviromine.main", getConfigElements(mainFile)));
		
		File caveFile = CaveBaseProperties.base.GetDefaultFile();
		list.add(new DummyCategoryElement("Cave Dimension", "editor.enviromine.cave", getConfigElements(caveFile)));
		
		File stabFile = StabilityType.base.GetDefaultFile();
		list.add(new DummyCategoryElement("Stability Types", "editor.enviromine.stability", getConfigElements(stabFile)));
		
		File[] customFiles = new File(EM_ConfigHandler.customPath).listFiles();
		customFiles = customFiles != null? customFiles : new File[0];
		
		list.add(new DummyCategoryElement("Custom Configs", "editor.enviromine.custom", getConfigElements(customFiles)));
		
		return list;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static List<IConfigElement> getConfigElements(File... files)
	{
		List<IConfigElement> customFileList = new ArrayList<IConfigElement>();
		
		for(File entry : files)
		{
			Configuration config = new Configuration(entry, true);
			tempConfigs.add(config);
			Iterator<String> iterator = config.getCategoryNames().iterator();
			List<IConfigElement> customConfigList = new ArrayList<IConfigElement>();
			while(iterator.hasNext())
			{
				ConfigCategory category = config.getCategory(iterator.next());
				if(!category.isChild())
				{
					customConfigList.add(new ConfigElement(category));
				}
			}
			customFileList.add(new DummyCategoryElement(entry.getName(), "editor.enviromine.custom", customConfigList));
		}
		
		return customFileList;
	}
}
