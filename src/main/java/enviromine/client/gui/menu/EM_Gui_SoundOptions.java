package enviromine.client.gui.menu;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

public class EM_Gui_SoundOptions extends GuiScreen
{
	private GuiScreen parentGuiScreen;

	public EM_Gui_SoundOptions(GuiScreen par1GuiScreen)
	{
		this.parentGuiScreen = par1GuiScreen;
	
	}
	//id, x, y, width, height, text

	@Override
	public void initGui()
	{
		//this.buttonList.add(new GuiButton(101, this.width / 2 + 5 - 20, this.height / 6 + 24 - 6, 120, 20, UI_Settings.waterBarPos  ));
		//this.buttonList.add(new GuiButton(102, this.width / 2 + 5 - 20, this.height / 6 + 48 - 6, 120, 20,  UI_Settings.heatBarPos ));
		//this.buttonList.add(new GuiButton(103, this.width / 2 + 5 - 20, this.height / 6 + 72 - 6, 120, 20,  UI_Settings.sanityBarPos ));
		//this.buttonList.add(new GuiButton(104, this.width / 2 + 5 - 20, this.height / 6 + 96 - 6, 120, 20,  UI_Settings.oxygenBarPos ));
		//this.buttonList.add(new GuiButton(200, this.width / 2 - 75, this.height / 6 + 120 - 6, 150, 20, I18n.format( "gui.back", new Object[0])));
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
	public void actionPerformed(GuiButton par1GuiButton)
	{
		if(par1GuiButton.enabled)
		{
			switch(par1GuiButton.id)
			{
				case 200:
					this.mc.displayGuiScreen(parentGuiScreen);
					return;
				case 101: 
					break;
				case 102: 
					break;
				case 103: //sanity
					break;
				case 104: // oxygen
				break;
			}
		}
	}
	
	@Override
	public void onGuiClosed() 
	{
	    UI_Settings.saveSettings();	
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("options.enviromine.guiBarPos.title"), this.width / 2, 15, 16777215);
		
//		this.drawString(this.fontRendererObj, StatCollector.translateToLocal("options.enviromine.barPos.hydration") +": ", this.width / 2 - 75 - 22, this.height / 6 + 24, 16777215);
//		this.drawString(this.fontRendererObj, StatCollector.translateToLocal("options.enviromine.barPos.temprature") +": ", this.width / 2 - 75 - 22, this.height / 6 + 48, 16777215);
//		this.drawString(this.fontRendererObj, StatCollector.translateToLocal("options.enviromine.barPos.sanity") +": ", this.width / 2 - 75 - 22, this.height / 6 + 72, 16777215);
//		this.drawString(this.fontRendererObj, StatCollector.translateToLocal("options.enviromine.barPos.airQuality") +": ", this.width / 2 - 75 - 22, this.height / 6 + 96, 16777215);

		super.drawScreen(par1, par2, par3);
	}
}
