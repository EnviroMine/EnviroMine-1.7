package enviromine.core.proxies;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraftforge.common.MinecraftForge;
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
import enviromine.blocks.tiles.TileEntityElevatorBottom;
import enviromine.blocks.tiles.TileEntityElevatorTop;
import enviromine.client.gui.EM_GuiEnviroMeters;
import enviromine.client.gui.Gui_EventManager;
import enviromine.client.gui.menu.UI_Settings;
import enviromine.client.renderer.tileentity.RenderGasHandler;
import enviromine.client.renderer.tileentity.TileEntityDavyLampRenderer;
import enviromine.client.renderer.tileentity.TileEntityElevatorBottomRenderer;
import enviromine.client.renderer.tileentity.TileEntityElevatorTopRenderer;
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
		RenderingRegistry.registerEntityRenderingHandler(EntityPhysicsBlock.class, new RenderFallingBlock());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevatorTop.class, new TileEntityElevatorTopRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevatorBottom.class, new TileEntityElevatorBottomRenderer());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDavyLamp.class, new TileEntityDavyLampRenderer());
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
	}
}
