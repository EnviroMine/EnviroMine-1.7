/**
 * @author thislooksfun
 */
package enviromine.network.packet;

import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.handlers.EM_EventManager;
import enviromine.handlers.EM_StatusManager;
import enviromine.trackers.EnviroDataTracker;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import org.apache.logging.log4j.Level;

public class PacketEnviroMine implements IMessage
{
	private String message;
	
	public PacketEnviroMine() {}
	public PacketEnviroMine(String message) {
		this.message = message;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.message = ByteBufUtils.readUTF8String(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, this.message);
	}
	
	public static class HandlerServer implements IMessageHandler<PacketEnviroMine,IMessage>
	{
		@Override
		public IMessage onMessage(PacketEnviroMine packet, MessageContext ctx)
		{
			String[] data = packet.message.split(",");
			if(data[0].trim().equalsIgnoreCase("ID:1"))
			{
				this.emptyRightClick(data);
			} else
			{
				EnviroMine.logger.log(Level.ERROR, "Received invalid packet on serverside!");
			}
			return null; //Reply
		}
		
		private void emptyRightClick(String[] data)
		{
			EntityPlayer player = EM_StatusManager.findPlayer(data[1].trim());
			
			if(player != null)
			{
				EM_EventManager.drinkWater(player, null);
			}
		}
	}
	
	public static class HandlerClient implements IMessageHandler<PacketEnviroMine,IMessage>
	{
		@Override
		public IMessage onMessage(PacketEnviroMine packet, MessageContext ctx)
		{
			String[] data = packet.message.split(",");
			if(data[0].trim().equalsIgnoreCase("ID:0"))
			{
				this.trackerSync(data);
			} else
			{
				EnviroMine.logger.log(Level.ERROR, "Received invalid packet on clientside!");
			}
			return null; //Reply
		}
		
		private void trackerSync(String[] data)
		{
			
			EnviroDataTracker tracker = EM_StatusManager.trackerList.get(data[1].trim());
			
			if(tracker != null)
			{
				tracker.prevAirQuality = tracker.airQuality;
				tracker.prevBodyTemp = tracker.bodyTemp;
				tracker.prevHydration = tracker.hydration;
				tracker.prevSanity = tracker.sanity;
				tracker.airQuality = Float.valueOf(data[2]);
				tracker.bodyTemp = Float.valueOf(data[3]);
				tracker.hydration = Float.valueOf(data[4]);
				tracker.sanity = Float.valueOf(data[5]);
				tracker.airTemp = Float.valueOf(data[6]);
			} else
			{
				EnviroMine.logger.log(Level.ERROR, "Failed to sync tracker for player " + data[1].trim());
				
				if(!(EM_Settings.enableAirQ || EM_Settings.enableBodyTemp || EM_Settings.enableHydrate || EM_Settings.enableSanity))
				{
					EnviroMine.logger.log(Level.ERROR, "Please change your settings to enable one or more status types");
				} else
				{
					EntityPlayer player = EM_StatusManager.findPlayer(data[1].trim());
					
					if(EnviroMine.proxy.isClient() && player != null)
					{
						EnviroMine.logger.log(Level.ERROR, "Attempting to create tracker for player...");
						EnviroDataTracker emTrack = new EnviroDataTracker(player);
						EM_StatusManager.addToManager(emTrack);
						
						emTrack.airQuality = Float.valueOf(data[2]);
						emTrack.bodyTemp = Float.valueOf(data[3]);
						emTrack.hydration = Float.valueOf(data[4]);
						emTrack.sanity = Float.valueOf(data[5]);
						emTrack.airTemp = Float.valueOf(data[6]);
						emTrack.prevAirQuality = emTrack.airQuality;
						emTrack.prevBodyTemp = emTrack.bodyTemp;
						emTrack.prevHydration = emTrack.hydration;
						emTrack.prevSanity = emTrack.sanity;
					}
				}
			}
		}
	}
}