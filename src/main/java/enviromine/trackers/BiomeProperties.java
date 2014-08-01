package enviromine.trackers;

public class BiomeProperties
{
	public int id;
	boolean biomeOveride;
	String waterQuality;
	double ambientTemp;
	double tempRate;
	double sanityRate;
	double dehydrateRate;
	
	public BiomeProperties(int id, boolean biomeOveride, String waterQuality, double ambientTemp, double tempRate, double sanityRate, double dehydrateRate)
	{
		this.id = id;
		this.biomeOveride = biomeOveride;
		this.waterQuality = waterQuality;
		this.ambientTemp = ambientTemp;
		this.tempRate = tempRate;
		this.sanityRate = sanityRate;
		this.dehydrateRate = dehydrateRate;
		
	}

}
