package enviromine.network.packet.encoders;

public class IntEncoder implements IPacketEncoder<Integer>
{
	@Override
	public String encode(Integer obj)
	{
		return obj.toString();
	}
	
	@Override
	public Integer decode(String str, Integer current)
	{
		return Integer.parseInt(str);
	}
}