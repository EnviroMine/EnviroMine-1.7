package enviromine.core.proxies;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import enviromine.handlers.CamelPackRefillHandler;
import enviromine.handlers.EM_EventManager;
import enviromine.handlers.EM_ServerScheduledTickHandler;

public class EM_CommonProxy
{
	public boolean isClient()
	{
		return false;
	}
	
	public boolean isOpenToLAN()
	{
		return false;
	}
	
	public void registerTickHandlers()
	{
		TickRegistry.registerTickHandler(new EM_ServerScheduledTickHandler(), Side.SERVER);
	}
	
	public void registerEventHandlers()
	{
		MinecraftForge.EVENT_BUS.register(new EM_EventManager());
		
		CamelPackRefillHandler tmp = new CamelPackRefillHandler();
		CraftingManager.getInstance().getRecipeList().add(tmp);
		GameRegistry.registerCraftingHandler(tmp);
	}
	
	public void preInit(FMLPreInitializationEvent event)
	{
		
	}
	
	public void init(FMLInitializationEvent event)
	{
		
	}
	
	public void postInit(FMLPostInitializationEvent event)
	{
		
	}
}
