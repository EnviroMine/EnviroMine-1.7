package enviromine.network.packet.encoders;

import java.lang.reflect.Field;

public interface IPacketEncoder
{
	public String encode(Field field);
	public void decode(String str);
}