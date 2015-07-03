package enviromine_temp.properties;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import enviromine.core.api.helpers.IPropScanner;
import enviromine.core.api.properties.PropertyTracker;
import enviromine.core.api.properties.PropertyType;

public class TrackerTemp extends PropertyTracker implements IPropScanner
{
	/**
	 * Player's body temperature in Celcius
	 */
	public float bodyTemp = 37F;
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
	}
	
	@Override
	public int ScanDiameter()
	{
		return 10;
	}
	
	@Override
	public int ScansPerTick()
	{
		return 25;
	}

	@Override
	public void DoScan(int x, int y, int z, int pass)
	{
		if(entityLiving == null || !entityLiving.isEntityAlive())
		{
			return;
		}
		
		Block block = entityLiving.worldObj.getBlock(x, y, z);
		float temp = 37F;
		
		if(block.getMaterial() == Material.air)
		{
			
		} else
		{
			// Get temperature attribute here
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
		compound.setFloat("bodyTemp", bodyTemp);
	}
	
	@Override
	public void loadNBT(NBTTagCompound compound)
	{
		bodyTemp = compound.hasKey("bodyTemp")? compound.getFloat("bodyTemp") : 37F;
	}
}
