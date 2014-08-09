package enviromine.trackers;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;

public class Hallucination
{
	public EntityLiving falseEntity;
	public String falseSound;
	public int x;
	public int y;
	public int z;
	public int time;
	public static int maxTime = 60;
	public static ArrayList<Hallucination> list = new ArrayList<Hallucination>();
	
	public Hallucination(EntityLivingBase entityLiving)
	{
		if(!(entityLiving instanceof EntityPlayer))
		{
			return;
		}
		
		Random rand = entityLiving.getRNG();
		
		x = (int)(entityLiving.posX + rand.nextInt(20) - 10);
		y = (int)(entityLiving.posY + rand.nextInt(2) - 1);
		z = (int)(entityLiving.posZ + rand.nextInt(20) - 10);

		BiomeGenBase biome = entityLiving.worldObj.getBiomeGenForCoords(MathHelper.floor_double(entityLiving.posX), MathHelper.floor_double(entityLiving.posZ));
		
		@SuppressWarnings("unchecked")
		ArrayList<SpawnListEntry> spawnList = (ArrayList<SpawnListEntry>)biome.getSpawnableList(EnumCreatureType.monster);
		
		if(spawnList.size() <= 0)
		{
			return;
		}
		
		int spawnIndex = entityLiving.getRNG().nextInt(spawnList.size());
		
        try
        {
            falseEntity = (EntityLiving)spawnList.get(spawnIndex).entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {entityLiving.worldObj});
        } catch (Exception exception)
        {
            exception.printStackTrace();
            return;
        }
		
		if(falseEntity == null)
		{
			System.out.println("Halucination instance returned null!");
			return;
		}
		
		falseEntity.setPositionAndRotation(x, y, z, rand.nextFloat() * 360F, 0.0F);
		
		if(!isAtValidSpawn(falseEntity))
		{
			return;
		} else if(!entityLiving.worldObj.spawnEntityInWorld(falseEntity))
		{
			return;
		}
		list.add(this);
		
		if(falseEntity.worldObj.isRemote && falseEntity instanceof EntityLiving)
		{
			//Minecraft.getMinecraft().sndManager.playSound(falseSound, x, y, z, 1.0F, 1.0F);
			falseEntity.getEntityData().setBoolean("EM_Hallucination", true);
			((EntityLiving)falseEntity).playLivingSound();
		}
	}
	
	public static void update()
	{
		if(list.size() >= 1)
		{
			for(int i = list.size() - 1; i >= 0; i -= 1)
			{
				Hallucination subject = list.get(i);
				if(subject.time >= maxTime)
				{
					subject.time += 1;
					subject.falseEntity.setDead();
				} else
				{
					subject.time += 1;
				}
			}
		}
	}
	
	public static boolean isAtValidSpawn(EntityLivingBase creature)
	{
		return creature.worldObj.checkNoEntityCollision(creature.boundingBox) && creature.worldObj.getCollidingBoundingBoxes(creature, creature.boundingBox).isEmpty() && !creature.worldObj.isAnyLiquid(creature.boundingBox) && isValidLightLevel(creature);
	}
	
	/**
	 * Checks to make sure the light is not too bright where the mob is spawning
	 */
	protected static boolean isValidLightLevel(EntityLivingBase creature)
	{
		if(creature instanceof EntityBlaze || creature instanceof EntityGhast)
		{
			return true;
		}
		
		int i = MathHelper.floor_double(creature.posX);
		int j = MathHelper.floor_double(creature.boundingBox.minY);
		int k = MathHelper.floor_double(creature.posZ);
		
		if(creature.worldObj.getSavedLightValue(EnumSkyBlock.Sky, i, j, k) > creature.getRNG().nextInt(32) && creature.worldObj.isDaytime() && !creature.worldObj.isThundering())
		{
			return false;
		} else
		{
			int l = creature.worldObj.getSavedLightValue(EnumSkyBlock.Block, i, j, k);
			
			return l <= creature.getRNG().nextInt(8);
		}
	}
}
