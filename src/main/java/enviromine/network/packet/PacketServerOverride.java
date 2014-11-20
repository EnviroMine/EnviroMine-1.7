package enviromine.network.packet;

import io.netty.buffer.ByteBuf;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
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
	protected NBTTagCompound tags = new NBTTagCompound();
	
	public PacketServerOverride()
	{
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
	
	public static class Handler implements IMessageHandler<PacketServerOverride, IMessage>
	{
		@Override
		public IMessage onMessage(PacketServerOverride message, MessageContext ctx)
		{
			if(!EnviroMine.proxy.isClient())
			{
				EnviroMine.logger.log(Level.WARN, "AutoOverride attempted to run serverside! This should not happen!");
				return null;
			}
			
			NBTTagCompound tags = message.tags;
			
			Field[] fields = EM_Settings.class.getDeclaredFields();
			
			for(Field f : fields)
			{
				try
				{
					ShouldOverride anno = f.getAnnotation(ShouldOverride.class);
					Class[] clazzes;
					
					if(anno != null)
					{
						clazzes = anno.value();
					} else
					{
						continue;
					}
					
					if(!f.isAccessible())
					{
						EnviroMine.logger.log(Level.WARN, "Field " + f.getName() + " is protected and cannot be synced!");
						continue;
					} else if(!Modifier.isStatic(f.getModifiers()))
					{
						EnviroMine.logger.log(Level.WARN, "Cannot sync non-static field " + f.getName() + "!");
						continue;
					}
					
					if(f.getType() == HashMap.class)
					{
						if(clazzes.length < 2)
						{
							EnviroMine.logger.log(Level.ERROR, "Annotation for field " + f.getName() + " (" + f.getType().getName() + ") is missing class types!");
							continue;
						}
						
						NBTTagList nbtList = tags.getTagList(f.getName(), 10);
						HashMap map = new HashMap();
						
						for(int i = 0; i < nbtList.tagCount(); i++)
						{
							NBTTagCompound tag = nbtList.getCompoundTagAt(i);
							Object keyObj = this.getNBTValue(tag, "key", clazzes[0]);
							Object valObj = this.getNBTValue(tag, "value", clazzes[1]);
							
							if(keyObj == null || valObj == null)
							{
								EnviroMine.logger.log(Level.WARN, "Position " + i + " in HashMap " + f.getName() + " returned a null entry! Skipping...");
								continue;
							}
							
							map.put(keyObj, valObj);
						}
						
						f.set(null, map);
					} else if(f.getType() == ArrayList.class)
					{
						if(clazzes.length < 1)
						{
							EnviroMine.logger.log(Level.ERROR, "Annotation for field " + f.getName() + " is missing class types!");
							continue;
						}
						
						Class clazz = clazzes[0];
						NBTTagList nbtList = tags.getTagList(f.getName(), 10);
						ArrayList list = new ArrayList();
						
						for(int i = 0; i < nbtList.tagCount(); i++)
						{
							NBTTagCompound tag = nbtList.getCompoundTagAt(i);
							Object valObj = this.getNBTValue(tag, "value", clazz);
							
							if(valObj == null)
							{
								EnviroMine.logger.log(Level.WARN, "Position " + i + " in ArrayList " + f.getName() + " returned a null entry! Skipping...");
								continue;
							}
							
							list.add(valObj);
						}
						
						f.set(null, list);
					} else
					{
						Object value = this.getNBTValue(tags, f.getName(), f.getType());
						
						if(value == null)
						{
							EnviroMine.logger.log(Level.WARN, "Field " + f.getName() + " returned a null value! Skipping...");
							continue;
						}
						
						f.set(null, value);
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
			} else if(clazz.isAssignableFrom(NBTBase.class))
			{
				return tag.getTag(key);
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
	}
}