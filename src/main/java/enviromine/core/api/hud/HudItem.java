package enviromine.core.api.hud;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import enviromine.client.gui.UI_Settings;
import enviromine.utils.Alignment;

public abstract class HudItem 
{
	  public Alignment alignment;
	    public int posX;
	    public int posY;
	    public int phase;
	    private int id;
	    
	    public HudItem() 
	    {
	        alignment = getDefaultAlignment();
	        posX = getDefaultPosX();
	        posY = getDefaultPosY();
	        id = getDefaultID();
	        phase = 0;
	    }

	    /**
	     * Unique name for the HudItem, only used for NBT saving/loading <br>
	     * Or getting {@link HudItem} from HudRegistry
	     * @return String value for unique identifier of the {@link HudItem}
	     */
	    public abstract String getUnLocolizedName();
	    
	    public abstract String getLocalizedName();

	    /**
	     * Should add button to the Enviromine Menu
	     * @return
	     */
	    public abstract boolean isInMenu();
	    
	    /**
	     * Display name for the HudItem in config screen. If you use make sure <br>
	     * {@link HudItem}.isInMenu() to true
	     * @return String value for display name of the {@link HudItem}
	     */
	    public abstract String getButtonLabel();
	    
	    /**
	     * Default {@link Alignment} of the HudItem instance.
	     * <p>
	     * For resolution-based movement, alignment values allow shifting along the
	     * alignment axis.
	     * 
	     * @return {@link Alignment}
	     */
	    public abstract Alignment getDefaultAlignment();

	    public abstract int getDefaultPosX();

	    public abstract int getDefaultPosY();

	    public abstract int getWidth();

	    public abstract int getHeight();
	    
	    public abstract ResourceLocation getResource(String type);
	    
		/**
	     * Button ID for configuration screen, 0-25 are reserved for Vanilla use.
	     */
	    public abstract int getDefaultID();

	    /**
	     * Render {@link HudItem} on screen 
	     */
	    public abstract void render();

	    /**
	     * Render a screen Overlay when certain condition are meet.
	     * 
	     * @param scaledwidth
	     * @param scaledheight
	     */
	    public void renderScreenOverlay(int scaledwidth, int scaledheight){};
	    
	    /** 
	     * Find out if your {@link HudItem} is on the left or right side of the screen. <br>
	     * Use this if you have a different way to render your {@link HudItem}, Mesured from <br>
	     * center of the screen.
	     * 
	     * @return {@link boolean}
	     */
		public boolean isLeftSide()
		{
			boolean Side = false;
			
			int ScreenHalf = HUDRegistry.screenWidth / 2;
			int BarPos =(getWidth() / 2) + posX;
			
			if(BarPos <= ScreenHalf)
				Side = true;
			
			return Side;
		}

	    
	    /**
	     * Called upon .updateTick(). If you use this, make sure you set<br>
	     * {@link HudItem}.needsTick() to true.
	     */
	    public void tick() 
	    {

	    }

	    /**
	     * Set this to true if you require the {@link HudItem}.tick() method to run<br>
	     */
	    public boolean needsTick() 
	    {
	        return false;
	    }

	    public boolean isMoveable() 
	    {
	        return true;
	    }

	    public boolean isEnabledByDefault() 
	    {
	        return true;
	    }

	    public boolean isRenderedInCreative() 
	    {
	        return true;
	    }

	    /**
	     * Ensures that the HudItem will never be off the screen
	     */
	    public void fixBounds() 
	    {
	        posX = Math.max(0, Math.min(HUDRegistry.screenWidth - (int)(getWidth() * UI_Settings.guiScale), posX));
	        posY = Math.max(0, Math.min(HUDRegistry.screenHeight - (int)(getHeight() * UI_Settings.guiScale), posY));
	    }

	    public void loadFromNBT(NBTTagCompound nbt) 
	    {
	        if (nbt.hasKey("posX")) {
	            posX = nbt.getInteger("posX");
	        } else {
	            posX = getDefaultPosX();
	        }
	        if (nbt.hasKey("posY")) {
	            posY = nbt.getInteger("posY");
	        } else {
	            posY = getDefaultPosY();
	        }
	        if (nbt.hasKey("alignment")) {
	            alignment = Alignment.fromString(nbt.getString("alignment"));
	        } else {
	            alignment = getDefaultAlignment();
	        }
	        if (nbt.hasKey("id")) {
	            id = nbt.getInteger("id");
	        } else {
	            id = getDefaultID();
	        }
	        
	        if(this instanceof IRotate)
	        {
	        	IRotate rotate = (IRotate) this;
	        	
	        	if (nbt.hasKey("rotated")) 
	        	{
	        		rotate.setRotated(nbt.getBoolean("rotated"));
	        	} else 
	        	{
	        		rotate.setRotated(false);
	        	}
	        }
	    }

	    public void saveToNBT(NBTTagCompound nbt) 
	    {
	        nbt.setInteger("posX", posX);
	        nbt.setInteger("posY", posY);
	        nbt.setString("alignment", alignment.toString());
	        nbt.setInteger("id", id);
	        
	        if(this instanceof IRotate)
	        {
	        	IRotate rotate = (IRotate) this;
	        	
	        	nbt.setBoolean("rotated", rotate.isRotated());
	        }
	    }

	    public boolean shouldDrawOnMount() 
	    {
	        return true;
	    }

	    public boolean shouldDrawAsPlayer() 
	    {
	        return true;
	    }

}