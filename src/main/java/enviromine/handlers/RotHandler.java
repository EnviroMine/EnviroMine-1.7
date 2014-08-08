package enviromine.handlers;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import enviromine.core.EM_Settings;
import enviromine.items.RottenFood;
import enviromine.trackers.RotProperties;

public class RotHandler
{
	public static ItemStack doRot(World world, ItemStack item)
	{
		
		RotProperties rotProps = null;
		long rotTime = (long)(EM_Settings.foodRotTime * 24000L);
		
		if(EM_Settings.rotProperties.containsKey(Item.itemRegistry.getNameForObject(item.getItem())))
		{
			rotProps = EM_Settings.rotProperties.get(Item.itemRegistry.getNameForObject(item.getItem()));
			rotTime = (long)(rotProps.days * 24000L);
		} else if(EM_Settings.rotProperties.containsKey(Item.itemRegistry.getNameForObject(item.getItem()) + "," + item.getItemDamage()))
		{
			rotProps = EM_Settings.rotProperties.get(Item.itemRegistry.getNameForObject(item.getItem()) + "," + item.getItemDamage());
			rotTime = (long)(rotProps.days * 24000L);
		}
		
		if(!EM_Settings.foodSpoiling || (!(item.getItem() instanceof ItemFood) && rotProps == null) || (rotTime < 0 && rotProps != null) || item.getItem() instanceof RottenFood || item.getItem() == Items.rotten_flesh)
		{
			return item;
		} else
		{
			if(item.getTagCompound() == null)
			{
				item.setTagCompound(new NBTTagCompound());
			}
			long UBD = item.getTagCompound().getLong("EM_ROT_DATE");
			
			if(UBD == 0)
			{
				long timeRound = (long)(world.getTotalWorldTime() % (rotTime < 24000L? rotTime/4D : 6000L));
				item.getTagCompound().setLong("EM_ROT_DATE", world.getTotalWorldTime() + (long)(timeRound >= (rotTime < 24000L? rotTime/4D : 6000L)/2? (rotTime < 24000L? rotTime/4D : 6000L) - timeRound : -timeRound));
				item.getTagCompound().setLong("EM_ROT_TIME", rotTime);
				return item;
			} else if(UBD + rotTime < world.getTotalWorldTime())
			{
				ItemStack rotStack = new ItemStack(ObjectHandler.rottenFood, item.stackSize);
				rotStack.setStackDisplayName("Rotten " + item.getDisplayName());
				return rotStack;
			} else
			{
				item.getTagCompound().setLong("EM_ROT_TIME", rotTime);
				return item;
			}
		}
	}
	
	public static void rotInvo(World world, IInventory inventory)
	{
		for(int i = 0; i < inventory.getSizeInventory(); i++)
		{
			ItemStack slotItem = inventory.getStackInSlot(i);
			
			if(slotItem != null)
			{
				ItemStack rotItem = doRot(world, slotItem);
				
				if(rotItem != slotItem)
				{
					inventory.setInventorySlotContents(i, rotItem);
				}
			}
		}
	}
}
