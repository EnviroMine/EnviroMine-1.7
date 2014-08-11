package enviromine.network.packet;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import enviromine.core.EM_Settings;

import io.netty.buffer.ByteBuf;

public class PacketServerOverride implements IMessage
{
	public PacketServerOverride() {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			readFromSettings();
		}
	}
	private void readFromSettings() {
		EM_Settings.
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
	}
	
	public static class Handler implements IMessageHandler<PacketServerOverride, IMessage>
	{
		@Override
		public IMessage onMessage(PacketServerOverride message, MessageContext ctx)
		{
			return null; //Reply
		}
	}
}