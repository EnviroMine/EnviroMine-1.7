package enviromine.client.gui.menu;

import java.awt.Desktop;
import java.net.URI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StatCollector;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.client.gui.UpdateNotification;
import enviromine.client.gui.menu.config.EM_ConfigMenu;
import enviromine.client.gui.menu.update.NewsPage;
import enviromine.core.EnviroMine;


@SideOnly(Side.CLIENT)
public class EM_Gui_Menu extends GuiScreen implements GuiYesNoCallback
{
	
	private GuiScreen parentGuiScreen = null;
	
	public EM_Gui_Menu()
	{
		
	}
	
	public EM_Gui_Menu(GuiScreen par1GuiScreen)
	{
		this.parentGuiScreen = par1GuiScreen;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		GuiButton serverSettings = new GuiButton(103, this.width / 2 - 90, this.height / 6 + 98 - 6, 180, 20, "(Coming Soon)"+ StatCollector.translateToLocal("options.enviromine.configSettings"));
		GuiButton customEditor =  new GuiButton(104, this.width / 2 - 90, this.height / 6 + 122 - 6, 180, 20, StatCollector.translateToLocal("options.enviromine.customEditor"));
		
		serverSettings.enabled = false;
		customEditor.enabled = Minecraft.getMinecraft().isIntegratedServerRunning();
		
		serverSettings.visible = true;
		
		String newPost = UpdateNotification.isNewPost() ? " " + StatCollector.translateToLocal("news.enviromine.newpost") : "";
		
		this.buttonList.add(new GuiButton(105, this.width / 2 - 90, this.height / 6 + 4, 180, 20, StatCollector.translateToLocal("options.enviromine.newsPage")+"..."+ newPost));
		this.buttonList.add(new GuiButton(101, this.width / 2 - 90, this.height / 6 + 44, 180, 20, StatCollector.translateToLocal("options.enviromine.guiOptions")+"..."));
		this.buttonList.add(new GuiButton(102, this.width / 2 - 90, this.height / 6 + 24, 180, 20, StatCollector.translateToLocal("options.enviromine.guiSounds")+"..."));
		this.buttonList.add(serverSettings);
		this.buttonList.add(customEditor);
		this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, StatCollector.translateToLocal("gui.done")));
		
		this.buttonList.add(new GuiButton(300, 30 , this.height -55 , 75, 20, StatCollector.translateToLocal("options.enviromine.supportUs")));
		this.buttonList.add(new GuiButton(301, 30, this.height -30 , 75,20, StatCollector.translateToLocal("options.enviromine.website")));
		
		
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}
	
	private String ourwebsite = "https://enviromine.wordpress.com/";
	private String supportPage = "https://enviromine.wordpress.com/support-us/";
	
	
	/* Send player to URL from this menu
	 * (non-Javadoc)
	 * @see net.minecraft.client.gui.GuiScreen#confirmClicked(boolean, int)
	 */
	@Override
	public void confirmClicked(boolean p_73878_1_, int p_73878_2_) 
	{
		String url = "";
		boolean go = false;
		
		if(p_73878_1_) // if true
		{
			if(p_73878_2_ == 1)
			{
				url = ourwebsite;
				go = true;
			}
			
			if(p_73878_2_ == 2)
			{
				url = supportPage;
				go = true;
			}		
			
			if(Desktop.isDesktopSupported() && go)
			{
				try 
				{
					Desktop.getDesktop().browse(new URI(url));
				}catch (Exception e) {
					EnviroMine.logger.log(Level.WARN, "(EM_Gui_Menu) Failed to open Default Browser to: " + url);
				}
			}

		}
		
		
		this.mc.displayGuiScreen(this);
	}
	
	/**
	 * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		if (par1GuiButton.id == 100)
		{
			this.mc.displayGuiScreen(new EM_Gui_General(this));	
		}
		else if (par1GuiButton.id == 101)
		{
			this.mc.displayGuiScreen(new EM_Gui_GuiSettings(this));	
		}
		else if (par1GuiButton.id == 102)
		{
			this.mc.displayGuiScreen(new EM_Gui_SoundSettings(this));	
		}
		else if (par1GuiButton.id == 103)
		{
			return; // Server settings. Coming soon...
		}
		else if (par1GuiButton.id == 104)
		{
			this.mc.displayGuiScreen(new EM_ConfigMenu(this)); // In game editor
		}
		else if (par1GuiButton.id == 105)
		{
			this.mc.displayGuiScreen(new NewsPage(this, 150));
		}
		else if(par1GuiButton.id == 301)
		{
			this.mc.displayGuiScreen(new GuiYesNo(this, StatCollector.translateToLocal("options.enviromine.website"), StatCollector.translateToLocal("options.enviromine.website.YesNo"), 1));
		}
		else if(par1GuiButton.id == 300)
		{
			this.mc.displayGuiScreen(new GuiYesNo(this, StatCollector.translateToLocal("options.enviromine.supportUs"), StatCollector.translateToLocal("options.enviromine.website.YesNo"), 2));
		}
		else if (par1GuiButton.id == 200)
		{
			this.mc.displayGuiScreen(parentGuiScreen);
		}

	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		
		if(!EnviroMine.proxy.isClient() && MinecraftServer.getServer().getConfigurationManager().func_152607_e(mc.thePlayer.getGameProfile()) || EnviroMine.proxy.isClient() )
		{
			this.drawString(this.fontRendererObj, StatCollector.translateToLocal("options.enviromine.adminOptions.title") +" ", this.width / 2 -30, this.height / 6 + 74, 16777215);
		}
		this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("options.enviromine.guiMainmenu.title"), this.width / 2, 15, 16777215);
		super.drawScreen(par1, par2, par3);
	}
}
