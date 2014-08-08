package enviromine.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ClientQuake extends Earthquake
{
	int dimensionID;
	
	public ClientQuake(int d, int i, int k, int l, int w, float a)
	{
		super(null, i, k, l, w);
		pendingQuakes.remove(this);
		clientQuakes.add(this);
		this.markRavine(a);
	}
	
	public static void UpdateQuakeHeight(int d, int x, int z, int l, int w, float a, int height)
	{
		for(int i = 0; i < clientQuakes.size(); i++)
		{
			ClientQuake quake = clientQuakes.get(i);
			
			if(quake.posX == x && quake.posZ == z)
			{
				quake.passY = height;
				return;
			}
		}
		
		ClientQuake newQuake = new ClientQuake(d, x, z, l, w, a);
		newQuake.passY = height;
	}
	
	public static void RemoveQuake(int x, int z)
	{
		for(int i = 0; i < clientQuakes.size(); i++)
		{
			ClientQuake quake = clientQuakes.get(i);
			
			if(quake.posX == x && quake.posZ == z)
			{
				clientQuakes.remove(i);
				return;
			}
		}
	}
	
	public static float GetQuakeShake(World world, Entity entity)
	{
		float dist = 64F;
		
		if(clientQuakes.size() > 0)
		{
			for(int i = clientQuakes.size() - 1; i >= 0; i--)
			{
				ClientQuake quake = clientQuakes.get(i);
				int size = quake.length > quake.width? quake.length/2 : quake.width/2;
				
				if(entity.getDistance(quake.posX, quake.passY, quake.posZ) < dist + size)
				{
					dist = (float)entity.getDistance(quake.posX, quake.passY, quake.posZ) - size;
					dist = dist < 0? 0 : dist;
				}
				
				if(entity.getDistance(quake.posX, quake.passY, quake.posZ) > 128 + size)
				{
					clientQuakes.remove(i);
				}
			}
		}
		
		return 1F - (dist/64F);
	}
}
