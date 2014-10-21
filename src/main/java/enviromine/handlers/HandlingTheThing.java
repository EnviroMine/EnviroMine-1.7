package enviromine.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import enviromine.EnviroDamageSource;
import enviromine.core.EM_Settings;
import enviromine.trackers.EnviroDataTracker;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class HandlingTheThing
{
	public static Calendar date = Calendar.getInstance();
	
	public static void stalkPlayer(EntityPlayer player)
	{
		boolean flag = false;
		
		// Check if Halloween or Friday 13th. Guarantees attack if true!
		if((date.get(Calendar.MONTH) == Calendar.OCTOBER && date.get(Calendar.DAY_OF_MONTH) == 31) || (date.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && date.get(Calendar.DAY_OF_MONTH) == 13))
		{
			flag = true;
		}
		
		if(!player.getEntityData().getBoolean("EM_THING_TARGET") && !flag)
		{
			if(player.worldObj.rand.nextInt(100000) == 0) // If you are REALLY unlucky you will be attacked at any time!
			{
				player.getEntityData().setBoolean("EM_THING_TARGET", true);
			}
			return;
		}
		
		EnviroDataTracker tracker = EM_StatusManager.lookupTrackerFromUsername(player.getCommandSenderName());
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
			if(hasWitnesses(player))
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
	
	public static boolean hasWitnesses(EntityPlayer victim)
	{
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
				return true;
			}
		}
		
		return false;
	}
}
