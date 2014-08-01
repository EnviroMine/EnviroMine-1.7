package enviromine.gases.types;

import java.awt.Color;
import enviromine.gases.EnviroGas;
import enviromine.gases.EnviroGasDictionary;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class GasFire extends EnviroGas
{
	public GasFire(String name, int ID)
	{
		super(name, ID);
		this.setColor(new Color(255, 128, 0, 64));
		this.setDensity(-10F);
		this.setDecayRates(1, 1, 1, 100, 100, 100);
	}
	
	@Override
	public void applyEffects(EntityLivingBase entityLiving, int amplifier)
	{
		entityLiving.attackEntityFrom(DamageSource.onFire, 0.5F * amplifier);
		entityLiving.setFire(10);
	}
	
	@Override
	public int getGasOnDeath(World world, int i, int j, int k)
	{
		return EnviroGasDictionary.carbonMonoxide.gasID;
	}
}
