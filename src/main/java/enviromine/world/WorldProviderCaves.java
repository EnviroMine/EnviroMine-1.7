package enviromine.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.world.chunk.ChunkProviderCaves;
import enviromine.world.chunk.WorldChunkManagerCaves;
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
		this.isHellWorld = true;
		this.hasNoSky = true;
		this.dimensionId = EM_Settings.caveDimID;
	}
	
	@SideOnly(Side.CLIENT)
	
	/**
	 * Return Vec3D with biome specific fog color
	 */
	@Override
	public Vec3 getFogColor(float par1, float par2)
	{
		//return this.worldObj.getWorldVec3Pool().getVecFromPool(0.20000000298023224D, 0.029999999329447746D, 0.029999999329447746D);
		return Vec3.createVectorHelper(0D, 0D, 0D);
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
		return new ChunkProviderCaves(this.worldObj, this.worldObj.getSeed());
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
		return false;
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
