package enviromine.handlers;

import net.minecraft.util.MathHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import enviromine.core.EM_Settings;
import enviromine.gases.GasBuffer;
import enviromine.world.Earthquake;

public class EM_ServerScheduledTickHandler
{
	@SubscribeEvent
	public void tickEnd(TickEvent.WorldTickEvent tick)
	{
		if(tick.side.isServer())
		{
			GasBuffer.update();
			
			if(EM_Settings.enablePhysics)
			{
				EM_PhysManager.updateSchedule();
			}
			
			TorchReplaceHandler.UpdatePass();
			
			Earthquake.updateEarthquakes();
			
			if(EM_Settings.enableQuakes && tick.world.getTotalWorldTime()%24000 < 100 && MathHelper.floor_double(tick.world.getTotalWorldTime()/24000L) != Earthquake.lastTickDay && !tick.world.provider.isHellWorld)
			{
				Earthquake.lastTickDay = MathHelper.floor_double(tick.world.getTotalWorldTime()/24000L);
				Earthquake.TickDay(tick.world);
			}
		}
	}
}
