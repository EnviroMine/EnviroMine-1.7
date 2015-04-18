package enviromine.trackers.properties;

import java.io.File;
import java.util.Iterator;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;

import enviromine.core.EM_ConfigHandler;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.helpers.PropertyBase;
import enviromine.trackers.properties.helpers.SerialisableProperty;
import enviromine.utils.EnviroUtils;
import enviromine.utils.ModIdentification;

public class EntityProperties implements SerialisableProperty, PropertyBase
{
	public static final EntityProperties base = new EntityProperties();
	static String[] EPName;
	
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
	public String loadedFrom;
	
	public EntityProperties(NBTTagCompound tags)
	{
		this.ReadFromNBT(tags);
	}
	
	public EntityProperties()
	{
		// THIS CONSTRUCTOR IS FOR STATIC PURPOSES ONLY!
		
		if(base != null && base != this)
		{
			throw new IllegalStateException();
		}
	}
	
	public EntityProperties(int id, boolean track, boolean dehydration, boolean bodyTemp, boolean airQ, boolean immuneToFrost, boolean immuneToHeat, float aSanity, float hSanity, float aTemp, float hTemp, float aAir, float hAir, float aHyd, float hHyd, String fileName)
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
		this.loadedFrom = fileName;
	}

	@Override
	public NBTTagCompound WriteToNBT()
	{
		NBTTagCompound tags = new NBTTagCompound();
		tags.setInteger("id", this.id);
		tags.setBoolean("shouldTrack", this.shouldTrack);
		tags.setBoolean("dehydration", this.dehydration);
		tags.setBoolean("bodyTemp", this.bodyTemp);
		tags.setBoolean("airQ", this.airQ);
		tags.setBoolean("immuneToFrost", this.immuneToFrost);
		tags.setBoolean("immuneToHeat", this.immuneToHeat);
		tags.setFloat("ambSanity", this.ambSanity);
		tags.setFloat("hitSanity", this.hitSanity);
		tags.setFloat("ambTemp", this.ambTemp);
		tags.setFloat("hitTemp", this.hitTemp);
		tags.setFloat("ambAir", this.ambAir);
		tags.setFloat("hitAir", this.hitAir);
		tags.setFloat("ambHydration", this.ambHydration);
		tags.setFloat("hitHydration", this.hitHydration);
		return tags;
	}

	@Override
	public void ReadFromNBT(NBTTagCompound tags)
	{
		this.id = tags.getInteger("id");
		this.shouldTrack = tags.getBoolean("shouldTrack");
		this.dehydration = tags.getBoolean("dehydration");
		this.bodyTemp = tags.getBoolean("bodyTemp");
		this.airQ = tags.getBoolean("airQ");
		this.immuneToFrost = tags.getBoolean("immuneToFrost");
		this.immuneToHeat = tags.getBoolean("immuneToHeat");
		this.ambSanity = tags.getFloat("ambSanity");
		this.hitSanity = tags.getFloat("hitSanity");
		this.ambTemp = tags.getFloat("ambTemp");
		this.hitTemp = tags.getFloat("hitTemp");
		this.ambAir = tags.getFloat("ambAir");
		this.hitAir = tags.getFloat("hitAir");
		this.ambHydration = tags.getFloat("ambHydration");
		this.hitHydration = tags.getFloat("hitHydration");
	}

	@Override
	public String categoryName()
	{
		return "entity";
	}

	@Override
	public String categoryDescription()
	{
		return "Custom properties for entities";
	}

	@Override
	public void LoadProperty(Configuration config, String category)
	{
		config.addCustomCategoryComment(this.categoryName(), this.categoryDescription());
		int id = config.get(category, EPName[0], 0).getInt(0);
		boolean track = config.get(category, EPName[1], true).getBoolean(true);
		boolean dehydration = config.get(category, EPName[2], true).getBoolean(true);
		boolean bodyTemp = config.get(category, EPName[3], true).getBoolean(true);
		boolean airQ = config.get(category, EPName[4], true).getBoolean(true);
		boolean immuneToFrost = config.get(category, EPName[5], false).getBoolean(false);
		boolean immuneToHeat = config.get(category, EPName[6], false).getBoolean(false);
		float aSanity = (float)config.get(category, EPName[7], 0.0D).getDouble(0.0D);
		float hSanity = (float)config.get(category, EPName[8], 0.0D).getDouble(0.0D);
		float aTemp = (float)config.get(category, EPName[9], 37.0D, "Overridden by body temp").getDouble(37.0D);
		float hTemp = (float)config.get(category, EPName[10], 0.0D).getDouble(0.0D);
		float aAir = (float)config.get(category, EPName[11], 0.0D).getDouble(0.0D);
		float hAir = (float)config.get(category, EPName[12], 0.0D).getDouble(0.0D);
		float aHyd = (float)config.get(category, EPName[13], 0.0D).getDouble(0.0D);
		float hHyd = (float)config.get(category, EPName[14], 0.0D).getDouble(0.0D);
		String filename = config.getConfigFile().getName();
		
		EntityProperties entry = new EntityProperties(id, track, dehydration, bodyTemp, airQ, immuneToFrost, immuneToHeat, aSanity, hSanity, aTemp, hTemp, aAir, hAir, aHyd, hHyd, filename);
		
		// If item already exist and current file hasn't completely been loaded do this
		if(EM_Settings.livingProperties.containsKey(id) && !EM_ConfigHandler.loadedConfigs.contains(filename)) EnviroMine.logger.log(Level.ERROR, "CONFIG DUPLICATE: Entity ID "+ id +" was already added from "+ EM_Settings.livingProperties.get(id).loadedFrom.toUpperCase() +" and will be overriden by "+ filename.toUpperCase());

		
		EM_Settings.livingProperties.put(id, entry);
	}

	@Override
	public void SaveProperty(Configuration config, String category)
	{
		config.get(category, EPName[0], id).getInt(id);
		config.get(category, EPName[1], shouldTrack).getBoolean(shouldTrack);
		config.get(category, EPName[2], dehydration).getBoolean(dehydration);
		config.get(category, EPName[3], bodyTemp).getBoolean(bodyTemp);
		config.get(category, EPName[4], airQ).getBoolean(airQ);
		config.get(category, EPName[5], immuneToFrost).getBoolean(immuneToFrost);
		config.get(category, EPName[6], immuneToHeat).getBoolean(immuneToHeat);
		config.get(category, EPName[7], ambSanity).getDouble(ambSanity);
		config.get(category, EPName[8], hitSanity).getDouble(hitSanity);
		config.get(category, EPName[9], ambTemp, "Overridden by body temp").getDouble(ambTemp);
		config.get(category, EPName[10], hitTemp).getDouble(hitTemp);
		config.get(category, EPName[11], ambAir).getDouble(ambAir);
		config.get(category, EPName[12], hitAir).getDouble(hitAir);
		config.get(category, EPName[13], ambHydration).getDouble(ambHydration);
		config.get(category, EPName[14], hitHydration).getDouble(hitHydration);
	}

	@Override
	public void GenDefaults()
	{
		@SuppressWarnings("unchecked")
		Iterator<Integer> iterator = EntityList.IDtoClassMapping.keySet().iterator();
		
		while(iterator.hasNext())
		{
			int eID = (Integer)iterator.next();
			Class<?> clazz = (Class<?>)EntityList.IDtoClassMapping.get(eID);
			
			if(clazz == null || !EntityLivingBase.class.isAssignableFrom(clazz))
			{
				continue;
			}
			
			String modID = ModIdentification.idFromObject(clazz);
			String eName = EntityList.getStringFromID(eID);
			
			File file = new File(EM_ConfigHandler.loadedProfile + EM_ConfigHandler.customPath + EnviroUtils.SafeFilename(modID) + ".cfg");
			
			if(!file.exists())
			{
				try
				{
					file.createNewFile();
				} catch(Exception e)
				{
					EnviroMine.logger.log(Level.ERROR, "Failed to create file for default entities", e);
					continue;
				}
			}
			
			String catName = this.categoryName() + "." + eName;
			
			Configuration config = new Configuration(file, true);
			
			config.load();
			
			if(eID == 65) // Bat
			{
				config.get(catName, EPName[0], eID).getInt(eID);
				config.get(catName, EPName[1], false).getBoolean(false);
				config.get(catName, EPName[2], false).getBoolean(false);
				config.get(catName, EPName[3], false).getBoolean(false);
				config.get(catName, EPName[4], false).getBoolean(false);
				config.get(catName, EPName[5], true).getBoolean(true);
				config.get(catName, EPName[6], true).getBoolean(true);
				config.get(catName, EPName[7], -0.05D).getDouble(-0.05D);
				config.get(catName, EPName[8], 0D).getDouble(0D);
				config.get(catName, EPName[9], 37D, "Overridden by body temp").getDouble(37D);
				config.get(catName, EPName[10], 0D).getDouble(0D);
				config.get(catName, EPName[11], 0D).getDouble(0D);
				config.get(catName, EPName[12], 0D).getDouble(0D);
				config.get(catName, EPName[13], 0D).getDouble(0D);
				config.get(catName, EPName[14], 0D).getDouble(0D);
			} else if(eID == 54) // Zombie
			{
				config.get(catName, EPName[0], eID).getInt(eID);
				config.get(catName, EPName[1], false).getBoolean(false);
				config.get(catName, EPName[2], false).getBoolean(false);
				config.get(catName, EPName[3], false).getBoolean(false);
				config.get(catName, EPName[4], false).getBoolean(false);
				config.get(catName, EPName[5], true).getBoolean(true);
				config.get(catName, EPName[6], true).getBoolean(true);
				config.get(catName, EPName[7], -0.1D).getDouble(-0.1D);
				config.get(catName, EPName[8], -1D).getDouble(-1D);
				config.get(catName, EPName[9], 10D, "Overridden by body temp").getDouble(10D);
				config.get(catName, EPName[10], 0D).getDouble(0D);
				config.get(catName, EPName[11], 0D).getDouble(0D);
				config.get(catName, EPName[12], 0D).getDouble(0D);
				config.get(catName, EPName[13], 0D).getDouble(0D);
				config.get(catName, EPName[14], 0D).getDouble(0D);
			} else if(eID == 51) // Skeleton
			{
				config.get(catName, EPName[0], eID).getInt(eID);
				config.get(catName, EPName[1], false).getBoolean(false);
				config.get(catName, EPName[2], false).getBoolean(false);
				config.get(catName, EPName[3], false).getBoolean(false);
				config.get(catName, EPName[4], false).getBoolean(false);
				config.get(catName, EPName[5], true).getBoolean(true);
				config.get(catName, EPName[6], true).getBoolean(true);
				config.get(catName, EPName[7], -0.1D).getDouble(-0.1D);
				config.get(catName, EPName[8], -1D).getDouble(-1D);
				config.get(catName, EPName[9], 10D, "Overridden by body temp").getDouble(10D);
				config.get(catName, EPName[10], 0D).getDouble(0D);
				config.get(catName, EPName[11], 0D).getDouble(0D);
				config.get(catName, EPName[12], 0D).getDouble(0D);
				config.get(catName, EPName[13], 0D).getDouble(0D);
				config.get(catName, EPName[14], 0D).getDouble(0D);
			} else if(eID == 57) // Zombie Pigman
			{
				config.get(catName, EPName[0], eID).getInt(eID);
				config.get(catName, EPName[1], false).getBoolean(false);
				config.get(catName, EPName[2], false).getBoolean(false);
				config.get(catName, EPName[3], false).getBoolean(false);
				config.get(catName, EPName[4], false).getBoolean(false);
				config.get(catName, EPName[5], true).getBoolean(true);
				config.get(catName, EPName[6], true).getBoolean(true);
				config.get(catName, EPName[7], -0.1D).getDouble(-0.1D);
				config.get(catName, EPName[8], -1D).getDouble(-1D);
				config.get(catName, EPName[9], 10D, "Overridden by body temp").getDouble(10D);
				config.get(catName, EPName[10], 0D).getDouble(0D);
				config.get(catName, EPName[11], 0D).getDouble(0D);
				config.get(catName, EPName[12], 0D).getDouble(0D);
				config.get(catName, EPName[13], 0D).getDouble(0D);
				config.get(catName, EPName[14], 0D).getDouble(0D);
			} else if(eID == 58) // Enderman
			{
				config.get(catName, EPName[0], eID).getInt(eID);
				config.get(catName, EPName[1], false).getBoolean(false);
				config.get(catName, EPName[2], false).getBoolean(false);
				config.get(catName, EPName[3], false).getBoolean(false);
				config.get(catName, EPName[4], false).getBoolean(false);
				config.get(catName, EPName[5], true).getBoolean(true);
				config.get(catName, EPName[6], true).getBoolean(true);
				config.get(catName, EPName[7], -0.5D).getDouble(-0.5D);
				config.get(catName, EPName[8], -5D).getDouble(-5D);
				config.get(catName, EPName[9], 10D, "Overridden by body temp").getDouble(10D);
				config.get(catName, EPName[10], 0D).getDouble(0D);
				config.get(catName, EPName[11], 0D).getDouble(0D);
				config.get(catName, EPName[12], 0D).getDouble(0D);
				config.get(catName, EPName[13], 0D).getDouble(0D);
				config.get(catName, EPName[14], 0D).getDouble(0D);
			} else if(eID == 61) // Blaze
			{
				config.get(catName, EPName[0], eID).getInt(eID);
				config.get(catName, EPName[1], false).getBoolean(false);
				config.get(catName, EPName[2], false).getBoolean(false);
				config.get(catName, EPName[3], false).getBoolean(false);
				config.get(catName, EPName[4], false).getBoolean(false);
				config.get(catName, EPName[5], true).getBoolean(true);
				config.get(catName, EPName[6], true).getBoolean(true);
				config.get(catName, EPName[7], 0D).getDouble(0D);
				config.get(catName, EPName[8], 0D).getDouble(0D);
				config.get(catName, EPName[9], 100D, "Overridden by body temp").getDouble(100D);
				config.get(catName, EPName[10], 0.5D).getDouble(0.5D);
				config.get(catName, EPName[11], 0D).getDouble(0D);
				config.get(catName, EPName[12], 0D).getDouble(0D);
				config.get(catName, EPName[13], -0.1D).getDouble(-0.1D);
				config.get(catName, EPName[14], -1D).getDouble(-1D);
			} else if(eID == 97) // Snowman
			{
				config.get(catName, EPName[0], eID).getInt(eID);
				config.get(catName, EPName[1], false).getBoolean(false);
				config.get(catName, EPName[2], false).getBoolean(false);
				config.get(catName, EPName[3], false).getBoolean(false);
				config.get(catName, EPName[4], false).getBoolean(false);
				config.get(catName, EPName[5], true).getBoolean(true);
				config.get(catName, EPName[6], true).getBoolean(true);
				config.get(catName, EPName[7], 0D).getDouble(0D);
				config.get(catName, EPName[8], 0D).getDouble(0D);
				config.get(catName, EPName[9], -1D, "Overridden by body temp").getDouble(-1D);
				config.get(catName, EPName[10], -0.5D).getDouble(-0.5D);
				config.get(catName, EPName[11], 0D).getDouble(0D);
				config.get(catName, EPName[12], 0D).getDouble(0D);
				config.get(catName, EPName[13], 0D).getDouble(0D);
				config.get(catName, EPName[14], 0D).getDouble(0D);
			} else if(EM_Settings.genConfigs)
			{
				this.generateEmpty(config, eID);
			}
			
			config.save();
		}
	}

	@Override
	public File GetDefaultFile()
	{
		return new File(EM_ConfigHandler.loadedProfile + EM_ConfigHandler.customPath + "Entities.cfg");
	}

	@Override
	public void generateEmpty(Configuration config, Object obj)
	{
		if(obj == null || !(obj instanceof Integer))
		{
			EnviroMine.logger.log(Level.ERROR, "Tried to register config with non EntityLivingBase id!", new Exception());
			return;
		}
		
		int id = (Integer)obj;
		String category = this.categoryName() + "." + EntityList.getStringFromID(id);
		
		config.get(category, EPName[0], id).getInt(id);
		config.get(category, EPName[1], false).getBoolean(false);
		config.get(category, EPName[2], false).getBoolean(false);
		config.get(category, EPName[3], false).getBoolean(false);
		config.get(category, EPName[4], false).getBoolean(false);
		config.get(category, EPName[5], true).getBoolean(true);
		config.get(category, EPName[6], true).getBoolean(true);
		config.get(category, EPName[7], 0D).getDouble(0D);
		config.get(category, EPName[8], 0D).getDouble(hitSanity);
		config.get(category, EPName[9], 35D, "Overridden by body temp").getDouble(35D);
		config.get(category, EPName[10], 0D).getDouble(0D);
		config.get(category, EPName[11], 0D).getDouble(0D);
		config.get(category, EPName[12], 0D).getDouble(0D);
		config.get(category, EPName[13], 0D).getDouble(0D);
		config.get(category, EPName[14], 0D).getDouble(0D);
	}

	@Override
	public boolean useCustomConfigs()
	{
		return true;
	}

	@Override
	public void customLoad()
	{
	}
	
	static
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
}
