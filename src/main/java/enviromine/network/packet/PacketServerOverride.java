package enviromine.network.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.network.packet.encoders.IPacketEncoder;

import io.netty.buffer.ByteBuf;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.Level;

public class PacketServerOverride implements IMessage
{
	private EntityPlayerMP player;
	protected Map<String, String[]> data = new HashMap<String, String[]>();
	
	public PacketServerOverride()
	{
	}
	
	public PacketServerOverride(EntityPlayerMP player)
	{
		this.player = player;
	}
	
	public PacketServerOverride(EntityPlayerMP player, Map<String, String[]> custom)
	{
		this.player = player;
		this.data = custom == null ? this.data : custom;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		String str = ByteBufUtils.readUTF8String(buf);
		String[] strs = str.split(";;");
		
		for (String type : strs)
		{
			String[] tmp = type.split("::");
			if (tmp.length >= 3)
			{
				int length = 0;
				try
				{
					length = Integer.parseInt(tmp[1]);
				} catch (NumberFormatException e)
				{
					e.printStackTrace();
				}
				String[] temp = new String[length];
				for (int i = 0; i < length; i++)
				{
					temp[i] = tmp[i + 2];
				}
				this.data.put(tmp[0], temp);
			}
		}
	}
	
	private String getDataAsString()
	{
		Iterator<String> iterator = data.keySet().iterator();
		String info = "";
		while (iterator.hasNext())
		{
			String key = iterator.next();
			if (!info.equals(""))
			{
				info += ";;";
			}
			
			String[] tmpData = data.get(key);
			
			String tmp = (key + "::" + tmpData.length);
			for (int i = 0; i < tmpData.length; i++)
			{
				tmp += "::" + tmpData[i];
			}
			
			info += tmp;
		}
		
		return info;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		String info = getDataAsString();
		
		if (info.length() > 2000)
		{
			if (data.size() <= 1)
			{
				EnviroMine.logger.log(Level.ERROR, "Packet has a string with length > 2000!");
				return;
			} else
			{
				Map<String, String[]> newData = new HashMap<String, String[]>();
				Iterator<String> iterator = data.keySet().iterator();
				while (iterator.hasNext())
				{
					if (info.length() <= 2000)
					{
						break;
					} else if (data.size() <= 1)
					{
						EnviroMine.logger.log(Level.ERROR, "Packet has a string with length > 2000!");
						return;
					} else
					{
						String key = iterator.next();
						newData.put(key, data.get(key));
						iterator.remove();
						info = getDataAsString();
					}
				}
				
				IMessage packet = new PacketServerOverride(this.player, this.data);
				EnviroMine.instance.network.sendTo(packet, player);
			}
		}
		
		ByteBufUtils.writeUTF8String(buf, info);
	}
	
	public static class Handler implements IMessageHandler<PacketServerOverride, IMessage>
	{
		@Override
		public IMessage onMessage(PacketServerOverride message, MessageContext ctx)
		{
			Iterator<String> iterator = message.data.keySet().iterator();
			while (iterator.hasNext())
			{
				String key = iterator.next();
				try
				{
					String[] strs = message.data.get(key);
					for (int i = 0; i < strs.length; i += 2)
					{
						decodeCustom(key, EM_Settings.class.getDeclaredField(strs[i]), strs[i + 1]);
					}
				} catch (NoSuchFieldException e)
				{
					e.printStackTrace();
				} catch (SecurityException e)
				{
					e.printStackTrace();
				}
			}
			
			EM_Settings.isOverridden = true;
			
			return null; //Reply
		}
		
		private void decodeCustom(String clazz, Field field, String msg)
		{
			try
			{
				Object obj = Class.forName(clazz).newInstance();
				if (obj instanceof IPacketEncoder)
				{
					IPacketEncoder encoder = (IPacketEncoder)obj;
					field.set(null, encoder.decode(msg, field.get(null)));
				}
			} catch (ClassNotFoundException e)
			{
				EnviroMine.logger.log(Level.ERROR, "Error decoding: Class " + clazz + " is not valid");
			} catch (InstantiationException e)
			{
				EnviroMine.logger.log(Level.ERROR, "Error decoding", e);
			} catch (IllegalAccessException e)
			{
				EnviroMine.logger.log(Level.ERROR, "Error decoding", e);
			} catch (NullPointerException e)
			{
				EnviroMine.logger.log(Level.ERROR, "Error decoding", e);
			}
		}
	}
}