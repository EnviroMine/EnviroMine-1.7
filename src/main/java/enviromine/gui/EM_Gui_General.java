package enviromine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import enviromine.core.EM_Settings;

@SideOnly(Side.CLIENT)
public class EM_Gui_General extends GuiScreen
{
	private GuiScreen parentGuiScreen;
	
	public EM_Gui_General(GuiScreen par1GuiScreen)
	{
		this.parentGuiScreen = par1GuiScreen;
	}
	
	@Override
	public void initGui()
	{
		
		this.buttonList.add(new GuiButton(150, this.width / 2 - 160, this.height / 6 + 96 - 6, 160, 20, "Physics: " +EM_Settings.enablePhysics));
		
		
		
		if (EM_Settings.useFarenheit == true)
		{
			this.buttonList.add(new GuiButton(152, this.width / 2 + 2, this.height / 6 + 96 - 6, 160, 20, "Temperature Scale: Fahrenheit"));	
		}
		else
		{
			this.buttonList.add(new GuiButton(152, this.width / 2 + 2, this.height / 6 + 96 - 6, 160, 20, "Temperature Scale: Celsius"));	
		}
		
		
		this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, "gui.back"));
		
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
		if (par1GuiButton.enabled)
		{
			if(par1GuiButton.id == 150)
			{
				System.out.println("Test");
				EM_Settings.enablePhysics = !EM_Settings.enablePhysics;
				
				par1GuiButton.displayString = "Physics: " + EM_Settings.enablePhysics;
			}
			else if(par1GuiButton.id == 152)
			{
				EM_Settings.useFarenheit = !EM_Settings.useFarenheit;
				
				if (EM_Settings.useFarenheit == true)
				{
					par1GuiButton.displayString = "Temperature Scale: Fahrenheit";	
				}
				else
				{
					par1GuiButton.displayString =  "Temperature Scale: Celsius";	
				}
				
			}
			else if (par1GuiButton.id == 200)
			{
				this.mc.displayGuiScreen(this.parentGuiScreen);
			}
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, "Enviromine Config Settings", this.width / 2, 15, 16777215);
		super.drawScreen(par1, par2, par3);
	}
}
