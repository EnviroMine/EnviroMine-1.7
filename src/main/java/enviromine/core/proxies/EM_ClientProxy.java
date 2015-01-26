package enviromine.core.proxies;

import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.EntityPhysicsBlock;
import enviromine.blocks.tiles.TileEntityDavyLamp;
import enviromine.blocks.tiles.TileEntityElevator;
import enviromine.blocks.tiles.TileEntityEsky;
import enviromine.blocks.tiles.TileEntityFreezer;
import enviromine.client.gui.Gui_EventManager;
import enviromine.client.gui.hud.HUDRegistry;
import enviromine.client.gui.hud.items.HudItemAirQuality;
import enviromine.client.gui.hud.items.HudItemHydration;
import enviromine.client.gui.hud.items.HudItemSanity;
import enviromine.client.gui.hud.items.HudItemTemperature;
import enviromine.client.renderer.itemInventory.ArmoredCamelPackRenderer;
import enviromine.client.renderer.tileentity.RenderGasHandler;
import enviromine.client.renderer.tileentity.RenderSpecialHandler;
import enviromine.client.renderer.tileentity.TileEntityDavyLampRenderer;
import enviromine.client.renderer.tileentity.TileEntityElevatorRenderer;
import enviromine.client.renderer.tileentity.TileEntityEskyRenderer;
import enviromine.client.renderer.tileentity.TileEntityFreezerRenderer;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
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
		if (Minecraft.getMinecraft().isIntegratedServerRunning())
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
		//MinecraftForge.EVENT_BUS.register(new EM_GuiEnviroMeters(Minecraft.getMinecraft(), Minecraft.getMinecraft().getResourceManager()));
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
        
		initRenderers();
		registerHudItems();	

	}
	
	@SideOnly(Side.CLIENT)
	public static void initRenderers()
	{
		ObjectHandler.renderGasID = RenderingRegistry.getNextAvailableRenderId();
		ObjectHandler.renderSpecialID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(ObjectHandler.renderGasID, new RenderGasHandler());
		RenderingRegistry.registerBlockHandler(ObjectHandler.renderSpecialID, new RenderSpecialHandler());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityPhysicsBlock.class, new RenderFallingBlock());
		
		armoredCamelRenderers();
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevator.class, new TileEntityElevatorRenderer());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDavyLamp.class, new TileEntityDavyLampRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEsky.class, new TileEntityEskyRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFreezer.class, new TileEntityFreezerRenderer());
	}
	
	@SideOnly(Side.CLIENT)
	public static void armoredCamelRenderers()
	{
		@SuppressWarnings("unchecked")
		Iterator<Item> tmp = Item.itemRegistry.iterator();
		
		while (tmp.hasNext())
		{
			Item itemArmor = tmp.next();
			if (itemArmor instanceof ItemArmor && ((ItemArmor)itemArmor).armorType == 1)
			{
				String name = Item.itemRegistry.getNameForObject(itemArmor);
				
				if(EM_Settings.armorProperties.containsKey(name) && EM_Settings.armorProperties.get(name).allowCamelPack)
				{
					IItemRenderer iRenderer = MinecraftForgeClient.getItemRenderer(new ItemStack((ItemArmor)itemArmor), ItemRenderType.INVENTORY);
					
					if(iRenderer != null)
					{
						EnviroMine.logger.log(Level.WARN, "Item " + name + " aready has a custom ItemRenderer associated with it!");
						EnviroMine.logger.log(Level.WARN, "EnviroMine will be unable to overlay the camel pack UI when attached!");
					} else
					{
						MinecraftForgeClient.registerItemRenderer((Item)itemArmor, new ArmoredCamelPackRenderer());
					}
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerHudItems()
	{
        HUDRegistry.registerHudItem(new HudItemTemperature());
        HUDRegistry.registerHudItem(new HudItemHydration());
        HUDRegistry.registerHudItem(new HudItemSanity());
        HUDRegistry.registerHudItem(new HudItemAirQuality());
        HUDRegistry.setInitialLoadComplete(true);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
	}
}
