package enviromine.network.packet.encoders;

public class StringEncoder implements IPacketEncoder<String>
{
	@Override
	public String encode(String obj)
	{
		System.out.println("Encoding: " + obj);
		return null;
	}
	
	@Override
	public String decode(String str, String current)
	{
		System.out.println("Decoding: " + str);
		return null;
	}
}