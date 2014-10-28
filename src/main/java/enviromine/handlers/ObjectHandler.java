package enviromine.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import enviromine.EntityPhysicsBlock;
import enviromine.blocks.*;
import enviromine.blocks.materials.MaterialGas;
import enviromine.blocks.tiles.*;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.items.*;

public class ObjectHandler
{
	public static HashMap<Block, ArrayList<Integer>> igniteList = new HashMap<Block, ArrayList<Integer>>();
	public static ArmorMaterial camelPackMaterial;
	
	public static Item badWaterBottle;
	public static Item saltWaterBottle;
	public static Item coldWaterBottle;
	
	public static Item airFilter;
	public static Item davyLamp;
	public static Item gasMeter;
	public static Item rottenFood;
	
	public static ItemArmor camelPack;
	public static ItemArmor gasMask;
	public static ItemArmor hardHat;
	
	public static Block davyLampBlock;
	public static Block elevator;
	public static Block gasBlock;
	public static Block fireGasBlock;
	
	public static Block flammableCoal;
	public static Block burningCoal;
	public static Block fireTorch;
	
	public static Block esky;
	public static Block freezer;
	
	public static int renderGasID;
	public static int renderSpecialID;
	
	public static Material gasMat;
	
	public static void initItems()
	{
		badWaterBottle = new EnviroItemBadWaterBottle().setMaxStackSize(1).setUnlocalizedName("enviromine.badwater").setCreativeTab(EnviroMine.enviroTab);
		saltWaterBottle = new EnviroItemSaltWaterBottle().setMaxStackSize(1).setUnlocalizedName("enviromine.saltwater").setCreativeTab(EnviroMine.enviroTab);
		coldWaterBottle = new EnviroItemColdWaterBottle().setMaxStackSize(1).setUnlocalizedName("enviromine.coldwater").setCreativeTab(EnviroMine.enviroTab);
		airFilter = new Item().setMaxStackSize(1).setUnlocalizedName("enviromine.airfilter").setCreativeTab(EnviroMine.enviroTab).setTextureName("enviromine:air_filter");
		rottenFood = new RottenFood(1).setPotionEffect(Potion.hunger.id, 30, 0, 0.2F).setMaxStackSize(64).setUnlocalizedName("enviromine.rottenfood").setCreativeTab(EnviroMine.enviroTab).setTextureName("enviromine:rot");
		
		camelPackMaterial = EnumHelper.addArmorMaterial("camelPack", 100, new int[]{1, 0, 0, 0}, 0);
		
		camelPack = (ItemArmor)new EnviroArmor(camelPackMaterial, 4, 1).setTextureName("camel_pack").setUnlocalizedName("enviromine.camelpack").setCreativeTab(EnviroMine.enviroTab);
		gasMask = (ItemArmor)new EnviroArmor(camelPackMaterial, 4, 0).setTextureName("gas_mask").setUnlocalizedName("enviromine.gasmask").setCreativeTab(EnviroMine.enviroTab);
		hardHat = (ItemArmor)new EnviroArmor(camelPackMaterial, 4, 0).setTextureName("hard_hat").setUnlocalizedName("enviromine.hardhat").setCreativeTab(EnviroMine.enviroTab);
	}
	
	public static void registerItems()
	{
		GameRegistry.registerItem(badWaterBottle, "badWaterBottle");
		GameRegistry.registerItem(saltWaterBottle, "saltWaterBottle");
		GameRegistry.registerItem(coldWaterBottle, "coldWaterBottle");
		GameRegistry.registerItem(airFilter, "airFilter");
		GameRegistry.registerItem(rottenFood, "rottenFood");
		GameRegistry.registerItem(camelPack, "camelPack");
		GameRegistry.registerItem(gasMask, "gasMask");
		GameRegistry.registerItem(hardHat, "hardHat");
	}
	
