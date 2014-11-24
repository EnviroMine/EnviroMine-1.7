package enviromine;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.client.gui.EM_GuiFakeDeath;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.handlers.EM_StatusManager;
import enviromine.handlers.EnviroAchievements;
import enviromine.trackers.EnviroDataTracker;

public class EnviroPotion extends Potion
{
	public static EnviroPotion hypothermia;
	public static EnviroPotion heatstroke;
	public static EnviroPotion frostbite;
	public static EnviroPotion dehydration;
	public static EnviroPotion insanity;
	
	public static ResourceLocation textureResource = new ResourceLocation("enviromine", "textures/gui/status_Gui.png");
	
	public EnviroPotion(int par1, boolean par2, int par3)
	{
		super(par1, par2, par3);
	}
	
	public static void RegisterPotions()
	{
		EnviroPotion.frostbite = ((EnviroPotion)new EnviroPotion(EM_Settings.frostBitePotionID, true, 8171462).setPotionName("potion.enviromine.frostbite")).setIconIndex(0, 0);
		EnviroPotion.dehydration = ((EnviroPotion)new EnviroPotion(EM_Settings.dehydratePotionID, true, 3035801).setPotionName("potion.enviromine.dehydration")).setIconIndex(1, 0);
		EnviroPotion.insanity = ((EnviroPotion)new EnviroPotion(EM_Settings.insanityPotionID, true, 5578058).setPotionName("potion.enviromine.insanity")).setIconIndex(2, 0);
		EnviroPotion.heatstroke = ((EnviroPotion)new EnviroPotion(EM_Settings.heatstrokePotionID, true, EnviroUtils.getColorFromRGBA(255, 0, 0, 255)).setPotionName("potion.enviromine.heatstroke")).setIconIndex(3, 0);
		EnviroPotion.hypothermia = ((EnviroPotion)new EnviroPotion(EM_Settings.hypothermiaPotionID, true, 8171462).setPotionName("potion.enviromine.hypothermia")).setIconIndex(4, 0);
	}
	
