package enviromine.handlers.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import enviromine.handlers.ObjectHandler;

public class LampLightingHandler
{
	// I feel as though making a whole class for this is probably overkill but it keeps with the organisation of crafting handlers - Funwayguy
	
	@SubscribeEvent
	public void onCrafting(PlayerEvent.ItemCraftedEvent event)
	{
		boolean hasLamp = false;
		ItemStack lighter = null;
		
		IInventory craftMatrix = event.craftMatrix;
		if (!(craftMatrix instanceof InventoryCrafting))
		{
			return;
		}
		
		for(int i = 0; i < craftMatrix.getSizeInventory(); i++)
		{
			ItemStack stack = craftMatrix.getStackInSlot(i);
			
			if(stack != null)
			{
				if(stack.getItem() == Item.getItemFromBlock(ObjectHandler.davyLampBlock) && !hasLamp)
				{
					hasLamp = true;
				} else if(stack.getItem() == Items.flint_and_steel && lighter == null)
				{
					lighter = stack;
				} else
				{
					return;
				}
			}
		}
		
		if(hasLamp && lighter != null && lighter.getItemDamage() < lighter.getMaxDamage())
		{
			if(!event.player.inventory.addItemStackToInventory(new ItemStack(Items.flint_and_steel, 1, lighter.getItemDamage() + 1)))
			{
				event.player.dropPlayerItemWithRandomChoice(new ItemStack(Items.flint_and_steel, 1, lighter.getItemDamage() + 1), false);
			}
		}
	}
}
