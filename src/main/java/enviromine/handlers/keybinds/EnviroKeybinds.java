package enviromine.handlers.keybinds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

import org.lwjgl.input.Keyboard;

import enviromine.client.gui.menu.EM_Gui_Menu;

public class EnviroKeybinds
{
	public static KeyBinding reloadConfig;
	public static KeyBinding addRemove;
	public static KeyBinding menu;
	
	public static void Init()
	{
		reloadConfig = new KeyBinding(StatCollector.translateToLocal("keybinds.enviromine.reload"), Keyboard.KEY_K, "EnviroMine");
		addRemove = new KeyBinding(StatCollector.translateToLocal("keybinds.enviromine.addremove"), Keyboard.KEY_J, "EnviroMine");
		menu = new KeyBinding(StatCollector.translateToLocal("options.enviromine.menu.title"), Keyboard.KEY_M, "EnviroMine");
		
		ClientRegistry.registerKeyBinding(reloadConfig);
		ClientRegistry.registerKeyBinding(addRemove);
		ClientRegistry.registerKeyBinding(menu);
	}
	
	@SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
	{
		if(reloadConfig.getIsKeyPressed())
		{
			ReloadCustomObjects.doReloadConfig();
		}
		
		if(addRemove.getIsKeyPressed())
		{
			AddRemoveCustom.doAddRemove();
		}
		
		if(menu.getIsKeyPressed())
		{
			Minecraft.getMinecraft().displayGuiScreen(new EM_Gui_Menu());
		}
		
	}
}