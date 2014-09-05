package enviromine.gases.types;

import java.awt.Color;
import enviromine.gases.EnviroGas;

public class GasCarbonDioxide extends EnviroGas
{
	public GasCarbonDioxide(String name, int ID)
	{
		super(name, ID);
		this.setColor(new Color(255, 255, 255, 0));
		this.setDensity(1F);
		this.setDecayRates(1, 0, 0, 100, 1, 1);
		this.setSuffocation(0.01F);
	}
}
