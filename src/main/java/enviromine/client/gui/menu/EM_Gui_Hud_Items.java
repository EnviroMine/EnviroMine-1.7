package enviromine.client.gui.menu;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StatCollector;
import enviromine.client.gui.SaveController;
import enviromine.client.gui.hud.HUDRegistry;
import enviromine.client.gui.hud.HudItem;

@SideOnly(Side.CLIENT)
public class EM_Gui_Hud_Items extends GuiScreen implements GuiYesNoCallback
{
	private GuiScreen parentGuiScreen;

	//"top_left""top_right""top_center""bottom_left""bottom_right"bottom_center_right""bottom_center_left""middle_right""middle_left"
	
	
	public EM_Gui_Hud_Items(GuiScreen par1GuiScreen)
	{
		this.parentGuiScreen = par1GuiScreen;
	}
	//id, x, y, width, height, text

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		int buttonCnt = 0;
		for(HudItem hudItem : HUDRegistry.getHudItemList())
		{

			
 			GuiButton Button = new GuiButton(hudItem.getDefaultID(), this.width / 2 + 5 - 20, this.height / 6 + 24 +(buttonCnt * 24) - 6, 120, 20, hudItem.getButtonLabel()  );
 			
 			GuiButton rotateButton = new GuiButton(hudItem.getDefaultID()+200, this.width / 2 + 5 - 20 + 130, this.height / 6 + 24 +(buttonCnt * 24) - 6, 20, 20, (hudItem.rotated ? "V" : "H" ));
 			
			
 			if(!hudItem.isEnabledByDefault()) Button.enabled = false;
 				
			this.buttonList.add(Button);
			this.buttonList.add(rotateButton);
			
			buttonCnt++;
		}
		
		
		buttonCnt++;
		this.buttonList.add(new GuiButton(801, this.width / 2 - 75, this.height / 6 + 24 +(buttonCnt * 24) - 6, 150, 20,  StatCollector.translateToLocal("options.enviromine.hud.resetDefault")));
		
		buttonCnt++;
		this.buttonList.add(new GuiButton(800, this.width / 2 - 75, this.height / 6 + 24 +(buttonCnt * 24) - 6, 150, 20, I18n.format( "gui.back", new Object[0])));
		
		
		
		
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
	public void confirmClicked(boolean p_73878_1_, int p_73878_2_) 
	{
		if(p_73878_1_) // if true
		{
			if(p_73878_2_ == 1) // if reset default id
			{
				HUDRegistry.resetAllDefaults();		
			}
		}
		
		
		this.mc.displayGuiScreen(this);
	}
	 
	@Override
	public void actionPerformed(GuiButton par1GuiButton)
	{
		if(par1GuiButton.enabled)
		{
			//int val = 0;
			//int where;
			switch(par1GuiButton.id)
			{
				case 800:
					this.mc.displayGuiScreen(parentGuiScreen);
					return;
				case 801:
					this.mc.displayGuiScreen(new GuiYesNo(this, StatCollector.translateToLocal("options.enviromine.hud.resetDefault"), StatCollector.translateToLocal("options.enviromine.hud.resetDefault.desc"), 1));
					return;					
				default:
					for(HudItem hudItem : HUDRegistry.getHudItemList())
					{
						if(hudItem.getDefaultID() == par1GuiButton.id)
						{
							this.mc.displayGuiScreen(new GuiScreenReposition(this, hudItem));
							break;
						}
						if(hudItem.getDefaultID() == par1GuiButton.id - 200 )
						{
							hudItem.rotated = !hudItem.rotated;
							par1GuiButton.displayString = (hudItem.rotated ? "V" : "H" );
							break;
						}
						
					}
					break;
					/*
				case 101: 
					this.mc.displayGuiScreen(new EM_Gui_Bars(this));
					
					 hudItem = HUDRegistry.getHudItemByID(1);
					
					this.mc.displayGuiScreen(new GuiScreenReposition(this, hudItem));
					break;
				case 102: 
					this.mc.displayGuiScreen(new EM_Gui_Bars(this));
					
					 hudItem = HUDRegistry.getHudItemByID(0);
					
					this.mc.displayGuiScreen(new GuiScreenReposition(this, hudItem));
					break;
				case 103: //sanity
					this.mc.displayGuiScreen(new EM_Gui_Bars(this));
					
					 hudItem = HUDRegistry.getHudItemByID(2);
					
					this.mc.displayGuiScreen(new GuiScreenReposition(this, hudItem));
					break;
				case 104: // oxygen
					this.mc.displayGuiScreen(new EM_Gui_Bars(this));
					
					 hudItem = HUDRegistry.getHudItemByID(3);
					
					this.mc.displayGuiScreen(new GuiScreenReposition(this, hudItem));
					break;
					*/
			}
		}
	}
	
	@Override
	public void onGuiClosed() 
	{
	    SaveController.saveConfig(SaveController.UISettingsData);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("options.enviromine.guiBarPos.title"), this.width / 2, 15, 16777215);
		
		
		int hudCnt = 0;
		for(HudItem hudItem : HUDRegistry.getHudItemList())
		{
			this.drawString(this.fontRendererObj,  hudItem.getButtonLabel()+": ", this.width / 2 - 75 - 22, this.height / 6 + 24 + (hudCnt * 24), 16777215);
			
			hudCnt++;
		}
		
		
		super.drawScreen(par1, par2, par3);
	}
}
