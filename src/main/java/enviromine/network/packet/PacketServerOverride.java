package enviromine.network.packet;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import enviromine.core.EM_Settings;
import enviromine.core.EM_Settings.ShouldOverride;
import enviromine.core.EnviroMine;
import enviromine.trackers.ArmorProperties;

import io.netty.buffer.ByteBuf;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Level;

public class PacketServerOverride implements IMessage
{
	/*
	public boolean enableAirQ;
	public boolean enableHydrate;
	public boolean enableSanity;
	public boolean enableBodyTemp;
	 */
	public Set<String> allowedArmors = new HashSet<String>();
	public Set<String> disallowedArmors = new HashSet<String>();
	
	private Map<String, String> strings = new HashMap<String, String>();
	private Map<String, Integer> integers = new HashMap<String, Integer>();
	private Map<String, Boolean> booleans = new HashMap<String, Boolean>();
	private Map<String, Float> floats = new HashMap<String, Float>();
	
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
			Annotation[] annos = fields[i].getAnnotations();
			for (int j = 0; j < annos.length; j++)
			{
				if (annos[j] instanceof ShouldOverride)
				{
					System.out.println("ShouldOverride!");
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
						EnviroMine.logger.log(Level.ERROR, fields[i].getName() + " has an unkown type - skipping");
					}
				}
			}
		}
		
		/*
		this.enableAirQ = EM_Settings.enableAirQ;
		this.enableBodyTemp = EM_Settings.enableBodyTemp;
		this.enableHydrate = EM_Settings.enableHydrate;
		this.enableSanity = EM_Settings.enableSanity;
		 */
		
		Iterator<String> iterator = EM_Settings.armorProperties.keySet().iterator();
		while (iterator.hasNext())
		{
			String name = iterator.next();
			if (EM_Settings.armorProperties.get(name).allowCamelPack)
			{
				this.allowedArmors.add(name);
			} else
			{
				this.disallowedArmors.add(name);
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
		
		String[] compound = ByteBufUtils.readUTF8String(buf).split(";");
		for (String name : compound)
		{
			this.allowedArmors.add(name);
		}
		
		compound = ByteBufUtils.readUTF8String(buf).split(";");
		for (String name : compound)
		{
			this.disallowedArmors.add(name);
		}
		
		/*
		this.enableAirQ = buf.readBoolean();
		this.enableBodyTemp = buf.readBoolean();
		this.enableHydrate = buf.readBoolean();
		this.enableSanity = buf.readBoolean();
		 */
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
		
		System.out.println("'" + strs + "'");
		System.out.println("'" + intss + "'");
		System.out.println("'" + bools + "'");
		System.out.println("'" + floatss + "'");
		ByteBufUtils.writeUTF8String(buf, strs);
		ByteBufUtils.writeUTF8String(buf, intss);
		ByteBufUtils.writeUTF8String(buf, bools);
		ByteBufUtils.writeUTF8String(buf, floatss);
		
		String compound = "";
		Iterator<String> iterator = this.allowedArmors.iterator();
		while (iterator.hasNext())
		{
			if (compound.equals(""))
			{
				compound = iterator.next();
			} else
			{
				compound += ";" + iterator.next();
			}
		}
		ByteBufUtils.writeUTF8String(buf, compound);
		
		iterator = this.disallowedArmors.iterator();
		while (iterator.hasNext())
		{
			if (compound.equals(""))
			{
				compound = iterator.next();
			} else
			{
				compound += ";" + iterator.next();
			}
		}
		ByteBufUtils.writeUTF8String(buf, compound);
		
		/*
		buf.writeBoolean(this.enableAirQ);
		buf.writeBoolean(this.enableBodyTemp);
		buf.writeBoolean(this.enableHydrate);
		buf.writeBoolean(this.enableSanity);
		 */
	}
	
	public static class Handler implements IMessageHandler<PacketServerOverride, IMessage>
	{
		@Override
		public IMessage onMessage(PacketServerOverride message, MessageContext ctx)
		{
			System.out.println("Starting to read packet. EnableBodyTemp is set to: " + EM_Settings.enableBodyTemp);
			
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
			
			/*
			EM_Settings.enableAirQ = message.enableAirQ;
			EM_Settings.enableBodyTemp = message.enableBodyTemp;
			EM_Settings.enableHydrate = message.enableHydrate;
			EM_Settings.enableSanity = message.enableSanity;
			 */
			
			iterator = message.disallowedArmors.iterator();
			while (iterator.hasNext())
			{
				String name = iterator.next();
				if (EM_Settings.armorProperties.containsKey(name))
				{
					ArmorProperties prop = EM_Settings.armorProperties.get(name);
					prop.allowCamelPack = false;
					EM_Settings.armorProperties.put(name, prop);
				} else
				{
					EM_Settings.armorProperties.put(name, new ArmorProperties(name, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, false));
				}
			}
			
			iterator = message.allowedArmors.iterator();
			while (iterator.hasNext())
			{
				String name = iterator.next();
				if (EM_Settings.armorProperties.containsKey(name))
				{
					ArmorProperties prop = EM_Settings.armorProperties.get(name);
					prop.allowCamelPack = true;
					EM_Settings.armorProperties.put(name, prop);
				} else
				{
					EM_Settings.armorProperties.put(name, new ArmorProperties(name, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, true));
				}
			}
			EM_Settings.isOverridden = true;
			
			System.out.println("Finished reading packet. EnableBodyTemp set to: " + EM_Settings.enableBodyTemp);
			
			return null; //Reply
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