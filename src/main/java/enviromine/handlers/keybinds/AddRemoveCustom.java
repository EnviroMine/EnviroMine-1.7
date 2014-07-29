package main.java.enviromine.handlers.keybinds;

import main.java.enviromine.core.EM_ConfigHandler;
import main.java.enviromine.core.EnviroMine;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

import cpw.mods.fml.common.registry.EntityRegistry;

import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;

public class AddRemoveCustom
{
	static Object[] dataToCustom = new Object[5];
	
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
						String unlocolizedName = mc.thePlayer.getHeldItem().getItem().getUnlocalizedName();
						String name = mc.thePlayer.getHeldItem().getDisplayName();
						
						unlocolizedName = replaceULN(unlocolizedName);
						name = replaceULN(name);

						
						dataToCustom[0] = item;
						dataToCustom[1] = itemMeta;
						dataToCustom[2] = unlocolizedName;

						if(item instanceof ItemArmor)
						{
							returnValue = EM_ConfigHandler.SaveMyCustom("ARMOR", name, dataToCustom);
							mc.thePlayer.addChatMessage(new ChatComponentText(name + " " + returnValue + " in MyCustom.cfg file. "));
						} else if(item instanceof Item)
						{
							returnValue = EM_ConfigHandler.SaveMyCustom("ITEM", name, dataToCustom);
							mc.thePlayer.addChatMessage(new ChatComponentText(name + " " + returnValue + " in MyCustom.cfg file. "));
						}
						
						return;
						
					}
					
					MovingObjectType type = Minecraft.getMinecraft().objectMouseOver.typeOfHit;
					
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
							mc.thePlayer.addChatMessage(new ChatComponentText("Failed to add/remove config entry. " + lookingAt.getCommandSenderName() + " has no ID!"));
							EnviroMine.logger.log(Level.WARN, "Failed to add/remove config entry. " + lookingAt.getCommandSenderName() + " has no ID!");
						}
						
						dataToCustom[0] = id;
						
						returnValue = EM_ConfigHandler.SaveMyCustom(type.name(), lookingAt.getCommandSenderName(), dataToCustom);
						mc.thePlayer.addChatMessage(new ChatComponentText(lookingAt.getCommandSenderName() + " (" + id + ") " + returnValue + " in MyCustom.cfg file."));
					} else if(type.name() == "BLOCK")
					{
						
						int blockX = Minecraft.getMinecraft().objectMouseOver.blockX;
						int blockY = Minecraft.getMinecraft().objectMouseOver.blockY;
						int blockZ = Minecraft.getMinecraft().objectMouseOver.blockZ;
						
						Block block = Minecraft.getMinecraft().thePlayer.worldObj.getBlock(blockX, blockY, blockZ);
						int blockMeta = Minecraft.getMinecraft().thePlayer.worldObj.getBlockMetadata(blockX, blockY, blockZ);
						String blockULName = block.getUnlocalizedName();
						String blockName = block.getLocalizedName();
						
						blockULName = replaceULN(blockULName);
						blockName = replaceULN(blockName);
							
						dataToCustom[0] = block;
						dataToCustom[1] = blockMeta;
						dataToCustom[2] = blockULName;
						
						returnValue = EM_ConfigHandler.SaveMyCustom(type.name(), blockName, dataToCustom);
						mc.thePlayer.addChatMessage(new ChatComponentText(blockName + "(" + Block.blockRegistry.getNameForObject(block) + ":" + blockMeta + ") " + returnValue + "  in MyCustom.cfg file."));
					}
				}
				catch(NullPointerException e)
				{
					EnviroMine.logger.log(Level.WARN, "A NullPointerException occured while adding/removing config entry!", e);
				}
			}
			else
			{
				
				mc.thePlayer.addChatMessage(new ChatComponentText("Must hold left shift to add/remove objects"));
			}
		}
	}
	
	public static String replaceULN(String unlocalizedName)
	{
		String newName = unlocalizedName.replaceAll("\\.+", "\\_");
		return newName;
		
	}
	
	
}
