package enviromine.handlers.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

import enviromine.handlers.ObjectHandler;

import java.util.ArrayList;

public class GaskMaskRefillHandler implements IRecipe
{
	public boolean isArmor;
	public int maskFill;
	public int maskMax;
	public ArrayList<ItemStack> filters = new ArrayList<ItemStack>();
	public ItemStack mask;
	public int filterFill = 500;
	public ItemStack output;
	
	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		if (!inv.getInventoryName().equals("container.crafting"))
		{
			return false;
		}
		
		this.output = null;
		this.isArmor = false;
		this.maskFill = 0;
		this.maskMax = 1000;
		this.mask = null;
		this.filters.clear();
		
		for (int i = inv.getSizeInventory() - 1; i >= 0; i--)
		{
			ItemStack item = inv.getStackInSlot(i);
			
			if (item == null)
			{
				continue;
			} else if (item.hasTagCompound() && item.getTagCompound().hasKey("gasMaskFill"))
			{
				if (mask != null)
				{
					return false;
				} else
				{
					mask = item.copy();
					maskFill = item.getTagCompound().getInteger("gasMaskFill");
					maskMax = (mask.getTagCompound().hasKey("gasMaskMax")? mask.getTagCompound().getInteger("gasMaskMax") : 1000);
				}
			} else if (item.getItem() == ObjectHandler.airFilter)
			{
					filters.add(item);
			}else if (item != null)
			{
				return false;
			}
		}
		
		if (mask == null || maskFill >= maskMax)
		{
			System.out.println("Mask not present or already full!");
			return false;
		} else if (maskFill + (filters.size() * filterFill) >= maskMax + filterFill)
		{
			System.out.println("Too many filters (" + (maskFill + (filters.size() * filterFill)) + " / " + (maskMax - filterFill));
			return false;
		}
	    else if(mask != null && filters.size() >= 1)
		{
			output = mask.copy();
			if ((maskFill +(filters.size() * filterFill)) <= maskMax)
			{
					output.getTagCompound().setInteger("gasMaskFill", (maskFill + (filters.size() * filterFill)));
			} else
			{
					output.getTagCompound().setInteger("gasMaskFill", maskMax);
			}
			
			return true;
		} else
		{
			return false;
		}
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		return output;
	}
	
	
	@Override
	public int getRecipeSize()
	{
		return 4;
	}
	
	@Override
	public ItemStack getRecipeOutput()
	{
		return output;
	}
	
	@SubscribeEvent
	public void onCrafting(PlayerEvent.ItemCraftedEvent event)
	{
		// This is completely redundant...
		
		/*IInventory craftMatrix = event.craftMatrix;
		if (!(craftMatrix instanceof InventoryCrafting))
		{
			return;
		}
		
		if (this.matches((InventoryCrafting)craftMatrix, event.player.worldObj)) {
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
		}*/
	}
}