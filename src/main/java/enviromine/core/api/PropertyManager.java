package enviromine.core.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import enviromine.core.EnviroMine;
import enviromine.core.network.PacketEnviroProperty;

public class PropertyManager
{
	@SubscribeEvent
	public void onConstruct(EntityConstructing event)
	{
		if(event.entity instanceof EntityLivingBase)
		{
			EntityLivingBase entityLiving = (EntityLivingBase)event.entity;
			
			for(PropertyType propType : PropertyRegistry.getAllTypes())
			{
				if(propType.isTrackable(entityLiving))
				{
					PropertyTracker tracker = propType.getNewTracker(entityLiving);
					
					if(tracker != null)
					{
						entityLiving.registerExtendedProperties(propType.getTrackerID(), tracker);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityJoin(EntityJoinWorldEvent event)
	{
		if(event.entity instanceof EntityLivingBase)
		{
			EntityLivingBase entityLiving = (EntityLivingBase)event.entity;
			
			for(PropertyType propType : PropertyRegistry.getAllTypes())
			{
				PropertyTracker tracker = propType.getTracker(entityLiving);
				
				if(tracker != null)
				{
					tracker.onSpawn();
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event)
	{
		for(PropertyType propType : PropertyRegistry.getAllTypes())
		{
			PropertyTracker tracker = propType.getTracker(event.entityLiving);
			
			if(tracker != null)
			{
				tracker.onDeath();
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		if(event.entityLiving == null)
		{
			return;
		}
		
		NBTTagCompound syncData = new NBTTagCompound();
		boolean flag = false; // Should sync
		
		for(PropertyType propType : PropertyRegistry.getAllTypes())
		{
			PropertyTracker tracker = propType.getTracker(event.entityLiving);
			
			if(tracker != null)
			{
				tracker.onLivingUpdate();
				
				if(!event.entityLiving.worldObj.isRemote && propType.SyncClient())
				{
					tracker.saveNBTData(syncData);
					flag = true;
				}
			}
		}
		
		if(flag && event.entityLiving.ticksExisted%20 == 0) // Sync once a second if necessary
		{
			SyncTrackers(syncData, event.entityLiving);
		}
	}
	
	public static void SyncTrackers(NBTTagCompound trackerData, EntityLivingBase entityLiving)
	{
		if(trackerData == null || entityLiving == null || entityLiving.worldObj.isRemote)
		{
			return; // Can't sync null or remote trackers
		}
		
		NBTTagCompound tags = new NBTTagCompound();
		
		tags.setTag("Properties", trackerData);
		tags.setInteger("EntityID", entityLiving.getEntityId());
		
		EnviroMine.instance.network.sendToAllAround(new PacketEnviroProperty(tags), new TargetPoint(entityLiving.dimension, entityLiving.posX, entityLiving.posY, entityLiving.posZ, 64D));
	}
}
