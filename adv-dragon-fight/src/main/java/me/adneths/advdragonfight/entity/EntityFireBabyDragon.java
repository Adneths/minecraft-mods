package me.adneths.advdragonfight.entity;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityFireBabyDragon extends EntityBabyDragonBase
{

	public EntityFireBabyDragon(World worldIn)
	{
		this(worldIn, 0, 0, 0, 1, 1);
	}

	public EntityFireBabyDragon(World worldIn, double x, double y, double z, int sumDiff, float avgDiff)
	{
		super(worldIn, x, y, z, sumDiff, avgDiff);
	}

	@Override
	public void initEntityAI()
	{
		this.tasks.addTask(1, new Evade(this));
		this.tasks.addTask(1, new LaunchFire(this));
	}

	class Evade extends EntityAIBase
	{
		private EntityFireBabyDragon babyDragon;

		private Evade(EntityFireBabyDragon babyDragon)
		{
			this.babyDragon = babyDragon;
			this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute()
		{
			return this.babyDragon.getTargetedPos() == null
					|| this.babyDragon.getPositionVector().squareDistanceTo(this.babyDragon.getTargetedPos()) < 16;

		}

		@Override
		public void startExecuting()
		{
			this.babyDragon.setTargetedPos(new Vec3d(this.babyDragon.rand.nextInt(81) - 40,
					this.babyDragon.rand.nextInt(10) + 75, this.babyDragon.rand.nextInt(81) - 40));
		}

		@Override
		public boolean shouldContinueExecuting()
		{
			return false;
		}

	}

	class LaunchFire extends EntityAIBase
	{
		private EntityFireBabyDragon babyDragon;
		private int waitTime;

		private LaunchFire(EntityFireBabyDragon babyDragon)
		{
			this.babyDragon = babyDragon;
			this.waitTime = this.babyDragon.rand.nextInt(30);
		}

		@Override
		public boolean shouldExecute()
		{
			return true;
		}

		@Override
		public boolean shouldContinueExecuting()
		{
			return true;
		}

		@Override
		public void updateTask()
		{
			if (waitTime++ < 170)
				return;
			waitTime = this.babyDragon.rand.nextInt(50);
			for (EntityPlayer p : this.babyDragon.world.playerEntities)
			{
				if (!p.capabilities.disableDamage && p.isEntityAlive() && !p.isSpectator())
					if (this.babyDragon.canEntityBeSeen(p))
					{
						Vec3d vel = new Vec3d(p.posX - this.babyDragon.posX, p.posY + p.height/2 - this.babyDragon.posY, p.posZ - this.babyDragon.posZ);
						EntitySmallDragonFireball fireball = new EntitySmallDragonFireball(this.babyDragon.world, this.babyDragon, vel.x, vel.y, vel.z);
						this.babyDragon.world.spawnEntity(fireball);
					}
			}
		}
	}
}
