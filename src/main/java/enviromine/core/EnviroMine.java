package enviromine.core;

import enviromine.EM_VillageMineshaft;
import enviromine.EnviroPotion;
import enviromine.core.proxies.EM_CommonProxy;
import enviromine.handlers.EnviroShaftCreationHandler;
import enviromine.items.EnviroArmor;
import enviromine.items.EnviroItemBadWaterBottle;
import enviromine.items.EnviroItemColdWaterBottle;
import enviromine.items.EnviroItemSaltWaterBottle;
import enviromine.network.packet.PacketEnviroMine;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.util.EnumHelper;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteOrder;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

@Mod(modid = EM_Settings.ID, name = EM_Settings.Name, version = EM_Settings.Version)
public class EnviroMine
{
	public static Logger logger;
	public static Item badWaterBottle;
	public static Item saltWaterBottle;
	public static Item coldWaterBottle;
	
	public static ArmorMaterial camelPackMaterial;
	public static ItemArmor camelPack;
	
	public SimpleNetworkWrapper network;
	
	@Instance(EM_Settings.ID)
	public static EnviroMine instance;
	
	@SidedProxy(clientSide = EM_Settings.Proxy + ".EM_ClientProxy", serverSide = EM_Settings.Proxy + ".EM_CommonProxy")
	public static EM_CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		
		proxy.preInit(event);
		
		// Load Configuration files And Custom files
		EM_ConfigHandler.initConfig();
		
		// Create Items
		badWaterBottle = new EnviroItemBadWaterBottle().setMaxStackSize(1).setUnlocalizedName("dirtyWaterBottle").setCreativeTab(CreativeTabs.tabBrewing);
		saltWaterBottle = new EnviroItemSaltWaterBottle().setMaxStackSize(1).setUnlocalizedName("saltWaterBottle").setCreativeTab(CreativeTabs.tabBrewing);
		coldWaterBottle = new EnviroItemColdWaterBottle().setMaxStackSize(1).setUnlocalizedName("coldWaterBottle").setCreativeTab(CreativeTabs.tabBrewing);
		
		camelPackMaterial = EnumHelper.addArmorMaterial("camelPack", 100, new int[]{0, 0, 0, 0}, 0);
		
		camelPack = (ItemArmor)new EnviroArmor(camelPackMaterial, 4, 1).setTextureName("camel_pack").setUnlocalizedName("camelPack").setCreativeTab(CreativeTabs.tabTools);
		
		GameRegistry.registerItem(badWaterBottle, "dirty_water_bottle");
		GameRegistry.registerItem(saltWaterBottle, "salt_water_bottle");
		GameRegistry.registerItem(coldWaterBottle, "cold_water_bottle");
		GameRegistry.registerItem(camelPack, "camel_pack");
		
		if(EM_Settings.shaftGen == true)
		{
			VillagerRegistry.instance().registerVillageCreationHandler(new EnviroShaftCreationHandler());
			MapGenStructureIO.func_143031_a(EM_VillageMineshaft.class, "ViMS");
		}
		
		this.network = NetworkRegistry.INSTANCE.newSimpleChannel(EM_Settings.Channel);
		this.network.registerMessage(PacketEnviroMine.HandlerServer.class, PacketEnviroMine.class, 0, Side.SERVER);
		this.network.registerMessage(PacketEnviroMine.HandlerClient.class, PacketEnviroMine.class, 1, Side.CLIENT);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init(event);
		
		//EntityRegistry.registerGlobalEntityID(EntityPhysicsBlock.class, "EnviroPhysicsBlock", EM_Settings.physBlockID);
		//EntityRegistry.registerModEntity(EntityPhysicsBlock.class, "EnviroPhysicsBlock", EM_Settings.physBlockID, instance, 64, 1, true);
		
		LanguageRegistry.addName(badWaterBottle, "Dirty Water Bottle");
		LanguageRegistry.addName(saltWaterBottle, "Salt Water Bottle");
		LanguageRegistry.addName(coldWaterBottle, "Cold Water Bottle");
		LanguageRegistry.addName(camelPack, "Camel Pack");
		
		extendPotionList();
		
		EnviroPotion.frostbite = ((EnviroPotion)(new EnviroPotion(EM_Settings.frostBitePotionID, true, 8171462).setPotionName("potion.frostbite"))).setIconIndex(0, 0);
		EnviroPotion.dehydration = ((EnviroPotion)(new EnviroPotion(EM_Settings.dehydratePotionID, true, 3035801).setPotionName("potion.dehydration"))).setIconIndex(1, 0);
		EnviroPotion.insanity = ((EnviroPotion)(new EnviroPotion(EM_Settings.insanityPotionID, true, 5578058).setPotionName("potion.insanity"))).setIconIndex(2, 0);
		EnviroPotion.heatstroke = ((EnviroPotion)(new EnviroPotion(EM_Settings.heatstrokePotionID, true, getColorFromRGBA(255, 0, 0, 255)).setPotionName("potion.heatstroke"))).setIconIndex(3, 0);
		EnviroPotion.hypothermia = ((EnviroPotion)(new EnviroPotion(EM_Settings.hypothermiaPotionID, true, 8171462).setPotionName("potion.hypothermia"))).setIconIndex(4, 0);
		
