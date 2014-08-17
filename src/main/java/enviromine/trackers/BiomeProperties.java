package enviromine.trackers;

import net.minecraft.world.biome.BiomeGenBase;

public class BiomeProperties
{
	public int id;
	public boolean biomeOveride;
	public String waterQuality;
	public double ambientTemp;
	public double tempRate;
	public double sanityRate;
	public double dehydrateRate;
	
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

	
	public int getWaterQualityId()
	{
System.out.println(this.waterQuality);

		if(this.waterQuality.equalsIgnoreCase("dirty"))
		{
			return 1;
		} else if(this.waterQuality.equalsIgnoreCase("salty"))
		{
			return 2;
		} else if(this.waterQuality.equalsIgnoreCase("cold"))
		{
			return 3;
		} else 
		{
			return 0;
		}
		

	}
}
