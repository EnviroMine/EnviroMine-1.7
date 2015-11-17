package enviromine_temp.core.proxies;

import enviromine.core.api.hud.HUDRegistry;
import enviromine_temp.client.gui.hud.HudTemperature;

public class ClientProxy extends CommonProxy
{
	@Override
	public boolean isClient()
	{
		return true;
	}
	
	@Override
	public void registerHandlers()
	{
		HUDRegistry.registerHudItem(new HudTemperature("temperature"));
		super.registerHandlers();
	}
}
