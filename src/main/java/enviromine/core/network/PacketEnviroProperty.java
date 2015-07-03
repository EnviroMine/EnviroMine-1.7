package enviromine.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import enviromine.core.api.properties.PropertyRegistry;
import enviromine.core.api.properties.PropertyTracker;
import enviromine.core.api.properties.PropertyType;

public class PacketEnviroProperty implements IMessage
{
	NBTTagCompound tags = new NBTTagCompound();
	
	public PacketEnviroProperty()
	{
	}
	
	public PacketEnviroProperty(NBTTagCompound tags)
	{
		this.tags = tags;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		tags = ByteBufUtils.readTag(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeTag(buf, tags);
	}
	
	/**
	 * Handler used to sync client side properties
	 */
	public static class HandleClientPacket implements IMessageHandler<PacketEnviroProperty, IMessage>
	{
		@Override
		public IMessage onMessage(PacketEnviroProperty message, MessageContext ctx)
		{
			Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(message.tags.getInteger("EntityID"));
			NBTTagCompound trackerData = message.tags.getCompoundTag("Properties");
			
			if(entity != null && entity instanceof EntityLivingBase)
			{
				for(PropertyType propType : PropertyRegistry.getAllTypes())
				{
					PropertyTracker tracker = propType.getTracker((EntityLivingBase)entity);
					
					if(tracker != null && trackerData.hasKey("ENVIROMINE_" + propType.getTrackerID()))
					{
						tracker.loadNBTData(trackerData.getCompoundTag("ENVIROMINE_" + propType.getTrackerID()));
					}
				}
			}
			
			return null;
		}
	}
}
