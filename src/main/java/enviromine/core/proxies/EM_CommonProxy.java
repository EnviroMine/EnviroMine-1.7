package enviromine.core.proxies;

import enviromine.core.api.hud.HUDRegistry;
import enviromine.core.api.properties.PropertyManager;
import net.minecraftforge.common.MinecraftForge;

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
	
	public void registerHandlers()
	{
		MinecraftForge.EVENT_BUS.register(new PropertyManager());
		MinecraftForge.EVENT_BUS.register(new HUDRegistry());
	}
}
