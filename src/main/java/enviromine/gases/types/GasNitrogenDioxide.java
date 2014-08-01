package enviromine.gases.types;

import java.awt.Color;
import enviromine.gases.EnviroGas;

public class GasNitrogenDioxide extends EnviroGas
{
	public GasNitrogenDioxide(String name, int ID)
	{
		super(name, ID);
		this.setColor(new Color(255, 255, 255, 0));
		this.setDensity(2F);
	}
}
