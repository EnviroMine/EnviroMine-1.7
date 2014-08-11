package enviromine.handlers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class CamelPackIntegrationHandler implements IRecipe
{
	CraftingHelper lastHelper;
	boolean isRemove;
	public ItemStack pack;
	public ItemStack armor;
	
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
		boolean hasArmor = false;
		
		for(int i = inv.getSizeInventory() - 1; i >= 0; i--)
		{
			ItemStack item = inv.getStackInSlot(i);
			if(item == null) {
				continue;
			} else if(item.getItem() == ObjectHandler.camelPack) {
				if(hasPack || lastHelper.isRemove) {
					return false;
				} else {
					lastHelper.pack = item.copy();
					hasPack = true;
				}
			} else if(item.getItem() instanceof ItemArmor) {
				if (((ItemArmor)item.getItem()).armorType == 1) {
					if(hasArmor) {
						return false;
					} else {
						if (item.hasTagCompound() && item.stackTagCompound.hasKey("camelPackFill")) {
							if (hasPack) {
								return false;
							} else {
								lastHelper.isRemove = true;
							}
						}
						lastHelper.armor = item.copy();
						hasArmor = true;
					}
				}
			} else if(item != null) {
				return false;
			}
		}
		
		return (hasArmor && lastHelper.armor != null && (lastHelper.isRemove || (hasPack && lastHelper.pack != null)));
	}
	
	private boolean matchesClient(InventoryCrafting inv)
	{
		if(!inv.getInventoryName().equals("container.crafting")) {
			return false;
		}
		boolean hasPack = false;
		boolean hasArmor = false;
		
		this.isRemove = false;
		this.pack = null;
		this.armor = null;
		
		for(int i = inv.getSizeInventory() - 1; i >= 0; i--)
		{
			ItemStack item = inv.getStackInSlot(i);
			if(item == null) {
				continue;
			} else if(item.getItem() == ObjectHandler.camelPack) {
				if(hasPack || isRemove) {
					return false;
				} else {
					pack = item.copy();
					hasPack = true;
				}
			} else if(item.getItem() instanceof ItemArmor) {
				if (((ItemArmor)item.getItem()).armorType == 1) {
					if(hasArmor) {
						return false;
					} else {
						if (item.hasTagCompound() && item.stackTagCompound.hasKey("camelPackFill")) {
							if (hasPack) {
								return false;
							} else {
								isRemove = true;
							}
						}
						armor = item.copy();
						hasArmor = true;
					}
				}
			} else if(item != null) {
				return false;
			}
		}
		
		return (hasArmor && armor != null && (isRemove || (hasPack && pack != null)));
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		CraftingHelper helper = CraftingHelper.getInstanceFromCraftmatrix(inv);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient() || helper == null) {
			return getCraftingResultClient();
		}
		
		if (helper.armor != null) {
			if (helper.isRemove) {
				ItemStack out = new ItemStack(ObjectHandler.camelPack);
				out.setItemDamage(100-helper.armor.getTagCompound().getInteger("camelPackFill"));
				return out;
			} else {
				if (!helper.armor.hasTagCompound()) {
					helper.armor.setTagCompound(new NBTTagCompound());
				}
				helper.armor.getTagCompound().setInteger("camelPackFill", 100-helper.pack.getItemDamage());
				
				return helper.armor;
			}
		}
		
		return null;
	}
	
	private ItemStack getCraftingResultClient()
	{
		if (armor != null) {
			if (isRemove) {
				ItemStack out = new ItemStack(ObjectHandler.camelPack);
				out.setItemDamage(100-armor.getTagCompound().getInteger("camelPackFill"));
				return out;
			} else {
				if (!armor.hasTagCompound()) {
					armor.setTagCompound(new NBTTagCompound());
				}
				armor.getTagCompound().setInteger("camelPackFill", 100-pack.getItemDamage());
				
				return armor;
			}
		}
		
		return null;
	}
	
	@Override
	public int getRecipeSize() {
		return 4;
	}
	
	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}
	
	@SubscribeEvent
	public void onCrafting(PlayerEvent.ItemCraftedEvent event)
	{
		IInventory craftMatrix = event.craftMatrix;
		if(!craftMatrix.getInventoryName().equals("container.crafting") || (CraftingHelper.hasInstanceForPlayer(event.player) && !CraftingHelper.getInstanceFromPlayer(event.player).isRemove)) {
			return;
		} else {
			for(int i = craftMatrix.getSizeInventory() - 1; i >= 0; i--)
			{
				ItemStack slot = craftMatrix.getStackInSlot(i);
				
				if(slot == null)
				{
					continue;
				} else if(slot.hasTagCompound() && slot.getTagCompound().hasKey("camelPackFill"))
				{
					slot.stackSize++;
					slot.getTagCompound().removeTag("camelPackFill");
				}
			}
		}
	}
}