package enviromine.network.packet.encoders;

import enviromine.core.EM_Settings;
import enviromine.trackers.ArmorProperties;

import java.util.Iterator;

public class ArmorPropsEncoder implements IPacketEncoder
{
	@Override
	public String encode()
	{
		Iterator<String> iterator = EM_Settings.armorProperties.keySet().iterator();
		String str = "";
		while (iterator.hasNext())
		{
			String name = iterator.next();
			if (!str.equals(""))
			{
				str += ";";
			}
			str += name + "," + EM_Settings.armorProperties.get(name).allowCamelPack;
		}
		
		return str;
	}
	
	@Override
	public void decode(String str)
	{
		String[] pairs = str.split(";");
		for (String pair : pairs)
		{
			String[] split = pair.split(",");
			String name = split[0];
			boolean state = Boolean.parseBoolean(split[1]);
			
			if (EM_Settings.armorProperties.containsKey(name))
			{
				ArmorProperties prop = EM_Settings.armorProperties.get(name);
				prop.allowCamelPack = state;
				EM_Settings.armorProperties.put(name, prop);
			} else
			{
				EM_Settings.armorProperties.put(name, new ArmorProperties(name, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, state));
			}
		}
	}
}