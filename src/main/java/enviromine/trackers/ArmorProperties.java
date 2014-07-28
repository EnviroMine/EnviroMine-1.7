package enviromine.trackers;

public class ArmorProperties
{
	public int id;
	public float nightTemp;
	public float shadeTemp;
	public float sunTemp;
	public float nightMult;
	public float shadeMult;
	public float sunMult;
	public float sanity;
	public float air;
	
	public ArmorProperties(int id, float nightTemp, float shadeTemp, float sunTemp, float nightMult, float shadeMult, float sunMult, float sanity, float air)
	{
		this.id = id;
		this.nightTemp = nightTemp;
		this.shadeTemp = shadeTemp;
		this.sunTemp = sunTemp;
		this.nightMult = nightMult;
		this.shadeMult = shadeMult;
		this.sunMult = sunMult;
		this.sanity = sanity;
		this.air = air;
	}
}
