package enviromine.handlers.keybinds;

import net.minecraft.client.settings.KeyBinding;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

import org.lwjgl.input.Keyboard;

public class EnviroKeybinds
{
	public static KeyBinding reloadConfig;
	public static KeyBinding addRemove;
	
	public static void Init()
	{
		reloadConfig = new KeyBinding("key.enviromine.reload", Keyboard.KEY_L, "key.categories.enviromine");
		addRemove = new KeyBinding("key.enviromine.addremove", Keyboard.KEY_L, "key.categories.enviromine");
		
		ClientRegistry.registerKeyBinding(reloadConfig);
		ClientRegistry.registerKeyBinding(addRemove);
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