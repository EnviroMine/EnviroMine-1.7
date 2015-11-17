package enviromine.core.api.hud;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class HudItem
{
	public final String ID;
	public boolean enabled = true;
	public Align alignment = Align.TOP_LEFT;
	public float scale = 1F;
	public float rotation = 0F;
	public int offsetX = 16;
	public int offsetY = 16;
	
	public HudItem(String ID)
	{
		this.ID = ID;
	}
	
	public abstract String getUnlocalizedName();
	
	public String getLocalizedName()
	{
		return StatCollector.translateToLocal(this.getUnlocalizedName());
	}
	
	/**
	 * Should this add button to the Enviromine Menu
	 */
	public boolean isInMenu()
	{
		return true; // Override if you really want to change this value
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
	
	/**
	 * Called before rendering the actual HUD. This offsets, rotates and scales before calling the true render method
	 */
	public final void preRenderHud(ElementType layer, int dispWidth, int dispHeight)
	{
		GL11.glPushMatrix();
		
		/* Debug stuffs for testing the UI
		Align alignment = Align.BOT_CENTER_R;
		int offsetX = 0;
		int offsetY = 0;
		float rotation = -30F;
		float scale = 1F;
		*/
		
		int ox = alignment.OffsetX(offsetX, getWidth(), dispWidth);
		int oy = alignment.OffsetY(offsetY, getHeight(), dispHeight);
		
		if(alignment.hFlip) // Correct rotation origin when flipped
		{
			Vec3 v = Vec3.createVectorHelper(getWidth(), 0F, 0F);
			v.rotateAroundZ((float)Math.toRadians(rotation));
			ox -= Math.round(v.xCoord) - getWidth();
			oy -= Math.round(v.yCoord);
		}
		
		GL11.glTranslatef(ox, oy, 0F);
		GL11.glRotatef(rotation * (alignment.hFlip? -1F : 1F), 0F, 0F, 1F);
		GL11.glScalef(scale, scale, scale);
		
		this.renderHud(layer, dispWidth, dispHeight);
		
		GL11.glPopMatrix();
	}
	
	/**
	 * Draws the HUD on the given layer.<br>
	 * <b>NOTE:</b> Offsets, rotations and scaling has already been pre-applied!
	 */
	public abstract void renderHud(ElementType layer, int dispWidth, int dispHeight);
	
	/**
	 * Used to determine whether this UI should render. Modify this if it is necessary to disable/enable it in specific cases such as riding entities
	 */
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void loadFromNBT(NBTTagCompound nbt)
	{
		offsetX = nbt.hasKey("offX")? nbt.getInteger("offX") : 16;
		offsetX = nbt.hasKey("offX")? nbt.getInteger("offX") : 16;
		scale = nbt.hasKey("scale")? nbt.getFloat("scale") : 1F;
		rotation = nbt.getFloat("rotation")%360F;
		alignment = Align.valueOf(nbt.getString("alignment").toString().toUpperCase());
		alignment = alignment != null? alignment : Align.TOP_LEFT;
	}
	
	public void saveToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("offX", offsetX);
		nbt.setInteger("offY", offsetY);
		nbt.setFloat("scale", scale);
		nbt.setFloat("rotation", rotation);
		nbt.setString("alignment", alignment.toString());
	}
	
	/**
	 * A rewritten version of the original Alignment enumerator types
	 */
	public static enum Align
	{
		TOP_LEFT		(0F, 0F, false),
		TOP_CENTER_L	(0.5F, 0F, true),
		TOP_CENTER_R	(0.5F, 0F, false),
		TOP_RIGHT		(1F, 0F, true),
		
		MID_LEFT		(0F, 0.5F, false),
		//MID_CENTER_L	(0.5F, 0.5F, true),  // Using these two alignments would
		//MID_CENTER_R	(0.5F, 0.5F, false), // just make things look terrible
		MID_RIGHT		(1F, 0.5F, true),
		
		BOT_LEFT		(0F, 1F, false),
		BOT_CENTER_L	(0.5F, 1F, true),
		BOT_CENTER_R	(0.5F, 1F, false),
		BOT_RIGHT		(1F, 1F, true);
		/**
		 * A number between 0 and 1 representing anchor X position
		 */
		public final float dx;
		
		/**
		 * A number between 0 and 1 representing anchor Y position
		 */
		public final float dy;
		
		/**
		 * Flips the offset to be relative to the right side of the element
		 */
		public final boolean hFlip;
		
		Align(float dx, float dy, boolean hFlip)
		{
			this.dx = dx;
			this.dy = dy;
			this.hFlip = hFlip;
		}
		
		public int OffsetX(int offset, int sizeX, int width)
		{
			if(hFlip)
			{
				return Math.round(width * dx) - (offset + sizeX);
			} else
			{
				return Math.round(width * dx) + offset;
			}
		}
		
		public int OffsetY(int offset, int sizeY, int height)
		{
			if(dy > 0.5F)
			{
				return height - (offset + sizeY);
			} else
			{
				return Math.round(height * dy) + offset;
			}
		}
	}
}