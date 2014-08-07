package enviromine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EM_Gui_GuiSettings extends GuiScreen
{
	private GuiScreen parentGuiScreen;
	
	public EM_Gui_GuiSettings(GuiScreen par1GuiScreen)
	{
		this.parentGuiScreen = par1GuiScreen;
	}
	
	@Override
	public void initGui()
	{
		this.buttonList.add(new GuiButton(150, this.width / 4 - 152, this.height / 6 + 96 - 6, 150, 20, "Enviromine Bars"));
		this.buttonList.add(new GuiButton(152, this.width / 4 + 2, this.height / 6 + 96 - 6, 150, 20, "Mask Overlay"));
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
		
		if (par1GuiButton.id == 200)
		{
			this.mc.displayGuiScreen(this.parentGuiScreen);
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, "Enviromine Gui Settings", this.width / 2, 15, 16777215);
		super.drawScreen(par1, par2, par3);
	}
}
