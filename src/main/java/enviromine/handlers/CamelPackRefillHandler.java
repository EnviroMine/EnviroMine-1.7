package enviromine.handlers;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

import java.util.ArrayList;
import java.util.Iterator;

public class CamelPackRefillHandler implements IRecipe
{
	CraftingHelper lastHelper;
	public boolean fillBottle;
	public boolean isArmor;
	public int packDamage;
	public ArrayList<ItemStack> bottles = new ArrayList<ItemStack>();
	public ItemStack pack;
	
	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		lastHelper = CraftingHelper.getInstanceFromCraftmatrix(inv);
		
		if (FMLCommonHandler.instance().getEffectiveSide().isClient() || lastHelper == null) {
			return matchesClient(inv);
		}
		
		if(!inv.getInventoryName().equals("container.crafting")) {
			return false;
		}
		
		lastHelper.reset();
		boolean hasPack = false;
		
		for(int i = inv.getSizeInventory() - 1; i >= 0; i--)
		{
			ItemStack item = inv.getStackInSlot(i);
			
			if(item == null)
			{
				continue;
			} else if(item.getItem() == ObjectHandler.camelPack)
			{
				if(hasPack)
				{
					return false;
				} else
				{
					lastHelper.pack = item.copy();
					lastHelper.packDamage = item.getItemDamage();
					hasPack = true;
				}
			} else if (item.hasTagCompound() && item.getTagCompound().hasKey("camelPackFill")) {
				if(hasPack)
				{
					return false;
				} else
				{
					lastHelper.pack = item.copy();
					lastHelper.packDamage = 100-item.getTagCompound().getInteger("camelPackFill");
					hasPack = true;
					lastHelper.isArmor = true;
				}
			} else if(item.getItem() == Items.potionitem && item.getItemDamage() == 0)
			{
				if (lastHelper.bottles.size() > 0 && lastHelper.fillBottle) {
					return false;
				} else {
					lastHelper.fillBottle = false;
					lastHelper.bottles.add(item);
				}
			} else if(item.getItem() == Items.glass_bottle && lastHelper.bottles.size() == 0)
			{
				if (lastHelper.bottles.size() > 0 && !lastHelper.fillBottle) {
					return false;
				} else {
					lastHelper.fillBottle = true;
					lastHelper.bottles.add(item);
				}
			} else if(item != null)
			{
				return false;
			}
		}
		
		if((lastHelper.packDamage == 0 && !lastHelper.fillBottle) || !hasPack || lastHelper.pack == null)
		{
			return false;
		} else if(lastHelper.packDamage - (lastHelper.bottles.size() * 25) <= -25 && lastHelper.fillBottle == false)
		{
			return false;
		} else if(lastHelper.packDamage + 25 > lastHelper.pack.getMaxDamage() && lastHelper.fillBottle == true)
		{
			return false;
		} else
		{
			if (FMLCommonHandler.instance().getEffectiveSide().isServer()){
				CraftingHelper.getInstanceFromCraftmatrix(inv).fillBottle = lastHelper.fillBottle;
			}
			return hasPack && lastHelper.bottles.size() >= 1;
		}
	}
	private boolean matchesClient(InventoryCrafting inv)
	{
		if(!inv.getInventoryName().equals("container.crafting")) {
			return false;
		}
		
		this.fillBottle = false;
		this.isArmor = false;
		this.packDamage = 0;
		this.pack = null;
		this.bottles.clear();
		boolean hasPack = false;
		
		for(int i = inv.getSizeInventory() - 1; i >= 0; i--)
		{
			ItemStack item = inv.getStackInSlot(i);
			
			if(item == null)
			{
				continue;
			} else if(item.getItem() == ObjectHandler.camelPack)
			{
				if(hasPack)
				{
					return false;
				} else
				{
					pack = item.copy();
					packDamage = item.getItemDamage();
					hasPack = true;
				}
			} else if (item.hasTagCompound() && item.getTagCompound().hasKey("camelPackFill")) {
				if(hasPack)
				{
					return false;
				} else
				{
					pack = item.copy();
					packDamage = 100-item.getTagCompound().getInteger("camelPackFill");
					hasPack = true;
					isArmor = true;
				}
			} else if(item.getItem() == Items.potionitem && item.getItemDamage() == 0)
			{
				if (bottles.size() > 0 && fillBottle) {
					return false;
				} else {
					fillBottle = false;
					bottles.add(item);
				}
			} else if(item.getItem() == Items.glass_bottle && bottles.size() == 0)
			{
				if (bottles.size() > 0 && !fillBottle) {
					return false;
				} else {
					fillBottle = true;
					bottles.add(item);
				}
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
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			return getCraftingResultClient();
		}
		
		CraftingHelper helper = CraftingHelper.getInstanceFromCraftmatrix(inv);
		if(helper.fillBottle)
		{
			ItemStack newItem = new ItemStack(Items.potionitem);
			newItem.setItemDamage(0);
			return newItem;
		} else
		{
			Iterator<ItemStack> iterator = helper.bottles.iterator();
			
			while(iterator.hasNext())
			{
				ItemStack bottle = iterator.next();
				bottle.getItem().setContainerItem(Items.glass_bottle);
			}
			
			if(helper.packDamage > (helper.bottles.size() * 25))
			{
				if (helper.isArmor) {
					helper.pack.getTagCompound().setInteger("camelPackFill", 100-(helper.packDamage - (helper.bottles.size() * 25)));
					return helper.pack;
				} else {
					helper.pack.setItemDamage(helper.packDamage - (helper.bottles.size() * 25));
					return helper.pack;
				}
			} else
			{
				if (helper.isArmor) {
					helper.pack.getTagCompound().setInteger("camelPackFill", 100);
					return helper.pack;
				} else {
					return new ItemStack(ObjectHandler.camelPack);
				}
			}
		}
	}
	private ItemStack getCraftingResultClient()
	{
		if(fillBottle)
		{
			ItemStack newItem = new ItemStack(Items.potionitem);
			newItem.setItemDamage(0);
			return newItem;
		} else
		{
			Iterator<ItemStack> iterator = bottles.iterator();
			
			while(iterator.hasNext())
			{
				ItemStack bottle = iterator.next();
				bottle.getItem().setContainerItem(Items.glass_bottle);
			}
			
			if(packDamage > (bottles.size() * 25))
			{
				if (isArmor) {
					pack.getTagCompound().setInteger("camelPackFill", 100-(packDamage - (bottles.size() * 25)));
					return pack;
				} else {
					pack.setItemDamage(packDamage - (bottles.size() * 25));
					return pack;
				}
			} else
			{
				if (isArmor) {
					pack.getTagCompound().setInteger("camelPackFill", 100);
					return pack;
				} else {
					return new ItemStack(ObjectHandler.camelPack);
				}
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
		if(!craftMatrix.getInventoryName().equals("container.crafting") || !CraftingHelper.getInstanceFromPlayer(event.player).fillBottle)
		{
			return;
		} else {
			for(int i = craftMatrix.getSizeInventory() - 1; i >= 0; i--)
			{
				ItemStack slot = craftMatrix.getStackInSlot(i);
				
				if(slot == null)
				{
					continue;
				} else if(slot.getItem() == ObjectHandler.camelPack)
				{
					slot.stackSize += 1;
					slot.setItemDamage(slot.getItemDamage() + 25);
				} else if (slot.hasTagCompound() && slot.getTagCompound().hasKey("camelPackFill"))
				{
					slot.stackSize += 1;
					slot.getTagCompound().setInteger("camelPackFill", slot.getTagCompound().getInteger("camelPackFill") - 25);
				}
			}
		}
	}
}