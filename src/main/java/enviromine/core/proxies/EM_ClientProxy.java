package enviromine.core.proxies;

import java.util.Iterator;

import org.apache.logging.log4j.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.blocks.tiles.TileEntityElevatorBottom;
import enviromine.blocks.tiles.TileEntityElevatorTop;
import enviromine.client.gui.EM_GuiEnviroMeters;
import enviromine.client.gui.Gui_EventManager;
import enviromine.client.gui.menu.UI_Settings;
import enviromine.client.renderer.itemInventory.ArmoredCamelPackRenderer;
import enviromine.client.renderer.tileentity.TileEntityElevatorBottomRenderer;
import enviromine.client.renderer.tileentity.TileEntityElevatorTopRenderer;
import enviromine.core.EnviroMine;
import enviromine.gases.RenderGasHandler;
import enviromine.handlers.ObjectHandler;
import enviromine.handlers.keybinds.EnviroKeybinds;

public class EM_ClientProxy extends EM_CommonProxy
{
	@Override
	public boolean isClient()
	{
		return true;
	}
	
	@Override
	public boolean isOpenToLAN()
	{
		if(Minecraft.getMinecraft().isIntegratedServerRunning())
		{
			return Minecraft.getMinecraft().getIntegratedServer().getPublic();
		} else
		{
			return false;
		}
	}
	
	@Override
	public void registerTickHandlers()
	{
		super.registerTickHandlers();
	}
	
	@Override
	public void registerEventHandlers()
	{
		super.registerEventHandlers();
		MinecraftForge.EVENT_BUS.register(new EM_GuiEnviroMeters(Minecraft.getMinecraft(), Minecraft.getMinecraft().getResourceManager()));
		MinecraftForge.EVENT_BUS.register(new ObjectHandler());
		MinecraftForge.EVENT_BUS.register(new Gui_EventManager());
		FMLCommonHandler.instance().bus().register(new EnviroKeybinds());
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
	}
	
	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		EnviroKeybinds.Init();
		UI_Settings.loadSettings();
		initRenderers();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initRenderers()
	{
		ObjectHandler.renderGasID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new RenderGasHandler());
		
		armoredCamelRenderers();
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevatorTop.class, new TileEntityElevatorTopRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevatorBottom.class, new TileEntityElevatorBottomRenderer());
	}
	
	@SideOnly(Side.CLIENT)
	public static void armoredCamelRenderers()
	{
		int cnt = 0;
		 Iterator tmp = Item.itemRegistry.iterator();
		 
		 while(tmp.hasNext())
		 {
			 Object itemArmor = tmp.next();
			 if (itemArmor instanceof ItemArmor && ((ItemArmor)itemArmor).armorType == 1 && (ItemArmor)itemArmor != ObjectHandler.camelPack) 
			 {
				 cnt++;
				 MinecraftForgeClient.registerItemRenderer((Item) itemArmor, new ArmoredCamelPackRenderer());				 
			 }	 
		 }
		 
		 EnviroMine.logger.log(Level.INFO, cnt + " Armored CamelPack Renders Loaded");
		
	}
	
	
	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
	}
}
