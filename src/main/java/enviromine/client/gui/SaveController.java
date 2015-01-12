package enviromine.client.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ReportedException;
import enviromine.client.gui.hud.HUDRegistry;
import enviromine.client.gui.hud.HudItem;
import enviromine.core.EnviroMine;

public class SaveController {
	
	/**
	 * Configuration version number. If changed the version file will be reset to defaults to prevent glitches
	 */
	static final String CONFIG_VERSION = "1.0.0";
	
	/**
	 * The version of the configs last loaded from file. This will be compared to the version number above when determining whether a reset is necessary
	 */
	static String LOADED_VERSION = "1.0.0";
	
    protected static final String dirName = Minecraft.getMinecraft().mcDataDir + File.separator + "config" + File.separator + "enviromine";
    
    protected static File dir = new File(dirName);
    
    public static String UISettingsData = "UI_Settings"; 
    
    
    public static boolean loadConfig(String name) {
        return loadConfig(name, null);
    }

    public static boolean loadConfig(String name, String dirName) {
        if (dirName != null) {

        	dir = new File(Minecraft.getMinecraft().mcDataDir + File.separator + dirName);
        }

        String fileName = name + ".dat";
        File file = new File(dir, fileName);

        if (!file.exists()) 
        {
            EnviroMine.logger.warn("Config load canceled, file ("+ file.getAbsolutePath()  +")does not exist. This is normal for first run.");
            return false;
        } else {
            EnviroMine.logger.info("Config load successful.");
        }
        try {
            NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(file));
            
            if (nbt.hasNoTags() || !nbt.hasKey(UISettingsData))
            {
            	return false;
            }

            UI_Settings.readFromNBT(nbt.getCompoundTag(UISettingsData));
            HUDRegistry.readFromNBT(nbt.getCompoundTag(UISettingsData));
            LOADED_VERSION = nbt.getCompoundTag(UISettingsData).getString("CONFIG_VERSION");
            UpdateNotification.readFromNBT(nbt.getCompoundTag("Notifications"));
            // New HUD Settings will be here
            
            for (HudItem item : HUDRegistry.getHudItemList()) {
                NBTTagCompound itemNBT = nbt.getCompoundTag(item.getName());
                item.loadFromNBT(itemNBT);
            }       
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return LOADED_VERSION.equals(CONFIG_VERSION);
    }

    public static void saveConfig(String name) {
        saveConfig(name, null);
    }

    public static void saveConfig(String name, String dirName) {

        if (dirName != null) {
            dir = new File(Minecraft.getMinecraft().mcDataDir + File.separator + dirName);
        }

        if (!dir.exists() && !dir.mkdirs())
            throw new ReportedException(new CrashReport("Unable to create the configuration directories", new Throwable()));

        String fileName = name + ".dat";
        File file = new File(dir, fileName);

        try {
            NBTTagCompound nbt = new NBTTagCompound();
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            NBTTagCompound globalNBT = new NBTTagCompound();
            	HUDRegistry.writeToNBT(globalNBT);
            	UI_Settings.writeToNBT(globalNBT);
            	globalNBT.setString("CONFIG_VERSION", CONFIG_VERSION); // VERY IMPORTANT
            	nbt.setTag(UISettingsData, globalNBT);
            	
            	NBTTagCompound notificationNBT = new NBTTagCompound();
            	UpdateNotification.writeToNBT(notificationNBT);
            	nbt.setTag("Notifications", notificationNBT);
               
            	for (HudItem item : HUDRegistry.getHudItemList()) {
                    NBTTagCompound itemNBT = new NBTTagCompound();
                    item.saveToNBT(itemNBT);
                    nbt.setTag(item.getName(), itemNBT);
                }
           // New HUD Settings will be here
            
            
            CompressedStreamTools.writeCompressed(nbt, fileOutputStream);
            fileOutputStream.close();
            EnviroMine.logger.info("Saved GUI properties");
        } catch (IOException e) {
            throw new ReportedException(new CrashReport("An error occured while saving", new Throwable()));
        }
    }

    public static File[] getConfigs() {
        return dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".dat");
            }
        });
    }

}