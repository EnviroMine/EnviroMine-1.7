package enviromine.client.gui;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.Level;

public class UpdateNotification
{
	boolean hasChecked = false;
	
	@SuppressWarnings("unused")
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
	{
		if(!EnviroMine.proxy.isClient() || hasChecked)
		{
			return;
		}
		
		hasChecked = true;
		
		
		UpdatePage.display();
		
		// DO NOT CHANGE THIS!
		if(EM_Settings.Version == "FWG_" + "EM" + "_VER")
		{
			event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "THIS COPY OF ENIVROMINE IS NOT FOR PUBLIC USE!"));
			return;
		}
		
		// File link: http://bit.ly/1r4JJt3;
		
		try
		{
			String[] data = getNotification("http://bit.ly/1pwDr2o", true);
			
			if(!EM_Settings.updateCheck)
			{
				return;
			}
			
			//Debug stuff that shouldn't be printed to the user's chat window!
			/*for(int i = 0; i < data.length; i++)
			{
				event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RESET + "" + data[i].trim()));
			}*/
			
			String version = data[0].trim();
			String http = data[1].trim();
			
			int verStat = compareVersions(EM_Settings.Version, version);
			
			if(verStat == -1)
			{
				event.player.addChatMessage(new ChatComponentTranslation("updatemsg.enviromine.avalible", version).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
				event.player.addChatMessage(new ChatComponentTranslation("updatemsg.enviromine.download"));
				event.player.addChatMessage(new ChatComponentText("https://github.com/Funwayguy/EnviroMine/wiki/Downloads").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.BLUE).setUnderlined(true)));
				for(int i = 2; i < data.length; i++)
				{
					if(i > 5)
					{
						event.player.addChatMessage(new ChatComponentText("" + (data.length - 6) + " more..."));
						break;
					} else
					{
						event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RESET + "" + data[i].trim()));
					}
				}
			} else if(verStat == 0)
			{
				event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "EnviroMine " + EM_Settings.Version + " is up to date"));
			} else if(verStat == 1)
			{
				event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "EnviroMine " + EM_Settings.Version + " is a debug version"));
			} else if(verStat == -2)
			{
				event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An error occured while parsing EnviroMine's version file!"));
			}
			
		} catch(IOException e)
		{
			if(EM_Settings.updateCheck)
			{
				EnviroMine.logger.log(Level.WARN, "Failed to get versions file!");
			}
		}
		
	}
	
	private String[] getNotification(String link, boolean doRedirect) throws IOException
	{
		URL url = new URL(link);
		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setDoOutput(false);
		con.setReadTimeout(20000);
		con.setRequestProperty("Connection", "keep-alive");
		
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:16.0) Gecko/20100101 Firefox/16.0");
		((HttpURLConnection)con).setRequestMethod("GET");
		con.setConnectTimeout(5000);
		BufferedInputStream in = new BufferedInputStream(con.getInputStream());
		int responseCode = con.getResponseCode();
		if(responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_MOVED_PERM)
		{
			EnviroMine.logger.log(Level.WARN, "Update request returned response code: " + responseCode + " " + con.getResponseMessage());
		} else if(responseCode == HttpURLConnection.HTTP_MOVED_PERM)
		{
			if(doRedirect)
			{
				try
				{
					return getNotification(con.getHeaderField("location"), false);
				} catch(IOException e)
				{
					throw e;
				}
			} else
			{
				throw new IOException();
			}
		}
		StringBuffer buffer = new StringBuffer();
		int chars_read;
		//	int total = 0;
		while((chars_read = in.read()) != -1)
		{
			char g = (char)chars_read;
			buffer.append(g);
		}
		final String page = buffer.toString();
		
		String[] pageSplit = page.split("\\n");
		
		return pageSplit;
	}
	
	public int compareVersions(String oldVer, String newVer)
	{
		int result = 0;
		int[] oldNum;
		int[] newNum;
		String[] oldNumStr;
		String[] newNumStr;
		
		try
		{
			oldNumStr = oldVer.split("\\.");
			newNumStr = newVer.split("\\.");
			
			oldNum = new int[]{Integer.valueOf(oldNumStr[0]),Integer.valueOf(oldNumStr[1]),Integer.valueOf(oldNumStr[2])};
			newNum = new int[]{Integer.valueOf(newNumStr[0]),Integer.valueOf(newNumStr[1]),Integer.valueOf(newNumStr[2])};
		} catch(IndexOutOfBoundsException e)
		{
			EnviroMine.logger.log(Level.WARN, "An IndexOutOfBoundsException occured while checking version!", e);
			return -2;
		} catch(NumberFormatException e)
		{
			EnviroMine.logger.log(Level.WARN, "A NumberFormatException occured while checking version!\n", e);
			return -2;
		}
		
		for(int i = 0; i < 3; i++)
		{
			if(oldNum[i] < newNum[i])
			{
				return -1;
			} else if(oldNum[i] > newNum[i])
			{
				return 1;
			}
		}
		return result;
	}
}
