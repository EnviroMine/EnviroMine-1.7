package enviromine.gases;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.logging.log4j.Level;
import com.google.common.base.Stopwatch;
import enviromine.blocks.BlockGas;
import enviromine.client.gui.EM_GuiEnviroMeters;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.handlers.EM_PhysManager;
import enviromine.handlers.ObjectHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

/**
 * @author Funwayguy
 *
 * This class is used to batch process the gas entity updates so as to sync up all their movements and allow for other processes to do work in between passes without interruption.
 */
public class GasBuffer
{
	static ArrayList<int[]> gasBuffer = new ArrayList<int[]>();
	static ArrayList<int[]> fireBuffer = new ArrayList<int[]>();
	static HashMap<String, Integer> chunkUpdates = new HashMap<String, Integer>();
	
	public static int curTick = 0;
	public static int debugInterval = 30;
	public static int debugTime = 0;
	public static int debugUpdatesCaptured = 0;
	private static Stopwatch timer = Stopwatch.createUnstarted();
	
	public static void reset()
	{
		gasBuffer.clear();
		fireBuffer.clear();
		curTick = 1;
	}
	
	public static int getChunkUpdates(int cx, int cz)
	{
		String key = cx + "," + cz;
		if(!chunkUpdates.containsKey(key))
		{
			return 0;
		} else
		{
			return chunkUpdates.get(key);
		}
	}
	
	public static void incrementUpdates(int cx, int cz)
	{
		String key = cx + "," + cz;
		if(!chunkUpdates.containsKey(key))
		{
			chunkUpdates.put(key, 1);
		} else
		{
			chunkUpdates.put(key, chunkUpdates.get(key) + 1);
		}
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
		
		/*int gasCutoff = gasBuffer.size() - 1;
		int fireCutoff = fireBuffer.size() - 1;
		
		if(EM_Settings.gasPassLimit < gasCutoff && EM_Settings.gasPassLimit > -1)
		{
			gasCutoff = EM_Settings.gasPassLimit;
		}
		
		if(EM_Settings.gasPassLimit/4 < fireCutoff && EM_Settings.gasPassLimit > -1)
		{
			fireCutoff = EM_Settings.gasPassLimit/4;
		}*/
		

		if(EnviroMine.proxy.isClient())
		{
			if(debugTime == 0)
			{
				if(!timer.isRunning())
				{
					timer.reset();
					timer.start();
				}
				debugUpdatesCaptured = 0;
			}
			
			if(Minecraft.getMinecraft().isIntegratedServerRunning())
			{
				if(Minecraft.getMinecraft().isGamePaused() && !EnviroMine.proxy.isOpenToLAN())
				{
					if(timer.isRunning())
					{
						timer.stop();
						debugTime = 0;
					}
				} else
				{
					if(!timer.isRunning())
					{
						timer.start();
					}
				}
			}
		}
		
		if(curTick%(EM_Settings.gasTickRate/4) == 0)
		{
			for(int i = 0; i < fireBuffer.size(); i++)
			{
				int[] entry = fireBuffer.get(i);
				
				World world = MinecraftServer.getServer().worldServerForDimension(entry[0]);
				
				if(world.getTotalWorldTime() < EM_PhysManager.worldStartTime + EM_Settings.worldDelay)
				{
					continue;
				} else if(EM_PhysManager.chunkDelay.containsKey("" + (entry[1] >> 4) + "," + (entry[3] >> 4)))
				{
					if(EM_PhysManager.chunkDelay.get("" + (entry[1] >> 4) + "," + (entry[3] >> 4)) > world.getTotalWorldTime())
					{
						continue;
					}
				}
				
				if(EM_Settings.gasPassLimit >= 0 && getChunkUpdates(entry[1] >> 4, entry[3] >> 4) >= EM_Settings.gasPassLimit/4)
				{
					continue;
				}
				
				if(world != null && world.getBlock(entry[1], entry[2], entry[3]) instanceof BlockGas)
				{
					Block block = world.getBlock(entry[1], entry[2], entry[3]);
					world.scheduleBlockUpdateWithPriority(entry[1], entry[2], entry[3], block, 1, 0);
					incrementUpdates(entry[1] >> 4, entry[3] >> 4);
				}
				
				fireBuffer.remove(i);
				i--; // Trust me this makes sense
				debugUpdatesCaptured++;
			}
		}
		
		if(curTick%EM_Settings.gasTickRate == 0)
		{
			for(int i = 0; i < gasBuffer.size(); i++)
			{
				int[] entry = gasBuffer.get(i);
				
				World world = MinecraftServer.getServer().worldServerForDimension(entry[0]);
				
				if(world.getTotalWorldTime() < EM_PhysManager.worldStartTime + EM_Settings.worldDelay)
				{
					continue;
				} else if(EM_PhysManager.chunkDelay.containsKey("" + (entry[1] >> 4) + "," + (entry[3] >> 4)))
				{
					if(EM_PhysManager.chunkDelay.get("" + (entry[1] >> 4) + "," + (entry[3] >> 4)) > world.getTotalWorldTime())
					{
						continue;
					}
				}
				
				if(EM_Settings.gasPassLimit >= 0 && getChunkUpdates(entry[1] >> 4, entry[3] >> 4) >= EM_Settings.gasPassLimit)
				{
					continue;
				}
				
				if(world != null && world.getChunkProvider().chunkExists(entry[1] >> 4, entry[3] >> 4) && world.getChunkFromBlockCoords(entry[1], entry[3]).isChunkLoaded && world.getBlock(entry[1], entry[2], entry[3]) instanceof BlockGas)
				{
					Block block = world.getBlock(entry[1], entry[2], entry[3]);
					world.scheduleBlockUpdateWithPriority(entry[1], entry[2], entry[3], block, 1, 0);
					incrementUpdates(entry[1] >> 4, entry[3] >> 4);
				}
				
				gasBuffer.remove(i);
				i--; // Trust me this makes sense
				debugUpdatesCaptured++;
			}
		}
		
		chunkUpdates.clear();
		
		if(EnviroMine.proxy.isClient() && debugTime >= debugInterval && timer.isRunning())
		{
			timer.stop();
			EM_GuiEnviroMeters.DB_gasTimer = timer.toString();
			EM_GuiEnviroMeters.DB_gasUpdates = debugUpdatesCaptured;
			EM_GuiEnviroMeters.DB_gasBuffer = gasBuffer.size();
			EM_GuiEnviroMeters.DB_gasBuffer = fireBuffer.size();
			timer.reset();
			debugTime = 0;
		} else if(EnviroMine.proxy.isClient())
		{
			debugTime += 1;
		}
	}
}
