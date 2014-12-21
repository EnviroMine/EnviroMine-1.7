package enviromine.utils;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockGlowstone;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.logging.log4j.Level;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.StabilityType;

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
		return unlocalizedName.replaceAll("\\.+", "\\_");
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
	
	public static StabilityType getDefaultStabilityType(Block block)
	{
		StabilityType type = null;
		
		Material material = block.getMaterial();
		
		if(!(block instanceof BlockMobSpawner) && !(block instanceof BlockLadder) && !(block instanceof BlockWeb) && !(block instanceof BlockSign) && !(block instanceof BlockBed) && !(block instanceof BlockDoor) && !(block instanceof BlockAnvil) && !(block instanceof BlockGravel) && !(block instanceof BlockPortal) && !(block instanceof BlockEndPortal) && !(block == Blocks.end_stone) && !(block instanceof BlockEndPortalFrame) && !(block.getMaterial() == Material.vine) && !block.getMaterial().blocksMovement())
		{
			type = EM_Settings.stabilityTypes.get("none");
		} else if(block instanceof BlockGlowstone)
		{
			type = EM_Settings.stabilityTypes.get("glowstone");
		} else if(block instanceof BlockFalling)
		{
			type = EM_Settings.stabilityTypes.get("sand-like");
		} else if(material == Material.iron || material == Material.wood || block instanceof BlockObsidian || block == Blocks.stonebrick || block == Blocks.brick_block || block == Blocks.quartz_block)
		{
			type = EM_Settings.stabilityTypes.get("strong");
		} else if(material == Material.rock || material == Material.glass || material == Material.ice || block instanceof BlockLeavesBase)
		{
			type = EM_Settings.stabilityTypes.get("average");
		} else
		{
			type = EM_Settings.stabilityTypes.get("loose");
		}
		
		if(type == null)
		{
			EnviroMine.logger.log(Level.ERROR, "Block " + block.getUnlocalizedName() + " has a null StabilityType. Crash imminent!");
		}
		
		return type;
	}
}
