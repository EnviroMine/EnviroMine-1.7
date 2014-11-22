package enviromine.trackers;

import net.minecraft.entity.Entity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Hallucination
{
	public EntityLiving falseEntity;
	public EntityPlayer overriding;
	public String falseSound;
	public int x;
	public int y;
	public int z;
	public int time;
	public static int maxTime = 60;
	private Type type = Type.NORMAL;
	public static ArrayList<Hallucination> list = new ArrayList<Hallucination>();
	public static HashMap<String, Hallucination> players = new HashMap<String, Hallucination>();
	
	@SuppressWarnings("unchecked")
	public Hallucination(EntityLivingBase entityLiving)
	{
		if(!(entityLiving instanceof EntityPlayer))
		{
			return;
		}
		
		Random rand = entityLiving.getRNG();
		
		if (rand.nextInt(10) == 0 || true) {
			this.overriding = this.findPlayer(entityLiving);
			if (this.overriding != null) {
				this.type = Type.OVERRIDE;
			}
		}
		
		x = (int)(this.type == Type.NORMAL ? (entityLiving.posX + rand.nextInt(20) - 10) : this.overriding.posX);
		y = (int)(this.type == Type.NORMAL ? (entityLiving.posY + rand.nextInt(2) - 1) : this.overriding.posY);
		z = (int)(this.type == Type.NORMAL ? (entityLiving.posZ + rand.nextInt(20) - 10) : this.overriding.posZ);

		BiomeGenBase biome = entityLiving.worldObj.getBiomeGenForCoords(MathHelper.floor_double(x), MathHelper.floor_double(z));
		
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
		if (this.type == Type.OVERRIDE) {
			players.put(entityLiving.getCommandSenderName(), this);
		}
		
		if(falseEntity.worldObj.isRemote && falseEntity instanceof EntityLiving)
		{
			//Minecraft.getMinecraft().sndManager.playSound(falseSound, x, y, z, 1.0F, 1.0F);
			falseEntity.getEntityData().setBoolean("EM_Hallucination", true);
			((EntityLiving)falseEntity).playLivingSound();
		}
	}
	
	private EntityPlayer findPlayer(EntityLivingBase entity)
	{
		List<EntityPlayer> players = getPlayerList(entity);
		if (players.size() > 0) {
			return players.get(entity.getRNG().nextInt(players.size()));
		} else {
			return null;
		}
	}
	
	private List<EntityPlayer> getPlayerList(EntityLivingBase entity)
	{
		List<EntityPlayer> players = new ArrayList<EntityPlayer>();
		Iterator ite = entity.worldObj.loadedEntityList.iterator();
		
		while (ite.hasNext())
		{
			Entity e = (Entity)ite.next();
			if (e instanceof EntityPlayer && !e.getCommandSenderName().equals(entity.getCommandSenderName()) && !isPlayerSeenWrong(e.getCommandSenderName())) {
				players.add((EntityPlayer)e);
			}
		}
		
		return players;
	}
	
	public void doOverride()
	{
		if (this.type == Type.OVERRIDE) {
			this.falseEntity.setPositionAndRotation(this.overriding.posX, this.overriding.posY, this.overriding.posZ, this.overriding.rotationYaw, this.overriding.rotationPitch);
		}
	}
	
	public void finish() {
		switch (this.type) {
			case OVERRIDE:
				players.remove(this.overriding.getCommandSenderName());
			case NORMAL:
				this.falseEntity.setDead();
				list.remove(this);
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
					subject.finish();
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
	
	public static boolean isPlayerSeenWrong(String username) {
		return players.containsKey(username);
	}
	
	public static void renderOverride(EntityPlayer player)
	{
		Hallucination hal = players.get(player.getCommandSenderName());
		hal.doOverride();
	}
	
	private static enum Type {
		NORMAL,
		OVERRIDE
	}
}
