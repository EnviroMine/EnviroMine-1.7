package enviromine.handlers.keybinds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

import org.lwjgl.input.Keyboard;

import enviromine.gui.menu.EM_Gui_Menu;

public class EnviroKeybinds
{
	public static KeyBinding reloadConfig;
	public static KeyBinding addRemove;
	public static KeyBinding menu;
	
	public static void Init()
	{
		reloadConfig = new KeyBinding("key.enviromine.reload", Keyboard.KEY_L, "key.categories.enviromine");
		addRemove = new KeyBinding("key.enviromine.addremove", Keyboard.KEY_K, "key.categories.enviromine");
		menu = new KeyBinding("key.enviromine.menu", Keyboard.KEY_O, "key.categories.enviromine");
		
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