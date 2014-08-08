package enviromine.world;

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
	
	public static float GetQuakeShake(World world, int x, int y, int z)
	{
		if(clientQuakes.size() > 0)
		{
			return 1F;
		} else
		{
			return 0F;
		}
	}
}
