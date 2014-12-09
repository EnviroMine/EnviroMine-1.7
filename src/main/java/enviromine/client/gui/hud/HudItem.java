package enviromine.client.gui.hud;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import enviromine.client.gui.UI_Settings;
import enviromine.utils.Alignment;

/**
 * 
 * Extend this to create your own elements which render on the GUI.<br>
 * Don't worry about saves, they are all done by the non-api part of the mod.
 * 
 * @author maxpowa
 * 
 */
public abstract class HudItem {
    public Alignment alignment;
    public int posX;
    public int posY;
    public int phase;
    private int id;
    public boolean rotated = false;
    
    public boolean blink = false;
    public static int blinkTick = 0;
    
    public HudItem() 
    {
        alignment = getDefaultAlignment();
        posX = getDefaultPosX();
        posY = getDefaultPosY();
        id = getDefaultID();
        phase = 0;
        
    }

    /**
     * Unique name for the HudItem, only used for NBT saving/loading
     * 
     * @return String value for unique identifier of the {@link HudItem}
     */
    public abstract String getName();
    
    public abstract String getNameLoc();

    /**
     * Display name for the HudItem in config screen
     * 
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
	 * Used to check if Frame is blinking
	 * @blink
	 */
	public abstract boolean isBlinking();

	/**
     * Button ID for configuration screen, 0-25 are reserved for Vanilla use.
     */
    public abstract int getDefaultID();

    public abstract void render();

    public abstract void renderScreenOverlay(int scaledwidth, int scaledheight);
    
	public int getTextFrameWidth()
	{
		if(!UI_Settings.ShowText || rotated) return 0;
		else return 32;
	}
	
	
	public int getIconPosX()
	{
		if(UI_Settings.minimalHud && !rotated)
		{
			if(!isLeftSide())
				return posX - getTextFrameWidth() - 16;
			else
				return posX + getTextFrameWidth();
		}
		else
		{
			if(rotated)
			{
				return posX - 16 + (getHeight() / 2);
			}
			else if(!isLeftSide()) 
				return posX - getTextFrameWidth() - 16  + (rotated? (getHeight() / 2) : 0);
			else 
				return posX + getWidth() + getTextFrameWidth();
		}
	}
	
	public int getTextPosX()
	{
		
			if(UI_Settings.minimalHud) 
			{
				if(!isLeftSide())
					return posX - getTextFrameWidth();
				else
					return posX;
			}
			else
			{
				if(!isLeftSide())
					return posX - getTextFrameWidth();
				else 
					return posX + getWidth();
			}
	}
	
	public int getTotalBarWidth()
	{
		return  (getWidth() + getTextFrameWidth() + 16);
	}
	
	
	public boolean isLeftSide()
	{
		boolean Side = false;
		
		int ScreenHalf = HUDRegistry.screenWidth / 2;
		int BarPos =(getTotalBarWidth() / 2) + posX;
		
		if(BarPos <= ScreenHalf)
			Side = true;
		
		return Side;
	}
    /**
     * 
     * If you don't want any rotation, you can <br>simply make this method return.
     *
     */
    public void rotate() {
        rotated = !rotated;
    }
    
    public boolean blink()
    {
		// count gui ticks
		if(blinkTick >= 60)
		{
			blink = !blink;
			blinkTick = 1;
		}
		
		return blink;
    }

    /**
     * Called upon .updateTick(). If you use this, make sure you set<br>
     * {@link HudItem}.needsTick() to true.
     */
    public void tick() {

    }

    /**
     * Set this to true if you require the {@link HudItem}.tick() method to run<br>
     */
    public boolean needsTick() {
        return false;
    }

    public boolean isMoveable() {
        return true;
    }

    public boolean isEnabledByDefault() {
        return true;
    }

    public boolean isRenderedInCreative() {
        return true;
    }

    /**
     * Ensures that the HudItem will never be off the screen
     */
    public void fixBounds() {
        posX = Math.max(0, Math.min(HUDRegistry.screenWidth - (int)( getWidth() * UI_Settings.guiScale), posX));
        posY = Math.max(0, Math.min(HUDRegistry.screenHeight - (int)(getHeight() * UI_Settings.guiScale), posY));
    }

    public void loadFromNBT(NBTTagCompound nbt) {
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
        if (nbt.hasKey("rotated")) {
            rotated = nbt.getBoolean("rotated");
        } else {
            rotated = false;
        }
        
    }

    public void saveToNBT(NBTTagCompound nbt) {
        nbt.setInteger("posX", posX);
        nbt.setInteger("posY", posY);
        nbt.setString("alignment", alignment.toString());
        nbt.setInteger("id", id);
        nbt.setBoolean("rotated", rotated);
    }

    public boolean shouldDrawOnMount() {
        return true;
    }

    public boolean shouldDrawAsPlayer() {
        return true;
    }

    public boolean canRotate() {
        return true;
    }


}