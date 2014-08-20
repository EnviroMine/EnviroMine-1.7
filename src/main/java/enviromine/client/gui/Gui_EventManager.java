package enviromine.client.gui;

import java.util.Iterator;

import org.apache.logging.log4j.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.client.gui.menu.EM_Gui_Menu;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;

@SideOnly(Side.CLIENT)
public class Gui_EventManager 
{

	int	width, height;
	
	
	//Render HUD
	
	
	//Render Player
	
	
	// Button Functions
	
	@SubscribeEvent
	public void renderevent(InitGuiEvent.Post event)
	{
		width = event.gui.width;
		height = event.gui.height;

		if(event.gui instanceof GuiIngameMenu)
		{
			try
			{
		        byte b0 = -16;
	   	        event.buttonList.set(1,new GuiButton(4, width / 2 - 100, height / 4 + 0 + b0, I18n.format("menu.returnToGame", new Object[0])));
		        event.buttonList.add(new GuiButton(1348, width / 2 - 100, height / 4 + 24 + b0, StatCollector.translateToLocal("options.enviromine.menu.title")));
	
			}catch(Exception e)
			{
				EnviroMine.logger.log(Level.ERROR, "Error shifting Minecrafts Menu to add in new button: "+ e);
				event.buttonList.add(new GuiButton(1348, width - 175, height  - 30, 160, 20, StatCollector.translateToLocal("options.enviromine.menu.title")));
			}
		}
	}
	@SubscribeEvent
	public void action(ActionPerformedEvent.Post event)
	{
		if(event.gui instanceof GuiIngameMenu)
		{
			if(event.button.id == 1348)
			{
				Minecraft.getMinecraft().displayGuiScreen(new EM_Gui_Menu(event.gui));
			}
	
		}	
	}	
	
}

