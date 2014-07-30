package enviromine.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

import enviromine.core.EM_Settings;

public class EM_ServerScheduledTickHandler
{
	@SubscribeEvent
	public void tickEnd(TickEvent.WorldTickEvent tick)
	{
		if(tick.side.isServer() && EM_Settings.enablePhysics) {
			EM_PhysManager.updateSchedule();
		}
	}
}
