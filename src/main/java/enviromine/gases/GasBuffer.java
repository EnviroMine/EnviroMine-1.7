package enviromine.gases;

import java.util.ArrayList;
import org.apache.logging.log4j.Level;
import enviromine.blocks.BlockGas;
import enviromine.core.EnviroMine;
import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class GasBuffer
{
	private static ArrayList<int[]> gasBuffer = new ArrayList<int[]>();
	private static ArrayList<int[]> fireBuffer = new ArrayList<int[]>();
	
	public static int gasRate = 15;
	public static int fireRate = 5;
	public static int curTick = 0;
	
	public static void add(World world, int x, int y, int z, boolean isFire)
	{
		if(world.isRemote)
		{
			EnviroMine.logger.log(Level.WARN, "Tried to register gas update in local world!");
			return;
		}
		
		int[] entry = new int[]{world.provider.dimensionId, x, y, z};
		
		if(isFire)
		{
			if(!fireBuffer.contains(entry))
			{
				fireBuffer.add(entry);
			}
		} else
		{
			if(!gasBuffer.contains(entry))
			{
				gasBuffer.add(entry);
			}
		}
	}
	
	public static void update()
	{
		curTick++;
		
		if(curTick > gasRate)
		{
			curTick = 0;
		}
		
		if(curTick == fireRate)
		{
			for(int i = fireBuffer.size() - 1; i >= 0; i--)
			{
				int[] entry = fireBuffer.get(i);
				World world = MinecraftServer.getServer().worldServerForDimension(entry[0]);
				
				if(world != null && world.getBlock(entry[1], entry[2], entry[3]) instanceof BlockGas)
				{
					Block block = world.getBlock(entry[1], entry[2], entry[3]);
					world.scheduleBlockUpdateWithPriority(entry[1], entry[2], entry[3], block, block.tickRate(world), 1);
				}
				
				fireBuffer.remove(i);
			}
		}
		
		if(curTick == gasRate)
		{
			for(int i = gasBuffer.size() - 1; i >= 0; i--)
			{
				int[] entry = gasBuffer.get(i);
				World world = MinecraftServer.getServer().worldServerForDimension(entry[0]);
				
				if(world != null && world.getBlock(entry[1], entry[2], entry[3]) instanceof BlockGas)
				{
					Block block = world.getBlock(entry[1], entry[2], entry[3]);
					world.scheduleBlockUpdateWithPriority(entry[1], entry[2], entry[3], block, block.tickRate(world), 1);
				}
				
				gasBuffer.remove(i);
			}
		}
	}
}
