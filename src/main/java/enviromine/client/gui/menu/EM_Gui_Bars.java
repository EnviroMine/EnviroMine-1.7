package enviromine.client.gui.menu;

import java.math.BigDecimal;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import enviromine.EnviroUtils;
import enviromine.client.gui.EM_GuiEnviroMeters;

@SideOnly(Side.CLIENT)
public class EM_Gui_Bars extends GuiScreen
{
	private GuiScreen parentGuiScreen;
	private int barwidth = 0;

	public EM_Gui_Bars(GuiScreen par1GuiScreen)
	{
		this.parentGuiScreen = par1GuiScreen;
	}
	//id, x, y, width, height, text

	@Override
	public void initGui()
	{
		
		barwidth = EM_GuiEnviroMeters.barWidth;
		
		if(UI_Settings.ShowText == true) barwidth += EM_GuiEnviroMeters.textWidth;
		if(UI_Settings.ShowGuiIcons == true) barwidth += EM_GuiEnviroMeters.iconWidth;
				
		int[] pos = getPos(UI_Settings.oxygenBarPos);
		//pos[0] = offsetButton(pos[0]);
		DragAndDrop Air = new DragAndDrop(100, pos[1]-2, pos[0]-2,barwidth+3, 13, "Oxygen Bar", EnviroUtils.getColorFromRGBA(44, 253, 255, 75));
		this.buttonList.add(Air);
		
		pos = getPos(UI_Settings.waterBarPos);
		DragAndDrop Hydration = new DragAndDrop(102, pos[1]-2, pos[0]-2, barwidth+3, 13, "Hydration Bar", EnviroUtils.getColorFromRGBA(0, 18, 255, 75));
		this.buttonList.add(Hydration);

		pos = getPos(UI_Settings.sanityBarPos);
		DragAndDrop Sanity = new DragAndDrop(103, pos[1]-2, pos[0]-2, barwidth+3, 13, "Sanity Bar", EnviroUtils.getColorFromRGBA(160, 0, 158, 75));
		this.buttonList.add(Sanity);
		
		pos = getPos(UI_Settings.heatBarPos);
		DragAndDrop Temp = new DragAndDrop(104, pos[1]-2, pos[0]-2, barwidth+3, 13, "Temprature Bar", EnviroUtils.getColorFromRGBA(198, 44, 44, 75));
		this.buttonList.add(Temp);
		
		this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, StatCollector.translateToLocal("gui.back")));

	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}
	
	//TODO Needs to be better.
	@Override
	public void mouseClickMove(int p_146273_1_, int p_146273_2_, int lastbutton, long time) 
	{
		DragAndDrop airButton = (DragAndDrop) this.buttonList.get(0);
		DragAndDrop hydratButton = (DragAndDrop) this.buttonList.get(1);
		DragAndDrop sanButton = (DragAndDrop) this.buttonList.get(2);
		DragAndDrop tempButton = (DragAndDrop) this.buttonList.get(3);

		if(airButton !=null && airButton.dragging)
		{
			
			int PosY = new BigDecimal(String.valueOf((((double)airButton.yPosition/(double)this.height)*100))).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			int PosX = new BigDecimal(String.valueOf((((double)airButton.xPosition/(double)this.width)*100))).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			UI_Settings.oxygenBarPos = "custom_"+ PosX +","+ PosY;
		}
		else if(hydratButton !=null && hydratButton.dragging)
		{
			
			int PosY = new BigDecimal(String.valueOf((((double)hydratButton.yPosition/(double)this.height)*100))).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			int PosX = new BigDecimal(String.valueOf((((double)hydratButton.xPosition/(double)this.width)*100))).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			UI_Settings.waterBarPos = "custom_"+ PosX +","+ PosY;
		}
		else if(sanButton !=null && sanButton.dragging)
		{
			
			int PosY = new BigDecimal(String.valueOf((((double)sanButton.yPosition/(double)this.height)*100))).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			int PosX = new BigDecimal(String.valueOf((((double)sanButton.xPosition/(double)this.width)*100))).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			UI_Settings.sanityBarPos = "custom_"+ PosX +","+ PosY;
		}
		else if(tempButton !=null && tempButton.dragging)
		{
			
			int PosY = new BigDecimal(String.valueOf((((double)tempButton.yPosition/(double)this.height)*100))).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			int PosX = new BigDecimal(String.valueOf((((double)tempButton.xPosition/(double)this.width)*100))).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			UI_Settings.heatBarPos = "custom_"+ PosX +","+ PosY;
		}

	}
	/**
	 * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	public void actionPerformed(GuiButton par1GuiButton)
	{

		if(par1GuiButton.enabled)
		{
			switch(par1GuiButton.id)
			{
				case 200:
					this.mc.displayGuiScreen(parentGuiScreen);
					return;
						
			}
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		//this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("emoptions.guiBars.title"), this.width / 2, 15, 16777215);
		super.drawScreen(par1, par2, par3);
	}
	
	public int[] getPos(String Splitter)
	{

			 Splitter = Splitter.toLowerCase().trim();
			
			 Splitter = Splitter.replaceFirst("custom_", "").trim();
				String pos[] = Splitter.split(",");
				int intPos[] = new int[2];

				if(pos.length == 2)
				{
					int cX = Integer.parseInt(pos[0].trim());
					int cY = Integer.parseInt(pos[1].trim());

					intPos[0] = MathHelper.floor_float(cY/100F * (float)this.height);
					intPos[1] = MathHelper.floor_float(cX/100F * (float)this.width);

						

				}
		
		return intPos;
	}
	
	private int offsetButton(int xPos)
	{
		
		return height;
	}
}