		LanguageRegistry.instance().addStringLocalization("potion.hypothermia", "Hypothermia");
		LanguageRegistry.instance().addStringLocalization("potion.heatstroke", "Heat Stroke");
		LanguageRegistry.instance().addStringLocalization("potion.frostbite", "Frostbite");
		LanguageRegistry.instance().addStringLocalization("potion.dehydration", "Dehydration");
		LanguageRegistry.instance().addStringLocalization("potion.insanity", "Insanity");
		
		GameRegistry.addSmelting(badWaterBottle, new ItemStack(Items.potionitem, 1, 0), 0.0F);
		GameRegistry.addSmelting(saltWaterBottle, new ItemStack(Items.potionitem, 1, 0), 0.0F);
		GameRegistry.addSmelting(coldWaterBottle, new ItemStack(Items.potionitem, 1, 0), 0.0F);
		GameRegistry.addShapelessRecipe(new ItemStack(coldWaterBottle, 1, 0), new ItemStack(Items.potionitem, 1, 0), new ItemStack(Items.snowball, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(badWaterBottle, 1, 0), new ItemStack(Items.potionitem, 1, 0), new ItemStack(Blocks.dirt, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(saltWaterBottle, 1, 0), new ItemStack(Items.potionitem, 1, 0), new ItemStack(Blocks.sand, 1));
		
		GameRegistry.addRecipe(new ItemStack(camelPack, 1, camelPack.getMaxDamage()), "xxx", "xyx", "xxx", 'x', new ItemStack(Items.leather), 'y', new ItemStack(Items.glass_bottle));
		
		proxy.registerTickHandlers();
		proxy.registerEventHandlers();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
		
		if(EM_Settings.genArmorConfigs)
		{
			EM_ConfigHandler.SearchForModdedArmors();
		}
		
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.armorProperties.size() + " armor properties");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.blockProperties.size() + " block properties");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.livingProperties.size() + " entity properties");
		EnviroMine.logger.log(Level.INFO, "Loaded " + EM_Settings.itemProperties.size() + " item properties");
	}
	
	public static int getColorFromRGBA_F(float par1, float par2, float par3, float par4)
	{
		int R = (int)(par1 * 255.0F);
		int G = (int)(par2 * 255.0F);
		int B = (int)(par3 * 255.0F);
		int A = (int)(par4 * 255.0F);
		
		return getColorFromRGBA(R, G, B, A);
	}
	
	public static int getColorFromRGBA(int R, int G, int B, int A)
	{
		if(R > 255)
		{
			R = 255;
		}
		
		if(G > 255)
		{
			G = 255;
		}
		
		if(B > 255)
		{
			B = 255;
		}
		
		if(A > 255)
		{
			A = 255;
		}
		
		if(R < 0)
		{
			R = 0;
		}
		
		if(G < 0)
		{
			G = 0;
		}
		
		if(B < 0)
		{
			B = 0;
		}
		
		if(A < 0)
		{
			A = 0;
		}
		
		if(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
		{
			return A << 24 | R << 16 | G << 8 | B;
		} else
		{
			return B << 24 | G << 16 | R << 8 | A;
		}
	}
	
	public static void extendPotionList()
	{
		int maxID = 32;
		
		if(EM_Settings.heatstrokePotionID >= maxID)
		{
			maxID = EM_Settings.heatstrokePotionID + 1;
		}
		
		if(EM_Settings.hypothermiaPotionID >= maxID)
		{
			maxID = EM_Settings.hypothermiaPotionID + 1;
		}
		
		if(EM_Settings.frostBitePotionID >= maxID)
		{
			maxID = EM_Settings.frostBitePotionID + 1;
		}
		
		if(EM_Settings.dehydratePotionID >= maxID)
		{
			maxID = EM_Settings.dehydratePotionID + 1;
		}
		
		if(EM_Settings.insanityPotionID >= maxID)
		{
			maxID = EM_Settings.insanityPotionID + 1;
		}
		
		if(Potion.potionTypes.length >= maxID)
		{
			return;
		}
		
		
		Potion[] potionTypes = null;

		for (Field f : Potion.class.getDeclaredFields())
		{
			f.setAccessible(true);
			
			try
			{
				if (f.getName().equals("potionTypes") || f.getName().equals("field_76425_a"))
				{
					Field modfield = Field.class.getDeclaredField("modifiers");
					modfield.setAccessible(true);
					modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);

					potionTypes = (Potion[])f.get(null);
					final Potion[] newPotionTypes = new Potion[maxID];
					System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
					f.set(null, newPotionTypes);
				}
			}
			catch (Exception e)
			{
				logger.log(Level.ERROR, "Failed to extend potion list for EnviroMine");
				e.printStackTrace();
			}
		}
	}
}
