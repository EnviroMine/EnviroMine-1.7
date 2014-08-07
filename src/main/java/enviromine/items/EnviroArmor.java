package enviromine.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import enviromine.handlers.ObjectHandler;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class EnviroArmor extends ItemArmor //implements ITextureProvider, IArmorTextureProvider
{
	public IIcon cpIcon;
	public IIcon gmIcon;
	public IIcon hhIcon;
	
	public EnviroArmor(ArmorMaterial par2EnumArmorMaterial, int par3, int par4)
	{
		super(par2EnumArmorMaterial, par3, par4);
		this.setMaxDamage(100);
		//this.setTextureName("enviromine:camel_pack");
		this.setNoRepair();
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
	{
		if(stack.getItem() == ObjectHandler.camelPack)
		{
			return "enviroMine:textures/models/armor/camelpack_layer_1.png";
		} else if(stack.getItem() == ObjectHandler.gasMask)
		{
			return "enviroMine:textures/models/armor/gasmask_layer_1.png";
		} else if(stack.getItem() == ObjectHandler.hardHat)
		{
			return "enviroMine:textures/models/armor/hardhat_layer_1.png";
		} else
		{
			return null;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.cpIcon = par1IconRegister.registerIcon("enviromine:camel_pack");
		this.gmIcon = par1IconRegister.registerIcon("enviromine:gas_mask");
		this.hhIcon = par1IconRegister.registerIcon("enviromine:hard_hat");
	}
	
	@SideOnly(Side.CLIENT)
	/**
	 * Gets an icon index based on an item's damage value
	 */
	@Override
	public IIcon getIconFromDamage(int par1)
	{
		if(this == ObjectHandler.camelPack && cpIcon != null)
		{
			return this.cpIcon;
		} else if(this == ObjectHandler.gasMask && gmIcon != null)
		{
			return this.gmIcon;
		} else if(this == ObjectHandler.hardHat && hhIcon != null)
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
		if(par1ItemStack.getItem() == ObjectHandler.hardHat && par2ItemStack.getItem() == ObjectHandler.hardHat)
		{
			return true;
		} else
		{
			return false;
		}
	}
}
