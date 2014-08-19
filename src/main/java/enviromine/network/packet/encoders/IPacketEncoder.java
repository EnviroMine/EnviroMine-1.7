package enviromine.network.packet.encoders;


public interface IPacketEncoder<T>
{
	public String encode(T obj);
	public T decode(String str);
}