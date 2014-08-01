package enviromine.handlers;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import enviromine.EntityPhysicsBlock;
import enviromine.blocks.BlockElevatorBottom;
import enviromine.blocks.BlockElevatorTop;
import enviromine.blocks.BlockGas;
import enviromine.blocks.renderers.TileEntityElevatorBottomRenderer;
import enviromine.blocks.renderers.TileEntityElevatorTopRenderer;
import enviromine.blocks.tiles.TileEntityElevatorBottom;
import enviromine.blocks.tiles.TileEntityElevatorTop;
import enviromine.blocks.tiles.TileEntityGas;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.gases.RenderGasHandler;
import enviromine.items.EnviroArmor;
import enviromine.items.EnviroItemBadWaterBottle;
import enviromine.items.EnviroItemColdWaterBottle;
import enviromine.items.EnviroItemSaltWaterBottle;
import enviromine.items.RottenFood;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.event.ForgeSubscribe;

public class ObjectHandler
{
	public static EnumArmorMaterial camelPackMaterial;
	
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
	
	public static Block elevatorTop;
	public static Block elevatorBottom;
	public static Block gasBlock;
	public static Block fireGasBlock;
	
	public static int renderGasID;
	
	public static void RegisterItems()
	{
		badWaterBottle = new EnviroItemBadWaterBottle(EM_Settings.dirtBottleID).setMaxStackSize(1).setUnlocalizedName("enviromine.item.badwater").setCreativeTab(EnviroMine.enviroTab);
		saltWaterBottle = new EnviroItemSaltWaterBottle(EM_Settings.saltBottleID).setMaxStackSize(1).setUnlocalizedName("enviromine.item.saltwater").setCreativeTab(EnviroMine.enviroTab);
		coldWaterBottle = new EnviroItemColdWaterBottle(EM_Settings.coldBottleID).setMaxStackSize(1).setUnlocalizedName("enviromine.item.coldwater").setCreativeTab(EnviroMine.enviroTab);
		airFilter = new Item(EM_Settings.airFilterID).setMaxStackSize(1).setUnlocalizedName("enviromine.item.airfilter").setCreativeTab(EnviroMine.enviroTab).setTextureName("enviromine:air_filter");
		rottenFood = new RottenFood(EM_Settings.rottenFoodID, 1).setMaxStackSize(64).setUnlocalizedName("enviromine.item.rottenfood").setCreativeTab(EnviroMine.enviroTab).setTextureName("enviromine:rot");
		
		camelPackMaterial = EnumHelper.addArmorMaterial("camelPack", 100, new int[]{1, 0, 0, 0}, 0);
		
		camelPack = (ItemArmor)new EnviroArmor(EM_Settings.camelPackID, camelPackMaterial, 4, 1).setTextureName("camel_pack").setUnlocalizedName("enviromine.item.camelpack").setCreativeTab(EnviroMine.enviroTab);
		gasMask = (ItemArmor)new EnviroArmor(EM_Settings.gasMaskID, camelPackMaterial, 4, 0).setTextureName("gas_mask").setUnlocalizedName("enviromine.item.gasmask").setCreativeTab(EnviroMine.enviroTab);
		hardHat = (ItemArmor)new EnviroArmor(EM_Settings.hardHatID, camelPackMaterial, 4, 0).setTextureName("hard_hat").setUnlocalizedName("enviromine.item.hardhat").setCreativeTab(EnviroMine.enviroTab);
		//GameRegistry.registerItem(airFilter, "enviromine.airFilter");
	}
	
	public static void RegisterBlocks()
	{
		//elevator = new BlockElevator(EM_Settings.blockElevatorID, Material.iron);
		gasBlock = new BlockGas(EM_Settings.gasBlockID, Material.air).setUnlocalizedName("enviromine.block.gas").setCreativeTab(EnviroMine.enviroTab);
		fireGasBlock = new BlockGas(EM_Settings.fireGasBlockID, Material.air).setUnlocalizedName("enviromine.block.firegas").setCreativeTab(EnviroMine.enviroTab).setLightValue(1.0F);
		
		GameRegistry.registerBlock(gasBlock, "enviromine.block.gas");
		GameRegistry.registerBlock(fireGasBlock, "enviromine.block.firegas");
		renderGasID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new RenderGasHandler());
		
