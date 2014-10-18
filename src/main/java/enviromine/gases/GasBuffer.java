package enviromine.gases;

import java.util.ArrayList;
import org.apache.logging.log4j.Level;
import enviromine.blocks.BlockGas;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.handlers.ObjectHandler;
import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

/**
 * @author Funwayguy
 *
 * This class is used to batch process the gas entity updates so as to sync up all their movements and allow for other processes to do work in between passes without interruption.
 */
public class GasBuffer
{
	private static ArrayList<int[]> gasBuffer = new ArrayList<int[]>();
	private static ArrayList<int[]> fireBuffer = new ArrayList<int[]>();
	
	public static int curTick = 0;
	
	public static void reset()
	{
		gasBuffer.clear();
		fireBuffer.clear();
		curTick = 1;
	}
	
	public static void scheduleUpdate(World world, int x, int y, int z, BlockGas block)
	{
		if(world.isRemote)
		{
			EnviroMine.logger.log(Level.WARN, "Tried to register gas update in local world!");
			return;
		}
		
		int[] entry = new int[]{world.provider.dimensionId, x, y, z};
		
		if(block == ObjectHandler.fireGasBlock)
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
		
		if(curTick > EM_Settings.gasTickRate)
		{
			curTick = 1;
		}
		
		int gasCutoff = gasBuffer.size() - 1;
		int fireCutoff = fireBuffer.size() - 1;
		
		if(EM_Settings.gasPassLimit < gasCutoff && EM_Settings.gasPassLimit > -1)
		{
			gasCutoff = EM_Settings.gasPassLimit;
		}
		
		if(EM_Settings.gasPassLimit < fireCutoff && EM_Settings.gasPassLimit > -1)
		{
			fireCutoff = EM_Settings.gasPassLimit;
		}
		
		if(curTick%(EM_Settings.gasTickRate/4) == 0)
		{
			for(int i = fireCutoff; i >= 0; i--)
			{
				int[] entry = fireBuffer.get(i);
				World world = MinecraftServer.getServer().worldServerForDimension(entry[0]);
				
				if(world != null && world.getBlock(entry[1], entry[2], entry[3]) instanceof BlockGas)
				{
					Block block = world.getBlock(entry[1], entry[2], entry[3]);
					world.scheduleBlockUpdateWithPriority(entry[1], entry[2], entry[3], block, 1, 1);
				}
				
				fireBuffer.remove(i);
			}
		}
		
		if(curTick%EM_Settings.gasTickRate == 0)
		{
			for(int i = gasCutoff; i >= 0; i--)
			{
				int[] entry = gasBuffer.get(i);
				World world = MinecraftServer.getServer().worldServerForDimension(entry[0]);
				
				if(world != null && world.getBlock(entry[1], entry[2], entry[3]) instanceof BlockGas)
				{
					Block block = world.getBlock(entry[1], entry[2], entry[3]);
					world.scheduleBlockUpdateWithPriority(entry[1], entry[2], entry[3], block, 1, 1);
				}
				
				gasBuffer.remove(i);
			}
		}
	}
}
