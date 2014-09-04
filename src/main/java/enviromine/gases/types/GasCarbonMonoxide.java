package enviromine.gases.types;

import java.awt.Color;

import enviromine.gases.EnviroGas;
import enviromine.gases.EnviroGasDictionary;
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
}
