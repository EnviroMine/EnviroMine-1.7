package enviromine.client.gui.menu.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.ArmorProperties;
import enviromine.trackers.properties.BiomeProperties;
import enviromine.trackers.properties.BlockProperties;
import enviromine.trackers.properties.CaveBaseProperties;
import enviromine.trackers.properties.EntityProperties;
import enviromine.trackers.properties.ItemProperties;
import enviromine.trackers.properties.RotProperties;
import enviromine.trackers.properties.StabilityType;

public class EM_ConfigMenu extends GuiConfig
{
	public static ArrayList<Configuration> tempConfigs = new ArrayList<Configuration>();
    //private GuiTextField searchBox; 
	
	public EM_ConfigMenu(GuiScreen parentScreen)
	{
		super(parentScreen, getMainElements(), EM_Settings.ModID, false, false, EM_Settings.Name, " ");
	}
	
    @Override
    protected void actionPerformed(GuiButton button)
    {

    		super.actionPerformed(button);
    }
	
    
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_)
    {

      //      this.searchBox.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
            
            super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);

    }
    
    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui()
    {
	    //Keyboard.enableRepeatEvents(true);
	      
    	//searchBox = new GuiTextField( mc.fontRenderer, this.width - 130, 5, 120, 20);
    	//searchBox.setText("Search");

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
	
	
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        //this.searchBox.updateCursorCounter();
    }
    
	protected void keyTyped(char p_73869_1_, int p_73869_2_)
	{
	    //this.searchBox.textboxKeyTyped(p_73869_1_, p_73869_2_);
	    //this.getSearchList(this.searchBox.getText());
	}
	   
	  @Override
	  public void drawScreen(int mouseX, int mouseY, float partialTicks)
	  {
 		  super.drawScreen(mouseX, mouseY, partialTicks);
	        this.drawCenteredString(this.fontRendererObj, I18n.format("editor.enviromine.currentProfile") +": "+ EM_ConfigHandler.getProfileName(), this.width / 2, 18, 16777215);
	      //  this.searchBox.drawTextBox();
	  }
	  
}
	