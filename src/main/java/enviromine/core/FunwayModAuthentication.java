package enviromine.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import enviromine.EnviroPotion;
import enviromine.client.gui.EM_GuiAuthWarn;
import enviromine.utils.ClassEnumerator;
import enviromine.utils.LockedClass;

/**
 * Controls authentication and unlocking of all classes in the mod. If a private method in this class is called through reflection it WILL intentionally crash
 */
public final class FunwayModAuthentication
{
	private static final String AUTH_LOC = "https://drone.io/github.com/Funwayguy/EnviroMine-1.7/files/build/libs/version.txt";
	public static boolean AUTH_RESULT = false; // Used as a basic reference. DO NOT use for security purposes!
	
	public static final void CheckAndUnlockMod()
	{
		if(!new Exception().getStackTrace()[1].getClassName().equals(EnviroMine.class.getName()) || AUTH_RESULT || LockedClass.GetUnlocked() == null || LockedClass.GetUnlocked().size() > 0)
		{
			throw new IllegalStateException("ENVIROMINE WAS TAMPERED WITH DURING AUTHENTICATION");
		}
		
		boolean flag = false; // Write to AUTH bytes to file on success?
		
		byte[] auth = GetAuthentication();
		byte[] data = GetOfflineAuth();
		
		if(data == null || !Arrays.equals(auth, data))
		{
			flag = true;
			try
			{
				// Don't use bitly here because it will skew the statistics
				data = getUrl(AUTH_LOC, false).split("\\n")[0].trim().split("\\.")[3].getBytes("UTF-8");
			} catch(Exception e)
			{
				data = null;
			}
		}
		
		 //We don't want to use the unsecure version var here. We use the raw keyword that will be converted at runtime
		if((auth != null && data != null && Arrays.equals(auth, data)) || "FWG_EM_VER".equals("FWG_" + "EM_VER"))
		{
			AUTH_RESULT = true;
			if(flag)
			{
				if(!"FWG_EM_VER".equals("FWG_" + "EM_VER"))
				{
					EM_GuiAuthWarn.shouldWarn = true;
				}
				SetOfflineAuth(auth);
			}
			UnlockClasses();
		} else
		{
			// MOD IS NOT AUTHENTICATED!
			SecurityException exception = new SecurityException("UNAUTHORIZED USE OF MOD " + EM_Settings.ModID.toUpperCase());
			exception.setStackTrace(new StackTraceElement[]{}); // Empties the stack trace to hinder debugging hacks
			throw exception;
		}
	}
	
	private static final void SetOfflineAuth(byte[] auth)
	{
		if(!new Exception().getStackTrace()[1].getClassName().equals(FunwayModAuthentication.class.getName()))
		{
			throw new IllegalStateException("ENVIROMINE WAS TAMPERED WITH DURING AUTHENTICATION");
		}
		
		File file = new File(EM_ConfigHandler.configPath, EM_Settings.ModID.toUpperCase() + "_AUTH");
		
		try
		{
			if(!file.exists())
			{
				new File(EM_ConfigHandler.configPath).mkdirs();
				file.createNewFile();
			}
			
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(auth);
			
			oos.close();
			fos.close();
		} catch(Exception e)
		{
			EnviroMine.logger.log(Level.ERROR, "Failed to save offline authentication file!", e);
		}
	}
	
	private static final byte[] GetOfflineAuth()
	{
		if(!new Exception().getStackTrace()[1].getClassName().equals(FunwayModAuthentication.class.getName()))
		{
			throw new IllegalStateException("ENVIROMINE WAS TAMPERED WITH DURING AUTHENTICATION");
		}
		
		File file = new File(EM_ConfigHandler.configPath, EM_Settings.ModID.toUpperCase() + "_AUTH");
		
		if(file.exists())
		{
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			
			try
			{
				fis = new FileInputStream(file);
				ois = new ObjectInputStream(fis);
				
				byte[] obj = (byte[])ois.readObject();
				
				ois.close();
				ois.close();
				return obj;
			} catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns a SHA-256 encrypted version of the original authentication key
	 */
	private static final byte[] GetAuthentication()
	{
		if(!new Exception().getStackTrace()[1].getClassName().equals(FunwayModAuthentication.class.getName()))
		{
			throw new IllegalStateException("ENVIROMINE WAS TAMPERED WITH DURING AUTHENTICATION");
		}
		
		try
		{
			/*long key = Long.parseLong("EM_AUTH_KEY");
			short unlock1 = (short)((key >> 48) & 0xFFFF);
			short unlock2 = (short)((key >> 32) & 0xFFFF);
			short unlock3 = (short)((key >> 16) & 0xFFFF);
			short unlock4 = (short)((key) & 0xFFFF);
			String keyString = unlock1 + "-" + unlock2 + "-" + unlock3 + "-" + unlock4;*/
			//MessageDigest digest = MessageDigest.getInstance("SHA-256");
			//return digest.digest("EM_AUTH_KEY".getBytes("UTF-8"));
			return "EM_AUTH_KEY".getBytes("UTF-8");
		} catch(Exception e)
		{
			return null;
		}
	}
	
	private static final void UnlockClasses()
	{
		if(!new Exception().getStackTrace()[1].getClassName().equals(FunwayModAuthentication.class.getName()) || LockedClass.GetUnlocked() == null || LockedClass.GetUnlocked().size() > 0)
		{
			throw new IllegalStateException("ENVIROMINE WAS TAMPERED WITH DURING AUTHENTICATION");
		}
		
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		
		if(AUTH_RESULT)
		{
			Package pack = EnviroPotion.class.getPackage();
			try
			{
				classes = ClassEnumerator.getClassesForPackage(pack);
			} catch(Exception e)
			{
				try
				{
					ModContainer mod = Loader.instance().getReversedModObjectList().get(EnviroMine.instance);
					ClassEnumerator.processJarfile(mod.getSource().getAbsolutePath(), "enviromine", classes);
				} catch(Exception e1)
				{
					e1.printStackTrace();
					return;
				}
			}
		}
		
		try
		{
			EnviroMine.logger.log(Level.INFO, "Unlocking " + classes.size() + " EnviroMine classes");
			Field f = LockedClass.class.getDeclaredField("LOCKED");
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true); // Start unlocking all the modifiers
			modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
			modifiers.setInt(f, f.getModifiers() & ~Modifier.PRIVATE);
			modifiers.setInt(f, f.getModifiers() | Modifier.PUBLIC);
			f.set(null, classes); // Set the unlocked class listing
			modifiers.setInt(f, f.getModifiers() & ~Modifier.PUBLIC);
			modifiers.setInt(f, f.getModifiers() | Modifier.PRIVATE);
			modifiers.setInt(f, f.getModifiers() | Modifier.FINAL);
			f.setAccessible(false); // LOCK IT BACK DOWN
		} catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Grabs http webpage and returns data. Dedicated version for security reasons (So someone can't intercept the call and inject false data)
	 */
	private static final String getUrl(String link, boolean doRedirect) throws IOException
	{
		if(!new Exception().getStackTrace()[1].getClassName().equals(FunwayModAuthentication.class.getName()))
		{
			throw new IllegalStateException("ENVIROMINE WAS TAMPERED WITH DURING AUTHENTICATION");
		}
		
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
}
