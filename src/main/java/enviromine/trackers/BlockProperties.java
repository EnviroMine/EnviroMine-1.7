package enviromine.trackers;

import net.minecraft.block.Block;

public class BlockProperties
{
	public boolean hasPhys;
	
	public Block block;
	public int meta;
	
	public int minFall;
	public int maxFall;
	public int supportDist;
	
	public int dropID;
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
	
	public BlockProperties(Block block, int meta, boolean hasPhys, int minFall, int maxFall, int supportDist, int dropID, int dropMeta, int dropNum, boolean enableTemp, float temp, float air, float sanity, boolean holdOther, boolean slides, boolean canHang, boolean wetSlide)
	{
		this.block = block;
		this.meta = meta;
		this.hasPhys = hasPhys;
		this.minFall = minFall;
		this.maxFall = maxFall;
		this.supportDist = supportDist;
		this.dropID = dropID;
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
