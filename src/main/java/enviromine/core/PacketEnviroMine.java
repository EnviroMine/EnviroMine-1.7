package enviromine.core;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import enviromine.handlers.EM_EventManager;
import enviromine.handlers.EM_StatusManager;
import enviromine.trackers.EnviroDataTracker;

import java.util.UUID;

import io.netty.buffer.ByteBuf;

public class PacketEnviroMine implements IMessage
{
	private String message;
	
	public PacketEnviroMine(String message) {
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.message = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, this.message);
	}
	
	public static class Handler implements IMessageHandler<PacketEnviroMine, IMessage>
	{
		@Override
		public IMessage onMessage(PacketEnviroMine packet, MessageContext ctx)
		{			
			String[] data = packet.message.split(",");
			
			if(data[0].equalsIgnoreCase("ID:0")) {
				this.trackerSync(data);
			} else if(data[0].equalsIgnoreCase("ID:1")) {
				this.emptyRightClick(data);
			}
			return null; //Reply
		}
		
		private void trackerSync(String[] data)
		{
			
			EnviroDataTracker tracker = EM_StatusManager.lookupTrackerFromUUID(UUID.fromString(data[1]));
			
			if(tracker != null)
			{
				tracker.airQuality = Float.valueOf(data[2]);
				tracker.bodyTemp = Float.valueOf(data[3]);
				tracker.hydration = Float.valueOf(data[4]);
				tracker.sanity = Float.valueOf(data[5]);
				tracker.airTemp = Float.valueOf(data[6]);
			}
		}
		
		private void emptyRightClick(String[] data)
		{
			EntityPlayer player = EM_StatusManager.findPlayer(UUID.fromString(data[1]));
			
			if(player != null)
			{
				EM_EventManager.drinkWater(player, null);
			}
		}
	}
}