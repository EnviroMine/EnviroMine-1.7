package enviromine.handlers;

import enviromine.EnviroDamageSource;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.trackers.EnviroDataTracker;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HandlingTheThing
{
	public static Calendar date = Calendar.getInstance();
	static ArrayList<String> messages = new ArrayList<String>();
	
	public static void stalkPlayer(EntityPlayer player)
	{
		if(player.dimension != EM_Settings.caveDimID || player.worldObj.difficultySetting == EnumDifficulty.PEACEFUL)
		{
			player.getEntityData().setBoolean("EM_THING_TARGET", false);
			player.getEntityData().setInteger("EM_THING", 0);
			return;
		}
		
		boolean flag = false;
		
		// Check if Halloween or Friday 13th. Guarantees attack if true!
		if((date.get(Calendar.MONTH) == Calendar.OCTOBER && date.get(Calendar.DAY_OF_MONTH) == 31) || (date.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && date.get(Calendar.DAY_OF_MONTH) == 13))
		{
			flag = true;
		}
		
		if(!player.getEntityData().getBoolean("EM_THING_TARGET") && !flag)
		{
			if(player.worldObj.rand.nextInt(player.worldObj.difficultySetting == EnumDifficulty.HARD? 1000 : 100000) == 0) // If you are REALLY unlucky you will be attacked at any time!
			{
				player.getEntityData().setBoolean("EM_THING_TARGET", true);
			}
			return;
		} else
		{
			// If you get this Achievement you're going to have a bad time :P
			player.addStat(EnviroAchievements.itsPitchBlack, 1);
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
		
		if(player.worldObj.getBlockLightValue(i, j, k) < 10 && !player.capabilities.isCreativeMode && !player.isPotionActive(Potion.nightVision))
		{
			if(!hasWitnesses(player))
			{
				darkness += deathSpeed;
			}
		} else
		{
			if(darkness > 0)
			{
				darkness -= 1;
			} else
			{
				darkness = 0;
			}
			
			if(player.isPotionActive(Potion.blindness) && darkness < 2000)
			{
				player.removePotionEffect(Potion.blindness.id);
			}
		}
		
		player.getEntityData().setInteger("EM_THING", darkness);
		
		if(darkness >= 500 && tracker != null)
		{
			if(tracker.sanity > 50F)
			{
				tracker.sanity -= 0.001F;
				tracker.fixFloatinfPointErrors();
			}
		}
		
		if(darkness >= 1000 && darkness%20 == 0 && player.worldObj.rand.nextInt(5) == 0)
		{
			System.out.println("playing...");
			float rndX = (player.getRNG().nextInt(6) - 3) * player.getRNG().nextFloat();
			float rndY = (player.getRNG().nextInt(6) - 3) * player.getRNG().nextFloat();
			float rndZ = (player.getRNG().nextInt(6) - 3) * player.getRNG().nextFloat();
			
			S29PacketSoundEffect packet = new S29PacketSoundEffect("enviromine:whispers", player.posX + rndX, player.posY + rndY, player.posZ + rndZ, 0.5F, player.getRNG().nextBoolean()? 0.2F : (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.2F + 1.0F);
			
			if(!EnviroMine.proxy.isClient() && player instanceof EntityPlayerMP)
			{
				((EntityPlayerMP)player).playerNetServerHandler.sendPacket(packet);
			} else if(EnviroMine.proxy.isClient() && !player.worldObj.isRemote)
			{
				player.worldObj.playSoundEffect(player.posX + rndX, player.posY + rndY, player.posZ + rndZ, "enviromine:whispers", 0.5F, (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.2F + 1.0F);
			}
		}
		
		if(darkness >= 2000)
		{
			player.addPotionEffect(new PotionEffect(Potion.blindness.id, 100));
		}
		
		if(darkness >= 3000)
		{
			player.attackEntityFrom(EnviroDamageSource.thething, 1000F);
		}
	}
	
	public static boolean hasWitnesses(EntityPlayer victim)
	{
		List players = victim.worldObj.getEntitiesWithinAABB(EntityPlayer.class, victim.boundingBox.expand(128, 128, 128));

		for (Object player : players)
		{
			EntityPlayer witness = (EntityPlayer)player;

			if (witness.equals(victim))
			{
				continue;
			}

			if (witness.canEntityBeSeen(victim))
			{
				return true;
			}
		}
		
		return false;
	}
	
	static
	{
		messages.add("Stay in the light!");
		messages.add("It's too dark...");
		messages.add("ESCAPE!");
		messages.add("...never safe...");
		messages.add("Where did they go...");
		messages.add("NOT INSANE... NOT INSANE...");
		messages.add("help... me...");
		messages.add("I hear it coming...");
		messages.add("...why can't they hear me...");
		messages.add("So alone...");
		messages.add("HELP!");
		messages.add("...can't hide...");
		messages.add("Run!");
		messages.add("Why me?");
	}
}
