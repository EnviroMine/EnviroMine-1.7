package enviromine.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.core.EnviroMine;

public class EM_WorldData extends WorldSavedData {

	private static final String IDENTIFIER = "EM_WorldData";
	
	private String profile = "default";

	public static EM_WorldData theWorldEM;		
	
	public EM_WorldData() 
	{
		super(IDENTIFIER);
	}
	
	public EM_WorldData(String identifier) 
	{
		super(identifier);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		this.profile = nbt.getString("Profile");

	}

	public boolean setProfile(String newProfile)
	{
		this.profile = newProfile;
		this.markDirty();
		return true;
	}
	
	public String getProfile()
	{
		return profile;
	}

	public EM_WorldData getEMWorldData()
	{
		return theWorldEM;
	}
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
		nbt.setString("Profile", profile);
	}
	
	public static EM_WorldData get(World world) 
	{
		EM_WorldData data = (EM_WorldData)world.loadItemData(EM_WorldData.class, IDENTIFIER);
		
		if (data == null) 
		{
			data = new EM_WorldData();
			world.setItemData(IDENTIFIER, data);
			EnviroMine.logger.log(Level.ERROR, "Enviromine World Data Doesn't Exist. Creating now");
		}
		else EnviroMine.logger.log(Level.INFO, "Loading Enviromine World Data");
		
		data.markDirty();
		return data;
	}
}
