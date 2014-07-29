package enviromine.trackers;

public class EntityProperties
{
	public int id;
	public boolean shouldTrack;
	public boolean dehydration;
	public boolean bodyTemp;
	public boolean airQ;
	public boolean immuneToFrost;
	public boolean immuneToHeat;
	public float ambSanity;
	public float hitSanity;
	public float ambTemp;
	public float hitTemp;
	public float ambAir;
	public float hitAir;
	public float ambHydration;
	public float hitHydration;
	
	public EntityProperties(int id, boolean track, boolean dehydration, boolean bodyTemp, boolean airQ, boolean immuneToFrost, boolean immuneToHeat, float aSanity, float hSanity, float aTemp, float hTemp, float aAir, float hAir, float aHyd, float hHyd)
	{
		this.id = id;
		this.shouldTrack = track;
		this.dehydration = dehydration;
		this.bodyTemp = bodyTemp;
		this.airQ = airQ;
		this.immuneToFrost = immuneToFrost;
		this.immuneToHeat = immuneToHeat;
		this.ambSanity = aSanity;
		this.hitSanity = hSanity;
		this.ambTemp = aTemp;
		this.hitTemp = hTemp;
		this.ambAir = aAir;
		this.hitAir = hAir;
		this.ambHydration = aHyd;
		this.hitHydration = hHyd;
	}
}
