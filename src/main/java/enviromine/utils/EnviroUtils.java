package enviromine.utils;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.logging.log4j.Level;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;

public class EnviroUtils
{

	public static void extendPotionList()
	{
		int maxID = 32;
		
		if(EM_Settings.heatstrokePotionID >= maxID)
		{
			maxID = EM_Settings.heatstrokePotionID + 1;
		}
		
		if(EM_Settings.hypothermiaPotionID >= maxID)
		{
			maxID = EM_Settings.hypothermiaPotionID + 1;
		}
		
		if(EM_Settings.frostBitePotionID >= maxID)
		{
			maxID = EM_Settings.frostBitePotionID + 1;
		}
		
		if(EM_Settings.dehydratePotionID >= maxID)
		{
			maxID = EM_Settings.dehydratePotionID + 1;
		}
		
		if(EM_Settings.insanityPotionID >= maxID)
		{
			maxID = EM_Settings.insanityPotionID + 1;
		}
		
		if(Potion.potionTypes.length >= maxID)
		{
			return;
		}
		
		Potion[] potionTypes = null;
		
		for(Field f : Potion.class.getDeclaredFields())
		{
			f.setAccessible(true);
			
			try
			{
				if(f.getName().equals("potionTypes") || f.getName().equals("field_76425_a"))
				{
					Field modfield = Field.class.getDeclaredField("modifiers");
					modfield.setAccessible(true);
					modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);
					
					potionTypes = (Potion[])f.get(null);
					final Potion[] newPotionTypes = new Potion[maxID];
					System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
					f.set(null, newPotionTypes);
				}
			} catch(Exception e)
			{
				EnviroMine.logger.log(Level.ERROR, "Failed to extend potion list for EnviroMine!", e);
			}
		}
	}
	
	public static int[] getAdjacentBlockCoordsFromSide(int x, int y, int z, int side)
	{
		int[] coords = new int[3];
		coords[0] = x;
		coords[1] = y;
		coords[2] = z;
		
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		switch(dir)
		{
			case NORTH:
			{
				coords[2] -= 1;
				break;
			}
			case SOUTH:
			{
				coords[2] += 1;
				break;
			}
			case WEST:
			{
				coords[0] -= 1;
				break;
			}
			case EAST:
			{
				coords[0] += 1;
				break;
			}
			case UP:
			{
				coords[1] += 1;
				break;
			}
			case DOWN:
			{
				coords[1] -= 1;
				break;
			}
			case UNKNOWN:
			{
				break;
			}
		}
		
		return coords;
	}
	
	
	public static String replaceULN(String unlocalizedName)
	{
		String newName = unlocalizedName.replaceAll("\\.+", "\\_");
		return newName;
	}
	
	public static float convertToFarenheit(float num)
	{
		return convertToFarenheit(num, 2);
	}
	public static float convertToFarenheit(float num, int decimalPlace)
	{
		float newNum = (float) ((num * 1.8) + 32F);
		BigDecimal convert = new BigDecimal(Float.toString(newNum));
		convert.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		
		return convert.floatValue();
	}
	
	public static float convertToCelcius(float num)
	{
		return((num - 32F) * (5 / 9));
	}
	
	public static double getBiomeTemp(BiomeGenBase biome)
	{
		// You can calibrate temperatures using these
		// This does not take into account the time of day (These are the midday maximums)
		float maxTemp = 45F; // Desert
		float minTemp = -15F;
		
		// CALCULATE!
		return biome.temperature >= 0? Math.sin(Math.toRadians(biome.temperature*45F))*maxTemp : Math.sin(Math.toRadians(biome.temperature*45F))*minTemp;
	}
	
	/*
	 * This isn't accurate enough to 
	 */
	public static String getBiomeWater(BiomeGenBase biome)
	{
		int waterColour = biome.getWaterColorMultiplier();
		boolean looksBad = false;
		
		if(waterColour != 16777215)
		{
			Color bColor = new Color(waterColour);
			
			if(bColor.getRed() < 200 || bColor.getGreen() < 200 || bColor.getBlue() < 200)
			{
				looksBad = true;
			}
		}
		
		ArrayList<Type> typeList = new ArrayList<Type>();
		Type[] typeArray = BiomeDictionary.getTypesForBiome(biome);
		for(int i = 0; i < typeArray.length; i++)
		{
			typeList.add(typeArray[i]);
		}
		
		
		if(typeList.contains(Type.SWAMP) || typeList.contains(Type.JUNGLE) || typeList.contains(Type.DEAD) || typeList.contains(Type.WASTELAND) || looksBad)
		{
			return "dirty";
		} else if(typeList.contains(Type.OCEAN) || typeList.contains(Type.BEACH))
		{
			return "salty";
		} else if(typeList.contains(Type.SNOWY) || typeList.contains(Type.CONIFEROUS) || biome.temperature < 0F)
		{
			return "cold";
		} else
		{
			return "clean";
		}
	}
}
