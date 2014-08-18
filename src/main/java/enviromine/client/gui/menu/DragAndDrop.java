package enviromine.client.gui.menu;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.EnviroUtils;

@SideOnly(Side.CLIENT)
public class DragAndDrop extends GuiButtonExt
{

	boolean dragging;
	int mx, my;
	int color = 0;
	
	//private boolean isDraging;

	public DragAndDrop(int id, int xPos, int yPos, int width, int height, String displayString, int color)
    {
        super(id, xPos, yPos, width, height, displayString);
        this.mx = 0;
        this.my = 0;
        this.dragging = false;
        this.color = color;
      //  this.isDraging = false;
		// TODO Auto-generated constructor stub
	}
	
	public DragAndDrop(int id, int xPos, int yPos, String displayString)
    {
        super(id, xPos, yPos, displayString);
    }
	
	  @Override
	    public void drawButton(Minecraft mc, int mouseX, int mouseY)
	    {
	        if (this.visible)
	        {
            	int ScreenWidth = Minecraft.getMinecraft().displayWidth;
            	int ScreenHeight = Minecraft.getMinecraft().displayHeight;

           

	        	
	            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
	            int k = this.getHoverState(this.field_146123_n);
	            //GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition, this.yPosition, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
	            this.mouseDragged(mc, mouseX, mouseY);
	            
            	if (this.xPosition < 0)
            	{
            		this.xPosition = 0;
            	}
            	//System.out.println(" X: "+(this.xPosition + this.width)*2 + ">" +ScreenWidth );
            	//System.out.println(" Y: "+(this.yPosition + this.height)*2 + ">" +  ScreenHeight);
            	
            	if((this.xPosition + this.width)*2 > ScreenWidth )
            	{
            		//System.out.println("before x width"+ this.xPosition);
            		
            		this.xPosition = (ScreenWidth - (this.width*2))/2; 
            		
            		//System.out.println("after x width"+ this.xPosition);
            	}
            	
            	if (this.yPosition < 0)
            	{
            		this.yPosition = 0;
            	}
            	if((this.yPosition + this.height)*2 > ScreenHeight)
            	{
            		this.yPosition = (ScreenHeight - (this.height*2))/2; 
            		//.out.println("y width"+ this.yPosition);
            	}
            	
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
	            int strWidth = mc.fontRenderer.getStringWidth(buttonText);
	            int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
	            
	            if (strWidth > width - 6 && strWidth > ellipsisWidth)
	                buttonText = mc.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";
	            
	            this.drawCenteredString(mc.fontRenderer, buttonText, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, color);
	            
	            this.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, this.color);

	        }
	    }

	  protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3)
	    {
	        if (this.visible)
	        {
	            if (this.dragging)
	            {
 	//System.out.println(par2 + "," + par3);
	            	
	            	this.xPosition = par2 + this.mx;
	            	this.yPosition = par3 + this.my;
	            	

	            	
	            //    this.sliderValue = (par2 - (this.xPosition + 4)) / (float)(this.width - 8);
	           //     updateSlider();
	            }

	        //    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	         //   this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
	         //   this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
	        }
	    }
	  
	  public void mouseReleased(int p_146118_1_, int p_146118_2_) 
	  {

			  this.dragging = false;
			  System.out.println("Released");
	  }

	  public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
	  {
	        if (super.mousePressed(par1Minecraft, par2, par3))
	        {
	            this.dragging = true;
	            System.out.println("Pressed");
	            this.mx = this.xPosition - par2;
	            this.my = this.yPosition - par3;
	            
	            System.out.println("xpos:" + this.xPosition);
	            System.out.println("ypos:" + this.yPosition);
	            System.out.println("wdith:" + this.width);
	            System.out.println("height:" + this.height);
	            System.out.println("MouseOffset X:" + mx);
	            System.out.println("MouseOffset Y:" + my);
	            return true;
	        }
	        else
	        {
	            return false;
	        }
	       // return this.enabled && this.visible && p_146116_2_ >= this.xPosition && p_146116_3_ >= this.yPosition && p_146116_2_ < this.xPosition + this.width && p_146116_3_ < this.yPosition + this.height;
	  }
	  
	  public void updateDrag()
	  {
		  
	  }

}
