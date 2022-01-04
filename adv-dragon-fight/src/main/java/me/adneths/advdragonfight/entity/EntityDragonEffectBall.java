package me.adneths.advdragonfight.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDragonEffectBall extends EntityFireball
{
	public EntityDragonEffectBall(World worldIn)
	{
		super(worldIn);
		setSize(1.0F, 1.0F);
	}

	@SideOnly(Side.CLIENT)
	public EntityDragonEffectBall(World worldIn, double x, double y, double z, double accelX, double accelY,
			double accelZ)
	{
		super(worldIn, x, y, z, accelX, accelY, accelZ);
		setSize(1.0F, 1.0F);
	}

	public EntityDragonEffectBall(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ)
	{
		super(worldIn, shooter, accelX, accelY, accelZ);
		setSize(1.0F, 1.0F);
	}

	protected void onImpact(RayTraceResult result)
	{
		if (result.entityHit == null || !result.entityHit.isEntityEqual((Entity) this.shootingEntity))
			if (!this.world.isRemote)
			{
				EntityAreaEffectCloud cloud = new EntityAreaEffectCloud(this.world, this.posX, this.posY, this.posZ);
				cloud.setOwner(this.shootingEntity);
				cloud.setRadius(3.0F);
				cloud.setDuration(600);
				cloud.setColor(0);
				cloud.setRadiusPerTick((7.0F - cloud.getRadius()) / cloud.getDuration());
				cloud.addEffect(new PotionEffect(MobEffects.NAUSEA, 600, 2));
				cloud.addEffect(new PotionEffect(MobEffects.WEAKNESS, 1200, 1));
				cloud.addEffect(new PotionEffect(MobEffects.BLINDNESS, 100, 0));
				cloud.addEffect(new PotionEffect(MobEffects.HUNGER, 1200, 3));
				cloud.addEffect(new PotionEffect(MobEffects.SLOWNESS, 300, 2));
				this.world.spawnEntity(cloud);
				setDead();
			}
	}

	public boolean canBeCollidedWith()
	{
		return false;
	}

	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		return false;
	}

	protected EnumParticleTypes getParticleType()
	{
		return EnumParticleTypes.DRAGON_BREATH;
	}

	protected boolean isFireballFiery()
	{
		return false;
	}
}
