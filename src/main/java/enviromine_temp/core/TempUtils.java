package enviromine_temp.core;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class TempUtils
{
	/**
	 * Used for generating default temperature values for blocks</br>
	 * <b>DO NOT</b> use for general purposes!
	 */
	public static float GetBlockTemp(Block block, int meta)
	{
		Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
		
		if(fluid != null)
		{
			return fluid.getTemperature() - 273.5F; // Kelvin to Celcius
		} else if(block.getMaterial() == Material.lava)
		{
			return 1026.85F; // Temperature of lava according to the fluid registry
		} else if(block.getMaterial() == Material.fire)
		{
			return 100F;
		} else if(block.getMaterial() == Material.ice || block.getMaterial() == Material.packedIce)
		{
			return -10F;
		} else if(block.getMaterial() == Material.snow || block.getMaterial() == Material.craftedSnow)
		{
			return -5F;
		} else if(block.getMaterial() == Material.cloth)
		{
			return 25F;
		} else if(block == Blocks.netherrack || block == Blocks.nether_brick || block == Blocks.nether_brick_fence || block == Blocks.nether_brick_stairs)
		{
			return 30F; // Fairly warm material
		} else if(block == Blocks.lit_pumpkin || block == Blocks.lit_furnace || block == Blocks.torch)
		{
			return 50F; // Hot but not dangerous in small amounts
		}
		
		return 25F; // Default temperature of a block
	}
	
	public static float GetBiomeTemp(BiomeGenBase biome, int x, int y, int z)
	{
		// You can calibrate temperatures using these
		// This does not take into account the time of day (These are the midday maximums)
		float maxTemp = 45F; // Desert
		float minTemp = -15F;
		float bTemp = biome.getFloatTemperature(x, y, z);
		
		// CALCULATE!
		return bTemp >= 0? (float)Math.sin(Math.toRadians(bTemp*45F))*maxTemp : (float)Math.sin(Math.toRadians(bTemp*45F))*minTemp;
	}
	
	public static float GetTempFalloff(float temp, float dist, int range)
	{
		float maximum = (float)Math.sqrt(3*(Math.pow(range, 2)));
		
		if(dist > maximum)
		{
			return 0;
		} else
		{
			return (float)((temp/Math.pow(maximum, 2)) * -Math.pow(dist, 2) + temp);
		}
	}
}