		elevatorTop = new BlockElevatorTop(EM_Settings.blockElevatorTopID, Material.iron).setUnlocalizedName("enviromine.block.elevator_top").setCreativeTab(EnviroMine.enviroTab);
		GameRegistry.registerBlock(elevatorTop, "enviromine.block.elevator_top");
		elevatorBottom = new BlockElevatorBottom(EM_Settings.blockElevatorBottomID, Material.iron).setUnlocalizedName("enviromine.block.elevator_bottom").setCreativeTab(EnviroMine.enviroTab);
		GameRegistry.registerBlock(elevatorBottom, "enviromine.block.elevator_bottom");
	}
	
	public static void RegisterGases()
	{
	}
	
	public static void RegisterEntities()
	{
		EntityRegistry.registerGlobalEntityID(EntityPhysicsBlock.class, "EnviroPhysicsBlock", EM_Settings.physBlockID);
		GameRegistry.registerTileEntity(TileEntityGas.class, "enviromine.tile.gas");
		
		GameRegistry.registerTileEntity(TileEntityElevatorTop.class, "enviromine.tile.elevator_top");
		GameRegistry.registerTileEntity(TileEntityElevatorBottom.class, "enviromine.tile.elevator_bottom");
		
		if(EnviroMine.proxy.isClient())
		{
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevatorTop.class, new TileEntityElevatorTopRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevatorBottom.class, new TileEntityElevatorBottomRenderer());
		}
	}
	
	public static void RegisterRecipes()
	{
		GameRegistry.addSmelting(badWaterBottle.itemID, new ItemStack(ItemPotion.potion.itemID, 1, 0), 0.0F);
		GameRegistry.addSmelting(saltWaterBottle.itemID, new ItemStack(ItemPotion.potion.itemID, 1, 0), 0.0F);
		GameRegistry.addSmelting(coldWaterBottle.itemID, new ItemStack(ItemPotion.potion.itemID, 1, 0), 0.0F);
		GameRegistry.addShapelessRecipe(new ItemStack(coldWaterBottle, 1, 0), new ItemStack(Item.potion, 1, 0), new ItemStack(Item.snowball, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(badWaterBottle, 1, 0), new ItemStack(Item.potion, 1, 0), new ItemStack(Block.dirt, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(saltWaterBottle, 1, 0), new ItemStack(Item.potion, 1, 0), new ItemStack(Block.sand, 1));
		
		GameRegistry.addRecipe(new ItemStack(Item.slimeBall, 1, 0), " r ", "rwr", " r ", 'w', new ItemStack(Item.bucketWater, 1, 0), 'r', new ItemStack(rottenFood, 1));
		GameRegistry.addRecipe(new ItemStack(Block.mycelium), "xyx", "yzy", "xyx", 'z', new ItemStack(Block.grass), 'x', new ItemStack(Block.mushroomBrown), 'y', new ItemStack(rottenFood, 1));
		GameRegistry.addRecipe(new ItemStack(Block.mycelium), "xyx", "yzy", "xyx", 'z', new ItemStack(Block.grass), 'y', new ItemStack(Block.mushroomBrown), 'x', new ItemStack(rottenFood, 1));
		GameRegistry.addRecipe(new ItemStack(Block.dirt, 1), "xxx", "xxx", "xxx", 'x', new ItemStack(rottenFood, 1));
		
		GameRegistry.addRecipe(new ItemStack(camelPack, 1, camelPack.getMaxDamage()), "xxx", "xyx", "xxx", 'x', new ItemStack(Item.leather), 'y', new ItemStack(Item.glassBottle));
		GameRegistry.addRecipe(new ItemStack(airFilter, 1), "xyx", "xzx", "xyx", 'x', new ItemStack(Item.ingotIron), 'y', new ItemStack(Block.cloth), 'z', new ItemStack(Item.coal, 1, 1));
		GameRegistry.addRecipe(new ItemStack(gasMask, 1), "xxx", "xzx", "yxy", 'x', new ItemStack(Item.ingotIron), 'y', new ItemStack(airFilter), 'z', new ItemStack(Block.thinGlass));
		GameRegistry.addRecipe(new ItemStack(hardHat, 1), "xyx", "xzx", 'x', new ItemStack(Block.cloth, 1, 4), 'y', new ItemStack(Block.redstoneLampIdle), 'z', new ItemStack(Item.helmetIron, 1, 0));
		
		GameRegistry.addRecipe(new ItemStack(elevatorTop), "xyx", "z z", "z z", 'x', new ItemStack(Block.blockIron), 'y', new ItemStack(Block.redstoneLampIdle), 'z', new ItemStack(Block.fenceIron));
		GameRegistry.addRecipe(new ItemStack(elevatorBottom), "z z", "xyx", "www", 'x', new ItemStack(Block.blockIron), 'y', new ItemStack(Block.furnaceIdle), 'z', new ItemStack(Block.fenceIron), 'w', new ItemStack(Item.pickaxeDiamond));
	}
	
	public static void RegisterNames()
	{
		LanguageRegistry.addName(badWaterBottle, "Dirty Water Bottle");
		LanguageRegistry.addName(saltWaterBottle, "Salt Water Bottle");
		LanguageRegistry.addName(coldWaterBottle, "Cold Water Bottle");
		LanguageRegistry.addName(camelPack, "Camel Pack");
		LanguageRegistry.addName(gasMask, "Gas Mask");
		LanguageRegistry.addName(hardHat, "Hard Hat");
		LanguageRegistry.addName(airFilter, "Air Filter");
		LanguageRegistry.addName(elevatorTop, "Elevator Top");
		LanguageRegistry.addName(elevatorBottom, "Elevator Bottom");
		LanguageRegistry.addName(rottenFood, "Rotten Food");
	}
	
	@ForgeSubscribe
	public void RegisterSounds(SoundLoadEvent event)
	{
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
	}
}
