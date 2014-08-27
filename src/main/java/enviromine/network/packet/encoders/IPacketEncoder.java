package enviromine.network.packet.encoders;

public interface IPacketEncoder<T>
{
	String encode(T obj);
	T decode(String str, T current);
}