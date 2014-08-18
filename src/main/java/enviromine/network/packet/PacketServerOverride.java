package enviromine.network.packet;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import enviromine.core.EM_Settings;
import enviromine.core.EM_Settings.ShouldOverride;
import enviromine.core.EnviroMine;
import enviromine.network.packet.encoders.IPacketEncoder;

import io.netty.buffer.ByteBuf;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.google.common.base.Strings;

public class PacketServerOverride implements IMessage
{
	private Map<String, String> strings = new HashMap<String, String>();
	private Map<String, Integer> integers = new HashMap<String, Integer>();
	private Map<String, Boolean> booleans = new HashMap<String, Boolean>();
	private Map<String, Float> floats = new HashMap<String, Float>();
	private Map<String, String> custom = new HashMap<String, String>();
	
	public PacketServerOverride()
	{
		if (FMLCommonHandler.instance().getSide().isServer())
		{
			readFromSettings();
		}
	}
	
	private <T> void addToMap(Map<String, T> map, Field field)
	{
		try
		{
			map.put(field.getName(), (T)field.get(null));
		} catch (ClassCastException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (NullPointerException e)
		{
			e.printStackTrace();
		}
	}
	
	private void readFromSettings()
	{
		Field[] fields = EM_Settings.class.getFields();
		
		for (int i = 0; i < fields.length; i++)
		{
			ShouldOverride annotation = fields[i].getAnnotation(ShouldOverride.class);
			if (annotation != null)
			{
				if (fields[i].getType() == String.class)
				{
					addToMap(strings, fields[i]);
				} else if (fields[i].getType() == int.class)
				{
					addToMap(integers, fields[i]);
				} else if (fields[i].getType() == boolean.class)
				{
					addToMap(booleans, fields[i]);
				} else if (fields[i].getType() == float.class)
				{
					addToMap(floats, fields[i]);
				} else
				{
					if (Strings.isNullOrEmpty(annotation.value()))
					{
						EnviroMine.logger.log(Level.ERROR, fields[i].getName() + " has an unkown type - skipping");
					} else
					{
						try
						{
							Object obj = Class.forName(annotation.value()).newInstance();
							if (obj instanceof IPacketEncoder)
							{
								IPacketEncoder encoder = (IPacketEncoder)obj;
								
								custom.put(annotation.value(), encoder.encode());
							}
						} catch (ClassNotFoundException e)
						{
							EnviroMine.logger.log(Level.ERROR, "Error encoding: Class " + annotation.value() + " on field " + fields[i].getName() + " is not valid");
						} catch (InstantiationException e)
						{
							EnviroMine.logger.log(Level.ERROR, "Error encoding: An error occoured getting encoder class", e);
						} catch (IllegalAccessException e)
						{
							EnviroMine.logger.log(Level.ERROR, "Error encoding: An error occoured getting encoder class", e);
						}
					}
				}
			}
		}
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
				this.custom.put(tmp[0], tmp[1]);
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
			
			customs += (key + "::" + custom.get(key));
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
				decodeCustom(key, message.custom.get(key));
			}
			
			EM_Settings.isOverridden = true;
			
			return null; //Reply
		}
		
		private void decodeCustom(String clazz, String msg)
		{
			try
			{
				Object obj = Class.forName(clazz).newInstance();
				if (obj instanceof IPacketEncoder)
				{
					IPacketEncoder encoder = (IPacketEncoder)obj;
					
					encoder.decode(msg);
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