package enviromine.handlers.crafting;

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
	public boolean isArmor;
	public int packFillCur;
	public int packFillMax;
	public ArrayList<ItemStack> bottles = new ArrayList<ItemStack>();
	public ItemStack pack;
	
	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		if (!inv.getInventoryName().equals("container.crafting"))
		{
			return false;
		}
		
		this.fillBottle = false;
		this.isArmor = false;
		this.packFillCur = 0;
		this.packFillMax = 0;
		this.pack = null;
		this.bottles.clear();
		boolean hasPack = false;
		
		for (int i = inv.getSizeInventory() - 1; i >= 0; i--)
		{
			ItemStack item = inv.getStackInSlot(i);
			
			if (item == null)
			{
				continue;
			} else if (item.hasTagCompound() && item.getTagCompound().hasKey("camelPackFill"))
			{
				if (hasPack)
				{
					return false;
				} else
				{
					pack = item.copy();
					packFillCur = item.getTagCompound().getInteger("camelPackFill");
					packFillMax = item.getTagCompound().getInteger("camelPackMax");
					hasPack = true;
					isArmor = true;
				}
			} else if (item.getItem() == Items.potionitem && item.getItemDamage() == 0)
			{
				if (bottles.size() > 0 && fillBottle)
				{
					return false;
				} else
				{
					fillBottle = false;
					bottles.add(item);
				}
			} else if (item.getItem() == Items.glass_bottle && bottles.size() == 0)
			{
				if (bottles.size() > 0 && !fillBottle)
				{
					return false;
				} else
				{
					fillBottle = true;
					bottles.add(item);
				}
			} else
			{
				return false;
			}
		}
		
		if ((packFillCur == packFillMax && !fillBottle) || !hasPack || pack == null)
		{
			return false;
		} else if (packFillCur + (bottles.size() * 25) >= packFillMax+25 && fillBottle == false)
		{
			return false;
		} else if (packFillCur - 25 < 0 && fillBottle == true)
		{
			return false;
		} else
		{
			return hasPack && bottles.size() >= 1;
		}
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		this.matches(inv, null);
		
		if (fillBottle)
		{
			ItemStack newItem = new ItemStack(Items.potionitem);
			newItem.setItemDamage(0);
			return newItem;
		} else
		{
			Iterator<ItemStack> iterator = bottles.iterator();
			
			while (iterator.hasNext())
			{
				ItemStack bottle = iterator.next();
				bottle.getItem().setContainerItem(Items.glass_bottle);
			}
			
			if ((packFillCur + (bottles.size() * 25)) <= packFillMax)
			{
				pack.getTagCompound().setInteger("camelPackFill", (packFillCur + (bottles.size() * 25)));
				return pack;
			} else
			{
				
				pack.getTagCompound().setInteger("camelPackFill", packFillMax);
				return pack;
			}
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
		
		if (!craftMatrix.getInventoryName().equals("container.crafting") || !fillBottle)
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
				} else if (slot.hasTagCompound() && slot.getTagCompound().hasKey("camelPackFill"))
				{
					slot.stackSize += 1;
					slot.getTagCompound().setInteger("camelPackFill", slot.getTagCompound().getInteger("camelPackFill") - 25);
				}
			}
		}
	}
}