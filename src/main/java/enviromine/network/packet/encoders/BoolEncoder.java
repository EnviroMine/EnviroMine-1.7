package enviromine.network.packet.encoders;

public class BoolEncoder implements IPacketEncoder<Boolean>
{
	@Override
	public String encode(Boolean obj)
	{
		return obj.toString();
	}
	
	@Override
	public Boolean decode(String str, Boolean current)
	{
		return Boolean.parseBoolean(str);
	}
}