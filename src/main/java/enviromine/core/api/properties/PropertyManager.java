package enviromine.core.api.properties;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.core.EnviroMine;
import enviromine.core.api.helpers.IPropScanner;
import enviromine.core.network.PacketEnviroProperty;

public class PropertyManager
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onGuiRender(RenderGameOverlayEvent.Post event)
	{
		if(event.isCanceled() || event.type != RenderGameOverlayEvent.ElementType.HELMET)
		{
			return;
		}
		
		for(PropertyType propType : PropertyRegistry.getAllTypes())
		{
			Minecraft mc = Minecraft.getMinecraft();
			PropertyRenderer renderer = propType.getGuiRenderer();
			PropertyTracker tracker = mc == null? null : propType.getTracker(mc.thePlayer);
			
			if(renderer == null)
			{
				continue;
			}
			
            ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			renderer.drawGui(tracker, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
		}
	}
	
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
				tracker.saveNBTData(syncData);
				flag = true;
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
