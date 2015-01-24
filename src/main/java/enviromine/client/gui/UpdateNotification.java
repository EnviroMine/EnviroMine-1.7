package enviromine.client.gui;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import enviromine.client.gui.menu.update.WordPressParser;
import enviromine.client.gui.menu.update.WordPressPost;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;

public class UpdateNotification
{
	boolean hasChecked = false;
	public static String version;
	public static String lastSeen;
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
	{
		if(!EnviroMine.proxy.isClient() || hasChecked)
		{
			return;
		}
		
		hasChecked = true;
		
		loadConfigLog();
		
		loadWordPress();
		
		displayUpdateCheck(event);
		
	}
	
	/**
	 * This will get grab Enviromines Change Logs
	 */
	private void loadConfigLog()
	{
		try {
			WordPressPost.changeLog = getUrl("https://drone.io/github.com/Funwayguy/EnviroMine-1.7/files/build/libs/full_changelog.txt", true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			EnviroMine.logger.log(Level.WARN, "Failed to get ChangeLog file!");
		}

	}
	
	/**
	 * This will grab Word Press Post Page and sent to parser
	 */
	private void loadWordPress()
	{
		try
		{
			String page = getUrl("https://enviromine.wordpress.com/news/feed/", true);
		
			try {
				WordPressParser.main(page);
			} catch (Exception e) {
				EnviroMine.logger.log(Level.WARN, "Failed to parse WordPress News Page");
			}
			
			
		}catch(IOException e)
		{
			if(EM_Settings.updateCheck)
			{
				EnviroMine.logger.log(Level.WARN, "Failed to get WordPress News Page!");
			}
		}
		
	}
	
	@SuppressWarnings("unused")
	private void displayUpdateCheck(PlayerLoggedInEvent event)
	{
		
		// File link: http://bit.ly/1r4JJt3;
		// DO NOT CHANGE THIS!
		if(EM_Settings.Version == "FWG_" + "EM" + "_VER")
		{
			event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "THIS COPY OF ENIVROMINE IS NOT FOR PUBLIC USE!"));
			return;
		}
		
		try
		{
			String page = getUrl("http://bit.ly/1pwDr2o", true);
			String[] data = page.split("\\n");
			
			
			if(!EM_Settings.updateCheck)
			{
				return;
			}
			
			//Debug stuff that shouldn't be printed to the user's chat window!
			/*for(int i = 0; i < data.length; i++)
			{
				event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RESET + "" + data[i].trim()));
			}*/
			
			version = data[0].trim();
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
				event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted("updatemsg.enviromine.uptodate", EM_Settings.Version)));
			} else if(verStat == 1)
			{
				event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("updatemsg.enviromine.debug", EM_Settings.Version)));
			} else if(verStat == -2)
			{
				event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("updatemsg.enviromine.error")));
			}
			
		} catch(IOException e)
		{
			if(EM_Settings.updateCheck)
			{
				EnviroMine.logger.log(Level.WARN, "Failed to get versions file!");
			}
		}
	}
	
	/**
	 * Grabs http webpage and returns data
	 * @param link
	 * @param doRedirect
	 * @return
	 * @throws IOException
	 */
	public static String getUrl(String link, boolean doRedirect) throws IOException
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
					return getUrl(con.getHeaderField("location"), false);
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
		
		return page;
	}

	/**
	 * Will compare Versions numbers and give difference
	 * @param oldVer
	 * @param newVer
	 * @return
	 */
	public static int compareVersions(String oldVer, String newVer)
	{
		if(oldVer == null || newVer == null || oldVer.isEmpty() || newVer.isEmpty())
		{
			return -2;
		}
		
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
	/**
	 *  This will update last seen post by player.
	 */
	public static void updateLastSeen()
	{
		if(WordPressPost.Posts.size() > 0)
		{
			lastSeen = WordPressPost.Posts.get(0).getPubDate();
		}
	}
	
	/**
	 * This will compare last seen post with last post and return boolean
	 * @return
	 */
	public static boolean isNewPost()
	{
		if(lastSeen == null) return true; 

		 if(!WordPressPost.Posts.isEmpty())
		 {
			 WordPressPost lastPost = WordPressPost.Posts.get(0);

		
			 if(lastPost.getPubDate().toLowerCase().trim().equals(lastSeen.toLowerCase().trim()))
			 {
				 return false;
			 }		
		 }
		return true;
	}
	
	public static void writeToNBT(NBTTagCompound nbt)
	{
		if(lastSeen != null)
		{
			nbt.setString("LastSeen", lastSeen);
		}
	}
	
	public static void readFromNBT(NBTTagCompound nbt)
	{
		if(nbt.hasKey("LastSeen"))
		{
			lastSeen = nbt.getString("LastSeen");	
		}
		 
	}
}
