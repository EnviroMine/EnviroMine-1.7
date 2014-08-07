package enviromine.gases.types;

import java.awt.Color;

import enviromine.gases.EnviroGas;
import enviromine.gases.EnviroGasDictionary;
import enviromine.handlers.EM_StatusManager;
import enviromine.trackers.EnviroDataTracker;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class GasCarbonMonoxide extends EnviroGas
{
	public GasCarbonMonoxide(String name, int id)
	{
		super(name, id);
		this.setColor(new Color(64, 64, 64, 64));
		this.setDensity(-1F);
		this.setDecayRates(1, 0, 1, 100, 1, 100);
	}
	
	@Override
	public void applyEffects(EntityLivingBase entityLiving, int amplifier)
	{
		if(entityLiving.worldObj.isRemote)
		{
			return;
		}
		
		EnviroDataTracker tracker = EM_StatusManager.lookupTracker(entityLiving);
		
		if(tracker != null)
		{
			tracker.gasAirDiff -= 0.01F * amplifier;
		}
	}
	
	@Override
	public int getGasOnDeath(World world, int i, int j, int k)
	{
		return EnviroGasDictionary.carbonDioxide.gasID;
	}
}
