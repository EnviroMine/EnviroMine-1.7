package enviromine.handlers;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class CamelPackIntegrationHandler implements IRecipe
{
	public int packDamage;
	public ItemStack pack;
	public ItemStack armor;
	
	public CamelPackIntegrationHandler() {}
	
	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		if(!inv.getInventoryName().equals("container.crafting")) {
			return false;
		}
		boolean hasPack = false;
		boolean hasArmor = false;
		
		for(int i = inv.getSizeInventory() - 1; i >= 0; i--)
		{
			ItemStack item = inv.getStackInSlot(i);
			if(item == null) {
				continue;
			} else if(item.getItem() == ObjectHandler.camelPack) {
				if(hasPack) {
					System.out.println("Debug 1");
					return false;
				} else {
					pack = item;
					packDamage = item.getItemDamage();
					hasPack = true;
				}
			} else if(item.getItem() instanceof ItemArmor) {
				if (((ItemArmor)item.getItem()).armorType == 1) {
					if(hasArmor) {
						System.out.println("Debug 2");
						return false;
					} else {
						if (!item.hasTagCompound() || !item.stackTagCompound.hasKey("camelPackFill")) {
							armor = item.copy();
							hasArmor = true;
						}
					}
				}
			} else if(item != null) {
				System.out.println("Debug 3");
				return false;
			}
		}
		
		boolean temp = !(!hasPack || !hasArmor || pack == null || armor == null);
		if (!temp) {
			System.out.println("Debug 4");
		}
		return temp;
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
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
		if (armor != null) {
			if (!armor.hasTagCompound()) {
				armor.setTagCompound(new NBTTagCompound());
			}
			armor.getTagCompound().setInteger("camelPackFill", 100-pack.getItemDamage());
			
			return armor;
		} else {
			return null;
		}
	}
}