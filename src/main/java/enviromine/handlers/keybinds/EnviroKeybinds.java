package enviromine.handlers.keybinds;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.StatCollector;
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
		reloadConfig = new KeyBinding(StatCollector.translateToLocal("keybinds.enviromine.reload"), Keyboard.KEY_K, "EnviroMine");
		addRemove = new KeyBinding(StatCollector.translateToLocal("keybinds.enviromine.addremove"), Keyboard.KEY_J, "EnviroMine");
		
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