	public static void initBlocks()
	{
		gasMat = new MaterialGas(MapColor.airColor);
		gasBlock = new BlockGas(gasMat).setBlockName("enviromine.gas").setCreativeTab(EnviroMine.enviroTab).setBlockTextureName("enviromine:gas_block");
		fireGasBlock = new BlockGas(gasMat).setBlockName("enviromine.firegas").setCreativeTab(EnviroMine.enviroTab).setBlockTextureName("enviromine:gas_block").setLightLevel(1.0F);
		
		elevator = new BlockElevator(Material.iron).setBlockName("enviromine.elevator").setCreativeTab(EnviroMine.enviroTab).setBlockTextureName("iron_block");
		
		davyLampBlock = new BlockDavyLamp(Material.iron).setBlockName("enviromine.davy_lamp").setCreativeTab(EnviroMine.enviroTab);
		davyLamp = new ItemDavyLamp(davyLampBlock).setUnlocalizedName("enviromine.davylamp").setCreativeTab(EnviroMine.enviroTab);
		
		flammableCoal = new BlockFlammableCoal();
		burningCoal = new BlockBurningCoal(Material.rock).setBlockName("enviromine.burningcoal").setCreativeTab(EnviroMine.enviroTab);
		fireTorch = new BlockFireTorch().setTickRandomly(true).setBlockName("torch").setBlockTextureName("torch_on").setLightLevel(0.9375F).setCreativeTab(EnviroMine.enviroTab);
		esky = new BlockEsky(Material.iron).setBlockName("enviromine.esky").setCreativeTab(EnviroMine.enviroTab);
		freezer = new BlockFreezer(Material.iron).setBlockName("enviromine.freezer").setCreativeTab(EnviroMine.enviroTab);
	}
	
	public static void registerBlocks()
	{
		GameRegistry.registerBlock(gasBlock, "gas");
		GameRegistry.registerBlock(fireGasBlock, "firegas");
		GameRegistry.registerBlock(elevator, ItemElevator.class, "elevator");
		GameRegistry.registerBlock(davyLampBlock, ItemDavyLamp.class, "davy_lamp");
		GameRegistry.registerBlock(fireTorch, "firetorch");
		GameRegistry.registerBlock(burningCoal, "burningcoal");
		GameRegistry.registerBlock(flammableCoal, "flammablecoal");
		GameRegistry.registerBlock(esky, "esky");
		GameRegistry.registerBlock(freezer, "freezer");
		
		// Must be done after registration
		Blocks.fire.setFireInfo(flammableCoal, 60, 100);
	}
	
	public static void registerGases()
	{
	}
	
	public static void registerEntities()
	{
		EntityRegistry.registerGlobalEntityID(EntityPhysicsBlock.class, "EnviroPhysicsBlock", EM_Settings.physBlockID);
		EntityRegistry.registerModEntity(EntityPhysicsBlock.class, "EnviroPhysicsEntity", EM_Settings.physBlockID, EnviroMine.instance, 64, 1, true);
		GameRegistry.registerTileEntity(TileEntityGas.class, "enviromine.tile.gas");
		GameRegistry.registerTileEntity(TileEntityBurningCoal.class, "enviromine.tile.burningcoal");
		GameRegistry.registerTileEntity(TileEntityEsky.class, "enviromine.tile.esky");
		
		GameRegistry.registerTileEntity(TileEntityElevator.class, "enviromine.tile.elevator");
		

		GameRegistry.registerTileEntity(TileEntityDavyLamp.class, "enviromine.tile.davy_lamp");
	}
	
