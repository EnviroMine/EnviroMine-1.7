package enviromine.handlers.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import java.util.ArrayList;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class CamelPackRefillHandler implements IRecipe
{
	public boolean emptyPack;
	public int packFillCur;
	public int packFillMax;
	public ArrayList<ItemStack> bottles = new ArrayList<ItemStack>();
	public ArrayList<ItemStack> buckets = new ArrayList<ItemStack>();
	public ItemStack pack;
	private static final int bottleFill = 25;
	private static final int bucketFill = 75;
	
	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		if (!inv.getInventoryName().equals("container.crafting"))
		{
			return false;
		}
		
		this.emptyPack = false;
		this.packFillCur = 0;
		this.packFillMax = 0;
		this.pack = null;
		this.bottles.clear();
		this.buckets.clear();
		boolean hasPack = false;
		
		for (int i = inv.getSizeInventory() - 1; i >= 0; i--)
		{
			ItemStack item = inv.getStackInSlot(i);
			
			if (item == null)
			{
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
				}
			} else if (item.getItem() == Items.potionitem && item.getItemDamage() == 0)
			{
				if (bottles.size() > 0 && emptyPack)
				{
					return false;
				} else
				{
					emptyPack = false;
					bottles.add(item);
				}
			} else if (item.getItem() == Items.glass_bottle && bottles.size() == 0)
			{
				if (bottles.size() > 0 && !emptyPack)
				{
					return false;
				} else
				{
					emptyPack = true;
					bottles.add(item);
				}
			} else if (item.getItem() == Items.water_bucket)
			{
				if (buckets.size() > 0 && emptyPack)
				{
					return false;
				} else
				{
					emptyPack = false;
					buckets.add(item);
				}
			} else if (item.getItem() == Items.bucket && buckets.size() == 0)
			{
				if (buckets.size() > 0 && !emptyPack)
				{
					return false;
				} else
				{
					emptyPack = true;
					buckets.add(item);
				}
			} else
			{
				return false;
			}
		}
		
		//Double checking everything is good.
		if ((packFillCur == packFillMax && !emptyPack) || !hasPack || pack == null)
		{
			return false;
		} else if (bottles.size() >= 0 || buckets.size() >= 0)
		{
			if (emptyPack)
			{
				if (bottles.size() >= 1 && buckets.size() >= 1)
				{
					return false;
				} else if (bottles.size() >= 1 && packFillCur < bottleFill)
				{
					return false;
				} else if (buckets.size() >= 1 && packFillCur < bucketFill)
				{
					return false;
				}
			} else if (packFillCur == packFillMax) {
				return false;
			}
		}
		
		return hasPack && (bottles.size() >= 1 || buckets.size() >= 1);
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		this.matches(inv, null);
		
		if (emptyPack)
		{
			ItemStack newItem;
			if (bottles.size() >= 1)
			{
				newItem = new ItemStack(Items.potionitem, 1, 0);
			} else
			{
				newItem = new ItemStack(Items.water_bucket);
			}
			return newItem;
		} else
		{

			for (ItemStack bottle : bottles)
			{
				bottle.getItem().setContainerItem(Items.glass_bottle);
			}
			
			int attemptedFill = (packFillCur + (bottles.size() * bottleFill) + (buckets.size() * bucketFill));
			
			if (attemptedFill <= packFillMax)
			{
				pack.getTagCompound().setInteger("camelPackFill", attemptedFill);
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
		
		if (this.matches((InventoryCrafting)craftMatrix, event.player.worldObj))
		{
			if (!craftMatrix.getInventoryName().equals("container.crafting") || !emptyPack)
			{
			} else
			{
				for (int i = craftMatrix.getSizeInventory() - 1; i >= 0; i--)
				{
					ItemStack slot = craftMatrix.getStackInSlot(i);
					
					if (slot == null)
					{
					} else if (slot.hasTagCompound() && slot.getTagCompound().hasKey("camelPackFill"))
					{
						slot.stackSize += 1;
						slot.getTagCompound().setInteger("camelPackFill", slot.getTagCompound().getInteger("camelPackFill") - (bottles.size() >= 1 ? bottleFill : bucketFill));
					}
				}
			}
		}
	}
}