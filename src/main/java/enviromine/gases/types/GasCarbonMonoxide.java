package enviromine.gases.types;

import java.awt.Color;
import enviromine.gases.EnviroGas;
import enviromine.gases.EnviroGasDictionary;
import enviromine.handlers.ObjectHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class GasCarbonMonoxide extends EnviroGas
{
	public GasCarbonMonoxide(String name, int id)
	{
		super(name, id);
		this.setColor(new Color(64, 64, 64, 64));
		this.setDensity(-1F);
		this.setDecayRates(1, 0, 1, 100, 1, 100);
		this.setSuffocation(0.1F);
	}
	
	@Override
	public int getGasOnDeath(World world, int i, int j, int k)
	{
		return EnviroGasDictionary.carbonDioxide.gasID;
	}
	
	@Override
	public void applyEffects(EntityLivingBase entityLiving, int amplifier)
	{
		super.applyEffects(entityLiving, amplifier);
		
		if(entityLiving.worldObj.isRemote || entityLiving.isEntityUndead() || (entityLiving.getEquipmentInSlot(4) != null && entityLiving.getEquipmentInSlot(4).getItem() == ObjectHandler.gasMask))
		{
			return;
		}
		
		if(amplifier >= 5 && entityLiving.getRNG().nextInt(10) == 0)
		{
			entityLiving.addPotionEffect(new PotionEffect(Potion.poison.id, 200));
		}
	}
}
