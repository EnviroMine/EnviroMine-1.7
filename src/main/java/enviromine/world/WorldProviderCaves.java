package enviromine.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.world.chunk.ChunkProviderCaves;
import enviromine.world.chunk.WorldChunkManagerCaves;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderCaves extends WorldProvider
{
	/**
	 * creates a new world chunk manager for WorldProvider
	 */
	@Override
	public void registerWorldChunkManager()
	{
		this.worldChunkMgr = new WorldChunkManagerCaves(EnviroMine.caves, 1.0F, 0.0F);
		this.isHellWorld = false;
		this.hasNoSky = true;
		this.dimensionId = EM_Settings.caveDimID;
	}
	
	/**
	 * Return Vec3D with biome specific fog color
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public Vec3 getFogColor(float par1, float par2)
	{
		if(Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.nightVision))
		{
			return Vec3.createVectorHelper(1D, 1D, 1D);
		} else
		{
			return Vec3.createVectorHelper(0D, 0D, 0D);
		}
	}
	
	/**
	 * Creates the light to brightness table
	 */
	@Override
	protected void generateLightBrightnessTable()
	{
		float f = 0.0F;
		
		for (int i = 0; i <= 15; ++i)
		{
			float f1 = 1.0F - (float)i / 15.0F;
			this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Returns a double value representing the Y value relative to the top of the map at which void fog is at its
	 * maximum. The default factor of 0.03125 relative to 256, for example, means the void fog will be at its maximum at
	 * (256*0.03125), or 8.
	 */
	public double getVoidFogYFactor()
	{
		return 1D;
	}
	
	/**
	 * Returns a new chunk provider which generates chunks for this world
	 */
	@Override
	public IChunkProvider createChunkGenerator()
	{
		long seed = this.worldObj.getSeed();
		File dimData = new File(EM_Settings.worldDir, "data/EnviroCaveData");
		File dimFolder = new File(EM_Settings.worldDir, "DIM" + EM_Settings.caveDimID + "/region");
		
		try
		{
			NBTTagCompound dataTags = new NBTTagCompound();
			
			if(!dimData.exists() || !dimFolder.exists())
			{
				if(!dimData.exists())
				{
					dimData.createNewFile();
				} else // This probably isn't necessary but I like to be sure it's reset completely
				{
					dimData.delete();
					dimData.createNewFile();
				}
				
				if(!dimFolder.exists()) // Checks if a cave map exists already. If not randomize the seed
				{
					seed = this.worldObj.rand.nextLong();
				}
				
				dataTags.setLong("CAVE_SEED", seed);
				
				FileOutputStream fos = new FileOutputStream(dimData);
				
				CompressedStreamTools.writeCompressed(dataTags, fos); // Saved seed to file
			} else
			{
				FileInputStream fis = new FileInputStream(dimData);
				
				dataTags = CompressedStreamTools.readCompressed(fis);
				
				if(dataTags != null && dataTags.hasKey("CAVE_SEED"))
				{
					seed = dataTags.getLong("CAVE_SEED");
				}
			}
		} catch(Exception e)
		{
			EnviroMine.logger.log(Level.ERROR, "Failed to save CaveDimension data to file: " + dimData.getAbsolutePath(), e);
		}
		
		return new ChunkProviderCaves(this.worldObj, seed);
	}
	
	/**
	 * Returns 'true' if in the "main surface world", but 'false' if in the Nether or End dimensions.
	 */
	@Override
	public boolean isSurfaceWorld()
	{
		return false;
	}
	
	/**
	 * Will check if the x, z position specified is alright to be set as the map spawn point
	 */
	@Override
	public boolean canCoordinateBeSpawn(int par1, int par2)
	{
		return false;
	}
	
	/**
	 * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
	 */
	@Override
	public float calculateCelestialAngle(long par1, float par3)
	{
		return 0.5F;
	}
	
	/**
	 * True if the player can respawn in this dimension (true = overworld, false = nether).
	 */
	@Override
	public boolean canRespawnHere()
	{
		return EM_Settings.caveRespawn;
	}
	
	/**
	 * Returns true if the given X,Z coordinate should show environmental fog.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public boolean doesXZShowFog(int par1, int par2)
	{
		return true;
	}
	
	/**
	 * Returns the dimension's name, e.g. "The End", "Nether", or "Overworld".
	 */
	@Override
	public String getDimensionName()
	{
		return "Caves";
	}
}
