package enviromine.network.packet;

import io.netty.buffer.ByteBuf;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import enviromine.core.EM_Settings;
import enviromine.core.EM_Settings.ShouldOverride;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.SerialisableProperty;

public class PacketServerOverride implements IMessage
{
	private EntityPlayerMP player;
	protected NBTTagCompound tags = new NBTTagCompound();
	
	public PacketServerOverride()
	{
	}
	
	public PacketServerOverride(EntityPlayerMP player)
	{
		this.player = player;
	}
	
	/*public PacketServerOverride(EntityPlayerMP player, Map<String, String[]> custom)
	{
		this.player = player;
		this.data = custom == null ? this.data : custom;
	}*/
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		tags = ByteBufUtils.readTag(buf);
		/*String str = ByteBufUtils.readUTF8String(buf);
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
		}*/
	}
	
	/*private String getDataAsString()
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
	}*/
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeTag(buf, tags);
		/*String info = getDataAsString();
		
		if (info.length() > 2000)
		{
			if (data.size() <= 1)
			{
				EnviroMine.logger.log(Level.ERROR, "Packet has a string with length > 2000! ("+info.length()+")");
				if (info.length() <= 10000) {
					EnviroMine.logger.log(Level.ERROR, "Packet data:\n"+info);
				} else {
					String name = EM_ConfigHandler.configPath+"/packetError_"+getFormattedDate()+".txt";
					EnviroMine.logger.log(Level.ERROR, "Packet length is > 10000! Writing to file "+name);
					
					try
					{
						FileWriter writer = new FileWriter(name);
						writer.write(info);
						writer.close();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				
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
						EnviroMine.logger.log(Level.ERROR, "Packet has a string with length > 2000! ("+info.length()+")");
						if (info.length() <= 10000) {
							EnviroMine.logger.log(Level.ERROR, "Packet data:\n"+info);
						} else {
							String name = EM_ConfigHandler.configPath+"/packetError_"+getFormattedDate()+".txt";
							EnviroMine.logger.log(Level.ERROR, "Packet length is > 10000! Writing to file "+name);
							
							try
							{
								FileWriter writer = new FileWriter(name);
								writer.write(info);
								writer.close();
							} catch (IOException e)
							{
								e.printStackTrace();
							}
						}
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
		
		ByteBufUtils.writeUTF8String(buf, info);*/
	}
	
	/*private String getFormattedDate()
	{
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR)+"-"+c.get(Calendar.MONTH)+"-"+c.get(Calendar.DAY_OF_MONTH)+"_"+c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND);
	}*/
	
	public static class Handler implements IMessageHandler<PacketServerOverride, IMessage>
	{
		@Override
		public IMessage onMessage(PacketServerOverride message, MessageContext ctx)
		{
			NBTTagCompound tags = message.tags;
			
			Field[] fields = EM_Settings.class.getDeclaredFields();
			
			for(Field f : fields)
			{
				try
				{
					if(!f.isAccessible())
					{
						continue;
					}
					
					ShouldOverride anno = f.getAnnotation(ShouldOverride.class);
					
					if(anno != null)
					{
						if(f.getType() == HashMap.class)
						{
							if(anno.value().length < 2)
							{
								EnviroMine.logger.log(Level.ERROR, "Annotation for field " + f.getName() + " (" + f.getType().getName() + ") is missing class types!");
								continue;
							}
							
							Class[] clazzes = anno.value();
							NBTTagList nbtList = tags.getTagList(f.getName(), 10);
							HashMap map = new HashMap();
							
							for(int i = 0; i < nbtList.tagCount(); i++)
							{
								NBTTagCompound tag = nbtList.getCompoundTagAt(i);
								map.put(this.getNBTValue(tag, "key", clazzes[0]), this.getNBTValue(tag, "value", clazzes[1]));
							}
							
							f.set(null, map);
						} else if(f.getType() == ArrayList.class)
						{
							if(anno.value().length < 1)
							{
								EnviroMine.logger.log(Level.ERROR, "Annotation for field " + f.getName() + " is missing class types!");
								continue;
							}
							ArrayList list = (ArrayList)f.get(null);
						} else
						{
						}
					}
				} catch(Exception e)
				{
					EnviroMine.logger.log(Level.ERROR, "An error occured while syncing setting " + f.getName(), e);
				}
			}
			
			EM_Settings.isOverridden = true;
			
			return null; //Reply
		}
		
		/**
		 * Returns the value of the given key and class type. If the class type implements SerialisableProperty then it will return a new instance
		 * of that object from the NBTTagCompound stored under said key.
		 * @param tag
		 * @param key
		 * @param clazz
		 * @return
		 */
		public Object getNBTValue(NBTTagCompound tag, String key, Class clazz)
		{
			if(key == null || key.length() <= 0 || !tag.hasKey(key))
			{
				return null;
			}
			
			if(clazz == Boolean.class ||clazz == boolean.class)
			{
				return tag.getBoolean(key);
			} else if(clazz == Integer.class || clazz == int.class)
			{
				return tag.getInteger(key);
			} else if(clazz == String.class)
			{
				return tag.getString(key);
			} else if(clazz == Byte.class || clazz == byte.class)
			{
				return tag.getByte(key);
			} else if(clazz == Float.class || clazz == float.class)
			{
				return tag.getFloat(key);
			} else if(clazz == Double.class || clazz == double.class)
			{
				return tag.getDouble(key);
			} else if(clazz == Short.class || clazz == short.class)
			{
				return tag.getShort(key);
			} else if(clazz == Long.class || clazz == long.class)
			{
				return tag.getLong(key);
			} else if(clazz == Byte[].class || clazz == byte[].class)
			{
				return tag.getByteArray(key);
			} else if(clazz == NBTTagCompound.class)
			{
				return tag.getCompoundTag(key);
			} else if(clazz == NBTTagList.class)
			{
				if(!tag.hasKey(key + "_type"))
				{
					EnviroMine.logger.log(Level.WARN, "NBTTagList '" + key + "' is missing type key '" + key + "_type'! Defaulting to NBTTagCompound(10)");
					return tag.getTagList(key, 10);
				} else
				{
					return tag.getTagList(key, tag.getInteger(key + "_type"));
				}
			} else if(clazz.isAssignableFrom(SerialisableProperty.class))
			{
				try
				{
					Constructor ctor = clazz.getConstructor(NBTTagCompound.class);
					return ctor.newInstance(tag.getCompoundTag(key));
				} catch(Exception e)
				{
					EnviroMine.logger.log(Level.ERROR, "An error occured while trying to instantiate " + clazz.getSimpleName(), e);
					return null;
				}
			} else
			{
				return null;
			}
		}
		
		/*private void decodeCustom(String clazz, Field field, String msg)
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
		}*/
	}
}