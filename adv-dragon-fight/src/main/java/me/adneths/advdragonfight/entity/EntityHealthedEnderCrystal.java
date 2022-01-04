package me.adneths.advdragonfight.entity;

import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityHealthedEnderCrystal extends EntityEnderCrystal implements DifficultyScaled
{

	private static final DataParameter<Float> HEALTH = EntityDataManager
			.<Float>createKey(EntityHealthedEnderCrystal.class, DataSerializers.FLOAT);
	private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager
			.<Integer>createKey(EntityHealthedEnderCrystal.class, DataSerializers.VARINT);

	private AbstractAttributeMap attributeMap;

	public EntityLivingBase targetedEntity;
	private int clientSideAttackTime;
	
	private int sumDiff;
	private float avgDiff;
	
	public EntityHealthedEnderCrystal(World worldIn)
	{
		this(worldIn, 0, 0, 0, 0, 0);
	}

	public EntityHealthedEnderCrystal(World worldIn, double x, double y, double z)
	{
		this(worldIn, x, y, z, 1, 1);
	}

	public EntityHealthedEnderCrystal(World worldIn, double x, double y, double z, int sumDiff, float avgDiff)
	{
		super(worldIn, x, y, z);
		applyEntityAttributes();
		this.getDataManager().register(TARGET_ENTITY, 0);
		this.getDataManager().register(HEALTH, 50f);

		this.setSumDifficulty(sumDiff);
		this.setAverageDifficulty(avgDiff);
		this.updateDifficulty();
	}
	
	protected void applyEntityAttributes()
	{
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.MAX_HEALTH);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
		this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48);
	}

	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (this.isEntityInvulnerable(source))
		{
			return false;
		}
		else if (source.getTrueSource() instanceof EntityDragon)
		{
			return false;
		}
		else
		{
			setHealth(getHealth() - amount);
			if (!this.world.isRemote)
			{
				EntityHardenedEndermite mite = new EntityHardenedEndermite(this.world);
				mite.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
				if (source.getTrueSource() instanceof EntityLivingBase)
				{
					EntityLivingBase living = (EntityLivingBase) source.getTrueSource();
					mite.setAttackTarget(living);
					
					if(this.getDistanceSq(living) > 25)
					{
						Vec3d vel = new Vec3d(-this.posX, -this.posY, -this.posZ);
						vel = vel.addVector(living.posX, living.posY, living.posZ);
						vel = vel.normalize();
						mite.motionX = vel.x;
						mite.motionY = 0.5;
						mite.motionZ = vel.z;
					}
				}
				this.world.spawnEntity(mite);
			}
			if (getHealth() == 0)
				super.attackEntityFrom(source, amount);
			return true;
		}
	}

	public AbstractAttributeMap getAttributeMap()
	{
		if (this.attributeMap == null)
		{
			this.attributeMap = new AttributeMap();
		}

		return this.attributeMap;
	}

	public IAttributeInstance getEntityAttribute(IAttribute attribute)
	{
		return this.getAttributeMap().getAttributeInstance(attribute);
	}

	public final float getMaxHealth()
	{
		return (float) this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
	}

	public float getHealth()
	{
		return ((Float) this.dataManager.get(HEALTH)).floatValue();
	}

	public void setHealth(float health)
	{
		this.dataManager.set(HEALTH, Float.valueOf(MathHelper.clamp(health, 0.0F, this.getMaxHealth())));
	}

	public EntityLivingBase getTargetedEntity()
	{
		if (!this.hasTargetedEntity())
		{
			return null;
		}
		else
		{
			if (this.targetedEntity != null)
			{
				return this.targetedEntity;
			}
			else
			{
				Entity entity = this.world.getEntityByID(((Integer) this.dataManager.get(TARGET_ENTITY)).intValue());

				if (entity instanceof EntityLivingBase)
				{
					this.targetedEntity = (EntityLivingBase) entity;
					return this.targetedEntity;
				}
				else
				{
					return null;
				}
			}
		}
	}

	public int getAttackDuration()
	{
		return 80;
	}

	private void setTargetedEntity(int entityId)
	{
		if(entityId==0)
			this.targetedEntity = null;
		this.dataManager.set(TARGET_ENTITY, Integer.valueOf(entityId));
	}

	public boolean hasTargetedEntity()
	{
		return this.dataManager.get(TARGET_ENTITY).intValue() != 0;
	}

	public float getAttackAnimationScale(float partial)
	{
		return ((float) this.clientSideAttackTime + partial) / this.getAttackDuration();
	}

	private int tickCounter = 0;

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (this.hasTargetedEntity())
			if (this.clientSideAttackTime < this.getAttackDuration())
				++this.clientSideAttackTime;

		if (this.hasTargetedEntity())
		{
			EntityLivingBase living = this.getTargetedEntity();

			if(this.world != null && living != null)
			if (!this.canEntityBeSeen(living))
			{
				this.setTargetedEntity(0);
				this.tickCounter = 0;
				this.clientSideAttackTime = 0;
			}
			else
			{
				if(this.world.isRemote)
					return;
				++this.tickCounter;

				if (this.tickCounter >= this.getAttackDuration())
				{
					this.setTargetedEntity(0);
					this.tickCounter = 0;
					this.clientSideAttackTime = 0;
					living.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this).setDifficultyScaled(), 4f + Math.min(this.getAverageDifficulty()/10,2.5f));
				}
			}
		}
		else if (this.rand.nextInt(10) == 0)
		{
			double dist = this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
			EntityPlayer targetEntity = this.getNearestAttackableVisiblePlayer(this.posX,
					this.posY + (double) this.getEyeHeight(), this.posZ, dist, dist, (p) -> {
						return 1d;
					}, null);
			if (targetEntity != null)
			{
				this.setTargetedEntity(targetEntity.getEntityId());
			}
		}
	}

	private EntityPlayer getNearestAttackableVisiblePlayer(double posX, double posY, double posZ, double maxXZDistance,
			double maxYDistance, @Nullable Function<EntityPlayer, Double> playerToDouble,
			@Nullable Predicate<EntityPlayer> predicate)
	{
		double d0 = -1.0D;
		EntityPlayer entityplayer = null;

		for (int j2 = 0; j2 < this.world.playerEntities.size(); ++j2)
		{
			EntityPlayer player = this.world.playerEntities.get(j2);

			if (!player.isCreative() && !player.capabilities.disableDamage && player.isEntityAlive()
					&& !player.isSpectator() && (predicate == null || predicate.apply(player)))
			{
				double d1 = player.getDistanceSq(posX, player.posY, posZ);
				double d2 = maxXZDistance;

				if (player.isSneaking())
				{
					d2 = maxXZDistance * 0.800000011920929D;
				}

				if (player.isInvisible())
				{
					float f = player.getArmorVisibility();

					if (f < 0.1F)
					{
						f = 0.1F;
					}

					d2 *= (double) (0.7F * f);
				}

				if (playerToDouble != null)
				{
					d2 *= ((Double) MoreObjects.firstNonNull(playerToDouble.apply(player), Double.valueOf(1.0D)))
							.doubleValue();
				}

				d2 = net.minecraftforge.common.ForgeHooks.getPlayerVisibilityDistance(player, d2, maxYDistance);

				if ((maxYDistance < 0.0D || Math.abs(player.posY - posY) < maxYDistance * maxYDistance)
						&& (maxXZDistance < 0.0D || d1 < d2 * d2) && (d0 == -1.0D || d1 < d0))
				{
					if(this.canEntityBeSeen(player))
					{
						d0 = d1;
						entityplayer = player;
					}
				}
			}
		}

		return entityplayer;
	}

	private boolean canEntityBeSeen(Entity entityIn)
	{
		return this.world.rayTraceBlocks(new Vec3d(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ),
				new Vec3d(entityIn.posX, entityIn.posY+entityIn.getEyeHeight()/2, entityIn.posZ), false, true,
				false) == null;
	}

	@Override
	public int getSumDifficulty()
	{
		return this.sumDiff;
	}

	@Override
	public float getAverageDifficulty()
	{
		return this.avgDiff;
	}

	@Override
	public void setSumDifficulty(int sumDiff)
	{
		this.sumDiff = sumDiff;
	}

	@Override
	public void setAverageDifficulty(float avgDiff)
	{
		this.avgDiff = avgDiff;
	}

	@Override
	public void updateDifficulty()
	{
		float health = Math.min(50 + 10 * (this.getSumDifficulty()-1), 200);
		this.dataManager.set(HEALTH, health);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
	}

}
