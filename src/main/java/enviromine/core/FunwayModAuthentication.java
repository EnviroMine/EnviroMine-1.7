package enviromine.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.util.ArrayList;
import org.apache.logging.log4j.Level;
import scala.actors.threadpool.Arrays;
import enviromine.EnviroPotion;
import enviromine.client.gui.UpdateNotification;
import enviromine.utils.ClassEnumerator;
import enviromine.utils.LockedClass;

public final class FunwayModAuthentication
{
	static final String AUTH_LOC = "https://drone.io/github.com/Funwayguy/EnviroMine-1.7/files/build/libs/version.txt";
	public static boolean AUTH_RESULT = false; // Used as a basic reference. DO NOT use for security purposes!
	
	public static final void CheckAndUnlockMod()
	{
		AUTH_RESULT = false; // Prevents pre-script tampering
		boolean flag = false; // Write to AUTH bytes to file on success?
		
		byte[] auth = GetAuthentication();
		byte[] data = GetOfflineAuth();
		
		if(data == null)
		{
			flag = true;
			try
			{
				// Don't use bitly here because it will skew the statistics
				data = UpdateNotification.getUrl(AUTH_LOC, false).split("\\n")[0].trim().split("\\.")[3].getBytes("UTF-8");
			} catch(Exception e)
			{
				data = null;
			}
		}
		
		if((auth != null && data != null && Arrays.equals(auth, data)) || EM_Settings.Version.equals("FWG_" + "EM_VER"))
		{
			AUTH_RESULT = true;
			if(flag)
			{
				SetOfflineAuth(auth);
			}
			UnlockClasses();
		} else
		{
			// MOD IS NOT AUTHENTICATED!
		}
	}
	
	private static final void SetOfflineAuth(byte[] auth)
	{
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
		File file = new File(EM_ConfigHandler.configPath, EM_Settings.ModID + "_" + EM_Settings.Version + "_AUTH");
		
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
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return digest.digest("AUTH_KEY".getBytes("UTF-8"));
		} catch(Exception e)
		{
			return null;
		}
	}
	
	private static final void UnlockClasses()
	{
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		
		if(AUTH_RESULT)
		{
			classes = ClassEnumerator.getClassesForPackage(EnviroPotion.class.getPackage());
		}
		
		try
		{
			EnviroMine.logger.log(Level.INFO, "Unlocking " + classes.size() + " EnviroMine classes");
			Field f = LockedClass.class.getDeclaredField("LOCKED");
			f.setAccessible(true);
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
			f.set(null, classes);
			f.setAccessible(false);
		} catch(Exception e)
		{
			return;
		}
	}
}
