package enviromine.client.gui.menu.config;

import java.io.File;
import java.util.ArrayList;
import enviromine.client.gui.SaveController;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class EM_ConfigMenu extends GuiScreen
{
	public int pageNum = 0;
	
	public EM_ConfigMenu(int page)
	{
		pageNum = page;
	}
	
	@Override
	public void initGui()
	{
		ArrayList<File> configFiles = ConfigMenuUtils.getCustomConfigs();
		
		//TODO: List all the config files as buttons for the given page
		// Also list the stability types file for editing
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
	}
	
	@Override
	public void actionPerformed(GuiButton par1GuiButton)
	{
	}
	
	@Override
	public void onGuiClosed() 
	{
	    //TODO: Reload all configs
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}
}
