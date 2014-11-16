package enviromine.core.proxies;

import enviromine.EntityPhysicsBlock;
import enviromine.blocks.tiles.TileEntityDavyLamp;
import enviromine.blocks.tiles.TileEntityElevator;
import enviromine.blocks.tiles.TileEntityEsky;
import enviromine.blocks.tiles.TileEntityFreezer;
import enviromine.blocks.tiles.ventilation.TileEntityFan;
import enviromine.blocks.tiles.ventilation.TileEntityVentSmall;
import enviromine.client.gui.EM_GuiEnviroMeters;
import enviromine.client.gui.Gui_EventManager;
import enviromine.client.gui.SaveController;
import enviromine.client.renderer.itemInventory.ArmoredCamelPackRenderer;
import enviromine.client.renderer.tileentity.*;
import enviromine.client.renderer.tileentity.ventilation.TileEntityFanRenderer;
import enviromine.client.renderer.tileentity.ventilation.TileEntityVentSmallRenderer;
import enviromine.handlers.ObjectHandler;
import enviromine.handlers.keybinds.EnviroKeybinds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class EM_ClientProxy extends EM_CommonProxy
{
	@Override
	public boolean isClient()
	{
		return true;
	}
	
	@Override
	public boolean isOpenToLAN() {
		return Minecraft.getMinecraft().isIntegratedServerRunning() && Minecraft.getMinecraft().getIntegratedServer().getPublic();
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
		SaveController.saveConfig("UI_Settings");
			
		initRenderers();
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
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFan.class, new TileEntityFanRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVentSmall.class, new TileEntityVentSmallRenderer());
	}
	
	@SideOnly(Side.CLIENT)
	public static void armoredCamelRenderers()
	{
		for (Object itemArmor : Item.itemRegistry)
		{
			if (itemArmor instanceof ItemArmor && ((ItemArmor) itemArmor).armorType == 1)
			{
				MinecraftForgeClient.registerItemRenderer((Item) itemArmor, new ArmoredCamelPackRenderer());
			}
		}
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
	}
}
