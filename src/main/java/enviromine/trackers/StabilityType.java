package main.java.enviromine.trackers;

public class StabilityType
{
	public boolean enablePhysics;
	public int supportDist;
	public int minFall;
	public int maxFall;
	public boolean canHang;
	public boolean holdOther;
	
	public StabilityType(boolean enablePhysics, int supportDist, int minFall, int maxFall, boolean canHang, boolean holdOther)
	{
		this.enablePhysics = enablePhysics;
		this.supportDist = supportDist;
		this.minFall = minFall;
		this.maxFall = maxFall;
		this.canHang = canHang;
		this.holdOther = holdOther;
	}
}
