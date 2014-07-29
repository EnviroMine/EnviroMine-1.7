package main.java.enviromine.trackers;

public class BlockProperties
{
	public boolean hasPhys;
	
	public String name;
	public int meta;
	
	public int minFall;
	public int maxFall;
	public int supportDist;
	
	public String dropName;
	public int dropMeta;
	public int dropNum;
	
	public boolean enableTemp;
	
	public float temp;
	public float air;
	public float sanity;
	
	public boolean holdsOthers;
	public boolean slides;
	public boolean canHang;
	public boolean wetSlide;
	
	public BlockProperties(String name, int meta, boolean hasPhys, int minFall, int maxFall, int supportDist, String dropName, int dropMeta, int dropNum, boolean enableTemp, float temp, float air, float sanity, boolean holdOther, boolean slides, boolean canHang, boolean wetSlide)
	{
		this.name = name;
		this.meta = meta;
		this.hasPhys = hasPhys;
		this.minFall = minFall;
		this.maxFall = maxFall;
		this.supportDist = supportDist;
		this.dropName = dropName;
		this.dropMeta = dropMeta;
		this.dropNum = dropNum;
		this.enableTemp = enableTemp;
		this.temp = temp;
		this.air = air;
		this.sanity = sanity;
		this.holdsOthers = holdOther;
		this.slides = slides;
		this.canHang = canHang;
		this.wetSlide = wetSlide;
	}
}
