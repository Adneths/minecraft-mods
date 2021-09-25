package me.adneths.bottle_bee.entity;

import java.util.Random;

import javax.annotation.Nullable;

import me.adneths.bottle_bee.init.ModContents;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class BottleBeeEntity extends ProjectileItemEntity {

	private int numOfBees;
	private boolean isKillerBees;
	private LivingEntity thrower;
	
	public BottleBeeEntity(double x, double y, double z, World worldIn) {
		super(ModContents.Entities.BOTTLE_OF_BEE, x, y, z, worldIn);
	}

	public BottleBeeEntity(EntityType<? extends ProjectileItemEntity> type, LivingEntity livingEntityIn, World worldIn) {
		super(type, livingEntityIn, worldIn);
	}

	public BottleBeeEntity(EntityType<? extends ProjectileItemEntity> type, World worldIn) {
		super(type, worldIn);
	}

	public BottleBeeEntity(FMLPlayMessages.SpawnEntity packet, World worldIn)
    {
        super(ModContents.Entities.BOTTLE_OF_BEE, worldIn);
    }
	
	public void setThrower(LivingEntity thrower)
	{
		this.thrower = thrower;
	}
	public LivingEntity getThrower()
	{
		return this.thrower;
	}
	
	@Override
	protected Item getDefaultItem() {
		return ModContents.Items.BOTTLE_OF_BEE;
	}
	
	@Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
	
	public void onImpact(RayTraceResult result) {
		super.onImpact(result);
		if (!this.world.isRemote) {
			this.world.setEntityState(this, (byte)3);
			remove();
		}
	}
	
	@Override
	protected void onEntityHit(EntityRayTraceResult hit) {
		if(hit.getEntity() instanceof LivingEntity)
		{
			((LivingEntity)hit.getEntity()).attackEntityFrom(DamageSource.causeIndirectDamage(this, this.getThrower()), 3);
			spawnBee(this.getEntityWorld(), this.numOfBees, this.isKillerBees, hit.getHitVec().x, hit.getHitVec().y, hit.getHitVec().z, (LivingEntity)hit.getEntity());
		}
		else
			spawnBee(this.getEntityWorld(), this.numOfBees, this.isKillerBees, hit.getHitVec().x, hit.getHitVec().y, hit.getHitVec().z, null);
	}
	
	/**
	 * Runs when the projectile hits a block
	 */
	@Override
	protected void func_230299_a_(BlockRayTraceResult hit) {
		spawnBee(this.getEntityWorld(), this.numOfBees, this.isKillerBees, hit.getHitVec().x+hit.getFace().getXOffset(), hit.getHitVec().y+hit.getFace().getYOffset(), hit.getHitVec().z+hit.getFace().getZOffset(), null);
	}
	
	public void setNumberOfBees(int num) {
		this.numOfBees = num;
	}
	
	public void setIsKillerBees(boolean killer)
	{
		this.isKillerBees = killer;
	}
	
	private static final EntityPredicate DEFAULT = new EntityPredicate().setUseInvisibilityCheck().setLineOfSiteRequired().setCustomPredicate(living -> {return !living.getType().equals(EntityType.BEE);});
	public static void spawnBee(World world, int numOfBees, boolean isKiller, double x, double y, double z, @Nullable LivingEntity target)
	{
		if(world.isRemote)
			return;
		Random random = new Random();
		if(isKiller)
		{
			KillerBeeEntity bee = new KillerBeeEntity(ModContents.Entities.KILLER_BEE, world);
			bee.setLocationAndAngles(x + (random.nextDouble()-.5), y + (random.nextDouble()-.5), z + (random.nextDouble()-.5), 0, 0);
			if(target == null || target.getType().equals(ModContents.Entities.KILLER_BEE))
			{
				PlayerEntity closestPlayer = world.getClosestPlayer(x, y, z, 8, entity -> {return true;});
				if(closestPlayer == null)
				{
					target = world.getClosestEntityWithinAABB(LivingEntity.class, DEFAULT, bee, x, y, z, new AxisAlignedBB(x-8,y-8,z-8,x+8,y+8,z+8));
				}
				else
				{
					target = closestPlayer;
				}
			}
			for(int i = 0; i < Math.max(1, numOfBees); i++)
			{
				if(i!=0)
				{
					bee = new KillerBeeEntity(ModContents.Entities.KILLER_BEE, world);
					bee.setLocationAndAngles(x, y, z, 0, 0);
				}
				if(target!=null)
				{
					bee.setAngerTarget(target.getUniqueID());
					bee.setAngerTime(1200);
					bee.setAggroed(true);
					bee.setAttackTarget(target);
				}
				world.addEntity(bee);
			}
		}
		else
		{
			BeeEntity bee = new BeeEntity(EntityType.BEE, world);
			bee.setLocationAndAngles(x + (random.nextDouble()-.5), y + (random.nextDouble()-.5), z + (random.nextDouble()-.5), 0, 0);
			if(target == null || target.getType().equals(EntityType.BEE))
			{
				PlayerEntity closestPlayer = world.getClosestPlayer(x, y, z, 8, entity -> {return true;});
				if(closestPlayer == null)
				{
					target = world.getClosestEntityWithinAABB(LivingEntity.class, DEFAULT, bee, x, y, z, new AxisAlignedBB(x-8,y-8,z-8,x+8,y+8,z+8));
				}
				else
				{
					target = closestPlayer;
				}
			}
			for(int i = 0; i < Math.max(1, numOfBees); i++)
			{
				if(i!=0)
				{
					bee = new BeeEntity(EntityType.BEE, world);
					bee.setLocationAndAngles(x, y, z, 0, 0);
				}
				if(target!=null)
				{
					bee.setAngerTarget(target.getUniqueID());
					bee.setAngerTime(1200);
					bee.setAggroed(true);
					bee.setAttackTarget(target);
				}
				world.addEntity(bee);
			}
		}
	}
	
}
