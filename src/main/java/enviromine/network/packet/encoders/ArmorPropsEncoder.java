package enviromine.network.packet.encoders;

import enviromine.core.EM_Settings;
import enviromine.trackers.ArmorProperties;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;

public class ArmorPropsEncoder implements IPacketEncoder
{
	@Override
	public String encode(Field field)
	{
		if (field.getType() == EM_Settings.armorProperties.getClass()) {
			HashMap<String, ArmorProperties> armorProps;
			try
			{
				armorProps = (HashMap<String, ArmorProperties>)field.get(null);
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
			} catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		
		return "";
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