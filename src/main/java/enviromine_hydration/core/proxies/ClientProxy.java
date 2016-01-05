package enviromine_hydration.core.proxies;

import enviromine.core.api.hud.HUDRegistry;
import enviromine_hydration.client.gui.hud.HudHydration;

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
		super.registerHandlers();
		HUDRegistry.registerHudItem(new HudHydration("hydration"));
	}
}
