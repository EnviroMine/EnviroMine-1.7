package enviromine.trackers.properties;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import enviromine.core.EM_Settings;

public class RotProperties implements SerialisableProperty
{
	public String name;
	public int meta;
	public String rotID;
	public int rotMeta;
	public int days;
	
	static String[] RPName;
	
	public static String categoryName = "spoiling";
	
	public RotProperties(NBTTagCompound tags)
	{
		this.ReadFromNBT(tags);
	}
	
	public RotProperties(String name, int meta, String rotID, int rotMeta, int days)
	{
		this.name = name;
		this.meta = meta;
		this.rotID = rotID;
		this.rotMeta = rotMeta;
		this.days = days;
	}
	
	public static void setConfigNames()
	{
		RPName = new String[5];
		RPName[0] = "01.ID";
		RPName[1] = "02.Damage";
		RPName[2] = "03.Rotten ID";
		RPName[3] = "04.Rotten Damage";
		RPName[4] = "05.Days To Rot";
	}
	
	public static void LoadProperty(Configuration config, String category)
	{
		config.addCustomCategoryComment(category, "");
		String name = config.get(category, RPName[0], "").getString();
		int meta = config.get(category, RPName[1], -1).getInt(-1);
		String rotID = config.get(category, RPName[2], "").getString();
		int rotMeta = config.get(category, RPName[3], 0).getInt(0);
		int DTR = config.get(category, RPName[4], 0, "Set this to -1 to disable rotting on this item").getInt(0);
		
		RotProperties entry = new RotProperties(name, meta, rotID, rotMeta, DTR);
		
		if(meta < 0)
		{
			EM_Settings.rotProperties.put("" + name, entry);
		} else
		{
			EM_Settings.rotProperties.put("" + name + "," + meta, entry);
		}
	}

	@Override
	public NBTTagCompound WriteToNBT()
	{
		NBTTagCompound tags = new NBTTagCompound();
		tags.setString("name", this.name);
		tags.setInteger("meta", this.meta);
		tags.setString("rotID", this.rotID);
		tags.setInteger("rotMeta", this.rotMeta);
		tags.setInteger("days", this.days);
		return tags;
	}

	@Override
	public void ReadFromNBT(NBTTagCompound tags)
	{
		this.name = tags.getString("name");
		this.meta = tags.getInteger("meta");
		this.rotID = tags.getString("rotID");
		this.rotMeta = tags.getInteger("rotMeta");
		this.days = tags.getInteger("days");
	}
}
