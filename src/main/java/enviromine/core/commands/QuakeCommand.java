package enviromine.core.commands;

import org.apache.logging.log4j.Level;
import enviromine.core.EnviroMine;
import enviromine.world.Earthquake;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class QuakeCommand extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "enviroquake";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_)
	{
		return "/enviroquake [<x> <z>] [<length> <width> <rotation> <mode(0 ~ 4)>]";
	}
	
	public void ShowUsage(ICommandSender sender)
	{
		sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
	}
	
	@Override
    public int getRequiredPermissionLevel()
    {
        return 4;
    }

	@Override
	public void processCommand(ICommandSender sender, String[] astring)
	{
		if(astring.length != 0 && astring.length != 2 && astring.length != 6)
		{
			this.ShowUsage(sender);
			return;
		}
		
		if(Earthquake.pendingQuakes.size() > 0)
		{
			sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You are not permitted to spawn more than one earthquake at a time!"));
			return;
		}
		
		World world = sender.getEntityWorld();
		int x = sender.getPlayerCoordinates().posX;
		int z = sender.getPlayerCoordinates().posZ;
		int l = 32 + world.rand.nextInt(128-32);
		int w = 4 + world.rand.nextInt(32-4);
		int m = world.rand.nextInt(4);
		float a = MathHelper.clamp_float(world.rand.nextFloat() * 4F - 2F, -2F, 2F);
		
		try
		{
			if(astring.length >= 3)
			{
				x = Integer.parseInt(astring[0]);
				z = Integer.parseInt(astring[1]);
			}
			
			if(astring.length >= 6)
			{
				l = Integer.parseInt(astring[2]);
				w = Integer.parseInt(astring[3]);
				a = ((Float.parseFloat(astring[4])%360F) - 180F)/90F;
				m = MathHelper.clamp_int(Integer.parseInt(astring[5]), 0, 4);
			}
		} catch(Exception e)
		{
			this.ShowUsage(sender);
			return;
		}
		
		if(l * w > 4096)
		{
			sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You may not spawn an earthquake over 4096 blocks!"));
			return;
		}
		
		new Earthquake(world, x, z, l, w, m, a, true);
		EnviroMine.logger.log(Level.INFO, sender.getCommandSenderName() + " spawned earthquake at (" + x + "," + z + ")");
	}
}
