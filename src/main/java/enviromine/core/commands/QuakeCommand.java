package enviromine.core.commands;

import java.util.Iterator;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.network.packet.PacketEnviroMine;
import enviromine.world.Earthquake;

public class QuakeCommand extends CommandBase
{
	
	private String lengthName = StatCollector.translateToLocal("commands.enviromine.enviroquake.length");
	private String widthName = StatCollector.translateToLocal("commands.enviromine.enviroquake.width");
	private String rotationName = StatCollector.translateToLocal("commands.enviromine.enviroquake.rotation");
	private String modeName = StatCollector.translateToLocal("commands.enviromine.enviroquake.mode");
	private String stopName = StatCollector.translateToLocal("commands.enviromine.enviroquake.stop");
	private String stoppedAll = StatCollector.translateToLocal("commands.enviromine.enviroquake.stoppedAll");
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
		return "/enviroquake ["+stopName+" | <x> <z> [<"+lengthName+"> <"+widthName+"> <"+rotationName+"> <"+modeName+"(0 ~ 4)>]]";
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
		if(astring.length != 0 && astring.length != 1 && astring.length != 2 && astring.length != 6)
		{
			this.ShowUsage(sender);
			return;
		}
		
		if(astring.length == 1)
		{
			if(astring[0].equalsIgnoreCase(stopName))
			{
				Iterator<Earthquake> iterator = Earthquake.pendingQuakes.iterator();
				
				while(iterator.hasNext())
				{
					Earthquake quake = iterator.next();
					int size = quake.length > quake.width? quake.length/2 : quake.width/2;
					NBTTagCompound pData = new NBTTagCompound();
					pData.setInteger("id", 3);
					pData.setInteger("dimension", quake.world.provider.dimensionId);
					pData.setInteger("posX", quake.posX);
					pData.setInteger("posZ", quake.posZ);
					pData.setInteger("length", quake.length);
					pData.setInteger("width", quake.width);
					pData.setFloat("angle", quake.angle);
					pData.setFloat("action", 2);
					pData.setFloat("height", quake.passY);
					EnviroMine.instance.network.sendToAllAround(new PacketEnviroMine(pData), new TargetPoint(quake.world.provider.dimensionId, quake.posX, quake.passY, quake.posZ, 128 + size));
					iterator.remove();
				}
				Earthquake.pendingQuakes.clear();
				sender.addChatMessage(new ChatComponentText(stoppedAll));
				return;
			} else
			{
				this.ShowUsage(sender);
				return;
			}
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
