package enviromine.client.gui.menu;

import java.math.BigDecimal;
import cpw.mods.fml.client.config.GuiSlider;
import enviromine.client.gui.SaveController;
import enviromine.client.gui.UI_Settings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StatCollector;

public class EM_Gui_SoundSettings extends GuiScreen
{
	
	private GuiScreen parentGuiScreen;

	public EM_Gui_SoundSettings(GuiScreen par1GuiScreen)
	{
		this.parentGuiScreen = par1GuiScreen;
	}
	
	@Override
	public void initGui()
	{
		float volume = UI_Settings.breathVolume * 100;
		
		GuiSlider maskSlider = new GuiSlider(149, this.width / 2 - 152, this.height / 6 + 24, 150, 20, StatCollector.translateToLocal("options.enviromine.breathVol") + ": ", "%", 0F, 100F, volume, false, true);
		maskSlider.updateSlider();
		this.buttonList.add(maskSlider);
		
		this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.back", new Object[0])));
	}

	@Override
	public void actionPerformed(GuiButton par1GuiButton)
	{

		if(par1GuiButton.enabled)
		{
			switch(par1GuiButton.id)
			{
				case 200:
				{
					this.mc.displayGuiScreen(parentGuiScreen);
					return;
				}
				case 149:
				{
					GuiSlider slider = (GuiSlider)par1GuiButton;
					
					UI_Settings.breathVolume = (float)slider.getValue() / 100;
					
					break;
				}
			}
		}
	}

	//TODO Needs to be better.. and not called every tick
	@Override
	public void mouseClickMove(int p_146273_1_, int p_146273_2_, int lastbutton, long time) 
	{
		GuiSlider Slide = (GuiSlider) this.buttonList.get(0);
		if(Slide.dragging)
		{
			actionPerformed(Slide);	
		}
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}
	
	@Override
	public void onGuiClosed() 
	{
		SaveController.saveConfig("UI_Settings");
	}
	
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("options.enviromine.guiSetting.title"), this.width / 2, 15, 16777215);
		super.drawScreen(par1, par2, par3);
	}
}
