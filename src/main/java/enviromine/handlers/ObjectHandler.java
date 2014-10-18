package enviromine.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.logging.log4j.Level;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import enviromine.EntityPhysicsBlock;
import enviromine.blocks.BlockBurningCoal;
import enviromine.blocks.BlockDavyLamp;
import enviromine.blocks.BlockElevatorBottom;
import enviromine.blocks.BlockElevatorTop;
import enviromine.blocks.BlockFireTorch;
import enviromine.blocks.BlockFlammableCoal;
import enviromine.blocks.BlockGas;
import enviromine.blocks.materials.MaterialGas;
import enviromine.blocks.tiles.TileEntityBurningCoal;
import enviromine.blocks.tiles.TileEntityDavyLamp;
import enviromine.blocks.tiles.TileEntityElevatorBottom;
import enviromine.blocks.tiles.TileEntityElevatorTop;
import enviromine.blocks.tiles.TileEntityGas;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.items.DavyLamp;
import enviromine.items.EnviroArmor;
import enviromine.items.EnviroItemBadWaterBottle;
import enviromine.items.EnviroItemColdWaterBottle;
import enviromine.items.EnviroItemSaltWaterBottle;
import enviromine.items.RottenFood;

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
	public static Block elevatorTop;
	public static Block elevatorBottom;
	public static Block gasBlock;
	public static Block fireGasBlock;
	
	public static Block burningCoal;
	public static Block fireTorch;
	
	public static int renderGasID;
	
	public static Material gasMat;
	
	public static void initItems()
	{
		badWaterBottle = new EnviroItemBadWaterBottle().setMaxStackSize(1).setUnlocalizedName("enviromine.badwater").setCreativeTab(EnviroMine.enviroTab);
		saltWaterBottle = new EnviroItemSaltWaterBottle().setMaxStackSize(1).setUnlocalizedName("enviromine.saltwater").setCreativeTab(EnviroMine.enviroTab);
		coldWaterBottle = new EnviroItemColdWaterBottle().setMaxStackSize(1).setUnlocalizedName("enviromine.coldwater").setCreativeTab(EnviroMine.enviroTab);
		airFilter = new Item().setMaxStackSize(1).setUnlocalizedName("enviromine.airfilter").setCreativeTab(EnviroMine.enviroTab).setTextureName("enviromine:air_filter");
		rottenFood = new RottenFood(1).setMaxStackSize(64).setUnlocalizedName("enviromine.rottenfood").setCreativeTab(EnviroMine.enviroTab).setTextureName("enviromine:rot");
		
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
		
		elevatorTop = new BlockElevatorTop(Material.iron).setBlockName("enviromine.elevator_top").setCreativeTab(EnviroMine.enviroTab).setBlockTextureName("enviromine:elevator_top_icon");
		elevatorBottom = new BlockElevatorBottom(Material.iron).setBlockName("enviromine.elevator_bottom").setCreativeTab(EnviroMine.enviroTab).setBlockTextureName("enviromine:elevator_bottom_icon");
		
		davyLampBlock = new BlockDavyLamp(Material.iron).setBlockName("enviromine.davy_lamp").setCreativeTab(EnviroMine.enviroTab).setBlockTextureName("enviromine:davy_lamp");
		davyLamp = new DavyLamp(davyLampBlock).setUnlocalizedName("enviromine.davylamp").setCreativeTab(EnviroMine.enviroTab);

		burningCoal = new BlockBurningCoal(Material.rock).setBlockName("enviromine.burningcoal").setCreativeTab(EnviroMine.enviroTab);
		fireTorch = new BlockFireTorch().setTickRandomly(true).setBlockName("torch").setBlockTextureName("torch_on").setLightLevel(0.9375F).setCreativeTab(EnviroMine.enviroTab);
		
		replaceBlocks();
	}
	
	public static void registerBlocks()
	{
		GameRegistry.registerBlock(gasBlock, "gas");
		GameRegistry.registerBlock(fireGasBlock, "firegas");
		GameRegistry.registerBlock(elevatorTop, "elevator_top");
		GameRegistry.registerBlock(elevatorBottom, "elevator_bottom");
		GameRegistry.registerBlock(davyLampBlock, DavyLamp.class, "davy_lamp");
		GameRegistry.registerBlock(fireTorch, "firetorch");
		GameRegistry.registerBlock(burningCoal, "burningcoal");
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
		
		GameRegistry.registerTileEntity(TileEntityElevatorTop.class, "enviromine.tile.elevator_top");
		GameRegistry.registerTileEntity(TileEntityElevatorBottom.class, "enviromine.tile.elevator_bottom");
		

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
		
		GameRegistry.addRecipe(new ItemStack(elevatorTop), "xyx", "z z", "z z", 'x', new ItemStack(Blocks.iron_block), 'y', new ItemStack(Blocks.redstone_lamp), 'z', new ItemStack(Blocks.iron_bars));
		GameRegistry.addRecipe(new ItemStack(elevatorBottom), "z z", "xyx", "www", 'x', new ItemStack(Blocks.iron_block), 'y', new ItemStack(Blocks.furnace), 'z', new ItemStack(Blocks.iron_bars), 'w', new ItemStack(Items.diamond_pickaxe));
		
		GameRegistry.addRecipe(new ItemStack(davyLampBlock), " x ", "zyz", "xxx", 'x', new ItemStack(Items.gold_ingot), 'y', new ItemStack(Blocks.torch), 'z', new ItemStack(Blocks.glass_pane));
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
	
	//TODO Should Probably be be Removed Sounds.Json now controls this
	//@ForgeSubscribe
	public void registerSounds(SoundLoadEvent event)
	{
		/*
		// You add them the same way as you add blocks.
		System.out.println("Loading Sounds");
		
		event.manager.addSound("enviromine:gasmask.ogg");
		
		event.manager.addSound("enviromine:thingdistant.ogg");
		event.manager.addSound("enviromine:thingkill.ogg");
		
		event.manager.addSound("enviromine:CaveIn.ogg");
		
		event.manager.addSound("enviromine:sizzle.ogg");
		event.manager.addSound("enviromine:chill.ogg");
		
		//Random Heavy(Panic) Breathing
		event.manager.addSound("enviromine:gag1.ogg");
		event.manager.addSound("enviromine:gag2.ogg");
		event.manager.addSound("enviromine:gag3.ogg");
		*/
	}
	
	@SuppressWarnings("unchecked")
	public static void replaceBlocks()
	{
		Field field = null; // Coal Block to replace
		Field field3 = null; // FML GameData instance
		Field field4 = null; // Block registry in GameData
		Field field5 = null; // Item registry in GameData (Need this for ItemBlock linking)
		Field modifiers = null;
		
		Block block = new BlockFlammableCoal();

		try
		{
			field = Blocks.class.getDeclaredField("coal_ore");
			field3 = GameData.class.getDeclaredField("mainData");
			field4 = GameData.class.getDeclaredField("iBlockRegistry");
			field5 = GameData.class.getDeclaredField("iItemRegistry");
			modifiers = Field.class.getDeclaredField("modifiers");
		} catch(NoSuchFieldException e)
		{
			try
			{
				field = Blocks.class.getDeclaredField("field_150365_q");
				field3 = GameData.class.getDeclaredField("mainData");
				field4 = GameData.class.getDeclaredField("iBlockRegistry");
				field5 = GameData.class.getDeclaredField("iBlockRegistry");
				modifiers = Field.class.getDeclaredField("modifiers");
			} catch(NoSuchFieldException e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return;
			} catch(SecurityException e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return;
			}
		} catch(SecurityException e)
		{
			try
			{
				field = Blocks.class.getDeclaredField("field_150365_q");
				field3 = GameData.class.getDeclaredField("mainData");
				field4 = GameData.class.getDeclaredField("iBlockRegistry");
				field5 = GameData.class.getDeclaredField("iBlockRegistry");
				modifiers = Field.class.getDeclaredField("modifiers");
			} catch(NoSuchFieldException e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return;
			} catch(SecurityException e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return;
			}
		}
		
		modifiers.setAccessible(true);
		
		try
		{
			modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			modifiers.setInt(field3, field3.getModifiers() & ~Modifier.FINAL);
			modifiers.setInt(field4, field4.getModifiers() & ~Modifier.FINAL);
			modifiers.setInt(field5, field5.getModifiers() & ~Modifier.FINAL);
		} catch(IllegalArgumentException e1)
		{
			e1.printStackTrace();
			return;
		} catch(IllegalAccessException e1)
		{
			e1.printStackTrace();
			return;
		}
		
		field.setAccessible(true);
		field3.setAccessible(true);
		field4.setAccessible(true);
		field5.setAccessible(true);
		
		try
		{
			field.set(null, block);
			
			try
			{
				Method addRawObj = FMLControlledNamespacedRegistry.class.getDeclaredMethod("addObjectRaw", int.class, String.class, Object.class);
				addRawObj.setAccessible(true);
				addRawObj.invoke(((FMLControlledNamespacedRegistry<Item>)field5.get(field3.get(null))), 16, "minecraft:coal_ore", new ItemBlock(block));
				addRawObj.invoke(((FMLControlledNamespacedRegistry<Block>)field4.get(field3.get(null))), 16, "minecraft:coal_ore", block);
			} catch(NoSuchMethodException e)
			{
				e.printStackTrace();
				return;
			} catch(SecurityException e)
			{
				e.printStackTrace();
				return;
			} catch(InvocationTargetException e)
			{
				e.printStackTrace();
				return;
			}
		} catch(IllegalArgumentException e2)
		{
			e2.printStackTrace();
			return;
		} catch(IllegalAccessException e2)
		{
			e2.printStackTrace();
			return;
		}
		
		if(Blocks.coal_ore instanceof BlockFlammableCoal && Block.blockRegistry.getObject("coal_ore") instanceof BlockFlammableCoal)
		{
			EnviroMine.logger.log(Level.INFO, "Successfully replaced Coal Ore block");
		} else
		{
			EnviroMine.logger.log(Level.ERROR, "Failed to override vanilla Coal Ore block");
		}

		Blocks.fire.setFireInfo(Blocks.coal_ore, 60, 100);
	}
}
