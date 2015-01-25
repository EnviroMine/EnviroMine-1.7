package enviromine.core.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;

import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.world.Earthquake;

public class QuakeCommand extends CommandBase
{
	
	private String lengthName = StatCollector.translateToLocal("commands.enviromine.enviroquake.length");
	private String widthName = StatCollector.translateToLocal("commands.enviromine.enviroquake.width");
	private String rotationName = StatCollector.translateToLocal("commands.enviromine.enviroquake.rotation");
	private String modeName = StatCollector.translateToLocal("commands.enviromine.enviroquake.mode");
	private String errorMany = StatCollector.translateToLocal("commands.enviromine.enviroquake.error.tooMany");
	private String errorBig = StatCollector.translateToLocal("commands.enviromine.enviroquake.error.tooBig");

	@Override
	public String getCommandName()
	{
		return "enviroquake";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_)
	{
		return "/enviroquake [<x> <z>] [<"+lengthName+"> <"+widthName+"> <"+rotationName+"> <"+modeName+"(0 ~ 4)>]";
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
			sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + errorMany));
			return;
		}
		
		World world = sender.getEntityWorld();
		int x = sender.getPlayerCoordinates().posX;
		int z = sender.getPlayerCoordinates().posZ;
		int l = 32 + world.rand.nextInt(128-32);
		int w = 4 + world.rand.nextInt(32-4);
		int m = EM_Settings.quakeMode;
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
			sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + errorBig));
			return;
		}
		
		new Earthquake(world, x, z, l, w, m, a, true);
		EnviroMine.logger.log(Level.INFO, sender.getCommandSenderName() + " spawned earthquake at (" + x + "," + z + ")");
	}
}
