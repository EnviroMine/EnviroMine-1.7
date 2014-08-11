package enviromine.gui.menu;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.core.EM_Settings;

@SideOnly(Side.CLIENT)
public class EM_Gui_GuiSettings extends GuiScreen
{
	
	private GuiScreen parentGuiScreen;

	public EM_Gui_GuiSettings(GuiScreen par1GuiScreen)
	{
		this.parentGuiScreen = par1GuiScreen;
	}
	//id, x, y, width, height, text

	@Override
	public void initGui()
	{
		//this.buttonList.add(new GuiDrag(151, this.width / 2 - 152, this.height / 6 + 200 - 6, 150, 20, StatCollector.translateToLocal("emoptions.tempScale")+": "+StatCollector.translateToLocal("empotions.tempScale.f")));
	

		GuiButton tempScalebutton = new GuiButton(151, this.width / 2 - 152, this.height / 6 + 30 - 6, 150, 20, StatCollector.translateToLocal("emoptions.tempScale")+": "+StatCollector.translateToLocal("empotions.tempScale.f"));

		GuiButton showstatusbutton = new GuiButton(156, this.width / 2 - 152, this.height / 6 + 96 - 6, 150, 20, StatCollector.translateToLocal("emoptions.status"));
		
		GuiButton showdebugbutton = new GuiButton(150, this.width / 2 - 152, this.height / 6 + 52 - 6, 150, 20, StatCollector.translateToLocal("emoptions.debug"));
		
		GuiButton showiconsbutton = new GuiButton(154,this.width / 2 - 152, this.height / 6 + 74 - 6 , 150, 20, StatCollector.translateToLocal("emoptions.showIcons"));

		GuiButton sweatParticlesbutton = new GuiButton(159, this.width / 2 + 2, this.height / 6 + 96 - 6, 150, 20, StatCollector.translateToLocal("emoptions.sweatParticles"));

		GuiButton insaneParticlesbutton = new GuiButton(152, this.width / 2 + 2, this.height / 6 + 74 - 6, 150, 20, StatCollector.translateToLocal("emoptions.insaneParticles"));


		GuiButton minbarsbutton = new GuiButton(158, this.width / 2 + 2, this.height / 6 + 52 - 6, 150, 20, StatCollector.translateToLocal("emoptions.minBars"));
	
		
		if(EM_Settings.minimalHud)
		{
			showstatusbutton.enabled = false;
			showiconsbutton.enabled = false;
		}
		else 
		{
			showstatusbutton.enabled = true;
			showiconsbutton.enabled = true;
		}		
		

		tempScalebutton.displayString = I18n.format("emoptions.tempScale", new Object[0]) + ": " + I18n.format("emoptions.tempScale."+ (EM_Settings.useFarenheit == true ? "f" : "c" ), new Object[0]);
			this.buttonList.add(tempScalebutton);
		showstatusbutton.displayString =I18n.format("emoptions.status", new Object[0]) + ": " + I18n.format("emoptions.status."+ (EM_Settings.ShowText == true || EM_Settings.minimalHud == true ? "visible" : "hidden" ), new Object[0]);
			this.buttonList.add(showstatusbutton);
		showdebugbutton.displayString =I18n.format("emoptions.debug", new Object[0]) + ": " + I18n.format("emoptions.debug."+ (EM_Settings.ShowDebug == true ? "visible" : "hidden" ), new Object[0]);
			this.buttonList.add(showdebugbutton);
		showiconsbutton.displayString =I18n.format("emoptions.showIcons", new Object[0]) + ": " + I18n.format("emoptions.showIcons."+ (EM_Settings.ShowGuiIcons == true || EM_Settings.minimalHud == true ? "visible" : "hidden" ), new Object[0]);
			this.buttonList.add(showiconsbutton);
		sweatParticlesbutton.displayString = I18n.format("emoptions.sweatParticles", new Object[0]) + ": " + I18n.format("emoptions.sweatParticles."+ (EM_Settings.sweatParticals == true ? "on" : "off" ), new Object[0]);
			this.buttonList.add(sweatParticlesbutton);
		insaneParticlesbutton.displayString = I18n.format("emoptions.insaneParticles", new Object[0]) + ": " + I18n.format("emoptions.insaneParticles."+ (EM_Settings.insaneParticals == true ? "on" : "off" ), new Object[0]);
			this.buttonList.add(insaneParticlesbutton);
		minbarsbutton.displayString = I18n.format("emoptions.minBars", new Object[0]) + ": " + I18n.format("emoptions.minBars."+ (EM_Settings.minimalHud == true ? "on" : "off" ), new Object[0]);
			this.buttonList.add(minbarsbutton);
	
			this.buttonList.add(new GuiButton(157, this.width / 2 + 2, this.height / 6 + 30 - 6, 150, 20, StatCollector.translateToLocal("emoptions.barPos")));
			
			this.buttonList.add(new GuiButton(200, this.width / 2 -95, this.height / 6 + 168, I18n.format( "gui.back", new Object[0])));

		/*		
		    # Change position of Enviro Bars. Options: Bottom_Left, Bottom_Right, Bottom_Center_Left, Bottom_Center_Right, Top_Left, Top_Right, Top_Center, Middle_Left, Middle_Right, Custom_#,# (Custom_X(0-100),Y(0-100))
		    S:"Position Air Quality Bar"=Bottom_Right
		    S:"Position Heat Bat"=Bottom_Left
		    S:"Position Sanity Bar"=Bottom_Right
		    S:"Position Thirst Bar"=Bottom_Left
*/
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
				case 150:
					EM_Settings.ShowDebug = !EM_Settings.ShowDebug; 
						par1GuiButton.displayString =I18n.format("emoptions.debug", new Object[0]) + ": " + I18n.format("emoptions.debug."+ (EM_Settings.ShowDebug == true ? "visible" : "hidden" ), new Object[0]);
					break;
				case 151:
					EM_Settings.useFarenheit = !EM_Settings.useFarenheit;
						par1GuiButton.displayString = I18n.format("emoptions.tempScale", new Object[0]) + ": " + I18n.format("emoptions.tempScale."+ (EM_Settings.useFarenheit == true ? "f" : "c" ), new Object[0]);
					break;
				case 152:
					EM_Settings.insaneParticals = !EM_Settings.insaneParticals;
						par1GuiButton.displayString = I18n.format("emoptions.insaneParticles", new Object[0]) + ": " + I18n.format("emoptions.insaneParticles."+ (EM_Settings.insaneParticals == true ? "on" : "off" ), new Object[0]);
					break;
				case 154:
					EM_Settings.ShowGuiIcons = !EM_Settings.ShowGuiIcons;
						par1GuiButton.displayString =I18n.format("emoptions.showIcons", new Object[0]) + ": " + I18n.format("emoptions.showIcons."+ (EM_Settings.ShowGuiIcons == true ? "visible" : "hidden" ), new Object[0]);
					break;
				case 156:
					EM_Settings.ShowText = !EM_Settings.ShowText;
						par1GuiButton.displayString =I18n.format("emoptions.status", new Object[0]) + ": " + I18n.format("emoptions.status."+ (EM_Settings.ShowText == true ? "visible" : "hidden" ), new Object[0]);
					break;
				case 157:
					break;//barpos
				case 158:
					EM_Settings.minimalHud = !EM_Settings.minimalHud;
						par1GuiButton.displayString = I18n.format("emoptions.minBars", new Object[0]) + ": " + I18n.format("emoptions.minBars."+ (EM_Settings.minimalHud == true ? "on" : "off" ), new Object[0]);
		               
					break;
				case 159:
					EM_Settings.sweatParticals = !EM_Settings.sweatParticals;
						par1GuiButton.displayString = I18n.format("emoptions.sweatParticles", new Object[0]) + ": " + I18n.format("emoptions.sweatParticles."+ (EM_Settings.sweatParticals == true ? "on" : "off" ), new Object[0]);
					break;
					
					
			}
			this.mc.displayGuiScreen(this);
		}
		
		
		
		
		
		
		
	/*	
		
		if (par1GuiButton.enabled)
		{
			
			if (par1GuiButton.id == 200)
			{
				this.mc.displayGuiScreen(this.parentGuiScreen);
			}
			else if(par1GuiButton.id == 151)
			{
				EM_Settings.useFarenheit = !EM_Settings.useFarenheit;
				
				if (EM_Settings.useFarenheit == true)
				{
					par1GuiButton.displayString = StatCollector.translateToLocal("emoptions.tempScale")+": "+StatCollector.translateToLocal("empotions.tempScale.f");	
				}
				else
				{
					par1GuiButton.displayString =  StatCollector.translateToLocal("emoptions.tempScale")+": "+StatCollector.translateToLocal("empotions.tempScale.c");	
				}
				
			}
			else if (par1GuiButton.id == 153)
			{
				this.mc.displayGuiScreen(this.parentGuiScreen);
			}
		}*/
	
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("emoptions.guiSetting.title"), this.width / 2, 15, 16777215);
		super.drawScreen(par1, par2, par3);
	}
	
}
