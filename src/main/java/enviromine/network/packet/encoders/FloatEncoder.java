package enviromine.network.packet.encoders;

public class FloatEncoder implements IPacketEncoder<Float>
{
	@Override
	public String encode(Float obj)
	{
		return obj.toString();
	}
	
	@Override
	public Float decode(String str, Float current)
	{
		return Float.parseFloat(str);
	}
}