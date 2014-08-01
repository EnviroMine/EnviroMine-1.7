package enviromine.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.handlers.ObjectHandler;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class EnviroArmor extends ItemArmor //implements ITextureProvider, IArmorTextureProvider
{
	public Icon cpIcon;
	public Icon gmIcon;
	public Icon hhIcon;
	
	public EnviroArmor(int par1, EnumArmorMaterial par2EnumArmorMaterial, int par3, int par4)
	{
		super(par1, par2EnumArmorMaterial, par3, par4);
		this.setMaxDamage(100);
		//this.setTextureName("enviromine:camel_pack");
		this.setNoRepair();
	}
	
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
	{
		if(itemID == ObjectHandler.camelPack.itemID)
		{
			return "enviroMine:textures/models/armor/camelpack_layer_1.png";
		} else if(itemID == ObjectHandler.gasMask.itemID)
		{
			return "enviroMine:textures/models/armor/gasmask_layer_1.png";
		} else if(itemID == ObjectHandler.hardHat.itemID)
		{
			return "enviroMine:textures/models/armor/hardhat_layer_1.png";
		} else
		{
			return null;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.cpIcon = par1IconRegister.registerIcon("enviromine:camel_pack");
		this.gmIcon = par1IconRegister.registerIcon("enviromine:gas_mask");
		this.hhIcon = par1IconRegister.registerIcon("enviromine:hard_hat");
	}
	
	@SideOnly(Side.CLIENT)
	/**
	 * Gets an icon index based on an item's damage value
	 */
	public Icon getIconFromDamage(int par1)
	{
		if(this.itemID == ObjectHandler.camelPack.itemID && cpIcon != null)
		{
			return this.cpIcon;
		} else if(this.itemID == ObjectHandler.gasMask.itemID && gmIcon != null)
		{
			return this.gmIcon;
		} else if(this.itemID == ObjectHandler.hardHat.itemID && hhIcon != null)
		{
			return this.hhIcon;
		}
		{
			return super.getIconFromDamage(par1);
		}
	}
	
	@Override
	/**
	 * Return whether this item is repairable in an anvil.
	 */
	public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
	{
		if(par1ItemStack.itemID == ObjectHandler.hardHat.itemID && par2ItemStack.itemID == ObjectHandler.hardHat.itemID)
		{
			return true;
		} else
		{
			return false;
		}
	}
}
