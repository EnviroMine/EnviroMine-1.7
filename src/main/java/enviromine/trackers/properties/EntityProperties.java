package enviromine.trackers.properties;

import net.minecraftforge.common.config.Configuration;
import enviromine.core.EM_Settings;

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
	
	/** Entity properties:<br>00 ({@link Int}) EntityID<br>01 ({@link Boolean}) Enable EnviroTracker<br>02 ({@link Boolean}) Enable Dehydration<br>03 ({@link Boolean}) Enable BodyTemp<br>04 ({@link Boolean}) Enable Air Quality<br>05 ({@link Boolean}) Immune To Frost<br>06 ({@link Boolean}) Immune To Heat<br>07 ({@link Double}) Ambient Sanity<br>08 ({@link Double}) Hit Sanity<br>09 ({@link Double}) Ambient Temperature<br>10 ({@link Double}) Hit Temperature<br>11 ({@link Double}) Ambient Air<br>12 ({@link Double}) Hit Air<br>13 ({@link Double}) Ambient Hydration<br>14 ({@link Double}) Hit Hydration */
	static String[] EPName;
	
	public static String categoryName = "entity";
	
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
	
	/**Set up config names for Entity properties:<br>00 ({@link Int}) EntityID<br>01 ({@link Boolean}) Enable EnviroTracker<br>02 ({@link Boolean}) Enable Dehydration<br>03 ({@link Boolean}) Enable BodyTemp<br>04 ({@link Boolean}) Enable Air Quality<br>05 ({@link Boolean}) Immune To Frost<br>06 ({@link Boolean}) Immune To Heat<br>07 ({@link Double}) Ambient Sanity<br>08 ({@link Double}) Hit Sanity<br>09 ({@link Double}) Ambient Temperature<br>10 ({@link Double}) Hit Temperature<br>11 ({@link Double}) Ambient Air<br>12 ({@link Double}) Hit Air<br>13 ({@link Double}) Ambient Hydration<br>14 ({@link Double}) Hit Hydration */
	public static void setConfigNames()
	{
		EPName = new String[15];
		EPName[0] = "01.Entity ID";
		EPName[1] = "02.Enable EnviroTracker";
		EPName[2] = "03.Enable Dehydration";
		EPName[3] = "04.Enable BodyTemp";
		EPName[4] = "05.Enable Air Quality";
		EPName[5] = "06.Immune To Frost";
		EPName[6] = "07.Immune To Heat";
		EPName[7] = "08.Ambient Sanity";
		EPName[8] = "09.Hit Sanity";
		EPName[9] = "10.Ambient Temperature";
		EPName[10] = "11.Hit Temperature";
		EPName[11] = "12.Ambient Air";
		EPName[12] = "13.Hit Air";
		EPName[13] = "14.Ambient Hydration";
		EPName[14] = "15.Hit Hydration";
	}
	
	
	public static void LoadProperty(Configuration config, String catagory)
	{
		config.addCustomCategoryComment(catagory, "");
		int id = config.get(catagory, EPName[0], 0).getInt(0);
		boolean track = config.get(catagory, EPName[1], true).getBoolean(true);
		boolean dehydration = config.get(catagory, EPName[2], true).getBoolean(true);
		boolean bodyTemp = config.get(catagory, EPName[3], true).getBoolean(true);
		boolean airQ = config.get(catagory, EPName[4], true).getBoolean(true);
		boolean immuneToFrost = config.get(catagory, EPName[5], false).getBoolean(false);
		boolean immuneToHeat = config.get(catagory, EPName[6], false).getBoolean(false);
		float aSanity = (float)config.get(catagory, EPName[7], 0.0D).getDouble(0.0D);
		float hSanity = (float)config.get(catagory, EPName[8], 0.0D).getDouble(0.0D);
		float aTemp = (float)config.get(catagory, EPName[9], 37.0D, "Overridden by body temp").getDouble(37.0D);
		float hTemp = (float)config.get(catagory, EPName[10], 0.0D).getDouble(0.0D);
		float aAir = (float)config.get(catagory, EPName[11], 0.0D).getDouble(0.0D);
		float hAir = (float)config.get(catagory, EPName[12], 0.0D).getDouble(0.0D);
		float aHyd = (float)config.get(catagory, EPName[13], 0.0D).getDouble(0.0D);
		float hHyd = (float)config.get(catagory, EPName[14], 0.0D).getDouble(0.0D);
		
		EntityProperties entry = new EntityProperties(id, track, dehydration, bodyTemp, airQ, immuneToFrost, immuneToHeat, aSanity, hSanity, aTemp, hTemp, aAir, hAir, aHyd, hHyd);
		EM_Settings.livingProperties.put(id, entry);
	}
	
	public static void SaveProperty(Configuration config, String catName, int id, boolean track, boolean dehydration, boolean bodyTemp, boolean airQ, boolean immuneToFrost, boolean immuneToHeat, double aSanity, double hSanity, double aTemp, double hTemp, double aAir, double hAir, double aHyd, double hHyd)
	{
		config.get(catName, EPName[0], id).getInt(id);
		config.get(catName, EPName[1], track).getBoolean(track);
		config.get(catName, EPName[2], dehydration).getBoolean(dehydration);
		config.get(catName, EPName[3], bodyTemp).getBoolean(bodyTemp);
		config.get(catName, EPName[4], airQ).getBoolean(airQ);
		config.get(catName, EPName[5], immuneToFrost).getBoolean(immuneToFrost);
		config.get(catName, EPName[6], immuneToHeat).getBoolean(immuneToHeat);
		config.get(catName, EPName[7], aSanity).getDouble(aSanity);
		config.get(catName, EPName[8], hSanity).getDouble(hSanity);
		config.get(catName, EPName[9], aTemp, "Overridden by body temp").getDouble(aTemp);
		config.get(catName, EPName[10], hTemp).getDouble(hTemp);
		config.get(catName, EPName[11], aAir).getDouble(aAir);
		config.get(catName, EPName[12], hAir).getDouble(hAir);
		config.get(catName, EPName[13], aHyd).getDouble(aHyd);
		config.get(catName, EPName[14], hHyd).getDouble(hHyd);
	}
	
	public static void SaveDefaults(Configuration configFile)
	{
		SaveProperty(configFile, categoryName + ".blaze",		61, false, false, false, false, true, true, -0.01, 0.0, 75.0, 0.1, -0.05, 0.0, -0.01, -0.01);
		SaveProperty(configFile, categoryName + ".wither", 	64,	false, false, false, false, true, true, -0.1, -0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	}

}
