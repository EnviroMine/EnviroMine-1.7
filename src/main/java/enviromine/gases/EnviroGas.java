package enviromine.gases;

import java.awt.Color;
import enviromine.handlers.EM_StatusManager;
import enviromine.trackers.EnviroDataTracker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EnviroGas
{
	public int gasID;
	public float suffocation;
	public float volitility;
	public float UFL; // Upper Flammability Limit (0-1)
	public float LFL; // Lower Flammability Limit (0-1)
	public float density;
	public String name;
	public Color color;
	public int airDecay;
	public int normDecay;
	public int randDecay;
	public int airDecayThresh;
	public int normDecayThresh;
	public int randDecayThresh;
	
	public EnviroGas(String name, int ID)
	{
		this.gasID = ID;
		this.density = 0F;
		this.name = name;
		this.suffocation = 0F;
		this.volitility = 0F;
		this.color = Color.WHITE;
		this.airDecay = 0;
		this.normDecay = 0;
		this.randDecay = 0;
		this.airDecayThresh = 1;
		this.normDecayThresh = 1;
		this.randDecayThresh = 1;
		
		EnviroGasDictionary.addNewGas(this, gasID);
	}
	
	public EnviroGas setDensity(float newDen)
	{
		this.density = newDen;
		return this;
	}
	
	public EnviroGas setColor(Color newCol)
	{
		this.color = newCol;
		return this;
	}
	
	public EnviroGas setVolitility(float newVol, float lowerLimit, float upperLimit)
	{
		this.LFL = lowerLimit;
		this.UFL = upperLimit;
		this.volitility = newVol;
		return this;
	}
	
	public int getFire(int amount, int air)
	{
		if(this.volitility <= 0)
		{
			return 0;
		}
		
		float ratio = (float)amount/(float)air;
		int extra = Math.round(this.volitility * amount) - amount;
		float midPoint = (this.UFL + this.LFL)/2F;
		float diff = midPoint - this.LFL;
		
		if(ratio < this.LFL || ratio > this.UFL)
		{
			return 1;
		} else if(ratio == this.LFL || ratio == this.UFL)
		{
			return amount;
		} else
		{
			return amount + MathHelper.floor_float(extra * (diff - Math.abs(ratio - midPoint)));
		}
	}
	
	public EnviroGas setSuffocation(float newSuff)
	{
		this.suffocation = newSuff;
		return this;
	}
	
	public EnviroGas setDecayRates(int airDecay, int normDecay, int randDecay, int adt, int ndt, int rdt)
	{
		this.airDecay = airDecay;
		this.normDecay = normDecay;
		this.randDecay = randDecay;
		this.airDecayThresh = adt;
		this.normDecayThresh = ndt;
		this.randDecayThresh = rdt;
		return this;
	}
	
	public int[] react(World world, int i, int j, int k, int amount)
	{
		return new int[]{this.gasID, amount};
	}
	
	public void applyEffects(EntityLivingBase entityLiving, int amplifier)
	{
		if(entityLiving.worldObj.isRemote)
		{
			return;
		}
		
		EnviroDataTracker tracker = EM_StatusManager.lookupTracker(entityLiving);
		
		if(tracker != null)
		{
			tracker.gasAirDiff -= (this.suffocation * amplifier)/10;
		}
	}
	
	public float getOpacity()
	{
		return this.color.getAlpha()/255F;
	}
	
	public int getGasOnDeath(World world, int i, int j, int k)
	{
		return -1;
	}
}
