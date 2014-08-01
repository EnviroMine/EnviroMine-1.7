package enviromine.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import enviromine.EnviroDamageSource;
import enviromine.core.EM_Settings;
import enviromine.trackers.EnviroDataTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;

public class HandlingTheThing
{
	public static void stalkPlayer(EntityPlayer player)
	{
		EnviroDataTracker tracker = EM_StatusManager.lookupTrackerFromUsername(player.username);
		int i = MathHelper.floor_double(player.posX);
		int j = MathHelper.floor_double(player.posY);
		int k = MathHelper.floor_double(player.posZ);
		
		int darkness = player.getEntityData().getInteger("EM_THING");
		int deathSpeed = 1;
		
		if(tracker != null && tracker.sanity <= 50)
		{
			deathSpeed = 2;
			
			if(tracker.sanity <= 25)
			{
				deathSpeed = 3;
			}
		}
		
		if(player.worldObj.getBlockLightValue(i, j, k) == 0 && !player.capabilities.isCreativeMode && player.dimension == EM_Settings.caveDimID && !player.isPotionActive(Potion.nightVision))
		{
			if(getWitnesses(player) <= 0)
			{
				darkness += deathSpeed;
			}
		} else
		{
			darkness = 0;
			
			if(player.isPotionActive(Potion.blindness))
			{
				player.removePotionEffect(Potion.blindness.id);
			}
		}
		
		player.getEntityData().setInteger("EM_THING", darkness);
		
		if(darkness >= 300 && tracker != null)
		{
			if(tracker.sanity > 50F)
			{
				tracker.sanity -= 0.001F;
				tracker.fixFloatinfPointErrors();
			}
		}
		
		if(darkness >= 900)
		{
			player.addPotionEffect(new PotionEffect(Potion.blindness.id, 100));
		}
		
		if(darkness >= 1200)
		{
			player.attackEntityFrom(EnviroDamageSource.thething, 1000F);
		}
	}
	
	public static int getWitnesses(EntityPlayer victim)
	{
		int count = 0;
		
		List players = victim.worldObj.getEntitiesWithinAABB(EntityPlayer.class, victim.boundingBox.expand(128, 128, 128));
		
		Iterator iterator = players.iterator();
		
		while(iterator.hasNext())
		{
			EntityPlayer witness = (EntityPlayer)iterator.next();
			
			if(witness.equals(victim))
			{
				continue;
			}
			
			if(witness.canEntityBeSeen(victim))
			{
				count++;
			}
		}
		
		return count;
	}
}
