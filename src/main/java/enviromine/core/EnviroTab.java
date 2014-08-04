package enviromine.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnviroTab extends CreativeTabs
{
	public EnviroTab(String par2Str) {
		super(par2Str);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return Items.apple; //TODO set display item
	}
}