package enviromine.client.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ReportedException;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.core.EnviroMine;

@SideOnly(Side.CLIENT)
public class UI_Settings {

	public static String enviroSettingsFile = "em_options.txt";

	public static boolean ShowGuiIcons;
	public static float guiScale;
	public static boolean sweatParticals;
	public static boolean insaneParticals;
	public static boolean useFarenheit;
	public static String heatBarPos;
	public static String waterBarPos;
	public static String sanityBarPos;
	public static String oxygenBarPos;
	public static boolean ShowText;
	public static boolean ShowDebug;
	public static boolean breathSound;
	public static int breathPause;
	public static float breathVolume;
	public static boolean minimalHud;
	public static int screenWidth;
	public static int screenHeight;
	
	@SideOnly(Side.CLIENT)
	public static void loadSettings() {

		File f = new File(enviroSettingsFile);
		if (!f.exists()) {
			// Check Propeties will Write new file
			checkProperties(new Properties());
		}

		readSettings();
	}

	@SideOnly(Side.CLIENT)
	public static void saveSettings() {
		Properties prop = new Properties();

		prop.setProperty("ShowGuiIcons", "" + ShowGuiIcons);
		prop.setProperty("guiScale", "" + guiScale);
		prop.setProperty("sweatParticals", "" + sweatParticals);
		prop.setProperty("insaneParticals", "" + insaneParticals);
		prop.setProperty("useFarenheit", "" + useFarenheit);
		prop.setProperty("heatBarPos", "" + heatBarPos);
		prop.setProperty("waterBarPos", "" + waterBarPos);
		prop.setProperty("sanityBarPos", "" + sanityBarPos);
		prop.setProperty("oxygenBarPos", "" + oxygenBarPos);
		prop.setProperty("ShowText", "" + ShowText);
		prop.setProperty("ShowDebug", "" + ShowDebug);
		prop.setProperty("breathSound", "" + breathSound);
		prop.setProperty("breathPause", "" + breathPause);
		prop.setProperty("breathVolume", "" + breathVolume);
		prop.setProperty("minimalHud", "" + minimalHud);
		prop.setProperty("screenWidth", "" + screenWidth);
		prop.setProperty("screenHeight", "" + screenHeight);

		WriteSettings(prop);
	}

	@SideOnly(Side.CLIENT)
	public static Properties checkProperties(Properties prop) {

		if (!prop.containsKey("ShowGuiIcons"))
			prop.setProperty("ShowGuiIcons", "true");
		if (!prop.containsKey("guiScale"))
			prop.setProperty("guiScale", "1.0");
		if (!prop.containsKey("sweatParticals"))
			prop.setProperty("sweatParticals", "true");
		if (!prop.containsKey("insaneParticals"))
			prop.setProperty("insaneParticals", "true");
		if (!prop.containsKey("useFarenheit"))
			prop.setProperty("useFarenheit", "true");
		if (!prop.containsKey("heatBarPos"))
			prop.setProperty("heatBarPos", "Bottom_Left");
		if (!prop.containsKey("waterBarPos"))
			prop.setProperty("waterBarPos", "Bottom_Left");
		if (!prop.containsKey("sanityBarPos"))
			prop.setProperty("sanityBarPos", "Bottom_Right");
		if (!prop.containsKey("oxygenBarPos"))
			prop.setProperty("oxygenBarPos", "Bottom_Right");
		if (!prop.containsKey("ShowText"))
			prop.setProperty("ShowText", "true");
		if (!prop.containsKey("ShowDebug"))
			prop.setProperty("ShowDebug", "false");
		if (!prop.containsKey("breathSound"))
			prop.setProperty("breathSound", "true");
		if (!prop.containsKey("breathPause"))
			prop.setProperty("breathPause", "300");
		if (!prop.containsKey("breathVolume"))
			prop.setProperty("breathVolume", "0.75");
		if (!prop.containsKey("minimalHud"))
			prop.setProperty("minimalHud", "false");
		if (!prop.containsKey("screenWidth"))
			prop.setProperty("screenWidth", ""+Minecraft.getMinecraft().displayWidth);
		if (!prop.containsKey("screenHeight"))
			prop.setProperty("screenHeight", ""+Minecraft.getMinecraft().displayHeight);

		WriteSettings(prop);
		return prop;
	}

