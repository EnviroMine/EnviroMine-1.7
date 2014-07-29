package main.java.enviromine.core.proxies;

import main.java.enviromine.gui.UpdateNotification;
import main.java.enviromine.handlers.CamelPackRefillHandler;
import main.java.enviromine.handlers.EM_EventManager;
import main.java.enviromine.handlers.EM_ServerScheduledTickHandler;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

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
		FMLCommonHandler.instance().bus().register(new EM_ServerScheduledTickHandler());
	}
	
	public void registerEventHandlers()
	{
		MinecraftForge.EVENT_BUS.register(new EM_EventManager());
		MinecraftForge.EVENT_BUS.register(new UpdateNotification());
		
		CamelPackRefillHandler tmp = new CamelPackRefillHandler();
		CraftingManager.getInstance().getRecipeList().add(tmp);
		FMLCommonHandler.instance().bus().register(tmp);
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
