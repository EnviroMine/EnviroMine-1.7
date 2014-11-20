package enviromine.network.packet;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import enviromine.core.EM_Settings;
import enviromine.core.EM_Settings.ShouldOverride;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.SerialisableProperty;

public class PacketAutoOverride extends PacketServerOverride implements IMessage
{
	public PacketAutoOverride()
	{
		if (!EnviroMine.proxy.isClient())
		{
			this.tags = readFromSettings();
		}
	}
	
	private NBTTagCompound readFromSettings()
	{
		NBTTagCompound nTags = new NBTTagCompound();
		Field[] fields = EM_Settings.class.getFields();
		
		for (Field f : fields)
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
					HashMap map = (HashMap)f.get(null);
					Set keys = map.keySet();
					Iterator iterator = keys.iterator();
					NBTTagList nbtList = new NBTTagList();
					
					while(iterator.hasNext())
					{
						NBTTagCompound entry = new NBTTagCompound();
						Object keyObj = iterator.next();
						Object valObj = map.get(keyObj);
						this.setNBTValue(entry, "key", keyObj);
						this.setNBTValue(entry, "value", valObj);
						nbtList.appendTag(entry);
					}
					
					nTags.setTag(f.getName(), nbtList);
				} else if(f.getType() == ArrayList.class)
				{
					ArrayList list = (ArrayList)f.get(null);
					Iterator iterator = list.iterator();
					NBTTagList nbtList = new NBTTagList();
					
					while(iterator.hasNext())
					{
						NBTTagCompound entry = new NBTTagCompound();
						Object valObj = iterator.next();
						this.setNBTValue(entry, "value", valObj);
						nbtList.appendTag(entry);
					}
					
					nTags.setTag(f.getName(), nbtList);
				} else
				{
					this.setNBTValue(nTags, f.getName(), f.get(null));
				}
			} catch(Exception e)
			{
				EnviroMine.logger.log(Level.ERROR, "An error occured while syncing setting " + f.getName(), e);
			}
		}
		
		return nTags;
	}
		
	public void setNBTValue(NBTTagCompound tag, String key, Object value)
	{
		if(key == null || key.length() <= 0 || value == null)
		{
			EnviroMine.logger.log(Level.ERROR, "Tried to set NBTTagCompound without a value and or key!");
			return;
		}
		
		if(value instanceof Boolean)
		{
			tag.setBoolean(key, (Boolean)value);
		} else if(value instanceof Integer)
		{
			tag.setInteger(key, (Integer)value);
		} else if(value instanceof String)
		{
			tag.setString(key, (String)value);
		} else if(value instanceof Byte)
		{
			tag.setByte(key, (Byte)value);
		} else if(value instanceof Float)
		{
			tag.setFloat(key, (Float)value);
		} else if(value instanceof Double)
		{
			tag.setDouble(key, (Double)value);
		} else if(value instanceof Short)
		{
			tag.setShort(key, (Short)value);
		} else if(value instanceof Long)
		{
			tag.setLong(key, (Long)value);
		} else if(value instanceof Byte[])
		{
			tag.setByteArray(key, (byte[])value);
		} else if(value instanceof NBTBase)
		{
			tag.setTag(key, (NBTBase)value);
		} else if(value instanceof SerialisableProperty)
		{
			tag.setTag(key, ((SerialisableProperty)value).WriteToNBT());
		} else
		{
			EnviroMine.logger.log(Level.ERROR, "Cannot set NBTTagCompound a value type of " + value.getClass().getSimpleName());
		}
	}
	
	public static class Handler implements IMessageHandler<PacketAutoOverride, IMessage>
	{
		@Override
		public IMessage onMessage(PacketAutoOverride message, MessageContext ctx)
		{
			return (new PacketServerOverride.Handler()).onMessage(message, ctx);
		}
	}
}