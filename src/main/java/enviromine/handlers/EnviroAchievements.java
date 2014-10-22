package enviromine.handlers;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
	//public static Achievement medicalMarvels; 	// Cure any infection/disease
	//public static Achievement suckItUpPrincess; // Attack & kill any hostile mob with one or more broken limbs
	
	public static void InitAchievements()
	{
		funwaysFault =     new Achievement("enviromine.FunwaysFault",     "enviromine.FunwaysFault",     -2, 0, Blocks.cobblestone,     AchievementList.buildPickaxe).registerStat();
		mindOverMatter =   new Achievement("enviromine.MindOverMatter",   "enviromine.MindOverMatter",   -1, 0, Items.ender_eye,          AchievementList.buildSword).registerStat();
		proMiner =         new Achievement("enviromine.ProMiner",         "enviromine.ProMiner",          0, 0, Items.diamond_pickaxe,  AchievementList.buildPickaxe).registerStat();
		hardBoiled =       new Achievement("enviromine.HardBoiled",       "enviromine.HardBoiled",        1, 0, Items.egg,                                      null).registerStat();
		ironArmy =         new Achievement("enviromine.IronArmy",         "enviromine.IronArmy",          2, 0, Items.iron_ingot, AchievementList.buildBetterPickaxe).registerStat();
		
		tradingFavours =   new Achievement("enviromine.TradingFavours",   "enviromine.TradingFavours",   -2, 1, Items.emerald,                                  null).registerStat();
		iNeededThat =      new Achievement("enviromine.INeededThat",      "enviromine.INeededThat",      -1, 1, Items.shears,                                   null);
		winterIsComing =   new Achievement("enviromine.WinterIsComing",   "enviromine.WinterIsComing",    0, 1, Blocks.snow,                                    null).registerStat();
		ohGodWhy =         new Achievement("enviromine.OhGodWhy",         "enviromine.OhGodWhy",          1, 1, Items.record_11,            AchievementList.diamonds).registerStat();
		safetyFirst =      new Achievement("enviromine.SafetyFirst",      "enviromine.SafetyFirst",       2, 1, ObjectHandler.hardHat,        AchievementList.portal).registerStat();
		
		boreToTheCore =    new Achievement("enviromine.BoreToTheCore",    "enviromine.BoreToTheCore",    -2, 2, ObjectHandler.elevator,    AchievementList.portal).registerStat();
		intoTheDarkness =  new Achievement("enviromine.IntoTheDarkness",  "enviromine.IntoTheDarkness",  -1, 2, Blocks.torch,                          boreToTheCore).registerStat();
		thatJustHappened = new Achievement("enviromine.ThatJustHappened", "enviromine.ThatJustHappened",  0, 2, Blocks.fire,                                    null).registerStat();
		itsPitchBlack =    new Achievement("enviromine.ItsPitchBlack",    "enviromine.ItsPitchBlack",     1, 2, Blocks.redstone_torch,                 boreToTheCore).registerStat();
		tenSecondRule =    new Achievement("enviromine.TenSecondRule",    "enviromine.TenSecondRule",     2, 2, Items.rotten_flesh,                             null).registerStat();
		
		//medicalMarvels =   new Achievement("enviromine.MedicalMarvels",   "enviromine.MedicalMarvels",   -2, 3, Items.potionitem,                               null).registerStat();
		//suckItUpPrincess = new Achievement("enviromine.SuckItUpPrincess", "enviromine.SuckItUpPrincess", -1, 3, Items.bone,               AchievementList.buildSword).registerStat();
		
		page = new AchievementPage("EnviroMine", funwaysFault, mindOverMatter, proMiner, hardBoiled, ironArmy, tradingFavours, iNeededThat, winterIsComing, ohGodWhy, safetyFirst, boreToTheCore, intoTheDarkness, thatJustHappened, itsPitchBlack, tenSecondRule/*, medicalMarvels, suckItUpPrincess*/);
		AchievementPage.registerAchievementPage(page);
	}
}
