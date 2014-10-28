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
	
		if(nbt.hasKey("ShowGuiIcons")) ShowGuiIcons = nbt.getBoolean("ShowGuiIcons");
			else nbt.setBoolean("ShowGuiIcons", true); 
		
		if(nbt.hasKey("ShowGuiIcons")) guiScale = nbt.getFloat("guiScale");
			else nbt.setFloat("guiScale", 1.0F);
	    	
		if(nbt.hasKey("sweatParticals")) sweatParticals = nbt.getBoolean("sweatParticals");
			else nbt.setBoolean("sweatParticals", true);
    	
		if(nbt.hasKey("insaneParticals")) insaneParticals = nbt.getBoolean("insaneParticals");
			else  nbt.setBoolean("insaneParticals", true);
		
		if(nbt.hasKey("useFarenheit")) useFarenheit = nbt.getBoolean("useFarenheit");
			else nbt.setBoolean("useFarenheit", false);
	    	
		if(nbt.hasKey(" heatBarPos")) heatBarPos = nbt.getString("heatBarPos");
			else  nbt.setString("heatBarPos", "Bottom_Left"); 
	    	
		if(nbt.hasKey("waterBarPos")) waterBarPos = nbt.getString("waterBarPos");
			else 	nbt.setString("waterBarPos",  "Bottom_Left"); 
		     
		if(nbt.hasKey("sanityBarPos")) sanityBarPos = nbt.getString("sanityBarPos");
			else  nbt.setString("sanityBarPos", "Bottom_Right");  
	    	
		if(nbt.hasKey("oxygenBarPos")) oxygenBarPos = nbt.getString("oxygenBarPos"); 
			else  nbt.setString("oxygenBarPos", "Bottom_Right"); 
	    	
		if(nbt.hasKey("ShowText")) ShowText = nbt.getBoolean("ShowText");
			else nbt.setBoolean("ShowText", false);
	    	
		if(nbt.hasKey("ShowDebug")) ShowDebug = nbt.getBoolean("ShowDebug");
			else nbt.setBoolean("ShowDebug", false);
    	
		if(nbt.hasKey("breathSound"))breathSound = nbt.getBoolean("breathSound");
			else nbt.setBoolean("breathSound", true);
		
		if(nbt.hasKey("breathPause")) breathPause = nbt.getInteger("breathPause");
			else nbt.setInteger("breathPause", 300);
				
		if(nbt.hasKey("breathVolume")) breathVolume = nbt.getFloat("breathVolume");
			else nbt.setFloat("breathVolume", .75F);
				
		if(nbt.hasKey("minimalHud")) minimalHud = nbt.getBoolean("minimalHud"); 
			else nbt.setBoolean("minimalHud", false);
	}
}
