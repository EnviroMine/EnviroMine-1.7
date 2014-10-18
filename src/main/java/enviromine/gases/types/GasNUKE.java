package enviromine.gases.types;

import java.awt.Color;
import enviromine.gases.EnviroGas;

public class GasNUKE extends EnviroGas
{
	public GasNUKE(String name, int ID)
	{
		super(name, ID);
		this.setColor(new Color(255, 0, 0, 255));
		this.setVolitility(1000F, 0F, 1F);
		this.setDensity(100F);
	}
}
