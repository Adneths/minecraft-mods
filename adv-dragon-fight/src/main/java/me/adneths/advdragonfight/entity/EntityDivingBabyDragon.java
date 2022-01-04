package me.adneths.advdragonfight.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityDivingBabyDragon extends EntityBabyDragonBase
{
	private AttackPhase phase = AttackPhase.SEARCH;

	private Vec3d home;

	public boolean isPhase(AttackPhase phase)
	{
		return this.phase == phase;
	}

	public static enum AttackPhase
	{
		SEARCH(0), DIVE(1), CARRY(2);

		private int id;

		AttackPhase(int i)
		{
			this.id = i;
		}

		public static AttackPhase fromId(int id)
		{
			switch (id)
			{
			case 0:
				return SEARCH;
			case 1:
				return DIVE;
			case 2:
				return CARRY;
			}
			return SEARCH;
		}

		public int getId()
		{
			return id;
		}

	}

	public EntityDivingBabyDragon(World worldIn)
	{
		this(worldIn, 0, 0, 0, 1, 1);
	}

	public EntityDivingBabyDragon(World worldIn, double x, double y, double z, int sumDiff, float avgDiff)
	{
		super(worldIn, x, y, z, sumDiff, avgDiff);
	}

	@Override
	public void initEntityAI()
	{
		this.tasks.addTask(1, new WaitAttack(this));
		this.tasks.addTask(2, new DiveAttack(this));
		this.tasks.addTask(2, new DropPlayer(this));
		this.tasks.addTask(3, new PatrolPoint(this));
		this.targetTasks.addTask(1,
				new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, 10, false, false, null));
	}

	@Override
	public double getMountedYOffset()
	{
		return -1.5;
	}

	@Override
	public boolean shouldDismountInWater(Entity entityIn)
	{
		return false;
	}

	@Override
	public boolean canRiderInteract()
	{
		return true;
	}

	@Override
	protected float getMaxRiseOrFall()
	{
		return this.phase == AttackPhase.DIVE ? 2.0f : super.getMaxRiseOrFall();
	}

	class WaitAttack extends EntityAIBase
	{
		private static final double TAU = 2 * Math.PI;
		private int counter = 0;
		private EntityDivingBabyDragon babyDragon;
		private double height;
		private double radius;
		private double theta;
		private int waitTime;

		private WaitAttack(EntityDivingBabyDragon babyDragon)
		{
			this.babyDragon = babyDragon;
			this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute()
		{
			return this.babyDragon.getAttackTarget() != null && this.babyDragon.phase == AttackPhase.SEARCH;
		}

		@Override
		public boolean shouldContinueExecuting()
		{
			return this.babyDragon.getAttackTarget() != null && this.babyDragon.phase == AttackPhase.SEARCH;
		}

		@Override
		public void startExecuting()
		{
			this.babyDragon.setFast(false);
			height = this.babyDragon.rand.nextDouble() * 10 + 15;
			radius = this.babyDragon.rand.nextDouble() * 5 + 5;
			waitTime = this.babyDragon.rand.nextInt(50);
		}

		@Override
		public void updateTask()
		{
			if (counter++ < 2)
				return;
			counter = 0;

			EntityLivingBase living = this.babyDragon.getAttackTarget();
			Vec3d pos = new Vec3d(living.posX + Math.cos(theta) * radius, living.posY + this.height,
					living.posZ + Math.sin(theta) * radius);
			this.babyDragon.setTargetedPos(pos);
			theta = (theta + 0.2) % (TAU);

			waitTime++;
			if (waitTime > 200)
			{
				waitTime = 0;
				this.babyDragon.phase = AttackPhase.DIVE;
			}
		}

	}

	class DiveAttack extends EntityAIBase
	{
		private EntityDivingBabyDragon babyDragon;
		private double yLevel;
		private int time = 0;

		private DiveAttack(EntityDivingBabyDragon babyDragon)
		{
			this.babyDragon = babyDragon;
			this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute()
		{
			EntityLivingBase living = this.babyDragon.getAttackTarget();
			return living != null && living.isEntityAlive() && this.babyDragon.phase == AttackPhase.DIVE;
		}

		@Override
		public void startExecuting()
		{
			this.babyDragon.setFast(false);
			time = 0;
			this.yLevel = this.babyDragon.posY;
			this.babyDragon.setTargetedPos(this.babyDragon.getAttackTarget().getPositionVector().addVector(0,
					this.babyDragon.getAttackTarget().height, 0));
		}

		@Override
		public boolean shouldContinueExecuting()
		{
			return this.babyDragon.phase == AttackPhase.DIVE && time < 10;
		}

		@Override
		public void updateTask()
		{
			if (this.babyDragon.getTargetedPos().squareDistanceTo(this.babyDragon.getPositionVector()) < 4)
				time++;
			EntityPlayer player = this.babyDragon.world.getNearestAttackablePlayer(babyDragon, 2, 2);
			if (player != null && this.babyDragon.getEntityBoundingBox().intersects(player.getEntityBoundingBox().grow(.5, 0, .5)))
			{
				if (player.isRiding())
				{
					this.babyDragon.phase = AttackPhase.SEARCH;
					return;
				}
				this.babyDragon.phase = AttackPhase.CARRY;
				player.startRiding(this.babyDragon);

				Vec3d dir = new Vec3d(this.babyDragon.posX, 0, this.babyDragon.posZ).normalize();
				dir = dir.scale(150);
				this.babyDragon.setTargetedPos(dir.addVector(0, yLevel, 0));
			}
		}

	}

	class DropPlayer extends EntityAIBase
	{
		private EntityDivingBabyDragon babyDragon;

		private DropPlayer(EntityDivingBabyDragon babyDragon)
		{
			this.babyDragon = babyDragon;
			this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute()
		{
			if (this.babyDragon.phase == AttackPhase.CARRY)
				if (this.babyDragon.isBeingRidden())
					return true;
			this.babyDragon.phase = AttackPhase.SEARCH;
			return false;

		}

		@Override
		public boolean shouldContinueExecuting()
		{
			return this.shouldExecute();
		}

		@Override
		public void startExecuting()
		{
			this.babyDragon.setFast(true);
		}
		
		@Override
		public void updateTask()
		{
			if (this.babyDragon.getTargetedPos().squareDistanceTo(this.babyDragon.getPositionVector()) < 25)
			{
				this.babyDragon.phase = AttackPhase.SEARCH;
				this.babyDragon.removePassengers();
			}
		}
	}

	class PatrolPoint extends EntityAIBase
	{
		private EntityDivingBabyDragon babyDragon;

		private PatrolPoint(EntityDivingBabyDragon babyDragon)
		{
			this.babyDragon = babyDragon;
			this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute()
		{
			return this.babyDragon.getAttackTarget() == null;
		}

		@Override
		public boolean shouldContinueExecuting()
		{
			return this.babyDragon.getAttackTarget() == null;
		}

		@Override
		public void startExecuting()
		{
			this.babyDragon.setFast(true);
		}
		
		@Override
		public void updateTask()
		{
			if (this.babyDragon.home == null)
				this.babyDragon.home = this.babyDragon.getPositionVector();

			if (this.babyDragon.getTargetedPos() == null
					|| this.babyDragon.getTargetedPos().squareDistanceTo(this.babyDragon.getPositionVector()) < 25)
			{
				Vec3d pos = this.babyDragon.home.addVector(this.babyDragon.rand.nextInt(60) - 30,
						this.babyDragon.rand.nextInt(20) - 10, this.babyDragon.rand.nextInt(60) - 30);
				this.babyDragon.setTargetedPos(pos);
			}
		}

	}

	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		compound.setInteger("AttackPhase", this.phase.getId());
		if(this.home != null)
			compound.setTag("HomePos", newDoubleNBTList(new double[] { this.home.x, this.home.y, this.home.z }));
	}

	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		if (compound.hasKey("AttackPhase"))
			this.phase = AttackPhase.fromId(compound.getInteger("AttackPhase"));
		if (compound.hasKey("HomePos"))
		{
			NBTTagList list = compound.getTagList("HomePos", 6);
			this.home = new Vec3d(list.getDoubleAt(0),list.getDoubleAt(1),list.getDoubleAt(2));
		}
	}
}
