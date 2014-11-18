package enviromine.blocks.ventilation.multipart;

import enviromine.core.EM_Settings;
import enviromine.handlers.ObjectHandler;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.Arrays;

import codechicken.lib.vec.BlockCoord;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.MultiPartRegistry.IPartConverter;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.TMultiPart;

public class PartRegister implements IPartFactory, IPartConverter
{
	public void init()
    {
        MultiPartRegistry.registerConverter(this);
        MultiPartRegistry.registerParts(this, new String[]{
                EM_Settings.ModID+"|fan",
                EM_Settings.ModID+"|ventSmall"
            });
    }
	
	@Override
	public Iterable<Block> blockTypes()
	{
		return Arrays.asList(ObjectHandler.fan, ObjectHandler.ventSmall);
	}
	
	@Override
	public TMultiPart convert(World world, BlockCoord pos)
	{
		Block b = world.getBlock(pos.x, pos.y, pos.z);
		
		if (b == ObjectHandler.fan)
		{
			return new FanPart();
		} else if (b == ObjectHandler.ventSmall)
		{
			return new VentSmallPart();
		}
		
		return null;
	}
	
	@Override
	public TMultiPart createPart(String name, boolean client)
	{
		if (name.equals(EM_Settings.ModID + "|fan"))
		{
			return new FanPart();
		} else if (name.equals(EM_Settings.ModID + "|ventSmall"))
		{
			return new VentSmallPart();
		}
		
		return null;
	}
}