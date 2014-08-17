package enviromine.gui.menu;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

public class EM_Gui_Bars extends GuiScreen
{
	private GuiScreen parentGuiScreen;

	public EM_Gui_Bars(GuiScreen par1GuiScreen)
	{
		this.parentGuiScreen = par1GuiScreen;
	}
	//id, x, y, width, height, text

	@Override
	public void initGui()
	{
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
						
			}
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("emoptions.guiBars.title"), this.width / 2, 15, 16777215);
		super.drawScreen(par1, par2, par3);
	}
}
