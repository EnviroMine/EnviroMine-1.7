package enviromine.trackers;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.EnviroDamageSource;
import enviromine.EnviroPotion;
import enviromine.client.gui.UI_Settings;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.handlers.EM_StatusManager;
import enviromine.trackers.properties.DimensionProperties;
import enviromine.trackers.properties.EntityProperties;

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
	
	public int frostbiteLevel = 0;
	public boolean frostIrreversible = false;
	
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
		
		if(trackedEntity == null || isDisabled)
		{
			EM_StatusManager.removeTracker(this);
			return;
		}
		
		if(trackedEntity.isDead)
		{
			return;
		}
		
		if(!(trackedEntity instanceof EntityPlayer) && !EM_Settings.trackNonPlayer || (EM_Settings.enableAirQ == false && EM_Settings.enableBodyTemp == false && EM_Settings.enableHydrate == false && EM_Settings.enableSanity == false))
		{
			EM_StatusManager.saveAndRemoveTracker(this);
			return;
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
		} else if(trackedEntity.getHealth() >= trackedEntity.getMaxHealth() && enviroData[7] < (0.1F * EM_Settings.sanityMult) && trackedEntity.worldObj.isDaytime() && !trackedEntity.worldObj.provider.hasNoSky && trackedEntity.worldObj.canBlockSeeTheSky(MathHelper.floor_double(trackedEntity.posX), MathHelper.floor_double(trackedEntity.posY), MathHelper.floor_double(trackedEntity.posZ)))
		{
			enviroData[7] = (float)(0.1F * EM_Settings.sanityMult);
		}
		
		// Air checks
		enviroData[0] += gasAirDiff;
		gasAirDiff = 0F;
		airQuality += enviroData[0];
		
		ItemStack helmet = trackedEntity.getEquipmentInSlot(4);
		if(helmet != null && !isCreative)
		{
			if(helmet.hasTagCompound() && helmet.getTagCompound().hasKey("gasMaskFill"))
			{
				NBTTagCompound tag = helmet.getTagCompound();
				int gasMaskFill = tag.getInteger("gasMaskFill");
				
				if(gasMaskFill > 0 && airQuality <= 99F)
				{
					int airDrop = 100 - MathHelper.ceiling_float_int(airQuality);
					airDrop = gasMaskFill >= airDrop? airDrop : gasMaskFill;
					
					if(airDrop > 0)
					{
						airQuality += airDrop;
						tag.setInteger("gasMaskFill", (gasMaskFill - airDrop));
					}
				}
			}
		}
		
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
		
		float relTemp = airTemp + 12F;
		
		if(bodyTemp - relTemp > 0) // Cold
		{
			float spAmp = Math.abs(bodyTemp - relTemp) > 10F? Math.abs(bodyTemp - relTemp)/10F : 1F;
			if(bodyTemp - relTemp >= tnm * spAmp)
			{
				bodyTemp -= tnm * spAmp;
			} else
			{
				bodyTemp = relTemp;
			}
		} else if(bodyTemp - relTemp < 0) // Hot
		{
			float spAmp = Math.abs(bodyTemp - relTemp) > 10F? Math.abs(bodyTemp - relTemp)/10F : 1F;
			if(bodyTemp - relTemp <= -tpm * spAmp)
			{
				bodyTemp += tpm * spAmp;
			} else
			{
				bodyTemp = relTemp;
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
			if (plate.hasTagCompound() && plate.getTagCompound().hasKey("camelPackFill"))
			{
				int fill = plate.getTagCompound().getInteger("camelPackFill");
				if(fill > 0 && hydration <= 100F - EM_Settings.hydrationMult)
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
			//Air Check
			if(airQuality <= 0)
			{
				trackedEntity.attackEntityFrom(EnviroDamageSource.suffocate, 4.0F);
				
				trackedEntity.worldObj.playSoundAtEntity(trackedEntity, "enviromine:gag", 1f, 1f);
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

			// Heat Temp Checks
			if(!trackedEntity.isPotionActive(Potion.fireResistance))
			{
				if(bodyTemp >= 39F && enableHeat && (enviroData[6] == 1 || !(trackedEntity instanceof EntityAnimal)))
				{
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
				}
			} else if(trackedEntity.isPotionActive(EnviroPotion.heatstroke))
			{
				trackedEntity.removePotionEffect(EnviroPotion.heatstroke.id);
			}
			
			//Cold Temp Checks
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
				
				if (this.side.isClient()) 
				{
					// This sounds like someone blowing into a mic
					//playSoundWithTimeCheck(17000, "enviromine:chill",  UI_Settings.breathVolume, 1.0F);
				}
			}
			
			if(enableFrostbite && (timeBelow10 >= 120 || (frostbiteLevel >= 1 && frostIrreversible)))
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
				
				// If frostbite is treated before this time then you can save your limbs!
				if(timeBelow10 > 360 && !frostIrreversible)
				{
					frostIrreversible = true;
					
					if(trackedEntity instanceof EntityPlayer)
					{
						((EntityPlayer)trackedEntity).addChatComponentMessage(new ChatComponentText("The flesh in your limbs have gone rock hard!"));
						((EntityPlayer)trackedEntity).addChatComponentMessage(new ChatComponentText("Your condition is now permanent!"));
					}
				}
				
				
				if (this.side.isClient()) {
					playSoundWithTimeCheck(1700, "enviromine:chill",  UI_Settings.breathVolume, 1.0F);
				}
			} else if(!frostIrreversible || !enableFrostbite)
			{
				frostbiteLevel = 0;
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
		
		DimensionProperties dimensionProp = null;
		
		if(EM_Settings.dimensionProperties.containsKey(trackedEntity.worldObj.provider.dimensionId))
		{ 
				
			dimensionProp = EM_Settings.dimensionProperties.get(trackedEntity.worldObj.provider.dimensionId);
			if(dimensionProp != null && dimensionProp.override)
			{   
				if(!dimensionProp.trackTemp && EM_Settings.enableBodyTemp) bodyTemp = prevBodyTemp;
				if(!dimensionProp.trackAirQuality && EM_Settings.enableAirQ) airQuality = prevAirQuality;
				if(!dimensionProp.trackHydration && EM_Settings.enableHydrate) hydration = prevHydration;
				if(!dimensionProp.trackSanity && EM_Settings.enableSanity) sanity = prevSanity;
								
			}
		}

		
		
		
		this.fixFloatinfPointErrors();
		EM_StatusManager.saveTracker(this);
	}
	
	@SideOnly(Side.CLIENT)
	private void playSoundWithTimeCheck(int time, String sound, float volume, float pitch)
	{
		if ((Minecraft.getSystemTime() - chillPrevTime) > 17000)
		{
			Minecraft.getMinecraft().thePlayer.playSound("enviromine:chill",  UI_Settings.breathVolume, 1.0F);
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
			EnviroDataTracker tracker = EM_StatusManager.lookupTracker(entity);
			
			if(tracker != null && !tracker.isDisabled && tracker.trackedEntity == entity)
			{
				return false;
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
	
	public void resetData()
	{
		airQuality = 100F;
		bodyTemp = 37F;
		hydration = 100F;
		sanity = 100F;
	}
	
	public void ClampSafeRange()
	{
		airQuality = MathHelper.clamp_float(airQuality, 25F, 100F);
		bodyTemp = MathHelper.clamp_float(bodyTemp, 35F, 39F);
		hydration = MathHelper.clamp_float(hydration, 25F, 100F);
		sanity = MathHelper.clamp_float(sanity, 50F, 100F);
	}
}
