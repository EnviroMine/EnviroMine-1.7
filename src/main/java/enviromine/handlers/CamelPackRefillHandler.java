package enviromine.handlers;

import enviromine.core.EnviroMine;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

import java.util.ArrayList;
import java.util.Iterator;

public class CamelPackRefillHandler implements IRecipe
{
	public boolean fillBottle;
	public int packDamage;
	public ArrayList<ItemStack> bottles = new ArrayList<ItemStack>();
	public ItemStack pack;
	
	public CamelPackRefillHandler()
	{
	}
	
	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		if(!inv.getInventoryName().equals("container.crafting"))
		{
			return false;
		}
		
		bottles.clear();
		boolean hasPack = false;
		
		for(int i = inv.getSizeInventory() - 1; i >= 0; i--)
		{
			ItemStack item = inv.getStackInSlot(i);
			
			if(item == null)
			{
				continue;
			} else if(item.getItem() == EnviroMine.camelPack)
			{
				if(hasPack)
				{
					return false;
				} else
				{
					pack = item;
					packDamage = item.getItemDamage();
					hasPack = true;
				}
			} else if(item.getItem() == Items.potionitem && item.getItemDamage() == 0)
			{
				fillBottle = false;
				bottles.add(item);
			} else if(item.getItem() == Items.glass_bottle && bottles.size() == 0)
			{
				fillBottle = true;
				bottles.add(item);
			} else if(item != null)
			{
				return false;
			}
		}
		
		if((packDamage == 0 && !fillBottle) || !hasPack || pack == null)
		{
			return false;
		} else if(packDamage - (bottles.size() * 25) <= -25 && fillBottle == false)
		{
			return false;
		} else if(packDamage + 25 > pack.getMaxDamage() && fillBottle == true)
		{
			return false;
		} else
		{
			return hasPack && bottles.size() >= 1;
		}
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventorycrafting)
	{
		return this.getRecipeOutput();
	}
	
	@Override
	public int getRecipeSize()
	{
		return 4;
	}
	
	@Override
	public ItemStack getRecipeOutput()
	{
		if(!fillBottle)
		{
			Iterator<ItemStack> iterator = bottles.iterator();
			
			while(iterator.hasNext())
			{
				ItemStack bottle = iterator.next();
				bottle.getItem().setContainerItem(Items.glass_bottle);
			}
		}
		
		if(fillBottle)
		{
			ItemStack newItem = new ItemStack(Items.potionitem);
			newItem.setItemDamage(0);
			return newItem;
		} else
		{
			if(packDamage > (bottles.size() * 25))
			{
				ItemStack newItem = new ItemStack(EnviroMine.camelPack);
				newItem.setItemDamage(packDamage - (bottles.size() * 25));
				return newItem;
			} else
			{
				return new ItemStack(EnviroMine.camelPack);
			}
		}
	}
	
	@SubscribeEvent
	public void onCrafting(PlayerEvent.ItemCraftedEvent event)
	{
		IInventory craftMatrix = event.craftMatrix;
		if(!craftMatrix.getInventoryName().equals("container.crafting") || !fillBottle)
		{
			return;
		} else {
			for(int i = craftMatrix.getSizeInventory() - 1; i >= 0; i--)
			{
				ItemStack slot = craftMatrix.getStackInSlot(i);
				
				if(slot == null)
				{
					continue;
				} else if(slot.getItem() == EnviroMine.camelPack)
				{
					slot.stackSize += 1;
					slot.setItemDamage(slot.getItemDamage() + 25);
					
					//ItemStack newItem = new ItemStack(EnviroMine.camelPack);
					//newItem.setItemDamage(slot.getItemDamage() + 25);
					//player.inventory.addItemStackToInventory(newItem);
				}
			}
		}
	}
}