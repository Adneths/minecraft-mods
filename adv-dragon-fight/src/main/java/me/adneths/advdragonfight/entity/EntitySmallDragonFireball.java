package me.adneths.advdragonfight.entity;

import me.adneths.advdragonfight.block.ModBlocks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class EntitySmallDragonFireball extends EntityFireball
{

	public EntitySmallDragonFireball(World worldIn, EntityLivingBase shooter, double accelX, double accelY,
			double accelZ)
	{
		super(worldIn, shooter, accelX, accelY, accelZ);
		setSize(0.3125F, 0.3125F);
	}

	public EntitySmallDragonFireball(World worldIn, double x, double y, double z, double accelX, double accelY,
			double accelZ)
	{
		super(worldIn, x, y, z, accelX, accelY, accelZ);
		setSize(0.3125F, 0.3125F);
	}

	public EntitySmallDragonFireball(World worldIn)
	{
		super(worldIn);
		setSize(0.3125F, 0.3125F);
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		this.ticksExisted++;
		if(ticksExisted > 1200)
			this.setDead();
	}

	@Override
	protected void onImpact(RayTraceResult result)
	{
		if (!this.world.isRemote)
		{
			if (result.entityHit != null)
			{
				if (!result.entityHit.isImmuneToFire())
				{
					if (result.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 10.0F))
					{
						applyEnchantments(this.shootingEntity, result.entityHit);
						result.entityHit.setFire(20);
					}
				}
			}
			
			boolean flag1 = true;
			if (this.shootingEntity != null && this.shootingEntity instanceof net.minecraft.entity.EntityLiving)
				flag1 = ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
			if (flag1)
			{
				Vec3d vec = result.hitVec;
				this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.8f+this.rand.nextFloat()*0.6f, 0.8f+this.rand.nextFloat()*0.4f, false);
				for(int i = -5; i < 6; i++)
					for(int j = -5; j < 6; j++)
						for(int k = -5; k < 6; k++)
						{
							if(i*i+j*j+k*k > 24)
								continue;
							BlockPos pos = new BlockPos((int)vec.x+i, (int)vec.y+j, (int)vec.z+k);
							if (this.world.isAirBlock(pos))
								this.world.setBlockState(pos, ModBlocks.dragonFire.getDefaultState());
						}	
			}
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
}
