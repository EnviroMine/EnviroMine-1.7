package enviromine.handlers.keybinds;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.client.gui.menu.config.GuiAddCustom;
import enviromine.core.EM_ConfigHandler;
import enviromine.core.EnviroMine;
import enviromine.utils.EnviroUtils;

@SideOnly(Side.CLIENT)
public class AddRemoveCustom
{	
	public boolean keydown = true;
	
	public static void doAddRemove()
	{
		Minecraft mc = Minecraft.getMinecraft();
		
		if(mc.currentScreen != null)
		{
			return;
		}
		
		if((!(Minecraft.getMinecraft().isSingleplayer()) || !EnviroMine.proxy.isClient()) && Minecraft.getMinecraft().thePlayer != null)
		{
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				mc.thePlayer.addChatMessage(new ChatComponentText("Single player only function."));
			}
			return;
		}
		// prevents key press firing while gui screen or chat open, if that's what you want
		// if you want your key to be able to close the gui screen, handle it outside this if statement
		if(mc.currentScreen == null)
		{
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				try
				{
					String returnValue = "";
					if(mc.thePlayer.getHeldItem() != null)
					{
						Item item = mc.thePlayer.getHeldItem().getItem();
						int itemMeta = mc.thePlayer.getHeldItem().getItemDamage();
						String idName = Item.itemRegistry.getNameForObject(item);
						String name = mc.thePlayer.getHeldItem().getDisplayName();
						
						if(item instanceof ItemArmor)
						{
							returnValue = EM_ConfigHandler.SaveMyCustom(item);
							mc.thePlayer.addChatMessage(new ChatComponentText(name + " " + returnValue));
						} else if(item instanceof Item)
						{
							EnviroMine.logger.log(Level.INFO, Block.blockRegistry.getNameForObject(Block.getBlockFromItem(item)) + "<--------------------------------");
							if(Block.getBlockFromItem(item) != Blocks.air)
							{
								Minecraft.getMinecraft().displayGuiScreen(new GuiAddCustom(item));
							}
							else 
							{
								returnValue = EM_ConfigHandler.SaveMyCustom(item);
								mc.thePlayer.addChatMessage(new ChatComponentText(name + " " + returnValue));								
							}
						}
						
						return;
					}
					
					MovingObjectType type = Minecraft.getMinecraft().objectMouseOver.typeOfHit;
					//System.out.println(type.name());
					if(type.name() == "ENTITY")
					{
						Entity lookingAt = Minecraft.getMinecraft().objectMouseOver.entityHit;
						int id = 0;
						
						if(EntityList.getEntityID(lookingAt) > 0)
						{
							id = EntityList.getEntityID(lookingAt);
						} else if(EntityRegistry.instance().lookupModSpawn(lookingAt.getClass(), false) != null)
						{
							id = EntityRegistry.instance().lookupModSpawn(lookingAt.getClass(), false).getModEntityId() + 128;
						} else
						{
							mc.thePlayer.addChatMessage(new ChatComponentText("Failed to add config entry. " + lookingAt.getCommandSenderName() + " has no ID!"));
							EnviroMine.logger.log(Level.WARN, "Failed to add config entry. " + lookingAt.getCommandSenderName() + " has no ID!");
						}
						
						returnValue = EM_ConfigHandler.SaveMyCustom(lookingAt);
						mc.thePlayer.addChatMessage(new ChatComponentText(lookingAt.getCommandSenderName() + " (" + id + ") " + returnValue));
					} else if(type.name() == "BLOCK")
					{
						
						int blockX = Minecraft.getMinecraft().objectMouseOver.blockX;
						int blockY = Minecraft.getMinecraft().objectMouseOver.blockY;
						int blockZ = Minecraft.getMinecraft().objectMouseOver.blockZ;
						
						Block block = Minecraft.getMinecraft().thePlayer.worldObj.getBlock(blockX, blockY, blockZ);
						int blockMeta = Minecraft.getMinecraft().thePlayer.worldObj.getBlockMetadata(blockX, blockY, blockZ);
						String blockULName = Block.blockRegistry.getNameForObject(block);
						String blockName = block.getLocalizedName();
					
						blockName = EnviroUtils.replaceULN(blockName);
											
						returnValue = EM_ConfigHandler.SaveMyCustom(block);
						mc.thePlayer.addChatMessage(new ChatComponentText(blockName + "(" + Block.blockRegistry.getNameForObject(block) + ":" + blockMeta + ") " + returnValue));
					}
				}
				catch(NullPointerException e)
				{
					EnviroMine.logger.log(Level.WARN, "A NullPointerException occured while adding config entry!", e);
				}
			}
			else
			{
				
				mc.thePlayer.addChatMessage(new ChatComponentText("Must hold left shift to add objects"));
			}
		}
	}
}
