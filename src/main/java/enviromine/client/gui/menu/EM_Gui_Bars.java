package enviromine.client.gui.menu;

import java.util.ArrayList;

import enviromine.client.gui.UI_Settings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StatCollector;

public class EM_Gui_Bars extends GuiScreen
{
	private GuiScreen parentGuiScreen;
	private ArrayList posList = new ArrayList();

	//"top_left""top_right""top_center""bottom_left""bottom_right"bottom_center_right""bottom_center_left""middle_right""middle_left"
	
	
	public EM_Gui_Bars(GuiScreen par1GuiScreen)
	{
		this.parentGuiScreen = par1GuiScreen;
		this.posList.add("top_left");
		this.posList.add("top_right");
		this.posList.add("top_center");
		this.posList.add("bottom_left");
		this.posList.add("bottom_right");
		this.posList.add("bottom_center_right");
		this.posList.add("bottom_center_left");
		this.posList.add("middle_right");
		this.posList.add("middle_left");
	}
	//id, x, y, width, height, text

	@Override
	public void initGui()
	{
		this.buttonList.add(new GuiButton(101, this.width / 2 + 5 - 20, this.height / 6 + 24 - 6, 120, 20, UI_Settings.waterBarPos  ));
		this.buttonList.add(new GuiButton(102, this.width / 2 + 5 - 20, this.height / 6 + 48 - 6, 120, 20,  UI_Settings.heatBarPos ));
		this.buttonList.add(new GuiButton(103, this.width / 2 + 5 - 20, this.height / 6 + 72 - 6, 120, 20,  UI_Settings.sanityBarPos ));
		this.buttonList.add(new GuiButton(104, this.width / 2 + 5 - 20, this.height / 6 + 96 - 6, 120, 20,  UI_Settings.oxygenBarPos ));
		this.buttonList.add(new GuiButton(200, this.width / 2 - 75, this.height / 6 + 120 - 6, 150, 20, I18n.format( "gui.back", new Object[0])));
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
			int val = 0;
			int where;
			switch(par1GuiButton.id)
			{
				case 200:
					this.mc.displayGuiScreen(parentGuiScreen);
					return;
				case 101: 
					 where = posList.indexOf(UI_Settings.waterBarPos.toLowerCase().trim());
					if(where + 1 < posList.size()-1)val = where + 1;
					UI_Settings.waterBarPos = (String) posList.get(val);
					par1GuiButton.displayString = UI_Settings.waterBarPos;
					break;
				case 102: 
					 where = posList.indexOf(UI_Settings.heatBarPos.toLowerCase().trim());
					if(where + 1 < posList.size()-1)val = where + 1;		
					UI_Settings.heatBarPos = (String) posList.get(val);
					par1GuiButton.displayString = UI_Settings.heatBarPos;
					break;
				case 103: //sanity
					where = posList.indexOf(UI_Settings.sanityBarPos.toLowerCase().trim());
					if(where + 1 < posList.size()-1)val = where + 1;					
					UI_Settings.sanityBarPos = (String) posList.get(val);
					par1GuiButton.displayString = UI_Settings.sanityBarPos;
					break;
				case 104: // oxygen
					 where = posList.indexOf(UI_Settings.oxygenBarPos.toLowerCase().trim());
					 if(where + 1 < posList.size()-1)val = where + 1;
					 UI_Settings.oxygenBarPos = (String) posList.get(val);
					 par1GuiButton.displayString = UI_Settings.oxygenBarPos;
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
		
		
		this.drawString(this.fontRendererObj, StatCollector.translateToLocal("options.enviromine.barPos.hydration") +": ", this.width / 2 - 75 - 22, this.height / 6 + 24, 16777215);
		this.drawString(this.fontRendererObj, StatCollector.translateToLocal("options.enviromine.barPos.temprature") +": ", this.width / 2 - 75 - 22, this.height / 6 + 48, 16777215);
		this.drawString(this.fontRendererObj, StatCollector.translateToLocal("options.enviromine.barPos.sanity") +": ", this.width / 2 - 75 - 22, this.height / 6 + 72, 16777215);
		this.drawString(this.fontRendererObj, StatCollector.translateToLocal("options.enviromine.barPos.airQuality") +": ", this.width / 2 - 75 - 22, this.height / 6 + 96, 16777215);
		
		super.drawScreen(par1, par2, par3);
	}
}
