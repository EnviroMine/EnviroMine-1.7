package enviromine.core.api.hud;

public interface IRotate {
	/**
	 * Set true if this item can rotate, Setting this to true will <br>
	 * add a rotate button to the enviromine menu ({@link HudItem}.isInMenu) <br>
	 *  has to be set to true.
	 *  
	 * @return boolean
	 */
	public abstract boolean canRotate();
	
	/**
	 * is {@link HudItem} rotated
	 * 
	 * @return boolean
	 */
	public abstract boolean isRotated();
	
	public abstract void setRotated(boolean value);
	
}
