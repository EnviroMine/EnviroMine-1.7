package enviromine.handlers.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CraftingHelper
{
	private static final Map<EntityPlayer, CraftingHelper> helpers = new HashMap<EntityPlayer, CraftingHelper>();
	
	public boolean fillBottle;
	public boolean isRemove;
	public boolean isArmor;
	public int packDamage;
	public ItemStack pack;
	public ItemStack armor;
	public ArrayList<ItemStack> bottles = new ArrayList<ItemStack>();
	
	public CraftingHelper(EntityPlayer player) {
		helpers.put(player, this);
	}
	
	public static CraftingHelper getInstanceFromCraftmatrix(InventoryCrafting matrix)
	{
		Iterator iterator = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator();
		
		while (iterator.hasNext()) {
			EntityPlayer player = (EntityPlayer)iterator.next();
			
			if (player.openContainer instanceof ContainerWorkbench) {
				if (((ContainerWorkbench)player.openContainer).craftMatrix == matrix) {
					return getInstanceFromPlayer(player);
				}
			}
		}
		
		return null;
	}
	public static CraftingHelper getInstanceFromPlayer(EntityPlayer player)
	{
		CraftingHelper helper = helpers.get(player);
		if (helper == null) {
			helper = new CraftingHelper(player);
		}
		return helper;
	}
	public static boolean hasInstanceForPlayer(EntityPlayer player) {
		return helpers.containsKey(player);
	}
	
	public void reset()
	{
		this.fillBottle = false;
		this.isRemove = false;
		this.isArmor = false;
		this.packDamage = 0;
		this.pack = null;
		this.armor = null;
		this.bottles.clear();
	}
}