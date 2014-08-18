package enviromine.network.packet.encoders;

public interface IPacketEncoder
{
	public String encode();
	public void decode(String str);
}