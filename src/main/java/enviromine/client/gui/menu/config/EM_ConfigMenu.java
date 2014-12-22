package enviromine.client.gui.menu.config;

import enviromine.core.EM_ConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StatCollector;

public class EM_ConfigMenu extends GuiScreen
{
	private GuiScreen parentGuiScreen;
	
	public EM_ConfigMenu(GuiScreen parent, int page)
	{
		parentGuiScreen = parent;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		GuiButton mainBut = new GuiButton(101, this.width/2 - 100, this.height/8, 200, 20, StatCollector.translateToLocal("options.enviromine.mainconfig.button"));
		this.buttonList.add(mainBut);
		
		GuiButton blockBut = new GuiButton(101, this.width/2 - 175, this.height/8 * 2, 150, 20, StatCollector.translateToLocal("options.enviromine.blocks.button"));
		this.buttonList.add(blockBut);
		GuiButton itemBut = new GuiButton(102, this.width/2 + 25, this.height/8 * 2, 150, 20, StatCollector.translateToLocal("options.enviromine.items.button"));
		this.buttonList.add(itemBut);
		GuiButton stabilityBut = new GuiButton(103, this.width/2 - 175, this.height/8 * 3, 150, 20, StatCollector.translateToLocal("options.enviromine.stability.button"));
		this.buttonList.add(stabilityBut);
		GuiButton armorBut = new GuiButton(104, this.width/2 + 25, this.height/8 * 3, 150, 20, StatCollector.translateToLocal("options.enviromine.armor.button"));
		this.buttonList.add(armorBut);
		GuiButton entityBut = new GuiButton(105, this.width/2 - 175, this.height/8 * 4, 150, 20, StatCollector.translateToLocal("options.enviromine.entity.button"));
		this.buttonList.add(entityBut);
		GuiButton rotBut = new GuiButton(106, this.width/2 + 25, this.height/8 * 4, 150, 20, StatCollector.translateToLocal("options.enviromine.rot.button"));
		this.buttonList.add(rotBut);
		GuiButton dimBut = new GuiButton(105, this.width/2 - 175, this.height/8 * 5, 150, 20, StatCollector.translateToLocal("options.enviromine.dimension.button"));
		this.buttonList.add(dimBut);
		GuiButton biomeBut = new GuiButton(106, this.width/2 + 25, this.height/8 * 5, 150, 20, StatCollector.translateToLocal("options.enviromine.rot.button"));
		this.buttonList.add(biomeBut);
		
		this.buttonList.add(new GuiButton(200, this.width/2 - 100, this.height/8 * 7, I18n.format("gui.back", new Object[0])));
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("options.enviromine.configeditor.title"), this.width / 2, 15, 16777215);
		super.drawScreen(par1, par2, par3);
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
			}
		}
	}
	
	@Override
	public void onGuiClosed() 
	{
	    EM_ConfigHandler.initConfig();
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}
}
