package enviromine.handlers.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

import enviromine.handlers.ObjectHandler;

import java.util.ArrayList;
import java.util.Iterator;

public class GaskMaskRefillHandler implements IRecipe
{
	public boolean isArmor;
	public int maskFill;
	public ArrayList<ItemStack> filters = new ArrayList<ItemStack>();
	public ItemStack mask;
	
	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		if (!inv.getInventoryName().equals("container.crafting"))
		{
			return false;
		}
		
		
		this.isArmor = false;
		this.maskFill = 0;
		this.mask = null;
		this.filters.clear();
		boolean hasMask = false;
		
		for (int i = inv.getSizeInventory() - 1; i >= 0; i--)
		{
			ItemStack item = inv.getStackInSlot(i);
			
			if (item == null)
			{
				continue;
			} else if (item.hasTagCompound() && item.getTagCompound().hasKey("gasMaskFill"))
			{
				if (hasMask)
				{
					return false;
				} else
				{
					mask = item.copy();
					maskFill = item.getTagCompound().getInteger("gasMaskFill");
					hasMask = true;
				}
			} else if (item.getItem() == ObjectHandler.airFilter)
			{
					filters.add(item);
			}else if (item != null)
			{
				return false;
			}
		}
		
		if (maskFill == 200 || !hasMask || mask == null)
		{
			return false;
		} else if (maskFill + (filters.size() * 100) >= 300)
		{
			return false;
		} else if (maskFill + 100 > mask.getTagCompound().getInteger("gasMaskMax"))
		{
			return false;
		} else
		{
			return hasMask && filters.size() >= 1;
		}
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		this.matches(inv, null);
		
			if (maskFill < (filters.size() * 100))
			{
					mask.getTagCompound().setInteger("gasMaskFill", (maskFill + (filters.size() * 100)));
					return mask;
			} else
			{
					mask.getTagCompound().setInteger("gasMaskFill", 200);
					return mask;
			}
		}
	
	
	@Override
	public int getRecipeSize()
	{
		return 4;
	}
	
	@Override
	public ItemStack getRecipeOutput()
	{
		return null;
	}
	
	@SubscribeEvent
	public void onCrafting(PlayerEvent.ItemCraftedEvent event)
	{
		IInventory craftMatrix = event.craftMatrix;
		if (!(craftMatrix instanceof InventoryCrafting))
		{
			return;
		}
		
		this.matches((InventoryCrafting)craftMatrix, event.player.worldObj);
		
		if (!craftMatrix.getInventoryName().equals("container.crafting"))
		{
			return;
		} else
		{
			for (int i = craftMatrix.getSizeInventory() - 1; i >= 0; i--)
			{
				ItemStack slot = craftMatrix.getStackInSlot(i);
				
				if (slot == null)
				{
					continue;
				}else if (slot.hasTagCompound() && slot.getTagCompound().hasKey("gasMaskFill"))
				{
					slot.stackSize -= 1;
				}
			}
		}
	}
}