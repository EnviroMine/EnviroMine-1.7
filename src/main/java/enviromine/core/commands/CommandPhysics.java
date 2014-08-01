package enviromine.core.commands;

import enviromine.core.EM_Settings;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;

public class CommandPhysics extends CommandBase
{

	@Override
	public String getCommandName() 
	{
		return "envirophysic";
	}

    public int getRequiredPermissionLevel()
    {
        return 2;
    }
    
	@Override
	public String getCommandUsage(ICommandSender icommandsender) 
	{
		return "/envirophysic <on, off, toggle, status>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] astring) 
	{
		
		if(astring.length != 1)
		{
			this.ShowUsage(sender);
			return;
		}
		try
		{
		
			if(astring[0].equalsIgnoreCase("toggle"))
			{
				this.togglePhy(sender);
			}
			else if(astring[0].equalsIgnoreCase("on"))
			{
				doPhy(true , sender);
			}
			else if(astring[0].equalsIgnoreCase("off"))
			{
				doPhy(false , sender);
			}
			else if(astring[0].equalsIgnoreCase("status"))
			{
				String Status = "";
				if(EM_Settings.enablePhysics)
				{
					Status = "On";
				}
				else
				{
					Status = "Off";
				}
				sender.sendChatToPlayer(ChatMessageComponent.createFromText("Enviromine Physics Status: " + Status));
			}
			else
			{
				this.ShowUsage(sender);
				return;
			}

		} catch(Exception e)
		{
			this.ShowUsage(sender);
			return;
		}
	}

	
	public void ShowUsage(ICommandSender sender)
	{
		sender.sendChatToPlayer(ChatMessageComponent.createFromText(getCommandUsage(sender)));
	}
	
	public void togglePhy( ICommandSender sender)
	{

		EM_Settings.enablePhysics = !EM_Settings.enablePhysics;
		
		if(EM_Settings.enablePhysics)
		{
			sender.sendChatToPlayer(ChatMessageComponent.createFromText("Enviromine Physics On"));
		}
		else
		{
			sender.sendChatToPlayer(ChatMessageComponent.createFromText("Enviromine Physics Off"));
		}
	}

	public void doPhy(boolean what, ICommandSender sender)
	{
			EM_Settings.enablePhysics = what;
			
			if(what)
			{
				sender.sendChatToPlayer(ChatMessageComponent.createFromText("Enviromine Physics On"));
			}
			else
			{
				sender.sendChatToPlayer(ChatMessageComponent.createFromText("Enviromine Physics Off"));
			}
	}
}
