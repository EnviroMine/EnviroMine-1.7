package enviromine.client.gui.menu.config;

import java.io.File;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.Configuration;

public class EM_ConfigMenuEdit extends GuiScreen
{
	File conFile;
	Configuration config;
	
	public EM_ConfigMenuEdit(File file)
	{
		conFile = file;
		config = new Configuration(conFile, true);
	}
	
	@Override
	public void initGui()
	{
		/* TODO: List all the config category buttons:
		 * Block
		 * Item
		 * Armor
		 * Rot
		 * Biome
		 * Dimension
		 */
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
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}
}
