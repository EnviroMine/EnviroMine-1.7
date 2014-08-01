package enviromine.trackers;

public class DimensionProperties
{
	public int id;
	public boolean override;
	public boolean sanity;
	public boolean darkAffectSanity;
	public double sanityMultiplyer;
	public boolean air;
	public double airMulti;
	public boolean water;
	public double waterMulti;
	public boolean temp;
	public double tempMulti;
	public boolean dayNightTemp;
	public boolean weatherAffectsTemp;
	public boolean mineshaftGen;
	public int sealevel;
	
	public DimensionProperties(int id, boolean override, boolean sanity, boolean darkAffectSanity, 	double sanityMultiplyer, boolean air, double airMulti, boolean water, double waterMulti, boolean temp, double tempMulti, boolean dayNightTemp, boolean weatherAffectsTemp, boolean mineshaftGen, int sealevel)
	{
		this.id = id;
		this.override = override;
		this.sanity = sanity;
		this.darkAffectSanity = darkAffectSanity;
		this.sanityMultiplyer = sanityMultiplyer;
		this.air = air;
		this.airMulti = airMulti;
		this.water = water;
		this.waterMulti = waterMulti;
		this.temp = temp;
		this.tempMulti = tempMulti;
		this.dayNightTemp = dayNightTemp;
		this.weatherAffectsTemp = weatherAffectsTemp;
		this.mineshaftGen = mineshaftGen;
		this.sealevel = sealevel;
		
				
	}
}
