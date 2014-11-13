package enviromine.blocks.ventilation.multipart;

import net.minecraft.block.Block;

import enviromine.core.EM_Settings;
import enviromine.handlers.ObjectHandler;

import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.minecraft.McMetaPart;

public class FanPart extends McMetaPart
{
	@Override
	public Cuboid6 getBounds()
	{
		return null; //TODO
	}
	
	@Override
	public Block getBlock()
	{
		return ObjectHandler.fan;
	}
	
	@Override
	public String getType()
	{
		return EM_Settings.ModID+"|fan";
	}
}