package enviromine.client.gui.menu.config;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentText;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.client.config.GuiButtonExt;
import enviromine.core.EM_ConfigHandler;
import enviromine.core.EnviroMine;

public class GuiAddCustom extends GuiScreen
{
	private String objName = "null";
	private Block objBlock;
	private Item objItem;
	private boolean fail = false;
	
	public GuiAddCustom(Object obj)
	{
		
		if(obj instanceof Item)
		{
			try
			{
				this.objItem = (Item) obj;
				this.objBlock = Block.getBlockFromItem(objItem);
				this.objName = objItem.getUnlocalizedName();
			}catch(Exception e)
			{
				this.fail = true;
				EnviroMine.logger.log(Level.WARN, "Failed to Cast Item and Block for GuiAddCustom");
			}

		}
		else if(obj instanceof Block)
		{
			try
			{
				
				this.objBlock = (Block) obj;
				this.objItem = Item.getItemFromBlock(objBlock);
				this.objName = objItem.getUnlocalizedName();
			}catch(Exception e)
			{
				this.fail = true;
				EnviroMine.logger.log(Level.WARN, "Failed to Cast Block and Item for GuiAddCustom");
				
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		if(!this.fail)
		{
			

			int blockWidth = 100;
			// Add Item / Block / Both
			String block = I18n.format("editor.enviromine.GuiAdd.block");
			String item = I18n.format("editor.enviromine.GuiAdd.item");
			String both = I18n.format("editor.enviromine.GuiAdd.both");
		
			this.buttonList.add(new GuiButtonExt(100, this.width/2-(blockWidth/2), (this.height/2)-25, blockWidth, 20, block));
		
			this.buttonList.add(new GuiButtonExt(101, this.width/2-(blockWidth/2), (this.height/2), blockWidth, 20, item));
		
			this.buttonList.add(new GuiButtonExt(102, this.width/2-(blockWidth/2), (this.height/2)+25, blockWidth, 20, both));
		
		}
		this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.cancel", new Object[0])));
		
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}
	
	/**
	 * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		if(par1GuiButton.enabled)
		{
			String returnValue;
			switch(par1GuiButton.id)
			{
				case 100:
						returnValue = EM_ConfigHandler.SaveMyCustom(this.objBlock);
						mc.thePlayer.addChatMessage(new ChatComponentText(this.objName + " " + returnValue));
			               this.mc.displayGuiScreen((GuiScreen)null);
			                this.mc.setIngameFocus();
					break;
					
				case 101: 
					returnValue = EM_ConfigHandler.SaveMyCustom(this.objItem);
					mc.thePlayer.addChatMessage(new ChatComponentText(this.objName + " " + returnValue));
		               this.mc.displayGuiScreen((GuiScreen)null);
		                this.mc.setIngameFocus();
					break;
					
				case 102:
					returnValue = EM_ConfigHandler.SaveMyCustom(this.objItem);
					mc.thePlayer.addChatMessage(new ChatComponentText(this.objName + " " + returnValue));

					returnValue = EM_ConfigHandler.SaveMyCustom(this.objBlock);
					mc.thePlayer.addChatMessage(new ChatComponentText(this.objName + " " + returnValue));

		               this.mc.displayGuiScreen((GuiScreen)null);
		                this.mc.setIngameFocus();
					break;
					
				case 200:
					returnValue = EM_ConfigHandler.SaveMyCustom(this.objBlock);
					mc.thePlayer.addChatMessage(new ChatComponentText(this.objName + " " + returnValue));
		               this.mc.displayGuiScreen((GuiScreen)null);
		                this.mc.setIngameFocus();
					break;
			}
		}
		
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		if(!this.fail)
		{
			this.drawCenteredString(this.fontRendererObj, I18n.format("editor.enviromine.GuiAdd.title", this.objName), this.width / 2, 15, 16777215);
		}
		else
		{
			this.drawCenteredString(this.fontRendererObj, I18n.format("editor.enviromine.GuiAdd.title.fail"), this.width / 2, 15, 16777215);
		}
		super.drawScreen(par1, par2, par3);
	}
}

