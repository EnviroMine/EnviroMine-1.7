package enviromine.handlers;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
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
		funwaysFault = new Achievement(12301,		"enviromine.FunwaysFault",		-2, 0, Block.cobblestone, AchievementList.buildPickaxe).registerAchievement();
		mindOverMatter = new Achievement(12302, 	"enviromine.MindOverMatter",	-1, 0, Item.eyeOfEnder, AchievementList.buildSword).registerAchievement();
		proMiner = new Achievement(12303,			"enviromine.ProMiner",			0, 0, Item.pickaxeDiamond, AchievementList.buildPickaxe).registerAchievement();
		hardBoiled = new Achievement(12304,			"enviromine.HardBoiled",		1, 0, Item.egg, null).registerAchievement();
		ironArmy = new Achievement(12305,			"enviromine.IronArmy",			2, 0, Item.ingotIron, AchievementList.buildBetterPickaxe).registerAchievement();
		
		tradingFavours = new Achievement(12306,		"enviromine.TradingFavours",	-2, 1, Item.emerald, null).registerAchievement();
		iNeededThat = new Achievement(12307,		"enviromine.INeededThat",		-1, 1, Item.shears, null);
		winterIsComing = new Achievement(12308,		"enviromine.WinterIsComing",	0, 1, Block.snow, null).registerAchievement();
		ohGodWhy = new Achievement(12309,			"enviromine.OhGodWhy",			1, 1, Item.record11, AchievementList.diamonds).registerAchievement();
		safetyFirst = new Achievement(12310,		"enviromine.SafetyFirst",		2, 1, ObjectHandler.hardHat, AchievementList.portal).registerAchievement();
		
		boreToTheCore = new Achievement(12311,		"enviromine.BoreToTheCore",		-2, 2, ObjectHandler.elevatorTop, AchievementList.portal).registerAchievement();
		intoTheDarkness = new Achievement(12312,	"enviromine.IntoTheDarkness",	-1, 2, Block.torchWood, boreToTheCore).registerAchievement();
		thatJustHappened = new Achievement(12313,	"enviromine.ThatJustHappened",	0, 2, Block.fire, null).registerAchievement();
		itsPitchBlack = new Achievement(12314, 		"enviromine.ItsPitchBlack",		1, 2, Block.torchRedstoneIdle, boreToTheCore).registerAchievement();
		tenSecondRule = new Achievement(12315, 		"enviromine.TenSecondRule",		2, 2, Item.rottenFlesh, null).registerAchievement();
		
		medicalMarvels = new Achievement(12316, 	"enviromine.MedicalMarvels",	-2, 3, Item.potion, null).registerAchievement();
		suckItUpPrincess = new Achievement(12317, 	"enviromine.SuckItUpPrincess",	-1, 3, Item.bone, AchievementList.buildSword).registerAchievement();
		
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
		LanguageRegistry.instance().addStringLocalization(ach.statName, title);
		LanguageRegistry.instance().addStringLocalization(ach.statName + ".desc", desc);
	}
}