	@SideOnly(Side.CLIENT)
	public static void WriteSettings(Properties prop) {

		OutputStream output = null;

		try {

			try {
				output = new FileOutputStream(enviroSettingsFile);

				EnviroMine.logger.log(Level.INFO, "Saving Options File: "
						+ enviroSettingsFile);

			} catch (NullPointerException e) {
				e.printStackTrace();
				EnviroMine.logger.log(Level.WARN,
						"FAILED TO LOAD Options File: " + enviroSettingsFile
								+ "\nNEW SETTINGS WILL BE IGNORED!");
				return;
			} catch (StringIndexOutOfBoundsException e) {
				e.printStackTrace();
				EnviroMine.logger.log(Level.WARN,
						"FAILED TO LOAD Options File: " + enviroSettingsFile
								+ "\nNEW SETTINGS WILL BE IGNORED!");
				return;
			}

			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	@SideOnly(Side.CLIENT)
	public static void readSettings() {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(enviroSettingsFile);

			// load a properties file
			prop.load(input);

			// Check all Proptires before reading
			prop = checkProperties(prop);

			ShowGuiIcons = getBoolean(prop.getProperty("ShowGuiIcons"));
			sweatParticals = getBoolean(prop.getProperty("sweatParticals"));
			insaneParticals = getBoolean(prop.getProperty("insaneParticals"));
			useFarenheit = getBoolean(prop.getProperty("useFarenheit"));
			ShowText = getBoolean(prop.getProperty("ShowText"));
			ShowDebug = getBoolean(prop.getProperty("ShowDebug"));
			guiScale = getFloat(prop.getProperty("guiScale"));
			breathSound = getBoolean(prop.getProperty("breathSound"));
			breathPause = getInterger(prop.getProperty("breathPause"));
			breathVolume = getFloat(prop.getProperty("breathVolume"));
			minimalHud = getBoolean(prop.getProperty("minimalHud"));
			heatBarPos = prop.getProperty("heatBarPos");
			waterBarPos = prop.getProperty("waterBarPos");
			sanityBarPos = prop.getProperty("sanityBarPos");
			oxygenBarPos = prop.getProperty("oxygenBarPos");
			screenWidth = getInterger(prop.getProperty("screenWidth"));
			screenHeight = getInterger(prop.getProperty("screenHeight"));

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static boolean getBoolean(String n) {
		boolean m = Boolean.valueOf(n.toLowerCase().trim());
		;
		return m;
	}

	private static int getInterger(String n) {
		int m = Integer.valueOf(n.trim());
		return m;
	}

	private static float getFloat(String n) {
		float m = Float.valueOf(n.trim());
		return m;
	}

	/**
	 * NBT FILE SAVES
	 */

	protected static final String dirName = Minecraft.getMinecraft().mcDataDir + File.separator + "config" + File.separator + "EnviroMine";
	protected static File dir = new File(dirName);

	public static boolean loadConfig(String name) {
		return loadConfig(name, null, null);
	}

	public static boolean loadConfig(String name, String dirName , NBTTagCompound nbtag) {
		if (dirName != null) 
		{
			dir = new File(Minecraft.getMinecraft().mcDataDir + File.separator + dirName);
		}

		String fileName = name + ".dat";
		File file = new File(dir, fileName);

		if (!file.exists()) {
			EnviroMine.logger
					.warn("Config load canceled, file does not exist. This is normal for first run.");
			return false;
		} else {
			EnviroMine.logger.info("Config load successful.");
		}
		/*
		try {
			NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(file));

			HUDRegistry.readFromNBT(nbt.getCompoundTag("global"));

			for (HudItem item : HUDRegistry.getHudItemList()) {
				NBTTagCompound itemNBT = nbt.getCompoundTag(item.getName());
				item.loadFromNBT(itemNBT);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		return true;
	}

	public static void saveConfig(String name) 
	{
		saveConfig(name, null);
	}

	public static void saveConfig(String name, String dirName) 
	{
		EnviroMine.logger.info("Saving...");

		if (dirName != null) {
			//HUDRegistry.getMinecraftInstance();
			dir = new File(Minecraft.getMinecraft().mcDataDir + File.separator
					+ dirName);
		}

		if (!dir.exists() && !dir.mkdirs())
			throw new ReportedException(new CrashReport(
					"Unable to create the configuration directories",
					new Throwable()));

		String fileName = name + ".dat";
		File file = new File(dir, fileName);

		try {
			NBTTagCompound nbt = new NBTTagCompound();
			FileOutputStream fileOutputStream = new FileOutputStream(file);

			NBTTagCompound globalNBT = new NBTTagCompound();
			//HUDRegistry.writeToNBT(globalNBT);
			nbt.setTag("global", globalNBT);

			//for (HudItem item : HUDRegistry.getHudItemList()) {
			//	NBTTagCompound itemNBT = new NBTTagCompound();
		//		item.saveToNBT(itemNBT);
		//		nbt.setTag(item.getName(), itemNBT);
		//	}

			CompressedStreamTools.writeCompressed(nbt, fileOutputStream);
			fileOutputStream.close();
		} catch (IOException e) 
		{
			throw new ReportedException(new CrashReport(
					"An error occured while saving", new Throwable()));
		}
	}

public static File[] getConfigs() 
{
	return dir.listFiles(new FilenameFilter() 
	{		
		@Override
		public boolean accept(File dir, String filename) 
		{
			return filename.endsWith(".dat");
		}
	});
}

	static {
		//HUDRegistry.getMinecraftInstance();
	}

}
