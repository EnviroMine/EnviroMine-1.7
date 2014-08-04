package enviromine.core.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import enviromine.handlers.EM_StatusManager;
import enviromine.trackers.EnviroDataTracker;

import java.util.ArrayList;
import java.util.List;

public class EnviroCommand extends CommandBase
{
	public EnviroCommand()
	{
	}

	@Override
	public String getCommandName()
	{
		return "envirostat";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/envirostat <playername, me> <add, set> <temp, sanity, water, air> <float>";
	}
	
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

	@Override
	public void processCommand(ICommandSender sender, String[] astring)
	{

		if(astring.length != 4)
		{
			this.ShowUsage(sender);
			return;
		}
		
		String target = "";
		if(astring[0].equalsIgnoreCase("me") )
		{
			target = sender.getCommandSenderName();
		}
		else
		{
			target = astring[0];
		}
		
		EnviroDataTracker tracker = EM_StatusManager.lookupTrackerFromUsername(target);
		
		if(tracker == null)
		{
			this.ShowNoTracker(sender);
			return;
		}
		
		try
		{
			float value = Float.parseFloat(astring[3]);
			
			if(astring[1].equalsIgnoreCase("add"))
			{
				if(astring[2].equalsIgnoreCase("temp"))
				{
					tracker.bodyTemp += value;
				} else if(astring[2].equalsIgnoreCase("sanity"))
				{
					tracker.sanity += value;
				} else if(astring[2].equalsIgnoreCase("water"))
				{
					tracker.hydration += value;
				} else if(astring[2].equalsIgnoreCase("air"))
				{
					tracker.airQuality += value;
				} else
				{
					this.ShowUsage(sender);
					return;
				}
			} else if(astring[1].equalsIgnoreCase("set"))
			{
				if(astring[2].equalsIgnoreCase("temp"))
				{
					tracker.bodyTemp = value;
				} else if(astring[2].equalsIgnoreCase("sanity"))
				{
					tracker.sanity = value;
				} else if(astring[2].equalsIgnoreCase("water"))
				{
					tracker.hydration = value;
				} else if(astring[2].equalsIgnoreCase("air"))
				{
					tracker.airQuality = value;
				} else
				{
					this.ShowUsage(sender);
					return;
				}
			} else
			{
				this.ShowUsage(sender);
				return;
			}
			
			tracker.fixFloatinfPointErrors();
			return;
		} catch(Exception e)
		{
			this.ShowUsage(sender);
			return;
		}
	}
	
	public void ShowUsage(ICommandSender sender)
	{
		sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
	}
	
	public void ShowNoTracker(ICommandSender sender)
	{
		sender.addChatMessage(new ChatComponentText("Target not found or has no enviro tracker!"));
	}

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender sender, String[] strings)
    {
        if(strings.length == 1)
        {
        	return getListOfStringsMatchingLastWord(strings, MinecraftServer.getServer().getAllUsernames());
        } else if(strings.length == 2)
        {
        	return getListOfStringsMatchingLastWord(strings, new String[]{"add", "set"});
        } else if(strings.length == 3)
        {
        	return getListOfStringsMatchingLastWord(strings, new String[]{"temp", "sanity", "water", "air"});
        } else
        {
        	return new ArrayList();
        }
    }
}
