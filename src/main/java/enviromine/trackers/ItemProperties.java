package enviromine.trackers;

public class ItemProperties
{
	public String name;
	public int meta;
	
	public boolean enableTemp;
	
	public float ambTemp;
	public float ambAir;
	public float ambSanity;
	
	public float effTemp;
	public float effAir;
	public float effSanity;
	public float effHydration;
	
	public float effTempCap;
	
	public ItemProperties(String name, int meta, boolean enableTemp, float ambTemp, float ambAir, float ambSanity, float effTemp, float effAir, float effSanity, float effHydration, float effTempCap)
	{
		this.name = name;
		this.meta = meta;
		this.enableTemp = enableTemp;
		
		this.ambTemp = ambTemp;
		this.ambAir = ambAir;
		this.ambSanity = ambSanity;
		
		this.effTemp = effTemp;
		this.effAir = effAir;
		this.effSanity = effSanity;
		this.effHydration = effHydration;
		
		this.effTempCap = effTempCap;
	}
}
