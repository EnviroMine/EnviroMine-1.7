package enviromine.handlers;

import java.util.ArrayList;
import enviromine.core.EM_Settings;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class TorchReplaceHandler
{
	/**
	 * Contains a list of torch positions waiting to be converted:
	 * @Entry_Format int[4] { DimensionID, x, y, z }
	 */
	static ArrayList<int[]> posBuffer = new ArrayList<int[]>();
	
	static int tick = 0;
	
	public static void UpdatePass()
	{
		if(tick < 60)
		{
			tick++;
			return;
		} else
		{
			tick = 0;
		}
		
		if(!EM_Settings.torchesBurn)
		{
			posBuffer.clear();
		} else
		{
			for(int i = posBuffer.size() - 1 > 100? 100 : posBuffer.size() - 1; i >= 0; i--)
			{
				System.out.println("Replacing...");
				int[] entry = posBuffer.get(i);
				World world = MinecraftServer.getServer().worldServerForDimension(entry[0]);
				
				if(world.getBlock(entry[1], entry[2], entry[3]) == Blocks.torch)
				{
					world.setBlock(entry[1], entry[2], entry[3], ObjectHandler.fireTorch, world.getBlockMetadata(entry[1], entry[2], entry[3]), 2);
				}
				
				posBuffer.remove(i);
			}
		}
	}
	
	public static void ScheduleReplacement(World world, int x, int y, int z)
	{
		if(world == null || world.isRemote)
		{
			return;
		}
		
		int[] entry = new int[4];
		
		entry[0] = world.provider.dimensionId;
		entry[1] = x;
		entry[2] = y;
		entry[3] = z;
		
		posBuffer.add(entry);
	}
}
