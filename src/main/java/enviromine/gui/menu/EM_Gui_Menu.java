package enviromine.gui.menu;

import enviromine.core.EnviroMine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StatCollector;

public class EM_Gui_Menu extends GuiScreen
{
	
	public EM_Gui_Menu()
	{
		System.out.println("tester");
	}
	
	@Override
	public void initGui()
	{
		GuiButton serverSettings = new GuiButton(100, this.width / 2 - 75, this.height / 6 + 74 - 6, 150, 20, StatCollector.translateToLocal("emoptions.configSettings"));
		GuiButton customEditor =  new GuiButton(100, this.width / 2 - 75, this.height / 6 + 98 - 6, 150, 20, StatCollector.translateToLocal("emoptions.customEditor"));
		
		if(!EnviroMine.proxy.isClient() && MinecraftServer.getServer().getConfigurationManager().func_152607_e(mc.thePlayer.getGameProfile()) || EnviroMine.proxy.isClient() )
		{
			serverSettings.enabled = true;
			customEditor.enabled = true;
		}
		else
		{
			serverSettings.enabled = false;
			customEditor.enabled = false;			
		}
		
		this.buttonList.add(new GuiButton(101, this.width / 2 - 75, this.height / 6 + 50 - 6, 150, 20, StatCollector.translateToLocal("emoptions.guiOptions")));
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
			this.mc.displayGuiScreen(null);
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("emoptions.guiMainmenu.title"), this.width / 2, 15, 16777215);
		super.drawScreen(par1, par2, par3);
	}
}
