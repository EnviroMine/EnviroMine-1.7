package enviromine.network.packet;

import enviromine.core.EM_Settings;
import enviromine.core.EM_Settings.ShouldOverride;
import enviromine.core.EnviroMine;
import enviromine.network.packet.encoders.IPacketEncoder;

import net.minecraft.entity.player.EntityPlayerMP;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import com.google.common.base.Strings;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.apache.logging.log4j.Level;

public class PacketAutoOverride extends PacketServerOverride implements IMessage
{
	public PacketAutoOverride()
	{
		
	}
	
	public PacketAutoOverride(EntityPlayerMP player)
	{
		super(player);
		if (FMLCommonHandler.instance().getSide().isServer())
		{
			readFromSettings();
		}
	}
	
	private void readFromSettings()
	{
		Field[] fields = EM_Settings.class.getFields();

		for (Field field : fields)
		{
			ShouldOverride annotation = field.getAnnotation(ShouldOverride.class);
			if (annotation != null)
			{
				IPacketEncoder encoder;
				String classPath = annotation.value();
				if (Strings.isNullOrEmpty(classPath))
				{
					if (field.getType() == String.class)
					{
						classPath = "enviromine.network.packet.encoders.StringEncoder";
					} else if (field.getType() == int.class)
					{
						classPath = "enviromine.network.packet.encoders.IntEncoder";
					} else if (field.getType() == boolean.class)
					{
						classPath = "enviromine.network.packet.encoders.BoolEncoder";
					} else if (field.getType() == float.class)
					{
						classPath = "enviromine.network.packet.encoders.FloatEncoder";
					} else
					{
						EnviroMine.logger.log(Level.ERROR, field.getName() + " has an unkown type - skipping");
					}
				}

				try
				{
					Object obj = Class.forName(classPath).newInstance();
					if (obj instanceof IPacketEncoder)
					{
						encoder = (IPacketEncoder)obj;

						String[] strs = data.get(classPath);
						if (strs != null)
						{
							strs = appendArrayToArray(strs, new String[]{field.getName(), encoder.encode(field.get(null))});
						} else
						{
							strs = new String[]{field.getName(), encoder.encode(field.get(null))};
						}
						data.put(classPath, strs);
					}
				} catch (ClassNotFoundException e)
				{
					EnviroMine.logger.log(Level.ERROR, "Error encoding: " + classPath + " is not a vaid class. (On field: " + field.getName() + ")");
				} catch (InstantiationException e)
				{
					EnviroMine.logger.log(Level.ERROR, "Error encoding: An error occoured getting encoder class", e);
				} catch (IllegalAccessException e)
				{
					EnviroMine.logger.log(Level.ERROR, "Error encoding: An error occoured getting encoder class", e);
				} catch (NullPointerException e)
				{
					EnviroMine.logger.log(Level.ERROR, "Error encoding: An error occoured getting encoder class", e);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T[] appendArrayToArray(T[] array, T[] newArray)
	{
		Class clazz = array.getClass().getComponentType();
		T[] temp = (T[])Array.newInstance(clazz, array.length+newArray.length);
		System.arraycopy(array, 0, temp, 0, array.length);
		
		return temp;
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