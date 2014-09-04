package enviromine.gases.types;

import java.awt.Color;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import enviromine.gases.EnviroGas;
import enviromine.handlers.EM_StatusManager;
import enviromine.trackers.EnviroDataTracker;

public class GasSulfurDioxide extends EnviroGas
{
	public GasSulfurDioxide(String name, int ID)
	{
		super(name, ID);
		this.setColor(new Color(192, 192, 0, 0));
		this.setDensity(3F);
		this.setSuffocation(0.01F);
	}
	
	@Override
	public void applyEffects(EntityLivingBase entityLiving, int amplifier)
	{
		super.applyEffects(entityLiving, amplifier);
		
		if(entityLiving.worldObj.isRemote)
		{
			return;
		}
		
		if(amplifier > 5 && entityLiving.getRNG().nextInt(100) == 0)
		{
			if(amplifier >= 10)
			{
				entityLiving.addPotionEffect(new PotionEffect(Potion.poison.id, 600));
			} else
			{
				entityLiving.addPotionEffect(new PotionEffect(Potion.poison.id, 200));
			}
		}
	}
}
