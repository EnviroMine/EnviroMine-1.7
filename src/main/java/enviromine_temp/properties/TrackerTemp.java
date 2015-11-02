package enviromine_temp.properties;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;
import enviromine.core.api.config.def.ConfigKeyBlock;
import enviromine.core.api.helpers.IPropScanner;
import enviromine.core.api.properties.PropertyTracker;
import enviromine.core.api.properties.PropertyType;
import enviromine_temp.config.AMTemperature;
import enviromine_temp.config.AttributeTemperature;
import enviromine_temp.core.TempUtils;

public class TrackerTemp extends PropertyTracker implements IPropScanner
{
	/**
	 * Player's body temperature in Celcius
	 */
	public float skinTemp = 32F;
	public float coreTemp = 37F;
    public int rateOfChange = 100;

    
    
	//public float bodyTemp = 37F;
	/**
	 * Cached for GUI
	 */
	public float changeRate = 0F;
	public float[] blockTemps; // Temperatures from blocks
	public ArrayList<Float> auxTemps; // Temperatures from auxiliary sources (Items/Entities)
	
	public TrackerTemp(PropertyType type, EntityLivingBase entityLiving)
	{
		super(type, entityLiving);
		blockTemps = new float[(int)Math.pow(this.ScanDiameter(), 3)];
		Arrays.fill(blockTemps, 37F);
	}
	
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		
		if(this.entityLiving.ticksExisted%20 == 0)
		{
			
			float airTemp = GetAirTemp();			
			
	        // New Temperature Code // Give Credit to Fokson
			float skinAirDiff = skinTemp - (airTemp + 12);
	        float coreSkinDiff = coreTemp - (skinTemp + 5);
	        float thermCoreDiff = 37.0F - coreTemp;

	        skinTemp = (skinTemp - (skinAirDiff / (3 * rateOfChange))) + (coreSkinDiff / rateOfChange);
	        coreTemp = (coreTemp - (coreSkinDiff / (3 * rateOfChange))) + (thermCoreDiff / rateOfChange);

	        /*
	            Air temperature is determined by surroundings.
	            Skin temperature drifts towards the average between air and core temp, biased towards core.
	            Core temperature drifts towards the average between skin temp and 37c, biased towards 37c.
	            At 20c air temperature (room temperature), skin temp will settle on 32c and core temp will settle on 37c.
	            rateOfChange determines how fast everything moves; right now it's constant but you can work in the config option.
	        */

	        //////////////////////////
	        

	// OLD CODE		
//			float relTemp = airTemp + 12F; // Offset temperature of air to body to maintain (25C Air = 37C Body)
//			float diff = relTemp - bodyTemp;
//			float speed = Math.abs(diff)/10F * Math.signum(diff) * 0.01F;// Temp loss/gain rate
//			float prevTemp = bodyTemp;
//			bodyTemp += speed;
//			changeRate = bodyTemp - prevTemp;
		}
	}
	
	@Override
	public int ScanDiameter()
	{
		return 10;
	}
	
	@Override
	public int ScansPerTick()
	{
		return 10;
	}

	@Override
	public void DoScan(int x, int y, int z, int pass)
	{
		if(entityLiving == null || !entityLiving.isEntityAlive())
		{
			return;
		}
		
		Block block = entityLiving.worldObj.getBlock(x, y, z);
		int meta = entityLiving.worldObj.getBlockMetadata(x, y, z);
		BiomeGenBase biome = entityLiving.worldObj.getBiomeGenForCoords(x, z);
		float temp = TempUtils.GetBiomeTemp(biome, x, y, z);
		
		if(block.getMaterial() != Material.air)
		{
			// Get temperature attribute
			// If not found, use biome temperature
			AttributeTemperature attribute = (AttributeTemperature)AMTemperature.instance.getAttribute(new ConfigKeyBlock(block, new int[]{meta}));
			
			if(attribute != null)
			{
				// Distance falloff
				temp = temp + TempUtils.GetTempFalloff(attribute.ambTemp - temp, (float)entityLiving.getDistance(x, y, z), ScanDiameter());
			}
		}
		
		blockTemps[pass%blockTemps.length] = temp;
	}
	
	public float GetAirTemp()
	{
		float temp = 0F;
		
		for(float t : blockTemps)
		{
			temp += t;
		}
		
		return temp/(float)blockTemps.length;
	}
	
	@Override
	public void saveNBT(NBTTagCompound compound)
	{
		compound.setFloat("coreTemp", coreTemp);
		compound.setFloat("skinTemp", skinTemp);
	}
	
	@Override
	public void loadNBT(NBTTagCompound compound)
	{
		//bodyTemp = compound.hasKey("coreTemp")? compound.getFloat("coreTemp") : 37F;
		//skinTemp = compound.hasKey("skinTemp")? compound.getFloat("skinTemp") : 32F;
	}
}
