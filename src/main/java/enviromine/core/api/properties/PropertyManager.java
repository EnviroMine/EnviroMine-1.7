package enviromine.core.api.properties;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import enviromine.core.EnviroMine;
import enviromine.core.api.helpers.IPropScanner;
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
			
			if(tracker == null)
			{
				continue;
			}
			
			if(tracker instanceof IPropScanner)
			{
				IPropScanner scanner = (IPropScanner)tracker;
				int d = scanner.ScanDiameter();
				int r = d/2;
				
				int i = 0;
				while(i < scanner.ScansPerTick())
				{
					int pass = (tracker.entityLiving.ticksExisted * scanner.ScansPerTick() + i)%(d * d * d);
					
					int x = pass%d;
					int y = pass/(d * d);
					int z = pass%(d * d)/d;
					
					x += MathHelper.floor_double(tracker.entityLiving.posX) - r;
					y += MathHelper.floor_double(tracker.entityLiving.posY) - r;
					z += MathHelper.floor_double(tracker.entityLiving.posZ) - r;
					
					scanner.DoScan(x, y, z, pass);
					i++;
				}
			}
			
			tracker.onLivingUpdate();
			
			if(!event.entityLiving.worldObj.isRemote && propType.SyncClient())
			{
				NBTTagCompound trackTags = new NBTTagCompound();
				tracker.saveNBTData(trackTags);
				syncData.setTag("ENVIROMINE_" + propType.getTrackerID(), trackTags);
				flag = true;
			}
		}
		
		if(flag && event.entityLiving.ticksExisted%20 == 0) // Sync once a second if necessary
		{
			SyncTrackers(syncData, event.entityLiving);
		}
	}
	
	@SubscribeEvent
	public void onClone(PlayerEvent.Clone event)
	{
		for(PropertyType propType : PropertyRegistry.getAllTypes())
		{
			PropertyTracker track1 = propType.getTracker(event.entityLiving);
			PropertyTracker track2 = propType.getTracker(event.original);
			
			if(track1 == null || track2 == null)
			{
				continue;
			}
			
			if(!event.wasDeath || propType.isPersistent())
			{
				NBTTagCompound tags = new NBTTagCompound();
				track2.saveNBT(tags);
				track1.loadNBT(tags);
			} else
			{
				track1.Reset();
			}
			
			track2.onClone(event.original, event.entityPlayer);
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
