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

	public static String enviroSettingsFile = "UI_Settings";

	public static boolean ShowGuiIcons = true;
	public static float guiScale = 1.0F;
	public static boolean sweatParticals = true;
	public static boolean insaneParticals = true;
	public static boolean useFarenheit = false;
	public static String heatBarPos = "Bottom_Left";
	public static String waterBarPos = "Bottom_Left";
	public static String sanityBarPos = "Bottom_Right";
	public static String oxygenBarPos = "Bottom_Right";
	public static boolean ShowText = false;
	public static boolean ShowDebug = false;
	public static boolean breathSound = true;
	public static int breathPause = 300;
	public static float breathVolume = .75F;
	public static boolean minimalHud =  false;
	public static int screenWidth;
	public static int screenHeight;
	public static boolean overlay;
	
	public static void writeToNBT(NBTTagCompound nbt) 
	{
		
    	nbt.setBoolean("ShowGuiIcons", ShowGuiIcons); 
    	nbt.setFloat("guiScale", guiScale);
    	nbt.setBoolean("sweatParticals", sweatParticals);
    	nbt.setBoolean("insaneParticals", insaneParticals);
    	nbt.setBoolean("useFarenheit", useFarenheit);
    	nbt.setString("heatBarPos", heatBarPos); 
    	nbt.setString("waterBarPos", waterBarPos); 
    	nbt.setString("sanityBarPos", sanityBarPos);  
    	nbt.setString("oxygenBarPos", oxygenBarPos); 
    	nbt.setBoolean("ShowText", ShowText);
    	nbt.setBoolean("ShowDebug", ShowDebug);
    	nbt.setBoolean("breathSound", breathSound);
    	nbt.setInteger("breathPause", breathPause);
    	nbt.setFloat("breathVolume", breathVolume);
    	nbt.setBoolean("minimalHud",minimalHud); 

    	
		// TODO Auto-generated method stub
		
	}
	
	public static void readFromNBT(NBTTagCompound nbt)
	{
	
		ShowGuiIcons = nbt.getBoolean("ShowGuiIcons");
		guiScale = nbt.getFloat("guiScale");
		sweatParticals = nbt.getBoolean("sweatParticals");
		insaneParticals = nbt.getBoolean("insaneParticals");
		useFarenheit = nbt.getBoolean("useFarenheit");
		heatBarPos = nbt.getString("heatBarPos");
		waterBarPos = nbt.getString("waterBarPos");
		sanityBarPos = nbt.getString("sanityBarPos");
		oxygenBarPos = nbt.getString("oxygenBarPos"); 
		ShowText = nbt.getBoolean("ShowText");
		ShowDebug = nbt.getBoolean("ShowDebug");
		breathSound = nbt.getBoolean("breathSound");
		breathPause = nbt.getInteger("breathPause");
		breathVolume = nbt.getFloat("breathVolume");
		minimalHud = nbt.getBoolean("minimalHud"); 
	}
}