	public static void checkAndApplyEffects(EntityLivingBase entityLiving)
	{
		if(entityLiving.worldObj.isRemote)
		{
			return;
		}
		
		EnviroDataTracker tracker = EM_StatusManager.lookupTracker(entityLiving);
		
		if(entityLiving.isPotionActive(heatstroke))
		{
			if(entityLiving.getActivePotionEffect(heatstroke).getDuration() == 0)
			{
				entityLiving.removePotionEffect(heatstroke.id);
			}
			
			PotionEffect effect = entityLiving.getActivePotionEffect(heatstroke);
			
			if(effect.getAmplifier() >= 2 && entityLiving.getRNG().nextInt(2) == 0)
			{
				entityLiving.attackEntityFrom(EnviroDamageSource.heatstroke, 4.0F);
			}
			
			if(effect.getAmplifier() >= 1)
			{
				entityLiving.addPotionEffect(new PotionEffect(Potion.weakness.id, 200, 0));
				entityLiving.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 200, 0));
				entityLiving.addPotionEffect(new PotionEffect(Potion.hunger.id, 200, 0));
				
				if(entityLiving.getRNG().nextInt(10) == 0)
				{
					entityLiving.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 0));
				}
			}
		}
		
		if(entityLiving.isPotionActive(hypothermia))
		{
			PotionEffect effect = entityLiving.getActivePotionEffect(hypothermia);
			
			if(effect.getDuration() == 0)
			{
				entityLiving.removePotionEffect(hypothermia.id);
			}
			
			if(effect.getAmplifier() >= 2 && entityLiving.getRNG().nextInt(2) == 0)
			{
				entityLiving.attackEntityFrom(EnviroDamageSource.organfailure, 4.0F);
			}
			
			if(effect.getAmplifier() >= 1)
			{
				entityLiving.addPotionEffect(new PotionEffect(Potion.weakness.id, 200, 0));
				entityLiving.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 200, 0));
			}
		}
		if(entityLiving.isPotionActive(frostbite))
		{
			if(entityLiving.getActivePotionEffect(frostbite).getDuration() == 0)
			{
				entityLiving.removePotionEffect(frostbite.id);
			}
			
			if(entityLiving.getRNG().nextInt(2) == 0 && entityLiving.getActivePotionEffect(frostbite).getAmplifier() >= 2)
			{
				entityLiving.attackEntityFrom(EnviroDamageSource.frostbite, 4.0F);
			}
			
			if(entityLiving.getHeldItem() != null)
			{
				if(entityLiving.getActivePotionEffect(EnviroPotion.frostbite) != null)
				{
					if(entityLiving.getRNG().nextInt(20) == 0)
					{
						EntityItem item = entityLiving.entityDropItem(entityLiving.getHeldItem(), 0.0F);
						item.delayBeforeCanPickup = 40;
						entityLiving.setCurrentItemOrArmor(0, null);
					
						entityLiving.worldObj.playSoundAtEntity(entityLiving, "enviromine:shiver", 1f, 1f);
						
						if(entityLiving instanceof EntityPlayer)
						{
							((EntityPlayer)entityLiving).addStat(EnviroAchievements.iNeededThat, 1);
						}
					}
				}
			}
		}
		if(entityLiving.isPotionActive(dehydration.id))
		{
			if(entityLiving.getActivePotionEffect(dehydration).getDuration() == 0)
			{
				entityLiving.removePotionEffect(dehydration.id);
			}
			
			if(tracker != null)
			{
				tracker.dehydrate(1F + (entityLiving.getActivePotionEffect(dehydration).getAmplifier() * 1F));
			}
		}
		if(entityLiving.isPotionActive(insanity.id))
		{
			PotionEffect effect = entityLiving.getActivePotionEffect(insanity);
			if(effect.getDuration() == 0)
			{
				entityLiving.removePotionEffect(insanity.id);
			}
			
			int chance = 50 / (effect.getAmplifier() + 1);
			
			chance = chance > 0? chance : 1;
			
			if(entityLiving.getRNG().nextInt(chance) == 0)
			{
				if(effect.getAmplifier() >= 1)
				{
					entityLiving.addPotionEffect(new PotionEffect(Potion.confusion.id, 200));
				}
			}
			
			if(effect.getAmplifier() >= 2 && entityLiving.getRNG().nextInt(1000) == 0 && EnviroMine.proxy.isClient())
			{
				if(Minecraft.getMinecraft().currentScreen == null)
				{
					Minecraft.getMinecraft().displayGuiScreen(new EM_GuiFakeDeath());
				}
			}
			
			String sound = "";
			if(entityLiving.getRNG().nextInt(chance) == 0 && entityLiving instanceof EntityPlayer)
			{
				switch(entityLiving.getRNG().nextInt(16))
				{
					case 0:
					{
						sound = "ambient.cave.cave";
						break;
					}
					case 1:
					{
						sound = "random.explode";
						break;
					}
					case 2:
					{
						sound = "creeper.primed";
						break;
					}
					case 3:
					{
						sound = "mob.zombie.say";
						break;
					}
					case 4:
					{
						sound = "mob.endermen.idle";
						break;
					}
					case 5:
					{
						sound = "mob.skeleton.say";
						break;
					}
					case 6:
					{
						sound = "mob.wither.idle";
						break;
					}
					case 7:
					{
						sound = "mob.spider.say";
						break;
					}
					case 8:
					{
						sound = "ambient.weather.thunder";
						break;
					}
					case 9:
					{
						sound = "liquid.lava";
						break;
					}
					case 10:
					{
						sound = "liquid.water";
						break;
					}
					case 11:
					{
						sound = "mob.ghast.moan";
						break;
					}
					case 12:
					{
						sound = "random.bowhit";
						break;
					}
					case 13:
					{
						sound = "game.player.hurt";
						break;
					}
					case 14:
					{
						sound = "mob.enderdragon.growl";
						break;
					}
					case 15:
					{
						sound = "mob.endermen.portal";
						break;
					}
				}
				
				EntityPlayer player = ((EntityPlayer)entityLiving);
				
				float rndX = (player.getRNG().nextInt(6) - 3) * player.getRNG().nextFloat();
				float rndY = (player.getRNG().nextInt(6) - 3) * player.getRNG().nextFloat();
				float rndZ = (player.getRNG().nextInt(6) - 3) * player.getRNG().nextFloat();
				
				S29PacketSoundEffect packet = new S29PacketSoundEffect(sound, entityLiving.posX + rndX, entityLiving.posY + rndY, entityLiving.posZ + rndZ, 1.0F, player.getRNG().nextBoolean()? 0.2F : (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.2F + 1.0F);
				
				if(!EnviroMine.proxy.isClient() && player instanceof EntityPlayerMP)
				{
					((EntityPlayerMP)player).playerNetServerHandler.sendPacket(packet);
				} else if(EnviroMine.proxy.isClient() && !player.worldObj.isRemote)
				{
					player.worldObj.playSoundEffect(entityLiving.posX + rndX, entityLiving.posY + rndY, entityLiving.posZ + rndZ, sound, 1.0F, (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.2F + 1.0F);
				}
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Returns true if the potion has a associated status icon to display in then inventory when active.
	 */
	public boolean hasStatusIcon()
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(textureResource);
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * This method returns true if the potion effect is bad - negative - for the entity.
	 */
	public boolean isBadEffect()
	{
		return true;
	}

    /**
     * Sets the index for the icon displayed in the player's inventory when the status is active.
     */
	@Override
    public EnviroPotion setIconIndex(int p_76399_1_, int p_76399_2_)
    {
        return (EnviroPotion)super.setIconIndex(p_76399_1_, p_76399_2_);
    }
}
