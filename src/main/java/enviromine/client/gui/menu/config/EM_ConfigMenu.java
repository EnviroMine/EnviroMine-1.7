package enviromine.client.gui.menu.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.java.games.input.Keyboard;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import enviromine.client.gui.menu.EM_Gui_General;
import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.CaveBaseProperties;
import enviromine.trackers.properties.StabilityType;

public class EM_ConfigMenu extends GuiConfig
{
	public static ArrayList<Configuration> tempConfigs = new ArrayList<Configuration>();
	
	public EM_ConfigMenu(GuiScreen parentScreen)
	{
		super(parentScreen, getMainElements(), EM_Settings.ModID, false, false, EM_Settings.Name, " ");
	}
	
    @Override
    protected void actionPerformed(GuiButton button)
    {

    		super.actionPerformed(button);
    	
    		if(button.id == 2050) this.mc.displayGuiScreen(new ProfileMenu(this));	
    }
	
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui()
    {
    	int changeProfileWidth = mc.fontRenderer.getStringWidth(I18n.format("editor.enviromine.changeprofile"));
    	this.buttonList.add(new GuiButtonExt(2050, this.width / 2 - (changeProfileWidth/2) - 10, 35,  changeProfileWidth + 20, 20, I18n.format("editor.enviromine.changeprofile")));
    	super.initGui();
    }
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private static List<IConfigElement> getMainElements()
	{
		tempConfigs.clear();
		List<IConfigElement> list = new ArrayList<IConfigElement>();
				
		File mainFile = new File(EM_ConfigHandler.configPath + "Global_Settings.cfg");
		list.add(new DummyCategoryElement("Global Config", "editor.enviromine.global", getConfigElements(mainFile)));
		
		File profilSettings = new File(EM_ConfigHandler.loadedProfile + EnviroMine.theWorldEM.getProfile()+"_Settings.cfg");
		list.add(new DummyCategoryElement("Profile Settings", "editor.enviromine.settings", getConfigElements(profilSettings)));
	
		File caveFile = CaveBaseProperties.base.GetDefaultFile();
		list.add(new DummyCategoryElement("Cave Dimension", "editor.enviromine.cave", getConfigElements(caveFile)));
		
		File stabFile = StabilityType.base.GetDefaultFile();
		list.add(new DummyCategoryElement("Stability Types", "editor.enviromine.stability", getConfigElements(stabFile)));
		
		File[] customFiles = new File(EM_ConfigHandler.loadedProfile + EM_ConfigHandler.customPath).listFiles();
		list.add(new DummyCategoryElement("Custom Configs", "editor.enviromine.custom", getConfigElements(customFiles)));
		
		return list;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static List<IConfigElement> getConfigElements(File... files)
	{
		List<IConfigElement> customFileList = new ArrayList<IConfigElement>();
		
		for(File entry : files)
		{
			if(entry.getName().contains(".DS_Store")) continue;
			
			//entry.getName()
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
			customFileList.add(new DummyCategoryElement(entry.getName(), "", customConfigList));
		}
		
		return customFileList;
	}
	
	  @Override
	  public void drawScreen(int mouseX, int mouseY, float partialTicks)
	  {
 		  super.drawScreen(mouseX, mouseY, partialTicks);
	        this.drawCenteredString(this.fontRendererObj, "Current Profile: "+ EM_ConfigHandler.getProfileName(), this.width / 2, 18, 16777215);

	  }
 
}
	