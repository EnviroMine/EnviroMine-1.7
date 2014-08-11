package enviromine.trackers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import enviromine.EnviroDamageSource;
import enviromine.EnviroPotion;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.handlers.EM_StatusManager;
import enviromine.handlers.ObjectHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EnviroDataTracker
{
	public EntityLivingBase trackedEntity;
	
	public float prevBodyTemp = 37F;
	public float prevHydration = 100F;
	public float prevAirQuality = 100;
	public float prevSanity = 100F;
	
	public float gasAirDiff = 0F;
	
	public float airQuality;
	
	public float bodyTemp;
	public float airTemp;
	
	public float hydration;
	
	public float sanity;
	
	public int attackDelay = 1;
	public int curAttackTime = 0;
	public boolean isDisabled = false;
	public int itemUse = 0;
	
	public int frostbiteLevel = 0;
	
	public boolean brokenLeg = false;
	public boolean brokenArm = false;
	public boolean bleedingOut = false;
	
	public String sleepState = "Awake";
	public int lastSleepTime = 0;
	
	public int timeBelow10 = 0;
	
	public int updateTimer = 0;
	
	private Side side = FMLCommonHandler.instance().getSide();
	
	//Sound Time
	public long chillPrevTime = 0;
	public long sizzlePrevTime = 0;
	
	public EnviroDataTracker(EntityLivingBase entity)
	{
		trackedEntity = entity;
		airQuality = 100F;
		bodyTemp = 37F;
		hydration = 100F;
		sanity = 100F;
	}
	
	public void updateData()
	{
		prevBodyTemp = bodyTemp;
		prevAirQuality = airQuality;
		prevHydration = hydration;
		prevSanity = sanity;
		
		updateTimer = 0;
		
		if(trackedEntity == null)
		{
			EM_StatusManager.removeTracker(this);
			return;
		}
		
		if(trackedEntity.isDead)
		{
			if(trackedEntity instanceof EntityPlayer)
			{
				EntityPlayer player = EM_StatusManager.findPlayer(trackedEntity.getUniqueID());
				
				if(player == null)
				{
					EM_StatusManager.saveAndRemoveTracker(this);
					return;
				} else
				{
					trackedEntity = player;
					this.loadNBTTags();
				}
			} else
			{
				EM_StatusManager.removeTracker(this);
				return;
			}
		}
		
		if(!(trackedEntity instanceof EntityPlayer) && !EM_Settings.trackNonPlayer || (EM_Settings.enableAirQ == false && EM_Settings.enableBodyTemp == false && EM_Settings.enableHydrate == false && EM_Settings.enableSanity == false))
		{
			EM_StatusManager.saveAndRemoveTracker(this);
		}
		
		int i = MathHelper.floor_double(trackedEntity.posX);
		int k = MathHelper.floor_double(trackedEntity.posZ);
		
		if(!trackedEntity.worldObj.getChunkFromBlockCoords(i, k).isChunkLoaded)
		{
			return;
		}
		
		float[] enviroData = EM_StatusManager.getSurroundingData(trackedEntity, 5);
		boolean isCreative = false;
		
		if(trackedEntity instanceof EntityPlayer)
		{
			if(((EntityPlayer)trackedEntity).capabilities.isCreativeMode)
			{
				isCreative = true;
			}
		}
		
		if((trackedEntity.getHealth() <= 2F || bodyTemp >= 41F) && enviroData[7] > (float)(-1F * EM_Settings.sanityMult))
		{
			enviroData[7] = (float)(-1F * EM_Settings.sanityMult);
		}
		
		// Air checks
		enviroData[0] += gasAirDiff;
		gasAirDiff = 0F;
		ItemStack helmet = trackedEntity.getEquipmentInSlot(4);
		if(helmet != null && !isCreative)
		{
			if(helmet.getItem() == ObjectHandler.gasMask)
			{
				if(helmet.getItemDamage() < helmet.getMaxDamage() && airQuality <= 99F)
				{
					int airDrop = MathHelper.floor_float(enviroData[0]);
					
					enviroData[0] -= helmet.getMaxDamage() - helmet.getItemDamage();
					
					if(enviroData[0] <= 0)
					{
						enviroData[0] = 0;
						helmet.setItemDamage(helmet.getItemDamage() - airDrop);
					} else
					{
						helmet.setItemDamage(helmet.getMaxDamage());
					}
				}
			}
		}
		
		airQuality += enviroData[0];
		
		if(airQuality <= 0F)
		{
			airQuality = 0;
		}
		
		if(airQuality >= 100F)
		{
			airQuality = 100F;
		}
		
		// Temperature checks
		airTemp = enviroData[1];
		float tnm = enviroData[4];
		float tpm = enviroData[5];
		
		if(bodyTemp - airTemp > 0)
		{
			float spAmp = Math.abs(bodyTemp - airTemp) > 10F? Math.abs(bodyTemp - airTemp)/10F : 1F;
			if(bodyTemp - airTemp >= tnm * spAmp)
			{
				bodyTemp -= tnm * spAmp;
			} else
			{
				bodyTemp = airTemp;
			}
		} else if(bodyTemp - airTemp < 0)
		{
			float spAmp = Math.abs(bodyTemp - airTemp) > 10F? Math.abs(bodyTemp - airTemp)/10F : 1F;
			if(bodyTemp - airTemp <= -tpm * spAmp)
			{
				bodyTemp += tpm * spAmp;
			} else
			{
				bodyTemp = airTemp;
			}
		}
		
		// Hydration checks
		if(hydration > 0F && (enviroData[6] == 1 || !(trackedEntity instanceof EntityAnimal)))
		{
			if(bodyTemp >= 38.02F)
			{
				dehydrate(0.1F);
				
				if(hydration >= 75F)
				{
					bodyTemp -= 0.01F;
				}
			}
			
			if(enviroData[3] > 0F)
			{
				dehydrate(0.05F + enviroData[3]);
			} else
			{
				if(enviroData[3] < 0F)
				{
					hydrate(-enviroData[3]);
				}
				dehydrate(0.05F);
			}
		} else if(enviroData[6] == -1 && trackedEntity instanceof EntityAnimal)
		{
			hydrate(0.05F);
		} else if(hydration <= 0F)
		{
			hydration = 0;
		}
		
		// Sanity checks
		if(sanity < 0F)
		{
			sanity = 0F;
		}
		
		if(enviroData[7] < 0F)
		{
			if(sanity + enviroData[7] >= 0F)
			{
				sanity += enviroData[7];
			} else
			{
				sanity = 0F;
			}
		} else if(enviroData[7] > 0F)
		{
			if(sanity + enviroData[7] <= 100F)
			{
				sanity += enviroData[7];
			} else
			{
				sanity = 100F;
			}
		}
		
		//Check for custom properties
		boolean enableAirQ = true;
		boolean enableBodyTemp = true;
		boolean enableHydrate = true;
		boolean enableFrostbite = true;
		boolean enableHeat = true;
		if(EntityList.getEntityID(trackedEntity) > 0)
		{
			if(EM_Settings.livingProperties.containsKey(EntityList.getEntityID(trackedEntity)))
			{
				EntityProperties livingProps = EM_Settings.livingProperties.get(EntityList.getEntityID(trackedEntity));
				enableHydrate = livingProps.dehydration;
				enableBodyTemp = livingProps.bodyTemp;
				enableAirQ = livingProps.airQ;
				enableFrostbite = !livingProps.immuneToFrost;
				enableHeat = !livingProps.immuneToHeat;
			} else if((trackedEntity instanceof EntitySheep) || (trackedEntity instanceof EntityWolf))
			{
				enableFrostbite = false;
			} else if(trackedEntity instanceof EntityChicken)
			{
				enableHeat = false;
			}
		}
		
		//Reset Disabled Values
		if(!EM_Settings.enableAirQ || !enableAirQ)
		{
			airQuality = 100F;
		}
		if(!EM_Settings.enableBodyTemp || !enableBodyTemp)
		{
			bodyTemp = 37F;
		}
		if(!EM_Settings.enableHydrate || !enableHydrate)
		{
			hydration = 100F;
		}
		if(!EM_Settings.enableSanity || !(trackedEntity instanceof EntityPlayer))
		{
			sanity = 100F;
		}
		
		// Camel Pack Stuff
		ItemStack plate = trackedEntity.getEquipmentInSlot(3);
		
		if(plate != null && !isCreative)
		{
			if(plate.getItem() == ObjectHandler.camelPack)
			{
				if(plate.getItemDamage() < plate.getMaxDamage() && hydration <= 99F - EM_Settings.hydrationMult)
				{
					plate.setItemDamage(plate.getItemDamage() + 1);
					hydrate((float)EM_Settings.hydrationMult);
					
					if(bodyTemp >= 37F + EM_Settings.tempMult/10F)
					{
						bodyTemp -= EM_Settings.tempMult/10F;
					}
				}
			} else if (plate.hasTagCompound() && plate.getTagCompound().hasKey("camelPackFill"))
			{
				int fill = plate.getTagCompound().getInteger("camelPackFill");
				if(fill > 0 && hydration <= 99F - EM_Settings.hydrationMult)
				{
					plate.getTagCompound().setInteger("camelPackFill", fill-1);
					hydrate((float)EM_Settings.hydrationMult);
					
					if(bodyTemp >= 37F + EM_Settings.tempMult/10F)
					{
						bodyTemp -= EM_Settings.tempMult/10F;
					}
				}
			}
		}
		
		// Fix floating point errors
		this.fixFloatinfPointErrors();
		
		if(trackedEntity instanceof EntityPlayer)
		{
			if(((EntityPlayer)trackedEntity).capabilities.isCreativeMode)
			{
				bodyTemp = prevBodyTemp;
				airQuality = prevAirQuality;
				hydration = prevHydration;
				sanity = prevSanity;
			}
		}
		
		// Apply side effects
		
		if(airTemp <= 10F && bodyTemp <= 35F || bodyTemp <= 30F)
		{
			timeBelow10 += 1;
		} else
		{
			timeBelow10 = 0;
		}
		
		if(curAttackTime >= attackDelay)
		{
			if(airQuality <= 0)
			{
				trackedEntity.attackEntityFrom(EnviroDamageSource.suffocate, 4.0F);
				
				trackedEntity.worldObj.playSoundAtEntity(trackedEntity, "enviromine:gag", 1f, 1f);
				//mc.sndManager.playSound("enviromine:gag", (float)trackedEntity.posX, (float)trackedEntity.posY, (float)trackedEntity.posZ, EM_Settings.breathVolume, 1.0F);
			}
			
			if(airQuality <= 10F)
			{
				trackedEntity.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 200, 1));
				trackedEntity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200, 1));
			} else if(airQuality <= 25F)
			{
				trackedEntity.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 200, 0));
				trackedEntity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200, 0));
			}
			
			/* For sound Overheating
			if(sizzlePrevTime == 0) 
			{
				sizzlePrevTime =  mc.getSystemTime() - 16001;
			}*/
			
			if(!trackedEntity.isPotionActive(Potion.fireResistance))
			{
				if(bodyTemp >= 39F && enableHeat && (enviroData[6] == 1 || !(trackedEntity instanceof EntityAnimal)))
				{
					trackedEntity.addPotionEffect(new PotionEffect(EnviroPotion.heatstroke.id, 200, 2));
				} else if(bodyTemp >= 41F)
				{
					trackedEntity.addPotionEffect(new PotionEffect(EnviroPotion.heatstroke.id, 200, 1));
					
				} else
				{
					trackedEntity.addPotionEffect(new PotionEffect(EnviroPotion.heatstroke.id, 200, 0));
				}
				
				
				
			}
			
			if(chillPrevTime == 0) 
			{
				if (side.isClient()) {
					chillPrevTime = Minecraft.getSystemTime() - 17001;
				} else {
					chillPrevTime =  MinecraftServer.getSystemTimeMillis() - 17001;
				}
				
				if(bodyTemp >= 43F)
				{
					trackedEntity.addPotionEffect(new PotionEffect(EnviroPotion.heatstroke.id, 200, 2));
				} else if(bodyTemp >= 41F)
				{
					trackedEntity.addPotionEffect(new PotionEffect(EnviroPotion.heatstroke.id, 200, 1));
				} else
				{
					trackedEntity.addPotionEffect(new PotionEffect(EnviroPotion.heatstroke.id, 200, 0));
				}
				/* For Adding Sound for Overheating
					System.out.println(mc.getSystemTime() +"-"+ sizzlePrevTime + "="+ (mc.getSystemTime() - sizzlePrevTime));
					if ((mc.getSystemTime() - sizzlePrevTime) > 16000)
					{
						mc.sndManager.playSound("enviromine:sizzle", (float)trackedEntity.posX, (float)trackedEntity.posY, (float)trackedEntity.posZ, .5f, 1.0F);
						sizzlePrevTime = mc.getSystemTime();
					}
				 */
				
			}
			else if(trackedEntity.isPotionActive(EnviroPotion.heatstroke))
			{
				trackedEntity.removePotionEffect(EnviroPotion.heatstroke.id);
			}
			
			if(bodyTemp <= 35F && enableFrostbite && (enviroData[6] == 1 || !(trackedEntity instanceof EntityAnimal)))
			{
				if(bodyTemp <= 30F)
				{
					trackedEntity.addPotionEffect(new PotionEffect(EnviroPotion.hypothermia.id, 200, 2));
					
				} else if(bodyTemp <= 32F)
				{
					trackedEntity.addPotionEffect(new PotionEffect(EnviroPotion.hypothermia.id, 200, 1));
					
				} else
				{
					trackedEntity.addPotionEffect(new PotionEffect(EnviroPotion.hypothermia.id, 200, 0));
				}
				
				if (this.side.isClient()) {
					System.out.println("Check:" + Minecraft.getSystemTime() +"-"+ chillPrevTime +"="+(Minecraft.getSystemTime() - chillPrevTime));
					
					playSoundWithTimeCheck(17000, "enviromine:chill",  EM_Settings.breathVolume, 1.0F);
				}
			}
			
			if(((timeBelow10 >= 120 && enableFrostbite) || (frostbiteLevel >= 1 && enableFrostbite)))
			{
				if(timeBelow10 >= 240 || frostbiteLevel >= 2)
				{
					trackedEntity.addPotionEffect(new PotionEffect(EnviroPotion.frostbite.id, 200, 1));
					
					if(frostbiteLevel <= 2)
					{
						frostbiteLevel = 2;
					}
				} else
				{
					trackedEntity.addPotionEffect(new PotionEffect(EnviroPotion.frostbite.id, 200, 0));
					
					if(frostbiteLevel <= 1)
					{
						frostbiteLevel = 1;
					}
				}
				
				
				if (this.side.isClient()) {
					playSoundWithTimeCheck(1700, "enviromine:chill",  EM_Settings.breathVolume, 1.0F);
				}
			}
			
			if(bodyTemp >= 45F && enviroData[2] == 1)
			{
				trackedEntity.setFire(10);
			}
			
			if(hydration <= 0F)
			{
				trackedEntity.attackEntityFrom(EnviroDamageSource.dehydrate, 4.0F);
			}
			
			if(sanity <= 10F)
			{
				trackedEntity.addPotionEffect(new PotionEffect(EnviroPotion.insanity.id, 600, 2));
			} else if(sanity <= 25F)
			{
				trackedEntity.addPotionEffect(new PotionEffect(EnviroPotion.insanity.id, 600, 1));
			} else if(sanity <= 50F)
			{
				trackedEntity.addPotionEffect(new PotionEffect(EnviroPotion.insanity.id, 600, 0));
			}
			
			curAttackTime = 0;
		}else
		{
			curAttackTime += 1;
		}
		
		EnviroPotion.checkAndApplyEffects(trackedEntity);
		
		if(isCreative)
		{
			bodyTemp = prevBodyTemp;
			airQuality = prevAirQuality;
			hydration = prevHydration;
			sanity = prevSanity;
		}
		
		this.fixFloatinfPointErrors();
		EM_StatusManager.saveTracker(this);
	}
	
	@SideOnly(Side.CLIENT)
	private void playSoundWithTimeCheck(int time, String sound, float volume, float pitch)
	{
		if ((Minecraft.getSystemTime() - chillPrevTime) > 17000)
		{
			Minecraft.getMinecraft().thePlayer.playSound("enviromine:chill",  EM_Settings.breathVolume, 1.0F);
			chillPrevTime = Minecraft.getSystemTime();
		}
	}
	
	public void fixFloatinfPointErrors()
	{
		airQuality = new BigDecimal(String.valueOf(airQuality)).setScale(2, RoundingMode.HALF_UP).floatValue();
		bodyTemp = new BigDecimal(String.valueOf(bodyTemp)).setScale(3, RoundingMode.HALF_UP).floatValue();
		airTemp = new BigDecimal(String.valueOf(airTemp)).setScale(3, RoundingMode.HALF_UP).floatValue();
		hydration = new BigDecimal(String.valueOf(hydration)).setScale(2, RoundingMode.HALF_UP).floatValue();
		sanity = new BigDecimal(String.valueOf(sanity)).setScale(3, RoundingMode.HALF_UP).floatValue();
	}
	
	public static boolean isLegalType(EntityLivingBase entity)
	{
		String name = EntityList.getEntityString(entity);
		
		if(EM_Settings.livingProperties.containsKey(EntityList.getEntityID(entity)))
		{
			return EM_Settings.livingProperties.get(EntityList.getEntityID(entity)).shouldTrack;
		}
		
		if(entity.isEntityUndead() || entity instanceof EntityMob)
		{
			return false;
		} else if(name == "Enderman")
		{
			return false;
		} else if(name == "Villager")
		{
			return false;
		} else if(name == "Slime")
		{
			return false;
		} else if(name == "Ghast")
		{
			return false;
		} else if(name == "Squid")
		{
			return false;
		} else if(name == "Blaze")
		{
			return false;
		} else if(name == "LavaSlime")
		{
			return false;
		} else if(name == "SnowMan")
		{
			return false;
		} else if(name == "MushroomCow")
		{
			return false;
		} else if(name == "WitherBoss")
		{
			return false;
		} else if(name == "EnderDragon")
		{
			return false;
		} else if(name == "VillagerGolem")
		{
			return false;
		} else
		{
			if(entity instanceof EntityPlayer)
			{
				EnviroDataTracker tracker = EM_StatusManager.lookupTracker(entity);
				
				if(tracker != null)
				{
					return false;
				} else
				{
					return true;
				}
			} else
			{
				return true;
			}
		}
	}
	
	public void hydrate(float amount)
	{
		float MAmount = (float)(amount * EM_Settings.hydrationMult);
		
		if(hydration >= 100F - MAmount)
		{
			hydration = 100.0F;
		} else
		{
			hydration += MAmount;
		}
		
		this.fixFloatinfPointErrors();
		
		if(!EnviroMine.proxy.isClient() || EnviroMine.proxy.isOpenToLAN())
		{
			EM_StatusManager.syncMultiplayerTracker(this);
		}
	}
	
	public void dehydrate(float amount)
	{
		float MAmount = (float)(amount * EM_Settings.hydrationMult);
		
		if(hydration >= MAmount)
		{
			hydration -= MAmount;
		} else
		{
			hydration = 0F;
		}
		
		this.fixFloatinfPointErrors();
		
		if(!EnviroMine.proxy.isClient() || EnviroMine.proxy.isOpenToLAN())
		{
			EM_StatusManager.syncMultiplayerTracker(this);
		}
	}
	
	public void loadNBTTags()
	{
		NBTTagCompound tags = trackedEntity.getEntityData();
		
		if(tags.hasKey("ENVIRO_AIR"))
		{
			airQuality = tags.getFloat("ENVIRO_AIR");
		}
		if(tags.hasKey("ENVIRO_HYD"))
		{
			hydration = tags.getFloat("ENVIRO_HYD");
		}
		if(tags.hasKey("ENVIRO_TMP"))
		{
			bodyTemp = tags.getFloat("ENVIRO_TMP");
		}
		if(tags.hasKey("ENVIRO_SAN"))
		{
			sanity = tags.getFloat("ENVIRO_SAN");
		}
	}
	
	public int getAndIncrementItemUse()
	{
		itemUse += 1;
		return itemUse;
	}
	
	public void resetItemUse()
	{
		itemUse = 0;
	}
	
	public void resetData()
	{
		airQuality = 100F;
		bodyTemp = 37F;
		hydration = 100F;
		sanity = 100F;
	}
}
