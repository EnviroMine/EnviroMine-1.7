package enviromine.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import enviromine.handlers.ObjectHandler;

public class EnviroTab extends CreativeTabs
{
	public EnviroTab(String par2Str) {
		super(par2Str);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return ObjectHandler.camelPack;
	}
}