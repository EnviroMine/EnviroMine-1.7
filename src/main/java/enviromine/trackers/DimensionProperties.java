package enviromine.trackers;

public class DimensionProperties
{
	public int id;
	public boolean override;
	public boolean trackSanity;
	public boolean darkAffectSanity;
	public double sanityMultiplyer;
	public boolean trackAirQuality;
	public double airMulti;
	public boolean trackHydration;
	public double hydrationMulti;
	public boolean trackTemp;
	public double tempMulti;
	public boolean dayNightTemp;
	public boolean weatherAffectsTemp;
	public boolean mineshaftGen;
	public int sealevel;
	
	public DimensionProperties(int id, boolean override, boolean trackSanity, boolean darkAffectSanity, 	double sanityMultiplyer, boolean trackAirQuality, double airMulti, boolean trackHydration, double hydrationMulti, boolean trackTemp, double tempMulti, boolean dayNightTemp, boolean weatherAffectsTemp, boolean mineshaftGen, int sealevel)
	{
		this.id = id;
		this.override = override;
		this.trackSanity = trackSanity;
		this.darkAffectSanity = darkAffectSanity;
		this.sanityMultiplyer = sanityMultiplyer;
		this.trackAirQuality = trackAirQuality;
		this.airMulti = airMulti;
		this.trackHydration = trackHydration;
		this.hydrationMulti = hydrationMulti;
		this.trackTemp = trackTemp;
		this.tempMulti = tempMulti;
		this.dayNightTemp = dayNightTemp;
		this.weatherAffectsTemp = weatherAffectsTemp;
		this.mineshaftGen = mineshaftGen;
		this.sealevel = sealevel;
		
				
	}
}
