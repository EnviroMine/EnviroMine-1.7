package enviromine.trackers;

public class RotProperties
{
	public String name;
	public int meta;
	public int rotID;
	public int rotMeta;
	public double days;
	
	public RotProperties(String name, int meta, int rotID, int rotMeta, double days)
	{
		this.name = name;
		this.meta = meta;
		this.rotID = rotID;
		this.rotMeta = rotMeta;
		this.days = days;
	}
}
