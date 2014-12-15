package enviromine.utils;


import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.HashMap;
import net.minecraft.item.ItemStack;

public class ModIdentification
{
  public static HashMap<String, String> modSource_Name = new HashMap();
  public static HashMap<String, String> modSource_ID = new HashMap();
  public static HashMap<Integer, String> itemMap = new HashMap();
  public static HashMap<String, String> keyhandlerStrings = new HashMap();
  
  public static void init()
  {
    for (ModContainer mod : Loader.instance().getModList())
    {
      addSource(mod.getSource().getName(), mod.getName());
    }
    addSource("1.6.2.jar", "Minecraft");
    addSource("1.6.3.jar", "Minecraft");
    addSource("1.6.4.jar", "Minecraft");
    addSource("1.7.2.jar", "Minecraft");
    addSource("Forge", "Minecraft");
  }
  
  public void addSource(String name, String ID)
  {
    modSource_Name.put(name, ID);
    modSource_ID.put(name, ID);
  }
  
  public static String nameFromObject(Object obj)
  {
    String objPath = obj.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
    
    try
    {
      objPath = URLDecoder.decode(objPath, "UTF-8");
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    String modName = "Unknown";
    for (String s : modSource_Name.keySet()) {
      if (objPath.contains(s))
      {
        modName = (String)modSource_Name.get(s);
        break;
      }
    }
    if (modName.equals("Minecraft Coder Pack")) {
      modName = "Minecraft";
    }
    return modName;
  }
  
  public static String nameFromStack(ItemStack stack)
  {
    try
    {
      ModContainer mod = GameData.findModOwner(GameData.getItemRegistry().getNameForObject(stack.getItem()));
      return mod == null ? "Minecraft" : mod.getName();
    }
    catch (NullPointerException e) {}
    return "";
  }
  
}
