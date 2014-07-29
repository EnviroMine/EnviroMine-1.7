package main.java.enviromine.items;

import main.java.enviromine.core.EnviroMine;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnviroArmor extends ItemArmor //implements ITextureProvider, IArmorTextureProvider
{
	public IIcon cpIcon;
	
	public EnviroArmor(ArmorMaterial par2ArmorMaterial, int par3, int par4)
	{
		super(par2ArmorMaterial, par3, par4);
		this.setMaxDamage(100);
		//this.setTextureName("enviromine:camel_pack");
		this.setNoRepair();
	}
	
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
	{
		if(stack.getItem() == EnviroMine.camelPack)
		{
			return "enviroMine:textures/models/armor/camelpack_layer_1.png";
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
	}
	
	@SideOnly(Side.CLIENT)
	/**
	 * Gets an icon index based on an item's damage value
	 */
	public IIcon getIconFromDamage(int par1)
	{
		if(cpIcon != null)
		{
			return this.cpIcon;
		} else
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
		return false;
	}
}
