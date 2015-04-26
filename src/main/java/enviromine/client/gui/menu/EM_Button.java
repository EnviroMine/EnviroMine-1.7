package enviromine.client.gui.menu;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiUtils;
import enviromine.utils.RenderAssist;

public class EM_Button extends GuiButtonExt
{
	/**
	 * 
	 * @param id
	 * @param xPos
	 * @param yPos
	 * @param width
	 * @param height
	 * @param displayString
	 * @param displayUpdateString
	 */
	
	String displayUpdate = "";
	int TickCnt = 0;
	boolean draw;
	
    public EM_Button(int id, int xPos, int yPos, String displayString, String displayUpdate)
    {
        super(id, xPos, yPos, displayString);
		this.displayUpdate =  displayUpdate;
		this.draw = true;
    }
    
	public EM_Button(int id, int xPos, int yPos, int width, int height,	String displayString, String displayUpdate) 
	{
		super(id, xPos, yPos, width, height, displayString);
		
		this.displayUpdate =  displayUpdate;
		
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
	        if (this.visible)
	        {
	            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
	            int k = this.getHoverState(this.field_146123_n);
	            GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition, this.yPosition, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
	            this.mouseDragged(mc, mouseX, mouseY);
	            int color = 14737632;
	            
	            if (packedFGColour != 0)
	            {
	                color = packedFGColour;
	            }
	            else if (!this.enabled)
	            {
	                color = 10526880;
	            }
	            else if (this.field_146123_n)
	            {
	                color = 16777120;
	            }
	            
	            String buttonText = this.displayString;
	            String UpdateText = this.displayUpdate;
	            
	            int strWidth = mc.fontRenderer.getStringWidth(buttonText);
	            int upWidth = mc.fontRenderer.getStringWidth(UpdateText);
	            
	            int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
	            
	            if (strWidth+upWidth > width - 6 && strWidth + upWidth > ellipsisWidth)
	            	UpdateText = mc.fontRenderer.trimStringToWidth(UpdateText, width - 6 - ellipsisWidth).trim() + "...";
	               // buttonText = mc.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";
	            
	            this.drawCenteredString(mc.fontRenderer, buttonText, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, color);
	            
	            if(this.TickCnt > 50)
	            {
	            	this.draw = !this.draw;
	            	this.TickCnt=0;
	            }
	            
	            if(this.draw)this.drawString(mc.fontRenderer, UpdateText, this.xPosition + this.width - upWidth - 3, this.yPosition + (this.height - 8) / 2, RenderAssist.getColorFromRGBA(214, 242, 36, 0));
	            
	            this.TickCnt++;
	        }
	}

	
	
	
	
	
}
