package enviromine.gases.types;

import java.awt.Color;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import enviromine.gases.EnviroGas;

public class GasHydrogenSulfide extends EnviroGas
{
	public GasHydrogenSulfide(String name, int ID)
	{
		super(name, ID);
		this.setColor(new Color(255, 255, 0, 64));
		this.setDensity(1F);
		this.setVolitility(100F);
	}
	
	@Override
	public void applyEffects(EntityLivingBase entityLiving, int amplifier)
	{
		if(entityLiving.worldObj.isRemote)
		{
			return;
		}
		
		if(amplifier > 3 && entityLiving.getRNG().nextInt(100) == 0)
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
