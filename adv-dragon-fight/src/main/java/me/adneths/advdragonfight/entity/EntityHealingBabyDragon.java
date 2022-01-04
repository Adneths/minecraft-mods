package me.adneths.advdragonfight.entity;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityHealingBabyDragon extends EntityBabyDragonBase
{
	
	private boolean isHealing;
	
	public EntityHealingBabyDragon(World worldIn)
	{
		this(worldIn, 0, 0, 0, 1, 1);
	}
	
	public EntityHealingBabyDragon(World worldIn, double x, double y, double z, int sumDiff, float avgDiff)
	{
		super(worldIn, x, y, z, sumDiff, avgDiff);
	}
	
	@Override
	public void initEntityAI()
	{
		this.tasks.addTask(1, new FollowDragon(this));
	}
	
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		EntityLivingBase target = this.getAttackTarget();
		if(target != null)
		{
			if(this.ticksExisted % 10 == 0)
				if(target.getMaxHealth() > target.getHealth())
					target.setHealth(target.getHealth() + 1);
		}
		else
		{
			List<EntityDragon> list = this.world.getEntities(EntityDragon.class, (in) -> {return true;});
			if(list.size() > 0)
				this.setAttackTarget(list.get(0));
		}
	}
	
	public boolean isHealing()
	{
		return isHealing;
	}

	class FollowDragon extends EntityAIBase
	{
		private static final double TAU = 2 * Math.PI;
		
		private EntityHealingBabyDragon babyDragon;
		private int counter;
		private double theta, phi, thetaRate, phiRate, r, dr, h, dh;
		
		public FollowDragon(EntityHealingBabyDragon babyDragon)
		{
			this.babyDragon = babyDragon;
			this.setMutexBits(1);
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
		public void startExecuting()
		{
			theta = this.babyDragon.rand.nextDouble()*2*Math.PI;
			phi = this.babyDragon.rand.nextDouble()*2*Math.PI;
			thetaRate = this.babyDragon.rand.nextDouble()*0.2+0.1;
			phiRate = this.babyDragon.rand.nextDouble()*0.2+0.1;
			dh = this.babyDragon.rand.nextInt(4)+4;
			dr = this.babyDragon.rand.nextInt(4)+4;
			r = this.babyDragon.rand.nextInt(50)+40;
			h = this.babyDragon.rand.nextInt(10)+75;
		}
		
		@Override
		public void updateTask()
		{
			if (counter++ < 4)
				return;
			counter = 0;
						
			double effR = r+dr*Math.cos(phi);
			Vec3d pos = new Vec3d(Math.cos(theta)*effR,
								  Math.sin(phi)*dh+h,
								  Math.sin(theta)*effR);
			this.babyDragon.setTargetedPos(pos);
			
			theta = (theta+thetaRate)%TAU;
			phi = (phi+phiRate)%TAU;
		}
		
	}

}
