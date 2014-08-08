package enviromine.handlers;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.common.AchievementPage;

public class EnviroAchievements
{
	public static AchievementPage page;
	
	public static Achievement funwaysFault; 	// Survive a cave in
	public static Achievement mindOverMatter; 	// Kill 5 mobs with Insanity III without getting hit
	public static Achievement proMiner; 		// 1 Hour total mining time (with physics & all effects)
	public static Achievement hardBoiled; 		// Survive Heat Stroke III
	public static Achievement ironArmy; 		// Name an iron golem Siyliss
	public static Achievement tradingFavours; 	// Receive villager assistance
	public static Achievement iNeededThat; 		// Fumble a tool under Frostbite I
	public static Achievement winterIsComing; 	// Survive 7 days in the snow without getting Hypothermia
	public static Achievement ohGodWhy; 		// Play disk 11
	public static Achievement safetyFirst; 		// Craft a hardhat
	public static Achievement boreToTheCore; 	// Enter the cave dimension
	public static Achievement intoTheDarkness; 	// Travel 1K from cave entrance and make it back alive
	public static Achievement thatJustHappened; // Survive a gas fire
	public static Achievement itsPitchBlack; 	// ???
	public static Achievement tenSecondRule; 	// Eat rotten food
	public static Achievement medicalMarvels; 	// Cure any infection/disease
	public static Achievement suckItUpPrincess; // Attack & kill any hostile mob with one or more broken limbs
	
	public static void InitAchievements()
	{
		funwaysFault = new Achievement("enviromine.FunwaysFault",		"enviromine.FunwaysFault",		-2, 0, Blocks.cobblestone, AchievementList.buildPickaxe).registerStat();
		mindOverMatter = new Achievement("enviromine.MindOverMatter", 	"enviromine.MindOverMatter",	-1, 0, Items.ender_eye, AchievementList.buildSword).registerStat();
		proMiner = new Achievement("enviromine.ProMiner",			"enviromine.ProMiner",			0, 0, Items.diamond_pickaxe, AchievementList.buildPickaxe).registerStat();
		hardBoiled = new Achievement("enviromine.HardBoiled",			"enviromine.HardBoiled",		1, 0, Items.egg, null).registerStat();
		ironArmy = new Achievement("enviromine.IronArmy",			"enviromine.IronArmy",			2, 0, Items.iron_ingot, AchievementList.buildBetterPickaxe).registerStat();
		
		tradingFavours = new Achievement("enviromine.TradingFavours",		"enviromine.TradingFavours",	-2, 1, Items.emerald, null).registerStat();
		iNeededThat = new Achievement("enviromine.INeededThat",		"enviromine.INeededThat",		-1, 1, Items.shears, null);
		winterIsComing = new Achievement("enviromine.WinterIsComing",		"enviromine.WinterIsComing",	0, 1, Blocks.snow, null).registerStat();
		ohGodWhy = new Achievement("enviromine.WinterIsComing",			"enviromine.OhGodWhy",			1, 1, Items.record_11, AchievementList.diamonds).registerStat();
		safetyFirst = new Achievement("enviromine.SafetyFirst",		"enviromine.SafetyFirst",		2, 1, ObjectHandler.hardHat, AchievementList.portal).registerStat();
		
		boreToTheCore = new Achievement("enviromine.BoreToTheCore",		"enviromine.BoreToTheCore",		-2, 2, ObjectHandler.elevatorTop, AchievementList.portal).registerStat();
		intoTheDarkness = new Achievement("enviromine.IntoTheDarkness",	"enviromine.IntoTheDarkness",	-1, 2, Blocks.torch, boreToTheCore).registerStat();
		thatJustHappened = new Achievement("enviromine.ThatJustHappened",	"enviromine.ThatJustHappened",	0, 2, Blocks.fire, null).registerStat();
		itsPitchBlack = new Achievement("enviromine.ItsPitchBlack", 		"enviromine.ItsPitchBlack",		1, 2, Blocks.redstone_torch, boreToTheCore).registerStat();
		tenSecondRule = new Achievement("enviromine.TenSecondRule", 		"enviromine.TenSecondRule",		2, 2, Items.rotten_flesh, null).registerStat();
		
		medicalMarvels = new Achievement("enviromine.MedicalMarvels", 	"enviromine.MedicalMarvels",	-2, 3, Items.potionitem, null).registerStat();
		suckItUpPrincess = new Achievement("enviromine.SuckItUpPrincess", 	"enviromine.SuckItUpPrincess",	-1, 3, Items.bone, AchievementList.buildSword).registerStat();
		
		page = new AchievementPage("EnviroMine", funwaysFault, mindOverMatter, proMiner, hardBoiled, ironArmy, tradingFavours, iNeededThat, winterIsComing, ohGodWhy, safetyFirst, boreToTheCore, intoTheDarkness, thatJustHappened, itsPitchBlack, tenSecondRule, medicalMarvels, suckItUpPrincess);
		AchievementPage.registerAchievementPage(page);
		
		SetupNames();
	}
	
	public static void SetupNames()
	{
		addLocalisation(funwaysFault, "Funway's Fault", "Survive a cave in");
		addLocalisation(mindOverMatter, "Mind Over Matter", "Kill 5 hostile mobs with Insanity III");
		addLocalisation(proMiner, "Pro Miner", "Get 1hr total mining time without dying (with physics & all effects)");
		addLocalisation(hardBoiled, "Hard Boiled", "Survive Heat Stroke III");
		addLocalisation(ironArmy, "Iron Army", "Name an Iron Golem Siyliss");
		addLocalisation(tradingFavours, "Trading Favours", "Receive assistance from a villager");
		addLocalisation(iNeededThat, "I Needed That", "Fumble a tool while under Frostbite I");
		addLocalisation(winterIsComing, "Winter Is Coming", "Survive 7 days in snow without Hypothermia");
		addLocalisation(ohGodWhy, "Oh God Why", "Play 11");
		addLocalisation(safetyFirst, "Safety First", "Craft a hardhat");
		addLocalisation(boreToTheCore, "Bore To The Core", "Enter the cave dimension");
		addLocalisation(intoTheDarkness, "Into The Darkness", "Travel 1K in the cave dimension and return alive");
		addLocalisation(thatJustHappened, "That Just Happened", "Survive a gas fire");
		addLocalisation(itsPitchBlack, "It's Pitch Black", "???");
		addLocalisation(tenSecondRule, "10 Second Rule", "Eat rotten food");
		addLocalisation(medicalMarvels, "Medical Marvels", "Cure any infection or disease");
		addLocalisation(suckItUpPrincess, "Suck It Up Princess", "Kill a hostile mob while having one or more broken limbs");
	}
	
	public static void addLocalisation(Achievement ach, String title, String desc)
	{
		LanguageRegistry.instance().addStringLocalization("achievement." + ach, "en_US", title);
		LanguageRegistry.instance().addStringLocalization("achievement." + ach + ".desc", "en_US", desc);
	}
}
