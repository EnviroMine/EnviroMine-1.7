package enviromine_temp;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import enviromine.core.api.EnviroProperty;
import enviromine.core.managers.PropertyManager.PropertyTracker;

public class PropertyTemperature extends EnviroProperty
{
	public static final int range = 5;
	
	public int index = 0;
	public int bodyTemp = 37;
	/**
	 * Cached array of temperatures.
	 */
	public float[] temps = new float[(int)Math.pow(range * 2, 3)];
	public int[] lastPos;
	
	public PropertyTemperature()
	{
	}
	
	@Override
	public int TickInterval()
	{
		return 0;
	}
	
	@Override
	public boolean RequiresSync()
	{
		return true;
	}
	
	@Override
	public void Update(PropertyTracker tracker)
	{
		if(lastPos == null)
		{
			lastPos = new int[3];
			lastPos[0] = MathHelper.floor_double(tracker.trackedEntity.posX);
			lastPos[1] = MathHelper.floor_double(tracker.trackedEntity.posY);
			lastPos[2] = MathHelper.floor_double(tracker.trackedEntity.posZ);
		}
		
		BiomeGenBase biome = tracker.trackedEntity.worldObj.getBiomeGenForCoords(lastPos[0], lastPos[1]);
		float bTemp = biome.getFloatTemperature(lastPos[0], lastPos[1], lastPos[2]); // Biome temperature accounting for height
		
		if(index <= (range*2 * range*2))
		{
			for(int i = 0; i < range*2; i++) // We do about 10 at a time to keep processing low
			{
				int x = i;
				int y = index%(range*2);
				int z = index/(range*2);
				
				int tempIndex = (z * (range*2 * range*2) + y * (range*2) + x);
				
				Block block = tracker.trackedEntity.worldObj.getBlock(lastPos[0] + x - range, lastPos[1] + y - range, lastPos[2] + z - range);
				
				if(block == null || block == Blocks.air)
				{
					this.temps[tempIndex] = bTemp; // There is no block here so add biome temperature instead
				}
				
				this.temps[tempIndex] = 37; // Replace with block specific temperature accounting for distance
			}
			
			index++;
			return;
		} else
		{
			index = 0;
		}
		
		float tmpAvg = 0;
		
		for(float temp : this.temps)
		{
			tmpAvg += temp;
		}
		
		tmpAvg = tmpAvg/this.temps.length;
		
		// Do stuff with the average temperature
	}
	
	@Override
	public void Reset()
	{
		this.bodyTemp = 37;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger("Temperature", this.bodyTemp);
		return null;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		if(tag.hasKey("Temperature"))
		{
			this.bodyTemp = tag.getInteger("Temperature");
		}
	}
	
}
