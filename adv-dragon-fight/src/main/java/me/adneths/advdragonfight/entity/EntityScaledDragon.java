package me.adneths.advdragonfight.entity;

import java.util.Comparator;
import java.util.List;

import me.adneths.advdragonfight.item.ModItems;
import me.adneths.advdragonfight.potion.ModPotions;
import me.adneths.advdragonfight.world.end.AdvDragonFightManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeEndDecorator;
import net.minecraft.world.gen.feature.WorldGenSpikes;

public class EntityScaledDragon extends EntityDragon implements DifficultyScaled
{

	private int sumDiff;
	private float avgDiff;

	private float phase2;
	private float phase3;

	public EntityScaledDragon(World worldIn)
	{
		this(worldIn, 0, 0, 0, 1, 1);
	}

	public EntityScaledDragon(World worldIn, double x, double y, double z, int sumDiff, float avgDiff)
	{
		super(worldIn);
		this.setSumDifficulty(sumDiff);
		this.setAverageDifficulty(avgDiff);
		this.updateDifficulty();
	}

	private int enderTime = 0;
	private int negBallTime = 0;
	private int scaleSpreadTime = 0;
	private int scaleSpreadCount;
	private int scaleHomeTime = 0;
	private boolean landing = false;

	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		if (this.ticksExisted % 5 == 0 && this.getFightManager() != null)
		{
			this.getFightManager().dragonUpdate(this);
			for(EntityPlayer p : ((AdvDragonFightManager)this.getFightManager()).getFightingPlayers())
			{
				p.capabilities.isFlying &= p.isCreative();
			}
			if (this.getPhaseManager().getCurrentPhase().getIsStationary())
			{
				if (!landing)
				{
					if (!this.world.isRemote)
						for (EntityPlayer p : this.world.playerEntities)
						{
							if (p.dimension == this.dimension
									&& p.getPositionVector().squareDistanceTo(0, 0, 0) < 22500)
							{
								Vec3d mot = new Vec3d(p.posX, 0, p.posZ).normalize();
								p.addVelocity(mot.x * 8, 1.5, mot.z * 8);
								p.velocityChanged = true;
							}
							p.playSound(SoundEvents.ITEM_TOTEM_USE, 2F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
						}
				}
				landing = true;
			}
			else
			{
				landing = false;
			}
			if (landing)
			{
				for (EntityPlayer p : this.world.playerEntities)
					if (p.getPositionVector().squareDistanceTo(this.getPositionVector()) < 100)
						p.addPotionEffect(new PotionEffect(ModPotions.intimidate, 600));
			}
		}
		if (this.getHealth() / this.getMaxHealth() < this.phase2)
		{
			this.phase2 = -1;

			int i = 0;
			for (WorldGenSpikes.EndSpike endspike : BiomeEndDecorator.getSpikesForWorld(this.world))
			{
				if (i++ % (sumDiff + 1) != 0)
				{
					this.world.createExplosion(null, endspike.getCenterX() + 0.5F, endspike.getHeight(),
							endspike.getCenterZ() + 0.5F, 5.0F, true);
					if (!this.world.isRemote)
						this.world.spawnEntity(new EntityHealthedEnderCrystal(this.world, endspike.getCenterX() + 0.5,
								endspike.getHeight() + 1, endspike.getCenterZ() + 0.5));
				}
			}
			if (!this.world.isRemote)
			{
				EntityBabyDragonBase entity;
				EntityDelaySpawn delay;
				for (i = 0; i < Math.min(this.avgDiff / 2, 4); i++)
				{
					int x = this.rand.nextInt(41) - 20;
					int y = this.rand.nextInt(15) + 75;
					int z = this.rand.nextInt(41) - 20;
					entity = new EntityDivingBabyDragon(this.world, x, y, z, this.sumDiff, this.avgDiff);
					entity.setPosition(x, y, z);
					delay = new EntityDelaySpawn(this.world, 150, entity);
					delay.setPosition(x, y, z);
					this.world.spawnEntity(delay);
				}
				for (i = 0; i < Math.min(this.avgDiff / 2, 4); i++)
				{
					int x = this.rand.nextInt(41) - 20;
					int y = this.rand.nextInt(15) + 75;
					int z = this.rand.nextInt(41) - 20;
					entity = new EntityFireBabyDragon(this.world, 0, 0, 0, this.sumDiff, this.avgDiff);
					entity.setPosition(x, y, z);
					delay = new EntityDelaySpawn(this.world, 150, entity);
					delay.setPosition(x, y, z);
					this.world.spawnEntity(delay);
				}
			}
		}

		if (!this.world.isRemote)
		{
			if (negBallTime++ < 1000)
			{
				negBallTime = this.rand.nextInt((int) Math.max(1, Math.min(800,
						400 * this.getAverageDifficulty() * (1 - this.getHealth() / this.getMaxHealth()))));

				EntityPlayer p = this.world.getNearestAttackablePlayer(this, 100, 100);
				if (p != null && this.canEntityBeSeen(p))
				{
					Vec3d vec3d2 = this.getLook(1.0F);
					double x = this.dragonPartHead.posX - vec3d2.x * 1.0D;
					double y = this.dragonPartHead.posY + (this.dragonPartHead.height / 2.0F) + 0.5D;
					double z = this.dragonPartHead.posZ - vec3d2.z * 1.0D;
					double dirX = p.posX - x;
					double dirY = p.posY + (p.height / 2.0F) - y + (this.dragonPartHead.height / 2.0F);
					double dirZ = p.posZ - z;
					EntityDragonEffectBall ball = new EntityDragonEffectBall(this.world, this, dirX, dirY, dirZ);
					ball.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
					this.world.spawnEntity(ball);
				}
			}

			if (this.getHealth() / this.getMaxHealth() < this.phase3)
			{
				this.phase3 = -1;

				for (int i = 0; i < 5 + Math.min(this.sumDiff, 35); i++)
				{
					BlockPos pos = new BlockPos(this.rand.nextInt(81) - 40, 100, this.rand.nextInt(81) - 40);
					for (; !this.world.getBlockState(pos).isFullCube(); pos = pos.down())
						;
					EntityShulker shulker = new EntityShulker(this.world);
					shulker.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
					this.world.spawnEntity(shulker);
				}
				EntityBabyDragonBase entity;
				EntityDelaySpawn delay;
				for (int i = 0; i < Math.min(this.avgDiff / 3, 6); i++)
				{
					int x = this.rand.nextInt(41) - 20;
					int y = this.rand.nextInt(15) + 75;
					int z = this.rand.nextInt(41) - 20;
					entity = new EntityDivingBabyDragon(this.world, x, y, z, this.sumDiff, this.avgDiff);
					entity.setPosition(x, y, z);
					delay = new EntityDelaySpawn(this.world, 150, entity);
					delay.setPosition(x, y, z);
					this.world.spawnEntity(delay);
				}
				for (int i = 0; i < Math.min(this.avgDiff / 3, 6); i++)
				{
					int x = this.rand.nextInt(41) - 20;
					int y = this.rand.nextInt(15) + 75;
					int z = this.rand.nextInt(41) - 20;
					entity = new EntityFireBabyDragon(this.world, 0, 0, 0, this.sumDiff, this.avgDiff);
					entity.setPosition(x, y, z);
					delay = new EntityDelaySpawn(this.world, 150, entity);
					delay.setPosition(x, y, z);
					this.world.spawnEntity(delay);
				}
				for (int i = 0; i < Math.min(this.avgDiff / 4, 3); i++)
				{
					int x = this.rand.nextInt(41) - 20;
					int y = this.rand.nextInt(15) + 75;
					int z = this.rand.nextInt(41) - 20;
					entity = new EntityHealingBabyDragon(this.world, 0, 0, 0, this.sumDiff, this.avgDiff);
					entity.setPosition(x, y, z);
					delay = new EntityDelaySpawn(this.world, 150, entity);
					delay.setPosition(x, y, z);
					this.world.spawnEntity(delay);
				}
			}
			if (this.phase2 < 0)
			{
				if (enderTime++ > 2400)
				{
					enderTime = this.rand.nextInt(Math.min((int) (this.avgDiff * 100), 1000));

					List<EntityEnderman> list = this.world.getEntities(EntityEnderman.class, (e) -> {return true;});
					list.sort(new Comparator<EntityEnderman>() {
						@Override
						public int compare(EntityEnderman o1, EntityEnderman o2)
						{
							return o1.getPositionVector().squareDistanceTo(0,0,0)>o2.getPositionVector().squareDistanceTo(0, 0, 0) ? 1 : -1;
						}});
					for (int i = 0; i < Math.min(Math.min(this.avgDiff, 6) + 2, list.size()); i++)
					{
						EntityEnderGuardian guard = new EntityEnderGuardian(this.world);
						list.get(i).setDead();
						Vec3d v = list.get(i).getPositionVector();
						guard.setPosition(v.x, v.y, v.z);
						this.world.spawnEntity(guard);
					}
				}
				if (scaleSpreadTime++ > 300)
				{
					if (scaleSpreadTime > 300 + 4 * scaleSpreadCount)
						scaleSpreadTime = this.rand.nextInt(Math.min((int) (this.avgDiff * 8), 150));

					EntityPlayer p = this.world.getNearestAttackablePlayer(this, 100, 100);
					if (scaleSpreadTime % 4 == 0)
					{
						if (p != null && this.canEntityBeSeen(p))
						{
							Vec3d vel = new Vec3d(p.posX - this.dragonPartWing1.posX,
									p.posY - this.dragonPartWing1.posY, p.posZ - this.dragonPartWing1.posZ);
							EntityScale scale = new EntityScale(this.world,
									this.dragonPartWing1.posX + this.rand.nextDouble() - 0.5,
									this.dragonPartWing1.posY - 4,
									this.dragonPartWing1.posZ + this.rand.nextDouble() - 0.5, false);
							scale.shoot(vel.x, vel.y, vel.z, 1.5f, 5);
							this.world.spawnEntity(scale);

							vel = new Vec3d(p.posX - this.dragonPartWing2.posX, p.posY - this.dragonPartWing2.posY,
									p.posZ - this.dragonPartWing2.posZ);
							scale = new EntityScale(this.world,
									this.dragonPartWing2.posX + this.rand.nextDouble() - 0.5,
									this.dragonPartWing2.posY - 4,
									this.dragonPartWing2.posZ + this.rand.nextDouble() - 0.5, false);
							scale.shoot(vel.x, vel.y, vel.z, 1.5f, 5);
							this.world.spawnEntity(scale);

							playSound(SoundEvents.ENTITY_ENDERDRAGON_FLAP, 1.0F,
									1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
						}
					}
				}
			}
			if (this.phase3 < 0)
			{
				if (scaleHomeTime++ > 150)
				{
					scaleHomeTime = this.rand.nextInt(Math.min((int) (this.avgDiff * 10), 100));

					EntityPlayer p = this.world.getNearestAttackablePlayer(this, 50, 100);
					if (p != null)
					{
						Vec3d vel = new Vec3d(p.posX - this.dragonPartWing1.posX, p.posY - this.dragonPartWing1.posY,
								p.posZ - this.dragonPartWing1.posZ);
						EntityScale scale = new EntityScale(this.world,
								this.dragonPartWing1.posX + this.rand.nextDouble() - 0.5, this.dragonPartWing1.posY - 4,
								this.dragonPartWing1.posZ + this.rand.nextDouble() - 0.5, true);
						scale.shoot(vel.x, vel.y, vel.z, 1.5f, 5);
						this.world.spawnEntity(scale);

						vel = new Vec3d(p.posX - this.dragonPartWing2.posX, p.posY - this.dragonPartWing2.posY,
								p.posZ - this.dragonPartWing2.posZ);
						scale = new EntityScale(this.world, this.dragonPartWing2.posX + this.rand.nextDouble() - 0.5,
								this.dragonPartWing2.posY - 4, this.dragonPartWing2.posZ + this.rand.nextDouble() - 0.5,
								true);
						scale.shoot(vel.x, vel.y, vel.z, 1.5f, 5);
						this.world.spawnEntity(scale);

						playSound(SoundEvents.ENTITY_ENDERDRAGON_FLAP, 1.0F,
								1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
					}
				}
			}
		}
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
		float health = 500 + Math.min(50 * (this.getSumDifficulty() - 1), this.getAverageDifficulty() * 100);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
		this.setHealth(this.getMaxHealth() - 1);

		this.phase2 = Math.min(0.6f + this.getAverageDifficulty() * 2 / 100, 0.8f);
		this.phase3 = Math.min(0.3f + this.getAverageDifficulty() * 2 / 100, 0.9f);
		this.setCustomNameTag(
				String.format("Ender Dragon [%d/%.2f]", this.getSumDifficulty(), this.getAverageDifficulty()));
		scaleSpreadCount = (int) Math.min(this.avgDiff + 2, 10);
	}

	@Override
	public void onDeathUpdate()
	{
		super.onDeathUpdate();
		if (this.deathTicks == 200 && !this.world.isRemote)
		{
			EntityFloatingItem item = new EntityFloatingItem(this.world, this.posX, this.posY, this.posZ,
					new ItemStack(ModItems.dragonSkin, (int) Math.min(this.getAverageDifficulty(), 64)));
			this.world.spawnEntity(item);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		compound.setFloat("AdvStage2", this.phase2);
		compound.setFloat("AdvStage3", this.phase3);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		if (compound.hasKey("AdvStage2"))
			this.phase2 = compound.getFloat("AdvStage2");
		if (compound.hasKey("AdvStage3"))
			this.phase2 = compound.getFloat("AdvStage3");
	}

}
