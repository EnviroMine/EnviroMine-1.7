package enviromine.core.api.helpers;

/**
 * Block scanning assistant for the PropertyTracker class
 */
public interface IPropScanner
{
	/**
	 * 3D diameter of the scan range around the entity
	 */
	public int ScanDiameter();
	
	/**
	 * How many scan passes are made per tick<br>
	 * Recommended to be a factor of diameter^3
	 */
	public int ScansPerTick();
	
	/**
	 * Does a scan pass of a block within range of the tracked entity.<br>
	 * It may be beneficial to apply/reset any calculations on pass 0
	 */
	public void DoScan(int x, int y, int z, int pass);
}
