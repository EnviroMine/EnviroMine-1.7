package enviromine.network.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import enviromine.core.EM_Settings;
import enviromine.core.EM_Settings.ShouldOverride;
import enviromine.core.EnviroMine;
import enviromine.network.packet.encoders.IPacketEncoder;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.google.common.base.Strings;

public class PacketAutoOverride extends PacketServerOverride implements IMessage
{
	public PacketAutoOverride(EntityPlayerMP player)
	{
		super(player);
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
								
								custom.put(annotation.value(), encoder.encode(fields[i]));
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
	
	public static class Handler implements IMessageHandler<PacketAutoOverride, IMessage>
	{
		@Override
		public IMessage onMessage(PacketAutoOverride message, MessageContext ctx)
		{
			return (new PacketServerOverride.Handler()).onMessage(message, ctx);
		}
	}
}