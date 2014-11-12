package enviromine.client.gui.menu;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StatCollector;

import enviromine.core.EnviroMine;

public class EM_Gui_Menu extends GuiScreen
{
	
	private GuiScreen parentGuiScreen;

	public EM_Gui_Menu(GuiScreen par1GuiScreen)
	{
		this.parentGuiScreen = par1GuiScreen;
	}
	
	@Override
	public void initGui()
	{
		GuiButton serverSettings = new GuiButton(100, this.width / 2 - 90, this.height / 6 + 98 - 6, 180, 20, "(Coming Soon)"+ StatCollector.translateToLocal("options.enviromine.configSettings"));
		GuiButton customEditor =  new GuiButton(100, this.width / 2 - 90, this.height / 6 + 122 - 6, 180, 20, "(Coming Soon)"+ StatCollector.translateToLocal("options.enviromine.customEditor"));
		
		// The old if statement would never work, GUIs are never run server side and mc.thePlayer is not accessible to server side functions - Funwayguy
		EntityPlayerMP playerMP = mc.getIntegratedServer().isServerRunning()? MinecraftServer.getServer().getConfigurationManager().func_152612_a(mc.thePlayer.getCommandSenderName()) : null;
		
		if(playerMP != null && playerMP.getGameProfile() != null && MinecraftServer.getServer().getConfigurationManager().func_152596_g(playerMP.getGameProfile()))
		{
			serverSettings.enabled = true;
			customEditor.enabled = true;
		}
		else
		{
			serverSettings.enabled = false;
			customEditor.enabled = false;			
		}
		
		serverSettings.enabled = false;
		customEditor.enabled = false;	
		
		serverSettings.visible = true;
		customEditor.visible = true;			
	
		this.buttonList.add(new GuiButton(101, this.width / 2 - 90, this.height / 6 + 50 - 6, 180, 20, StatCollector.translateToLocal("options.enviromine.guiOptions")+"..."));
		this.buttonList.add(serverSettings);
		this.buttonList.add(customEditor);
		this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, StatCollector.translateToLocal("gui.done")));
		
		
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
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
