package enviromine.client.gui.menu.config;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.IConfigElement;
import enviromine.core.EM_ConfigHandler;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.ArmorProperties;
import enviromine.trackers.properties.BlockProperties;
import enviromine.trackers.properties.ItemProperties;

public class GuiAddCustom extends GuiScreen
{
	private String objName = "null";
	private Block objBlock;
	private Item objItem;
	private ItemArmor objArmor;
	private boolean fail = false;
	
	public GuiAddCustom(Object obj)
	{
		
		if(obj instanceof Item)
		{
			try
			{
				this.objItem = (Item) obj;
				
				if(obj instanceof ItemArmor)
				{
					System.out.println("ItemArmor");
					this.objArmor = (ItemArmor)obj;
				}
				else
				{
					this.objBlock = Block.getBlockFromItem(objItem);
				}
				
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
			String editItem = I18n.format("Edit Item");
			String editBlock = I18n.format("Edit Block");
			
			String armorW = I18n.format("Add Armor Worn");
			String armorI = I18n.format("Add Armor Item");
			String editArmorW = I18n.format("Edit Armor Worn");
			String editArmorI = I18n.format("Edit Armor Item");
			
			if(this.objArmor != null)
			{
				if(EM_ConfigMenu.ElementExist(this.objItem, ItemProperties.base) && EM_ConfigMenu.ElementExist(this.objArmor, ArmorProperties.base))
				{
					this.buttonList.add(new GuiButtonExt(107, this.width/2-(blockWidth/2), (this.height/2)-25, blockWidth, 20, editArmorI));
					
					this.buttonList.add(new GuiButtonExt(108, this.width/2-(blockWidth/2), (this.height/2), blockWidth, 20, editArmorW));
					
				}
				else if(!EM_ConfigMenu.ElementExist(this.objItem, ItemProperties.base) && EM_ConfigMenu.ElementExist(this.objArmor, ArmorProperties.base))
				{
					this.buttonList.add(new GuiButtonExt(105, this.width/2-(blockWidth/2), (this.height/2)-25, blockWidth, 20, armorI));
					
					this.buttonList.add(new GuiButtonExt(108, this.width/2-(blockWidth/2), (this.height/2), blockWidth, 20, editArmorW));
					
				}
				else if(EM_ConfigMenu.ElementExist(this.objItem, ItemProperties.base) && !EM_ConfigMenu.ElementExist(this.objArmor, ArmorProperties.base))
				{
					this.buttonList.add(new GuiButtonExt(107, this.width/2-(blockWidth/2), (this.height/2)-25, blockWidth, 20, editArmorI));
					
					this.buttonList.add(new GuiButtonExt(106, this.width/2-(blockWidth/2), (this.height/2), blockWidth, 20, armorW));
					
				}
				else 
				{
					this.buttonList.add(new GuiButtonExt(100, this.width/2-(blockWidth/2), (this.height/2)-25, blockWidth, 20, block));
					
					this.buttonList.add(new GuiButtonExt(101, this.width/2-(blockWidth/2), (this.height/2), blockWidth, 20, item));
					
					this.buttonList.add(new GuiButtonExt(102, this.width/2-(blockWidth/2), (this.height/2)+25, blockWidth, 20, both));

				}
				
			}
			else
			{
				if(EM_ConfigMenu.ElementExist(this.objItem, ItemProperties.base) && EM_ConfigMenu.ElementExist(this.objBlock, BlockProperties.base))
				{
					this.buttonList.add(new GuiButtonExt(103, this.width/2-(blockWidth/2), (this.height/2)-25, blockWidth, 20, editBlock));
					
					this.buttonList.add(new GuiButtonExt(104, this.width/2-(blockWidth/2), (this.height/2), blockWidth, 20, editItem));
					
				}
				else if(EM_ConfigMenu.ElementExist(this.objItem, ItemProperties.base) && !EM_ConfigMenu.ElementExist(this.objBlock, BlockProperties.base))
				{
					this.buttonList.add(new GuiButtonExt(100, this.width/2-(blockWidth/2), (this.height/2)-25, blockWidth, 20, block));
					
					this.buttonList.add(new GuiButtonExt(104, this.width/2-(blockWidth/2), (this.height/2), blockWidth, 20, editItem));
					
				}
				else if(!EM_ConfigMenu.ElementExist(this.objItem, ItemProperties.base) && EM_ConfigMenu.ElementExist(this.objBlock, BlockProperties.base))
				{
					this.buttonList.add(new GuiButtonExt(103, this.width/2-(blockWidth/2), (this.height/2)-25, blockWidth, 20, editBlock));
					
					this.buttonList.add(new GuiButtonExt(101, this.width/2-(blockWidth/2), (this.height/2), blockWidth, 20, item));
					
				}
				else 
				{
					this.buttonList.add(new GuiButtonExt(100, this.width/2-(blockWidth/2), (this.height/2)-25, blockWidth, 20, block));
					
					this.buttonList.add(new GuiButtonExt(101, this.width/2-(blockWidth/2), (this.height/2), blockWidth, 20, item));
					
					this.buttonList.add(new GuiButtonExt(102, this.width/2-(blockWidth/2), (this.height/2)+25, blockWidth, 20, both));

				}
				
				
			}
			
			
			
			
			
			
			
			
			
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
			Configuration config;
			List<IConfigElement> configElements;
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
				case 103:
					config = EM_ConfigHandler.getConfigFromObj(this.objBlock);
					configElements = EM_ConfigMenu.GetElement(config,this.objBlock, BlockProperties.base);
					mc.displayGuiScreen(new EM_ConfigMenu(configElements, config));
					
					break;
				case 104:
					config = EM_ConfigHandler.getConfigFromObj(this.objItem);
					configElements = EM_ConfigMenu.GetElement(config, this.objItem, ItemProperties.base);
					mc.displayGuiScreen(new EM_ConfigMenu(configElements, config));
					
					break;
				case 105:
					returnValue = EM_ConfigHandler.SaveMyCustom(this.objItem);
					mc.thePlayer.addChatMessage(new ChatComponentText(this.objName + " " + returnValue));

		               this.mc.displayGuiScreen((GuiScreen)null);
		                this.mc.setIngameFocus();
		            break;
				case 106:
					returnValue = EM_ConfigHandler.SaveMyCustom(this.objArmor,ArmorProperties.base);
					mc.thePlayer.addChatMessage(new ChatComponentText(this.objName + " " + returnValue));

		               this.mc.displayGuiScreen((GuiScreen)null);
		                this.mc.setIngameFocus();
		            break;
				case 107:
					config = EM_ConfigHandler.getConfigFromObj(this.objItem);
					configElements = EM_ConfigMenu.GetElement(config, this.objItem, ItemProperties.base);
					mc.displayGuiScreen(new EM_ConfigMenu(configElements, config));
					
					break;
				case 108:
					config = EM_ConfigHandler.getConfigFromObj(this.objArmor);
					configElements = EM_ConfigMenu.GetElement(config, this.objArmor, ArmorProperties.base);
					mc.displayGuiScreen(new EM_ConfigMenu(configElements, config));
					
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

