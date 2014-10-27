/**
 * @author thislooksfun
 */
package enviromine.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import enviromine.core.EnviroMine;
import enviromine.handlers.EM_EventManager;
import enviromine.handlers.EM_StatusManager;
import enviromine.trackers.EnviroDataTracker;
import enviromine.world.ClientQuake;

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
			if(data[0].equalsIgnoreCase("ID:1"))
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
			EntityPlayer player = EM_StatusManager.findPlayer(data[1]);
			
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
			if(data[0].equalsIgnoreCase("ID:0"))
			{
				this.trackerSync(data);
			} else if(data[0].equalsIgnoreCase("ID:3"))
			{
				this.registerQuake(data);
			} else
			{
				EnviroMine.logger.log(Level.ERROR, "Received invalid packet on clientside!");
			}
			return null; //Reply
		}
		
		private void trackerSync(String[] data)
		{
			
			EnviroDataTracker tracker = EM_StatusManager.lookupTrackerFromUsername(data[1]);
			
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
			}
		}
		
		private void registerQuake(String[] data)
		{
			int b = Integer.valueOf(data[1]);
			int d = Integer.valueOf(data[2]);
			int x = Integer.valueOf(data[3]);
			int z = Integer.valueOf(data[4]);
			int l = Integer.valueOf(data[5]);
			int w = Integer.valueOf(data[6]);
			float a = Float.valueOf(data[7]);
			int h = Integer.valueOf(data[8]);
			
			if(b == 0)
			{
				new ClientQuake(d, x, z, l, w, a);
			} else if(b == 1)
			{
				ClientQuake.UpdateQuakeHeight(d, x, z, l, w, a, h);
			} else if(b == 2)
			{
				ClientQuake.RemoveQuake(x, z);
			}
		}
	}
}