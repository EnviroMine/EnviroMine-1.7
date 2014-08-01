package enviromine.core.proxies;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import enviromine.core.EnviroMine;
import enviromine.gui.EM_GuiEnviroMeters;
import enviromine.handlers.ObjectHandler;

public class EM_ClientProxy extends EM_CommonProxy
{
	public boolean isClient()
	{
		return true;
	}
	
	public boolean isOpenToLAN()
	{
		if(Minecraft.getMinecraft().isIntegratedServerRunning())
		{
			if(Minecraft.getMinecraft().getIntegratedServer().getPublic())
			{
				return true;
			} else
			{
				return false;
			}
		} else
		{
			return false;
		}
	}
	
	public void registerTickHandlers()
	{
		super.registerTickHandlers();
	}
	
	public void registerEventHandlers()
	{
		super.registerEventHandlers();
		MinecraftForge.EVENT_BUS.register(new EM_GuiEnviroMeters(Minecraft.getMinecraft()));
		MinecraftForge.EVENT_BUS.register(new ObjectHandler());
	}
	
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
	}
	
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		EnviroMine.registerKeyBindings(event);
	}
	
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
	}
}
