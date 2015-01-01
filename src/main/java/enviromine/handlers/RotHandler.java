package enviromine.handlers;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.trackers.properties.RotProperties;

public class RotHandler
{
	public static ItemStack doRot(World world, ItemStack item)
	{
		//System.out.println("Rotting: " + item.getDisplayName());
		RotProperties rotProps = null;
		long rotTime = (long)(EM_Settings.foodRotTime * 24000L);
		
		if(EM_Settings.rotProperties.containsKey("" + Item.itemRegistry.getNameForObject(item.getItem())))
		{
			rotProps = EM_Settings.rotProperties.get("" + Item.itemRegistry.getNameForObject(item.getItem()));
			rotTime = (long)(rotProps.days * 24000L);
		} else if(EM_Settings.rotProperties.containsKey("" + Item.itemRegistry.getNameForObject(item.getItem()) + "," + item.getItemDamage()))
		{
			rotProps = EM_Settings.rotProperties.get("" + Item.itemRegistry.getNameForObject(item.getItem()) + "," + item.getItemDamage());
			rotTime = (long)(rotProps.days * 24000L);
		}
		
		if(!EM_Settings.foodSpoiling || rotProps == null || rotTime < 0)
		{
			if(item.getTagCompound() != null)
			{
				if(item.getTagCompound().hasKey("EM_ROT_DATE"))
				{
					item.getTagCompound().removeTag("EM_ROT_DATE");
				}
				if(item.getTagCompound().hasKey("EM_ROT_TIME"))
				{
					item.getTagCompound().removeTag("EM_ROT_TIME");
				}
			}
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
				UBD = (world.getTotalWorldTime()/24000L) * 24000L;
				UBD = UBD <= 0L? 1L : UBD;
				item.getTagCompound().setLong("EM_ROT_DATE", UBD);
				item.getTagCompound().setLong("EM_ROT_TIME", rotTime);
				return item;
			} else if(UBD + rotTime < world.getTotalWorldTime())
			{
				return Item.itemRegistry.getObject(rotProps.rotID) == null? null : new ItemStack((Item)Item.itemRegistry.getObject(rotProps.rotID), item.stackSize, rotProps.rotMeta < 0? item.getItemDamage() : rotProps.rotMeta);
			} else
			{
				item.getTagCompound().setLong("EM_ROT_TIME", rotTime);
				return item;
			}
		}
	}
	
	public static void rotInvo(World world, IInventory inventory)
	{
		if(inventory == null || inventory.getSizeInventory() <= 0 || world.isRemote)
		{
			return;
		}
		
		boolean flag = false;
		
		try
		{
			for(int i = 0; i < inventory.getSizeInventory(); i++)
			{
				ItemStack slotItem = inventory.getStackInSlot(i);
				
				if(slotItem != null)
				{
					ItemStack rotItem = doRot(world, slotItem);
					
					if(rotItem == null || rotItem.getItem() != slotItem.getItem())
					{
						inventory.setInventorySlotContents(i, rotItem);
						flag = true;
					}
				}
			}
			
			if(flag && inventory instanceof TileEntity)
			{
				((TileEntity)inventory).markDirty();
			}
		} catch(Exception e)
		{
			EnviroMine.logger.log(Level.ERROR, "An error occured while attempting to rot inventory:", e);
			return;
		}
	}
}
