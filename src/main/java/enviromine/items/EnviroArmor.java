package enviromine.items;

import enviromine.handlers.EnviroAchievements;
import enviromine.handlers.ObjectHandler;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnviroArmor extends ItemArmor //implements ITextureProvider, IArmorTextureProvider
{
	public IIcon cpIcon;
	public IIcon gmIcon;
	public IIcon hhIcon;
	
	public int gasMaskFillMax = 200;
	
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
		if (stack.getItem() == ObjectHandler.camelPack)
		{
			return "enviroMine:textures/models/armor/camelpack_layer_1.png";
		} else if (stack.getItem() == ObjectHandler.gasMask)
		{
			return "enviroMine:textures/models/armor/gasmask_layer_1.png";
		} else if (stack.getItem() == ObjectHandler.hardHat)
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
		
		if (this == ObjectHandler.camelPack && cpIcon != null)
		{
			return this.cpIcon;
		} else if (this == ObjectHandler.gasMask && gmIcon != null)
		{
			return this.gmIcon;
		} else if (this == ObjectHandler.hardHat && hhIcon != null)
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
		if (par1ItemStack.getItem() == ObjectHandler.hardHat && par2ItemStack.getItem() == ObjectHandler.hardHat)
		{
			return true;
		} else if (par1ItemStack.getItem() == ObjectHandler.gasMask && par2ItemStack.getItem() == ObjectHandler.gasMask)
		{
			return true;
		} else if (par1ItemStack.getItem() == ObjectHandler.camelPack && par2ItemStack.getItem() == ObjectHandler.camelPack)
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	// Creates a tag if item was grabbed from creative menu
	@Override
	public void onUpdate(ItemStack armor, World p_77663_2_, Entity entity, int p_77663_4_, boolean p_77663_5_)
	{
		if (armor.getItem() == ObjectHandler.camelPack)
		{
			if (!armor.hasTagCompound())
			{
				armor.setTagCompound(new NBTTagCompound());
			}
			if (!armor.getTagCompound().hasKey("camelPackFill"))
			{
				int meta = armor.getItemDamage() > 0 ? 100 - armor.getItemDamage() : 0;
				armor.getTagCompound().setInteger("camelPackFill", meta);
				armor.setItemDamage(0);
			}
			if (!armor.getTagCompound().hasKey("camelPackMax"))
			{
				armor.getTagCompound().setInteger("camelPackMax", 100);
			}
			if (!armor.getTagCompound().hasKey("isCamelPack"))
			{
				armor.getTagCompound().setBoolean("isCamelPack", true);
			}
		} else if (armor.getItem() == ObjectHandler.gasMask)
		{
			if (!armor.hasTagCompound())
			{
				armor.setTagCompound(new NBTTagCompound());
			}
			if (!armor.getTagCompound().hasKey("gasMaskFill"))
			{
				armor.getTagCompound().setInteger("gasMaskFill", 1000);
			}
			if (!armor.getTagCompound().hasKey("gasMaskMax"))
			{
				armor.getTagCompound().setInteger("gasMaskMax", 1000);
			}
		} else if (armor.getItem() == ObjectHandler.hardHat)
		{
		} else
		{
		}
	}
	
	@Override
	public void onCreated(ItemStack armor, World world, EntityPlayer player)
	{
		if (armor.getItem() == ObjectHandler.camelPack)
		{
			if (!armor.hasTagCompound())
			{
				armor.setTagCompound(new NBTTagCompound());
			}
			if (!armor.getTagCompound().hasKey("camelPackFill"))
			{
				int meta = armor.getItemDamage() > 0 ? 100 - armor.getItemDamage() : 0;
				armor.getTagCompound().setInteger("camelPackFill", meta);
				armor.setItemDamage(0);
			}
			if (!armor.getTagCompound().hasKey("camelPackMax"))
			{
				armor.getTagCompound().setInteger("camelPackMax", 100);
			}
			if (!armor.getTagCompound().hasKey("isCamelPack"))
			{
				armor.getTagCompound().setBoolean("isCamelPack", true);
			}
		} else if (armor.getItem() == ObjectHandler.gasMask)
		{
			if (!armor.hasTagCompound())
			{
				armor.setTagCompound(new NBTTagCompound());
			}
			armor.getTagCompound().setInteger("gasMaskFill", 1000);
			armor.getTagCompound().setInteger("gasMaskMax", 1000);
			player.addStat(EnviroAchievements.breatheEasy, 1);
		} else if (armor.getItem() == ObjectHandler.hardHat)
		{
			player.addStat(EnviroAchievements.safetyFirst, 1);
		} else
		{
		}
	}
}
