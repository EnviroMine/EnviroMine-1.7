package enviromine.network.packet.encoders;

import enviromine.core.EM_Settings;
import enviromine.trackers.ArmorProperties;

import java.util.HashMap;
import java.util.Iterator;

public class ArmorPropsEncoder<T> implements IPacketEncoder<HashMap<String, ArmorProperties>>
{
	@Override
	public String encode(HashMap<String, ArmorProperties> armorProps)
	{
		try
		{
			Iterator<String> iterator = armorProps.keySet().iterator();
			String str = "";
			while (iterator.hasNext())
			{
				String name = iterator.next();
				if (!str.equals(""))
				{
					str += ";";
				}
				str += name + "," + armorProps.get(name).allowCamelPack;
			}
			
			return str;
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		
		return "";
	}
	
	@Override
	public HashMap<String, ArmorProperties> decode(String str)
	{
		//HashMap<String, ArmorProperties> map = 
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
		
		return null;
	}
}