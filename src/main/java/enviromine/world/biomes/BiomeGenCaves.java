package enviromine.world.biomes;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGenCaves extends BiomeGenBase
{
	@SuppressWarnings("unchecked")
	public BiomeGenCaves(int par1)
	{
		super(par1);
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        this.spawnableCreatureList.add(new SpawnListEntry(EntityBat.class, 100, 4, 8));
        this.spawnableCaveCreatureList.add(new SpawnListEntry(EntityBat.class, 100, 4, 8));
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySilverfish.class, 95, 1, 1));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 5, 1, 1));
	}
}
