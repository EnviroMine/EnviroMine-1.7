package enviromine.handlers;

import java.util.List;
import java.util.Random;

import net.minecraft.world.gen.structure.ComponentVillageStartPiece;
import net.minecraft.world.gen.structure.StructureVillagePieceWeight;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import enviromine.EM_VillageMineshaft;

public class EnviroShaftCreationHandler implements IVillageCreationHandler
{
	
	@Override
	public StructureVillagePieceWeight getVillagePieceWeight(Random random, int i)
	{
		return new StructureVillagePieceWeight(EM_VillageMineshaft.class, 1, 1);
	}
	
	@Override
	public Class<?> getComponentClass()
	{
		return EM_VillageMineshaft.class;
	}
	
	@Override
	public Object buildComponent(StructureVillagePieceWeight villagePiece, ComponentVillageStartPiece startPiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5)
	{
		return EM_VillageMineshaft.buildComponent(startPiece, pieces, random, p1, p2, p3, p4, p5);
	}
}
