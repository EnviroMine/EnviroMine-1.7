package enviromine.core.managers;

import java.util.HashMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import enviromine.core.EnviroMine;
import enviromine.core.api.EnviroProperty;
import enviromine.core.api.PropertyRegistry;
import enviromine.core.network.PacketEnviroProperty;

public class PropertyManager
{
	static HashMap<EntityLivingBase, PropertyTracker> trackerList = new HashMap<EntityLivingBase, PropertyTracker>();
	
	public static PropertyTracker GetTracker(EntityLivingBase entityLiving)
	{
		return trackerList.get(entityLiving);
	}
	
	@SubscribeEvent
	public void onEntityJoin(EntityJoinWorldEvent event)
	{
		if(event.entity instanceof EntityLivingBase)
		{
			EntityLivingBase entityLiving = (EntityLivingBase)event.entity;
			
			if(!trackerList.containsKey(entityLiving))
			{
				PropertyTracker tracker = new PropertyTracker(entityLiving);
				tracker.readFromNBT(GetEnviroNBT(entityLiving));
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event)
	{
		PropertyTracker tracker = trackerList.get(event.original);
		
		if(tracker == null)
		{
			return;
		}
		
		tracker.TransferOwner(event.entityLiving);
		
		if(event.wasDeath)
		{
			tracker.Reset(true);
		}
	}
	
	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event)
	{
		PropertyTracker tracker = trackerList.get(event.entityLiving);
		
		if(tracker != null)
		{
			tracker.Reset(true);
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		PropertyTracker tracker = trackerList.get(event.entityLiving);
		
		if(tracker == null)
		{
			return;
		}
		
		if(event.entityLiving.isDead || event.entityLiving.getHealth() <= 0F)
		{
			tracker.Reset(true);
			return;
		}
		
		if(tracker.Update())
		{
			SyncTracker(tracker);
		}
	}
	
	public static void SyncTracker(PropertyTracker tracker)
	{
		if(tracker.trackedEntity.worldObj.isRemote)
		{
			return; // Can't sync remote trackers
		}
		
		NBTTagCompound tags = new NBTTagCompound();
		
		tags.setTag("Properties", tracker.writeToNBT(new NBTTagCompound()));
		tags.setInteger("EntityID", tracker.trackedEntity.getEntityId());
		
		EnviroMine.instance.network.sendToAllAround(new PacketEnviroProperty(tags), new TargetPoint(tracker.trackedEntity.dimension, tracker.trackedEntity.posX, tracker.trackedEntity.posY, tracker.trackedEntity.posZ, 64D));
	}
	
	public static NBTTagCompound GetEnviroNBT(EntityLivingBase entityLiving)
	{
		if(entityLiving == null)
		{
			return new NBTTagCompound();
		}
		
		if(entityLiving instanceof EntityPlayer)
		{
			return ((EntityPlayer)entityLiving).getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag("ENVIRO_DATA");
		} else
		{
			return entityLiving.getEntityData().getCompoundTag("ENVIRO_DATA");
		}
	}
	
	public static void SetEnviroNBT(EntityLivingBase entityLiving, NBTTagCompound nbtTags)
	{
		if(entityLiving instanceof EntityPlayer)
		{
			((EntityPlayer)entityLiving).getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setTag("ENVIRO_DATA", nbtTags);
		} else
		{
			entityLiving.getEntityData().setTag("ENVIRO_DATA", nbtTags);
		}
	}
	
	public static class PropertyTracker
	{
		public long tick = 0;
		public EntityLivingBase trackedEntity;
		public EnviroProperty[] properties;
		
		public PropertyTracker(EntityLivingBase entityLiving)
		{
			this.trackedEntity = entityLiving;
			properties = PropertyRegistry.InstatiateNewList(entityLiving);
		}
		
		/**
		 * Updates all properties currently being tracked. Returns true if one or more properties updated requires re-sync
		 */
		public boolean Update()
		{
			boolean flag = false;
			
			for(EnviroProperty prop : properties)
			{
				if(tick%prop.TickInterval() == 0)
				{
					prop.Update(this);
					
					if(prop.RequiresSync())
					{
						flag = true;
					}
				}
			}
			
			tick++;
			return flag;
		}
		
		public void Reset(boolean isDeath)
		{
			for(EnviroProperty prop : properties)
			{
				if(isDeath && prop.isPersistent())
				{
					continue;
				} else
				{
					prop.Reset();
				}
			}
		}
		
		public void TransferOwner(EntityLivingBase entityLiving)
		{
			this.trackedEntity = entityLiving;
		}
		
		public NBTTagCompound writeToNBT(NBTTagCompound tags)
		{
			for(EnviroProperty prop : properties)
			{
				NBTTagCompound propTags = new NBTTagCompound();
				prop.writeToNBT(propTags);
				tags.setTag(PropertyRegistry.GetID(prop.getClass()), propTags);
			}
			
			return tags;
		}
		
		public void readFromNBT(NBTTagCompound tags)
		{
			for(EnviroProperty prop : properties)
			{
				prop.readFromNBT(tags.getCompoundTag(PropertyRegistry.GetID(prop.getClass())));
			}
		}
	}
}
