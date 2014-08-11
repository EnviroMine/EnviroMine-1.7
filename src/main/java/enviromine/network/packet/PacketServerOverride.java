package enviromine.network.packet;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import enviromine.core.EM_Settings;
import enviromine.trackers.ArmorProperties;
import io.netty.buffer.ByteBuf;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PacketServerOverride implements IMessage
{
	public boolean enableAirQ;
	public boolean enableHydrate;
	public boolean enableSanity;
	public boolean enableBodyTemp;
	public Set<String> allowedArmors = new HashSet<String>();
	public Set<String> disallowedArmors = new HashSet<String>();
	
	public PacketServerOverride()
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			readFromSettings();
		}
	}
	
	private void readFromSettings()
	{
		this.enableAirQ = EM_Settings.enableAirQ;
		this.enableBodyTemp = EM_Settings.enableBodyTemp;
		this.enableHydrate = EM_Settings.enableHydrate;
		this.enableSanity = EM_Settings.enableSanity;
		
		Iterator<String> iterator = EM_Settings.armorProperties.keySet().iterator();
		while(iterator.hasNext())
		{
			String name = iterator.next();
			if(EM_Settings.armorProperties.get(name).allowCamelPack)
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
		String[] compound = ByteBufUtils.readUTF8String(buf).split(";");
		for(String name : compound)
		{
			this.allowedArmors.add(name);
		}
		
		compound = ByteBufUtils.readUTF8String(buf).split(";");
		for(String name : compound)
		{
			this.disallowedArmors.add(name);
		}
		
		this.enableAirQ = buf.readBoolean();
		this.enableBodyTemp = buf.readBoolean();
		this.enableHydrate = buf.readBoolean();
		this.enableSanity = buf.readBoolean();
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		String compound = "";
		Iterator<String> iterator = this.allowedArmors.iterator();
		while(iterator.hasNext())
		{
			if(compound.equals(""))
			{
				compound = iterator.next();
			} else
			{
				compound += ";" + iterator.next();
			}
		}
		ByteBufUtils.writeUTF8String(buf, compound);
		
		iterator = this.disallowedArmors.iterator();
		while(iterator.hasNext())
		{
			if(compound.equals(""))
			{
				compound = iterator.next();
			} else
			{
				compound += ";" + iterator.next();
			}
		}
		ByteBufUtils.writeUTF8String(buf, compound);
		
		buf.writeBoolean(this.enableAirQ);
		buf.writeBoolean(this.enableBodyTemp);
		buf.writeBoolean(this.enableHydrate);
		buf.writeBoolean(this.enableSanity);
	}
	
	public static class Handler implements IMessageHandler<PacketServerOverride,IMessage>
	{
		@Override
		public IMessage onMessage(PacketServerOverride message, MessageContext ctx)
		{
			EM_Settings.enableAirQ = message.enableAirQ;
			EM_Settings.enableBodyTemp = message.enableBodyTemp;
			EM_Settings.enableHydrate = message.enableHydrate;
			EM_Settings.enableSanity = message.enableSanity;
			
			Iterator<String> iterator = message.allowedArmors.iterator();
			while(iterator.hasNext())
			{
				String name = iterator.next();
				if(EM_Settings.armorProperties.containsKey(name))
				{
					ArmorProperties prop = EM_Settings.armorProperties.get(name);
					prop.allowCamelPack = true;
					EM_Settings.armorProperties.put(name, prop);
				} else
				{
					EM_Settings.armorProperties.put(name, new ArmorProperties(name, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, true));
				}
			}
			
			iterator = message.disallowedArmors.iterator();
			while(iterator.hasNext())
			{
				String name = iterator.next();
				if(EM_Settings.armorProperties.containsKey(name))
				{
					ArmorProperties prop = EM_Settings.armorProperties.get(name);
					prop.allowCamelPack = false;
					EM_Settings.armorProperties.put(name, prop);
				} else
				{
					EM_Settings.armorProperties.put(name, new ArmorProperties(name, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, false));
				}
			}
			
			return null; //Reply
		}
	}
}