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
	protected Map<String, String> strings = new HashMap<String, String>();
	protected Map<String, Integer> integers = new HashMap<String, Integer>();
	protected Map<String, Boolean> booleans = new HashMap<String, Boolean>();
	protected Map<String, Float> floats = new HashMap<String, Float>();
	protected Map<String, String[]> custom = new HashMap<String, String[]>();
	
	public PacketServerOverride()
	{
	}
	
	public PacketServerOverride(EntityPlayerMP player)
	{
		this.player = player;
	}
	
	public PacketServerOverride(EntityPlayerMP player, Map<String, String> strings, Map<String, Integer> integers, Map<String, Boolean> booleans, Map<String, Float> floats, Map<String, String[]> custom)
	{
		this.player = player;
		this.strings = strings == null ? this.strings : strings;
		this.integers = integers == null ? this.integers : integers;
		this.booleans = booleans == null ? this.booleans : booleans;
		this.floats = floats == null ? this.floats : floats;
		this.custom = custom == null ? this.custom : custom;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		String[] strs = ByteBufUtils.readUTF8String(buf).split(";");
		String[] intss = ByteBufUtils.readUTF8String(buf).split(";");
		String[] bools = ByteBufUtils.readUTF8String(buf).split(";");
		String[] floatss = ByteBufUtils.readUTF8String(buf).split(";");
		String[] customs = ByteBufUtils.readUTF8String(buf).split(";;");
		
		for (String type : strs)
		{
			String[] tmp = type.split(":");
			if (tmp.length == 2)
			{
				this.strings.put(tmp[0], tmp[1]);
			}
		}
		for (String type : intss)
		{
			String[] tmp = type.split(":");
			if (tmp.length == 2)
			{
				this.integers.put(tmp[0], Integer.parseInt(tmp[1]));
			}
		}
		for (String type : bools)
		{
			String[] tmp = type.split(":");
			if (tmp.length == 2)
			{
				this.booleans.put(tmp[0], Boolean.parseBoolean(tmp[1]));
			}
		}
		for (String type : floatss)
		{
			String[] tmp = type.split(":");
			if (tmp.length == 2)
			{
				this.floats.put(tmp[0], Float.parseFloat(tmp[1]));
			}
		}
		for (String type : customs)
		{
			String[] tmp = type.split("::");
			if (tmp.length == 2)
			{
				this.custom.put(tmp[0], new String[]{tmp[1], tmp[2]});
			}
		}
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		Iterator<String> ite = strings.keySet().iterator();
		String strs = "";
		while (ite.hasNext())
		{
			String key = ite.next();
			if (!strs.equals(""))
			{
				strs += ";";
			}
			
			strs += (key + ":" + strings.get(key));
		}
		ite = integers.keySet().iterator();
		String intss = "";
		while (ite.hasNext())
		{
			String key = ite.next();
			if (!intss.equals(""))
			{
				intss += ";";
			}
			
			intss += (key + ":" + integers.get(key));
		}
		ite = booleans.keySet().iterator();
		String bools = "";
		while (ite.hasNext())
		{
			String key = ite.next();
			if (!bools.equals(""))
			{
				bools += ";";
			}
			
			bools += (key + ":" + booleans.get(key));
		}
		ite = floats.keySet().iterator();
		String floatss = "";
		while (ite.hasNext())
		{
			String key = ite.next();
			if (!floatss.equals(""))
			{
				floatss += ";";
			}
			
			floatss += (key + ":" + floats.get(key));
		}
		ite = custom.keySet().iterator();
		String customs = "";
		while (ite.hasNext())
		{
			String key = ite.next();
			if (!customs.equals(""))
			{
				customs += ";;";
			}
			
			customs += (key + "::" + custom.get(key)[0] + "::" + custom.get(key)[1]);
		}
		
		int length = strs.length() + intss.length() + bools.length() + floatss.length() + customs.length();
		if (strs.length() > 2000 || intss.length() > 2000 || bools.length() > 2000 || floatss.length() > 2000 || customs.length() > 2000)
		{
			EnviroMine.logger.log(Level.ERROR, "Packet has a string with length > 2000!"); //TODO handle
			return;
		}
		while (length > 2000)
		{
			IMessage packet = null;
			if (strs.length() > 0)
			{
				packet = new PacketServerOverride(player, strings, null, null, null, null);
				strs = "";
			} else if (intss.length() > 0)
			{
				packet = new PacketServerOverride(player, null, integers, null, null, null);
				intss = "";
			} else if (bools.length() > 0)
			{
				packet = new PacketServerOverride(player, null, null, booleans, null, null);
				bools = "";
			} else if (floatss.length() > 0)
			{
				packet = new PacketServerOverride(player, null, null, null, floats, null);
				floatss = "";
			} else if (customs.length() > 0)
			{
				packet = new PacketServerOverride(player, null, null, null, null, custom);
				customs = "";
			}
			
			if (packet != null)
			{
				EnviroMine.instance.network.sendTo(packet, player);
			}
			length = strs.length() + intss.length() + bools.length() + floatss.length() + customs.length();
		}
		
		ByteBufUtils.writeUTF8String(buf, strs);
		ByteBufUtils.writeUTF8String(buf, intss);
		ByteBufUtils.writeUTF8String(buf, bools);
		ByteBufUtils.writeUTF8String(buf, floatss);
		ByteBufUtils.writeUTF8String(buf, customs);
	}
	
	public static class Handler implements IMessageHandler<PacketServerOverride, IMessage>
	{
		@Override
		public IMessage onMessage(PacketServerOverride message, MessageContext ctx)
		{
			Iterator<String> iterator = message.strings.keySet().iterator();
			while (iterator.hasNext())
			{
				String key = iterator.next();
				setField(key, message.strings.get(key));
			}
			iterator = message.integers.keySet().iterator();
			while (iterator.hasNext())
			{
				String key = iterator.next();
				setField(key, message.integers.get(key));
			}
			iterator = message.booleans.keySet().iterator();
			while (iterator.hasNext())
			{
				String key = iterator.next();
				setField(key, message.booleans.get(key));
			}
			iterator = message.floats.keySet().iterator();
			while (iterator.hasNext())
			{
				String key = iterator.next();
				setField(key, message.floats.get(key));
			}
			iterator = message.custom.keySet().iterator();
			while (iterator.hasNext())
			{
				String key = iterator.next();
				try
				{
					decodeCustom(key, EM_Settings.class.getDeclaredField(message.custom.get(key)[0]), message.custom.get(key)[1]);
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
					
					field.set(null, encoder.decode(msg));
				}
			} catch (ClassNotFoundException e)
			{
				EnviroMine.logger.log(Level.ERROR, "Error decoding: Class " + clazz + " is not valid");
			} catch (InstantiationException e)
			{
				EnviroMine.logger.log(Level.ERROR, "Error decoding: An error occoured getting encoder class", e);
			} catch (IllegalAccessException e)
			{
				EnviroMine.logger.log(Level.ERROR, "Error decoding: An error occoured getting encoder class", e);
			}
		}
		
		private void setField(String name, Object obj)
		{
			try
			{
				EM_Settings.class.getDeclaredField(name).set(null, obj);
			} catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				e.printStackTrace();
			} catch (NoSuchFieldException e)
			{
				e.printStackTrace();
			} catch (SecurityException e)
			{
				e.printStackTrace();
			}
		}
	}
}