	public static void registerRecipes()
	{
		GameRegistry.addSmelting(badWaterBottle, new ItemStack(Items.potionitem, 1, 0), 0.0F);
		GameRegistry.addSmelting(saltWaterBottle, new ItemStack(Items.potionitem, 1, 0), 0.0F);
		GameRegistry.addSmelting(coldWaterBottle, new ItemStack(Items.potionitem, 1, 0), 0.0F);
		GameRegistry.addShapelessRecipe(new ItemStack(coldWaterBottle, 1, 0), new ItemStack(Items.potionitem, 1, 0), new ItemStack(Items.snowball, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(badWaterBottle, 1, 0), new ItemStack(Items.potionitem, 1, 0), new ItemStack(Blocks.dirt, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(saltWaterBottle, 1, 0), new ItemStack(Items.potionitem, 1, 0), new ItemStack(Blocks.sand, 1));
		
		GameRegistry.addRecipe(new ItemStack(Items.slime_ball, 1, 0), " r ", "rwr", " r ", 'w', new ItemStack(Items.water_bucket, 1, 0), 'r', new ItemStack(rottenFood, 1));
		GameRegistry.addRecipe(new ItemStack(Blocks.mycelium), "xyx", "yzy", "xyx", 'z', new ItemStack(Blocks.grass), 'x', new ItemStack(Blocks.brown_mushroom_block), 'y', new ItemStack(rottenFood, 1));
		GameRegistry.addRecipe(new ItemStack(Blocks.mycelium), "xyx", "yzy", "xyx", 'z', new ItemStack(Blocks.grass), 'y', new ItemStack(Blocks.brown_mushroom_block), 'x', new ItemStack(rottenFood, 1));
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 1), "xxx", "xxx", "xxx", 'x', new ItemStack(rottenFood, 1));
		
		GameRegistry.addRecipe(new ItemStack(camelPack, 1, camelPack.getMaxDamage()), "xxx", "xyx", "xxx", 'x', new ItemStack(Items.leather), 'y', new ItemStack(Items.glass_bottle));
		GameRegistry.addRecipe(new ItemStack(gasMask, 1), "xxx", "xzx", "yxy", 'x', new ItemStack(Items.iron_ingot), 'y', new ItemStack(airFilter), 'z', new ItemStack(Blocks.glass_pane));
		GameRegistry.addRecipe(new ItemStack(hardHat, 1), "xyx", "xzx", 'x', new ItemStack(Items.dye, 1, 11), 'y', new ItemStack(Blocks.redstone_lamp), 'z', new ItemStack(Items.iron_helmet, 1, 0));

		GameRegistry.addRecipe(new ItemStack(airFilter, 1), "xyx", "xzx", "xyx", 'x', new ItemStack(Items.iron_ingot), 'y', new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE), 'z', new ItemStack(Items.coal, 1, 1));
		GameRegistry.addRecipe(new ItemStack(airFilter, 1), "xyx", "xzx", "xyx", 'x', new ItemStack(Items.iron_ingot), 'y', new ItemStack(Items.paper), 'z', new ItemStack(Items.coal, 1, 1));
		GameRegistry.addRecipe(new ItemStack(airFilter, 1), "xyx", "xzx", "xpx", 'x', new ItemStack(Items.iron_ingot), 'y', new ItemStack(Items.paper), 'p', new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE),'z', new ItemStack(Items.coal, 1, 1));
		GameRegistry.addRecipe(new ItemStack(airFilter, 1), "xpx", "xzx", "xyx", 'x', new ItemStack(Items.iron_ingot), 'y', new ItemStack(Items.paper), 'p', new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE),'z', new ItemStack(Items.coal, 1, 1));
		
		GameRegistry.addRecipe(new ItemStack(elevator, 1, 0), "xyx", "z z", "z z", 'x', new ItemStack(Blocks.iron_block), 'y', new ItemStack(Blocks.redstone_lamp), 'z', new ItemStack(Blocks.iron_bars));
		GameRegistry.addRecipe(new ItemStack(elevator, 1, 1), "z z", "xyx", "www", 'x', new ItemStack(Blocks.iron_block), 'y', new ItemStack(Blocks.furnace), 'z', new ItemStack(Blocks.iron_bars), 'w', new ItemStack(Items.diamond_pickaxe));
		
		GameRegistry.addRecipe(new ItemStack(davyLampBlock), " x ", "zyz", "xxx", 'x', new ItemStack(Items.gold_ingot), 'y', new ItemStack(Blocks.torch), 'z', new ItemStack(Blocks.glass_pane));
		GameRegistry.addRecipe(new ItemStack(esky), "xxx", "yzy", "yyy", 'x', new ItemStack(Blocks.snow), 'y', new ItemStack(Items.dye, 1, 4), 'z', new ItemStack(Blocks.chest));
		GameRegistry.addRecipe(new ItemStack(freezer), "xyx", "yzy", "xyx", 'x', new ItemStack(Blocks.iron_block), 'y', new ItemStack(Blocks.ice), 'z', new ItemStack(esky));
		GameRegistry.addRecipe(new ItemStack(freezer), "xyx", "yzy", "xyx", 'x', new ItemStack(Blocks.iron_block), 'y', new ItemStack(Blocks.packed_ice), 'z', new ItemStack(esky));
	}
	
	public static void LoadIgnitionSources()
	{
		igniteList.put(Blocks.flowing_lava, new ArrayList<Integer>());
		igniteList.put(Blocks.lava, new ArrayList<Integer>());
		igniteList.put(Blocks.torch, new ArrayList<Integer>());
		igniteList.put(Blocks.lit_furnace, new ArrayList<Integer>());
		igniteList.put(Blocks.fire, new ArrayList<Integer>());
		igniteList.put(ObjectHandler.fireGasBlock, new ArrayList<Integer>());
		igniteList.put(ObjectHandler.fireTorch, new ArrayList<Integer>());
		igniteList.put(ObjectHandler.burningCoal, new ArrayList<Integer>());
	}
}
