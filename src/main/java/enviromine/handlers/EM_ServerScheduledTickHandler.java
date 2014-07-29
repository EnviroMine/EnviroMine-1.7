package main.java.enviromine.handlers;

import main.java.enviromine.core.EM_Settings;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class EM_ServerScheduledTickHandler
{
	@SubscribeEvent
	public void tickEnd(TickEvent tick)
	{
		if(tick.side.isClient() && EM_Settings.enablePhysics)
		{
			EM_PhysManager.updateSchedule();
		}
	}
}
