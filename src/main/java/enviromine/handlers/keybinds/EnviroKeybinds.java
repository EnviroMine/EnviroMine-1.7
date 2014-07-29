package main.java.enviromine.handlers.keybinds;

import org.lwjgl.input.Keyboard;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.settings.KeyBinding;

public class EnviroKeybinds
{
	public static KeyBinding reloadConfig;
	public static KeyBinding addRemove;
	
	public static void Init()
	{
		reloadConfig = new KeyBinding("key.enviromine.reload", Keyboard.KEY_L, "key.categories.enviromine");
		addRemove = new KeyBinding("key.enviromine.addremove", Keyboard.KEY_L, "key.categories.enviromine");
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
	}
}
