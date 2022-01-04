package me.adneths.advdragonfight.entity;

import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityBabyDragonBase extends EntityMob implements IMob, IEntityMultiPart, DifficultyScaled
{

	public MultiPartEntityPart[] dragonPartArray;

	public MultiPartEntityPart dragonPartHead = new MultiPartEntityPart(this, "head", 6.0F, 6.0F);

	public MultiPartEntityPart dragonPartNeck = new MultiPartEntityPart(this, "neck", 6.0F, 6.0F);

	public MultiPartEntityPart dragonPartBody = new MultiPartEntityPart(this, "body", 8.0F, 8.0F);

	public MultiPartEntityPart dragonPartTail1 = new MultiPartEntityPart(this, "tail", 4.0F, 4.0F);

	public MultiPartEntityPart dragonPartTail2 = new MultiPartEntityPart(this, "tail", 4.0F, 4.0F);

	public MultiPartEntityPart dragonPartTail3 = new MultiPartEntityPart(this, "tail", 4.0F, 4.0F);

	public MultiPartEntityPart dragonPartWing1 = new MultiPartEntityPart(this, "wing", 4.0F, 4.0F);

	public MultiPartEntityPart dragonPartWing2 = new MultiPartEntityPart(this, "wing", 4.0F, 4.0F);

	private int growlTime;

	private Vec3d targetedPos;

	private boolean fast = false;
	
	private int sumDiff;
	private float avgDiff;

	public EntityBabyDragonBase(World worldIn, double x, double y, double z, int sumDiff, float avgDiff)
	{
		super(worldIn);
		this.dragonPartArray = new MultiPartEntityPart[] { this.dragonPartHead, this.dragonPartNeck,
				this.dragonPartBody, this.dragonPartTail1, this.dragonPartTail2, this.dragonPartTail3,
				this.dragonPartWing1, this.dragonPartWing2 };
		this.setSize(2f, 1f);
		
		this.setSumDifficulty(sumDiff);
		this.setAverageDifficulty(avgDiff);
		this.updateDifficulty();
		
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48);

		this.noClip = true;
		this.isImmuneToFire = true;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		setNoGravity(true);
	}

	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		if (this.world.isRemote)
		{
			setHealth(getHealth());
			if (!isSilent())
			{
				float f = MathHelper.cos(this.animTime * 6.2831855F);
				float f1 = MathHelper.cos(this.prevAnimTime * 6.2831855F);
				if (f1 <= -0.3F && f >= -0.3F)
					this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ENDERDRAGON_FLAP,
							getSoundCategory(), 0.3F, 0.9F + this.rand.nextFloat() * 0.3F, false);
				if (--this.growlTime < 0)
				{
					this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ENDERDRAGON_GROWL,
							getSoundCategory(), 0.1F, 1.5F + this.rand.nextFloat() * 0.3F, false);
					this.growlTime = 600 + this.rand.nextInt(500);
				}
			}
		}
		this.prevAnimTime = this.animTime;

		float f11 = 0.2F / (MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0F + 1.0F);
		if (Math.abs(this.motionY) < 0.0001)
			this.animTime += 0.1;
		else
			this.animTime += f11 * Math.pow(2.0D, this.motionY) * 0.5;
		this.rotationYaw = MathHelper.wrapDegrees(this.rotationYaw);

		if (this.ringBufferIndex < 0)
			for (int i = 0; i < this.ringBuffer.length; i++)
			{
				this.ringBuffer[i][0] = this.rotationYaw;
				this.ringBuffer[i][1] = this.posY;
			}
		if (++this.ringBufferIndex == this.ringBuffer.length)
			this.ringBufferIndex = 0;
		this.ringBuffer[this.ringBufferIndex][0] = this.rotationYaw;
		this.ringBuffer[this.ringBufferIndex][1] = this.posY;

		if (this.world.isRemote)
		{
			if (this.newPosRotationIncrements > 0)
			{
				double d5 = this.posX + (this.interpTargetX - this.posX) / this.newPosRotationIncrements;
				double d0 = this.posY + (this.interpTargetY - this.posY) / this.newPosRotationIncrements;
				double d1 = this.posZ + (this.interpTargetZ - this.posZ) / this.newPosRotationIncrements;
				double d2 = MathHelper.wrapDegrees(this.interpTargetYaw - this.rotationYaw);
				this.rotationYaw = (float) (this.rotationYaw + d2 / this.newPosRotationIncrements);
				this.rotationPitch = (float) (this.rotationPitch
						+ (this.interpTargetPitch - this.rotationPitch) / this.newPosRotationIncrements);
				this.newPosRotationIncrements--;
				setPosition(d5, d0, d1);
				setRotation(this.rotationYaw, this.rotationPitch);
			}
		}
		else
		{
			Vec3d vec3d = this.targetedPos;
			if (vec3d != null)
			{
				double d6 = vec3d.x - this.posX;
				double d7 = vec3d.y - this.posY;
				double d8 = vec3d.z - this.posZ;
				double d3 = d6 * d6 + d7 * d7 + d8 * d8;
				float f5 = getMaxRiseOrFall();
				d7 = MathHelper.clamp(d7 / MathHelper.sqrt(d6 * d6 + d8 * d8), -f5, f5);
				this.motionY += d7 * 0.10000000149011612D;
				this.rotationYaw = MathHelper.wrapDegrees(this.rotationYaw);
				double d4 = MathHelper.clamp(
						MathHelper
								.wrapDegrees(180.0D - MathHelper.atan2(d6, d8) * 57.29577951308232D - this.rotationYaw),
						-50.0D, 50.0D);
				Vec3d vec3d1 = (new Vec3d(vec3d.x - this.posX, vec3d.y - this.posY, vec3d.z - this.posZ)).normalize();
				Vec3d vec3d2 = (new Vec3d(MathHelper.sin(this.rotationYaw * 0.017453292F), this.motionY,
						-MathHelper.cos(this.rotationYaw * 0.017453292F))).normalize();
				float f7 = Math.max(((float) vec3d2.dotProduct(vec3d1) + 0.5F) / 1.5F, 0.0F);
				this.randomYawVelocity *= 0.8F;
				this.randomYawVelocity = (float) (this.randomYawVelocity + d4 * this.getYawFactor());
				this.rotationYaw += this.randomYawVelocity * 0.1F;
				float f8 = (float) (2.0D / (d3 + 1.0D));
//				float f9 = 0.06F;
				moveRelative(0.0F, 0.0F, -1.0F, 0.06F * (f7 * f8 + 1.0F - f8));

				if(fast)
					move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

				Vec3d vec3d3 = (new Vec3d(this.motionX, this.motionY, this.motionZ)).normalize();
				float f10 = ((float) vec3d3.dotProduct(vec3d2) + 1.0F) / 2.0F;
				f10 = 0.8F + 0.15F * f10;
				this.motionX *= f10;
				this.motionZ *= f10;
				this.motionY *= 0.9100000262260437D;
			}
		}
		this.renderYawOffset = this.rotationYaw;
		this.dragonPartHead.width = 1.0F;
		this.dragonPartHead.height = 1.0F;
		this.dragonPartNeck.width = 3.0F;
		this.dragonPartNeck.height = 3.0F;
		this.dragonPartTail1.width = 2.0F;
		this.dragonPartTail1.height = 2.0F;
		this.dragonPartTail2.width = 2.0F;
		this.dragonPartTail2.height = 2.0F;
		this.dragonPartTail3.width = 2.0F;
		this.dragonPartTail3.height = 2.0F;
		this.dragonPartBody.height = 3.0F;
		this.dragonPartBody.width = 5.0F;
		this.dragonPartWing1.height = 2.0F;
		this.dragonPartWing1.width = 4.0F;
		this.dragonPartWing2.height = 3.0F;
		this.dragonPartWing2.width = 4.0F;
		Vec3d[] avec3d = new Vec3d[this.dragonPartArray.length];
		for (int j = 0; j < this.dragonPartArray.length; j++)
			avec3d[j] = new Vec3d((this.dragonPartArray[j]).posX, (this.dragonPartArray[j]).posY,
					(this.dragonPartArray[j]).posZ);
		float f14 = (float) (getMovementOffsets(5, 1.0F)[1] - getMovementOffsets(10, 1.0F)[1]) * 10.0F * 0.017453292F;
		float f16 = MathHelper.cos(f14);
		float f2 = MathHelper.sin(f14);
		float f17 = this.rotationYaw * 0.017453292F;
		float f3 = MathHelper.sin(f17);
		float f18 = MathHelper.cos(f17);
		this.dragonPartBody.onUpdate();
		this.dragonPartBody.setLocationAndAngles(this.posX + (f3 * 0.5F), this.posY, this.posZ - (f18 * 0.5F), 0.0F,
				0.0F);
		this.dragonPartWing1.onUpdate();
		this.dragonPartWing1.setLocationAndAngles(this.posX + (f18 * 4.5F), this.posY + 2.0D, this.posZ + (f3 * 4.5F),
				0.0F, 0.0F);
		this.dragonPartWing2.onUpdate();
		this.dragonPartWing2.setLocationAndAngles(this.posX - (f18 * 4.5F), this.posY + 2.0D, this.posZ - (f3 * 4.5F),
				0.0F, 0.0F);

		double[] adouble = getMovementOffsets(5, 1.0F);
		float f19 = MathHelper.sin(this.rotationYaw * 0.017453292F - this.randomYawVelocity * 0.01F);
		float f4 = MathHelper.cos(this.rotationYaw * 0.017453292F - this.randomYawVelocity * 0.01F);
		this.dragonPartHead.onUpdate();
		this.dragonPartNeck.onUpdate();
		float f20 = getHeadYOffset(1.0F);
		this.dragonPartHead.setLocationAndAngles(this.posX + (f19 * 6.5F * f16), this.posY + f20 + (f2 * 6.5F),
				this.posZ - (f4 * 6.5F * f16), 0.0F, 0.0F);
		this.dragonPartNeck.setLocationAndAngles(this.posX + (f19 * 5.5F * f16), this.posY + f20 + (f2 * 5.5F),
				this.posZ - (f4 * 5.5F * f16), 0.0F, 0.0F);
		for (int k = 0; k < 3; k++)
		{
			MultiPartEntityPart multipartentitypart = null;
			if (k == 0)
				multipartentitypart = this.dragonPartTail1;
			if (k == 1)
				multipartentitypart = this.dragonPartTail2;
			if (k == 2)
				multipartentitypart = this.dragonPartTail3;
			double[] adouble1 = getMovementOffsets(12 + k * 2, 1.0F);
			float f21 = this.rotationYaw * 0.017453292F + simplifyAngle(adouble1[0] - adouble[0]) * 0.017453292F;
			float f6 = MathHelper.sin(f21);
			float f22 = MathHelper.cos(f21);
//			float f23 = 1.5F;
			float f24 = (k + 1) * 2.0F;
			multipartentitypart.onUpdate();
			multipartentitypart.setLocationAndAngles(this.posX - ((f3 * 1.5F + f6 * f24) * f16),
					this.posY + adouble1[1] - adouble[1] - ((f24 + 1.5F) * f2) + 1.5D,
					this.posZ + ((f18 * 1.5F + f22 * f24) * f16), 0.0F, 0.0F);
		}
		for (int l = 0; l < this.dragonPartArray.length; l++)
		{
			(this.dragonPartArray[l]).prevPosX = (avec3d[l]).x;
			(this.dragonPartArray[l]).prevPosY = (avec3d[l]).y;
			(this.dragonPartArray[l]).prevPosZ = (avec3d[l]).z;
		}
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source)
	{
		return super.isEntityInvulnerable(source) || source.getTrueSource() instanceof EntityDragon;
	}

	protected float getMaxRiseOrFall()
	{
		return 0.6f;
	}

	protected Vec3d getTargetedPos()
	{
		return this.targetedPos;
	}

	protected void setTargetedPos(Vec3d vec)
	{
		this.targetedPos = vec;
	}

	public float getYawFactor()
	{
		float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) + 1.0F;
		float f1 = Math.min(f, 40.0F);
		return 0.7F / f1 / f;
	}

	private float simplifyAngle(double p_70973_1_)
	{
		return (float) MathHelper.wrapDegrees(p_70973_1_);
	}

	private float getHeadYOffset(float p_184662_1_)
	{
		return (float) (getMovementOffsets(5, 1.0F)[1] - getMovementOffsets(0, 1.0F)[1]);
	}

	public double[][] ringBuffer = new double[64][3];

	public int ringBufferIndex = -1;

	public float prevAnimTime;
	public float animTime;

	public double[] getMovementOffsets(int p_70974_1_, float p_70974_2_)
	{
		if (getHealth() <= 0.0F)
			p_70974_2_ = 0.0F;
		p_70974_2_ = 1.0F - p_70974_2_;
		int i = this.ringBufferIndex - p_70974_1_ & 0x3F;
		int j = this.ringBufferIndex - p_70974_1_ - 1 & 0x3F;
		double[] adouble = new double[3];
		double d0 = this.ringBuffer[i][0];
		double d1 = MathHelper.wrapDegrees(this.ringBuffer[j][0] - d0);
		adouble[0] = d0 + d1 * p_70974_2_;
		d0 = this.ringBuffer[i][1];
		d1 = this.ringBuffer[j][1] - d0;
		adouble[1] = d0 + d1 * p_70974_2_;
		adouble[2] = this.ringBuffer[i][2] + (this.ringBuffer[j][2] - this.ringBuffer[i][2]) * p_70974_2_;
		return adouble;
	}

	@SideOnly(Side.CLIENT)
	public float getHeadPartYOffset(int i, double[] arr1, double[] arr2)
	{
		return (float) (i == 6 ? 0 : arr2[1] - arr1[1]);
	}

	@Override
	public World getWorld()
	{
		return this.world;
	}

	@Override
	public boolean attackEntityFromPart(MultiPartEntityPart dragonPart, DamageSource source, float damage)
	{
		return false;
	}
	
	protected void setFast(boolean fast)
	{
		this.fast = fast;
	}
	
	protected boolean isFast()
	{
		return fast;
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
		float health = Math.min(50 + 10 * (int) avgDiff/2, 200);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
		this.setHealth(this.getMaxHealth());
	}
}
