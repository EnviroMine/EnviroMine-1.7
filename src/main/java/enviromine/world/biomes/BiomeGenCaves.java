package enviromine.world.biomes;

import java.util.Iterator;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import enviromine.core.EM_Settings;
import enviromine.trackers.properties.CaveSpawnProperties;

public class BiomeGenCaves extends BiomeGenBase
{
	public BiomeGenCaves(int par1)
	{
		super(par1);
		this.RefreshSpawnList();
        //this.spawnableCreatureList.add(new SpawnListEntry(EntityBat.class, 100, 4, 8));
        //this.spawnableCaveCreatureList.add(new SpawnListEntry(EntityBat.class, 100, 4, 8));
        //this.spawnableMonsterList.add(new SpawnListEntry(EntitySilverfish.class, 95, 1, 1));
        //this.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 5, 1, 1));
	}
	
	@SuppressWarnings("unchecked")
	public void RefreshSpawnList()
	{
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        
		Iterator<CaveSpawnProperties> iterator = EM_Settings.caveSpawnProperties.iterator();
		
		while(iterator.hasNext())
		{
			CaveSpawnProperties props = iterator.next();
			
			Class<?> clazz = EntityList.getClassFromID(props.id);
			
			if(clazz == null || !(EntityLiving.class.isAssignableFrom(clazz)))
			{
				continue;
			}
			
			if(EnumCreatureType.monster.getCreatureClass().isAssignableFrom(clazz))
			{
				this.spawnableMonsterList.add(new SpawnListEntry(clazz, props.weight, props.minGroup, props.maxGroup));
			} else if(EnumCreatureType.waterCreature.getCreatureClass().isAssignableFrom(clazz))
			{
				this.spawnableWaterCreatureList.add(new SpawnListEntry(clazz, props.weight, props.minGroup, props.maxGroup));
			} else
			{
				this.spawnableCaveCreatureList.add(new SpawnListEntry(clazz, props.weight, props.minGroup, props.maxGroup));
				this.spawnableCreatureList.add(new SpawnListEntry(clazz, props.weight, props.minGroup, props.maxGroup));
			}
		}
	}
}
