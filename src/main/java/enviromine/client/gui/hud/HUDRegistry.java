package enviromine.client.gui.hud;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import enviromine.utils.Alignment;

/**
 * 
 * Register your HUD elements with this.
 * <p>
 * You can register them at init, postinit, world load, really where ever you
 * feel the need to.
 * 
 * @author maxpowa
 * 
 */
public class HUDRegistry {
    protected static List<HudItem> hudItemList = new ArrayList<HudItem>();
    protected static boolean initialLoadComplete = false;
    protected static List<HudItem> hudItemListActive = new ArrayList<HudItem>();

    private static Logger log = LogManager.getLogger("AdvancedHUD-API");
    public static int screenWidth;
    public static int screenHeight;
    public static int updateCounter;

    public static void registerHudItem(HudItem hudItem) {
        if (hudItem.getDefaultID() <= 25 && initialLoadComplete)
        {
            log.info("Rejecting " + hudItem.getName() + " due to invalid ID.");
        }
        if (!hudItemList.contains(hudItem)) 
        {
            hudItemList.add(hudItem);
            //System.out.println(hudItem.getName() +":"+ hudItem.isEnabledByDefault());
            if (hudItem.isEnabledByDefault()) 
            {
                enableHudItem(hudItem);
            }
        }
    }

    public static List<HudItem> getHudItemList() {
        return hudItemList;
    }

    public static void enableHudItem(HudItem hudItem) 
    {
        if (hudItemList.contains(hudItem) && !hudItemListActive.contains(hudItem)) 
        {
        	//System.out.println(hudItem.getName() +" is Active now");
            hudItemListActive.add(hudItem);
        }
    }

    public static void disableHudItem(HudItem hudItem) 
    {
        hudItemListActive.remove(hudItem);
    }

    public static List<HudItem> getActiveHudItemList() 
    {
        return hudItemListActive;
    }

    public static boolean isActiveHudItem(HudItem hudItem) 
    {
        return getActiveHudItemList().contains(hudItem);
    }

    public static Minecraft getMinecraftInstance() 
    {
        return Minecraft.getMinecraft();
    }

    public static String getMinecraftVersion() 
    {
        return "@MCVERSION@";
    }

    public static HudItem getHudItemByID(int id) 
    {
        for (HudItem huditem : getHudItemList()) 
        {
            if (id == huditem.getDefaultID())
                return huditem;
        }
        return null;
    }

    public static void resetAllDefaults() 
    {
        for (HudItem huditem : HUDRegistry.getHudItemList()) 
        {
            huditem.rotated = false;
            huditem.alignment = huditem.getDefaultAlignment();
            huditem.posX = huditem.getDefaultPosX();
            huditem.posY = huditem.getDefaultPosY();
        }
    }

    public static boolean checkForResize() 
    {
        Minecraft mc = getMinecraftInstance();

        ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        if (scaledresolution.getScaledWidth() != screenWidth || scaledresolution.getScaledHeight() != screenHeight) 
        {
            if (screenWidth != 0) {
                fixHudItemOffsets(scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), screenWidth, screenHeight);
            }
            screenWidth = scaledresolution.getScaledWidth();
            screenHeight = scaledresolution.getScaledHeight();
            return true;
        }
        return false;
    }

    private static void fixHudItemOffsets(int newScreenWidth, int newScreenHeight, int oldScreenWidth, int oldScreenHeight) 
    {
        for (HudItem hudItem : hudItemList) {
            if (Alignment.isHorizontalCenter(hudItem.alignment)) {
                int offsetX = hudItem.posX - oldScreenWidth / 2;
                hudItem.posX = newScreenWidth / 2 + offsetX;
            } else if (Alignment.isRight(hudItem.alignment)) {
                int offsetX = hudItem.posX - oldScreenWidth;
                hudItem.posX = newScreenWidth + offsetX;
            }

            if (Alignment.isVerticalCenter(hudItem.alignment)) {
                int offsetY = hudItem.posY - oldScreenHeight / 2;
                hudItem.posY = newScreenHeight / 2 + offsetY;
            } else if (Alignment.isBottom(hudItem.alignment)) {
                int offsetY = hudItem.posY - oldScreenHeight;
                hudItem.posY = newScreenHeight + offsetY;
            }
        }
    }

    public static void readFromNBT(NBTTagCompound nbt) 
    {
        screenWidth = nbt.getInteger("screenWidth");
        screenHeight = nbt.getInteger("screenHeight");

        hudItemListActive = new ArrayList<HudItem>(hudItemList);
        
        for (int i = 0; i < hudItemList.size(); i++) 
        {
            HudItem hudItem = hudItemList.get(i);
            //System.out.println(hudItem.getName());
            
            
            if(!hudItem.isEnabledByDefault())
            {
                disableHudItem(hudItem);
            }
        }
    }

    public static void writeToNBT(NBTTagCompound nbt) 
    {
        nbt.setInteger("screenWidth", screenWidth);
        nbt.setInteger("screenHeight", screenHeight);

        for (Object hudItem_ : hudItemListActive) 
        {
            HudItem hudItem = (HudItem) hudItem_;
            nbt.setBoolean(hudItem.getName(), true);
        }

    }

    public static void setInitialLoadComplete(boolean b) 
    {
        initialLoadComplete = b;
    }
}