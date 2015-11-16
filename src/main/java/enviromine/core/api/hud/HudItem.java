package enviromine.core.api.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.utils.Alignment;

@SideOnly(Side.CLIENT)
public abstract class HudItem
{
	public final String ID;
	public boolean enabled = true;
	public Alignment alignment = Alignment.TOPLEFT;
	public float scale = 1F;
	public int posX = 16;
	public int posY = 16;
	
	public HudItem(String ID)
	{
		this.ID = ID;
	}
	
	/**
	 * Unique name for the HudItem, only used for NBT saving/loading <br>
	 * Or getting {@link HudItem} from HudRegistry
	 * @return String value for unique identifier of the {@link HudItem}
	 */
	public abstract String getUnlocalizedName();
	
	public String getLocalizedName()
	{
		return StatCollector.translateToLocal(this.getUnlocalizedName());
	}
	
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
	public String getButtonLabel() // This could probably be removed
	{
		return this.getLocalizedName();
	}
	
	public abstract int getWidth();
	
	public abstract int getHeight();
	
	/**
	 * What layer(s) should this HudItem render on. Defaults to the HELMET layer
	 */
	public boolean canRenderOnLayer(RenderGameOverlayEvent.ElementType layer)
	{
		return layer == RenderGameOverlayEvent.ElementType.HELMET;
	}
	
	public abstract ResourceLocation getResource(String type);
	
	/**
	 * Render {@link HudItem} on screen 
	 */
	//public abstract void render();
	
	/**
	 * Render a screen Overlay when certain condition are meet.
	 * @param displayHeight 
	 * @param displayWidth 
	 * @param type 
	 * 
	 * @param scaledwidth
	 * @param scaledheight
	 */
	/*public void renderScreenOverlay(int scaledwidth, int scaledheight)
	{
	}*/
	
	public void renderHud(ElementType layer, int dispWidth, int dispHeight)
	{
		
	}
	
	/** 
	 * Find out if your {@link HudItem} is on the left or right side of the screen. <br>
	 * Use this if you have a different way to render your {@link HudItem}, Mesured from <br>
	 * center of the screen.
	 * 
	 * @return {@link boolean}
	 */
	/*public boolean isLeftSide()
	{
		boolean Side = false;
		
		int ScreenHalf = HUDRegistry.screenWidth / 2;
		int BarPos = (getWidth() / 2) + posX;
		
		if(BarPos <= ScreenHalf)
			Side = true;
		
		return Side;
	}*/
	
	/**
	 * Called upon .updateTick(). If you use this, make sure you set<br>
	 * {@link HudItem}.needsTick() to true.
	 */
	/*public void tick() // Not sure why you would need to tick a renderer
	{
		
	}*/
	
	/**
	 * Set this to true if you require the {@link HudItem}.tick() method to run<br>
	 */
	/*public boolean needsTick()
	{
		return false;
	}*/
	
	/**
	 * @deprecated This should always be true as we cannot guarantee an arbitrary number of items will automatically fit
	 */
	@Deprecated
	public boolean isMoveable()
	{
		return true;
	}
	
	/**
	 * @deprecated The default value(s) should be set in the constructor
	 */
	@Deprecated
	public boolean isEnabledByDefault()
	{
		return true;
	}
	
	/**
	 * @deprecated Use a modified 'isEnabled' to determine this
	 */
	@Deprecated
	public boolean isRenderedInCreative()
	{
		return true;
	}
	
	/**
	 * @deprecated Use a modified 'isEnabled' to determine this
	 */
	@Deprecated
	public boolean shouldDrawOnMount()
	{
		return true;
	}
	
	/**
	 * @deprecated Use a modified 'isEnabled' to determine this
	 */
	@Deprecated
	public boolean shouldDrawAsPlayer()
	{
		return true;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	/**
	 * Ensures that the HudItem will never be off the screen
	 */
	public void clampToScreen()
	{
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution scaledRes = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		posX = MathHelper.clamp_int(posX, 0, scaledRes.getScaledWidth() - MathHelper.ceiling_float_int(getWidth() * scale));
		posY = MathHelper.clamp_int(posY, 0, scaledRes.getScaledHeight() - MathHelper.ceiling_float_int(getHeight() * scale));
	}
	
	public void loadFromNBT(NBTTagCompound nbt)
	{
		if(nbt.hasKey("posX"))
		{
			posX = nbt.getInteger("posX");
		} else
		{
			posX = 16;
		}
		if(nbt.hasKey("posY"))
		{
			posY = nbt.getInteger("posY");
		} else
		{
			posY = 16;
		}
		if(nbt.hasKey("alignment"))
		{
			alignment = Alignment.fromString(nbt.getString("alignment"));
		} else
		{
			alignment = Alignment.TOPLEFT;
		}
		
		if(this instanceof IRotate)
		{
			IRotate rotate = (IRotate)this;
			
			if(nbt.hasKey("rotated"))
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
		
		if(this instanceof IRotate)
		{
			IRotate rotate = (IRotate)this;
			
			nbt.setBoolean("rotated", rotate.isRotated());
		}
	}
}