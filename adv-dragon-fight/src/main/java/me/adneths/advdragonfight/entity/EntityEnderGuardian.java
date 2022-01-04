package me.adneths.advdragonfight.entity;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityEnderGuardian extends EntityEnderman
{

	private BlockPos preWaterPos = null;
	private BlockPos waterPos = null;
	private boolean nearWater = false;

	public EntityEnderGuardian(World worldIn)
	{
		super(worldIn);
	}

	protected void initEntityAI()
	{
		this.tasks.addTask(0, new FindLand(this));
		this.tasks.addTask(0, new SearchWater(this));
		this.tasks.addTask(1, new FindWater(this));
		this.tasks.addTask(2, new AITakeBlock(this));
		this.tasks.addTask(3, new EntityAIMoveTowardsTarget(this, 1, 48));
		this.tasks.addTask(4, new EntityAIWanderAvoidWater(this, 1.0D, 0.0F));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, false));
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source)
	{
		return source.getTrueSource() instanceof EntityDragon || source == DamageSource.FALL;
	}

	public boolean teleportAboveWithBlock(BlockPos pos)
	{
		double d0 = this.posX;
		double d1 = this.posY;
		double d2 = this.posZ;
		this.posX = pos.getX() + 0.5;
		this.posY = pos.getY() + 1.1;
		this.posZ = pos.getZ() + 0.5;
		boolean flag = false;
		BlockPos blockpos = new BlockPos(this);
		World world = this.world;
		Random random = this.getRNG();

		if (world.isBlockLoaded(blockpos))
		{
			this.setPositionAndUpdate(this.posX, this.posY, this.posZ);

			if (world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty()
					&& !world.containsAnyLiquid(this.getEntityBoundingBox()))
			{
				flag = true;
			}
		}

		if (!flag)
		{
			this.setPositionAndUpdate(d0, d1, d2);
			return false;
		}
		else
		{
			for (int j = 0; j < 128; ++j)
			{
				double d6 = (double) j / 127.0D;
				float f = (random.nextFloat() - 0.5F) * 0.2F;
				float f1 = (random.nextFloat() - 0.5F) * 0.2F;
				float f2 = (random.nextFloat() - 0.5F) * 0.2F;
				double d3 = d0 + (this.posX - d0) * d6 + (random.nextDouble() - 0.5D) * (double) this.width * 2.0D;
				double d4 = d1 + (this.posY - d1) * d6 + random.nextDouble() * (double) this.height;
				double d5 = d2 + (this.posZ - d2) * d6 + (random.nextDouble() - 0.5D) * (double) this.width * 2.0D;
				world.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, (double) f, (double) f1, (double) f2);
			}
			this.getNavigator().clearPath();

			this.world.setBlockState(pos, this.getHeldBlockState());
			this.setHeldBlockState(null);

			this.world.playSound(null, this.prevPosX, this.prevPosY, this.prevPosZ,
					SoundEvents.ENTITY_ENDERMEN_TELEPORT, getSoundCategory(), 1.0F, 1.0F);
			playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);

			return true;
		}
	}

	static class FindLand extends EntityAIBase
	{
		private final EntityEnderGuardian enderman;

		protected FindLand(EntityEnderGuardian enderman)
		{
			this.enderman = enderman;
		}

		@Override
		public boolean shouldExecute()
		{
			return enderman.nearWater && this.enderman.rand.nextInt(5) == 0;
		}

		@Override
		public void startExecuting()
		{
			BlockPos pos = this.enderman.getPosition().add(enderman.rand.nextInt(33) - 16, enderman.rand.nextInt(17),
					enderman.rand.nextInt(33) - 16);
			if (this.enderman.attemptTeleport(pos.getX(), pos.getY(), pos.getZ()))
			{
				enderman.world.playSound((EntityPlayer) null, enderman.prevPosX, enderman.prevPosY, enderman.prevPosZ,
						SoundEvents.ENTITY_ENDERMEN_TELEPORT, enderman.getSoundCategory(), 1.0F, 1.0F);
				enderman.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);

				enderman.nearWater = enderman.isNearWater(1, 3, 1, 5);
			}
		}

	}

	private boolean isNearWater(int xRange, int yRange, int zRange, int threshold)
	{
		int count = 0;
		for (int i = -xRange; i < xRange + 1; i++)
			for (int j = -yRange; j < yRange + 1; j++)
				for (int k = -zRange; k < zRange + 1; k++)
				{
					if (this.world.getBlockState(this.getPosition().add(i, j, k)).getMaterial().equals(Material.WATER))
						if (++count > threshold)
							return true;
				}
		return false;
	}

	private boolean holdingEndstone()
	{
		return getHeldBlockState() != null && getHeldBlockState().getBlock().equals(Blocks.END_STONE);
	}

	static class SearchWater extends EntityAIBase
	{
		private static final EnumFacing[] ADJS = { EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST,
				EnumFacing.WEST };

		private final EntityEnderGuardian enderman;
		private int distance = 0;

		protected SearchWater(EntityEnderGuardian enderman)
		{
			this.enderman = enderman;
		}

		@Override
		public boolean shouldExecute()
		{
			return enderman.waterPos != null && enderman.holdingEndstone();
		}

		@Override
		public void startExecuting()
		{
			distance = 0;
		}

		@Override
		public void updateTask()
		{
			IBlockState state = enderman.world.getBlockState(enderman.waterPos);
			if (!state.getMaterial().equals(Material.WATER))
			{
				enderman.preWaterPos = enderman.waterPos;
				enderman.waterPos = null;
				return;
			}
			int level = state.getValue(BlockLiquid.LEVEL);
			for (EnumFacing dir : ADJS)
			{
				BlockPos pos = enderman.waterPos.offset(dir);
				IBlockState next = enderman.world.getBlockState(pos);
				if (next.getMaterial().equals(Material.WATER))
					if (next.getValue(BlockLiquid.LEVEL) < level || dir == EnumFacing.UP || enderman.world
							.getBlockState(pos.offset(EnumFacing.UP)).getMaterial().equals(Material.WATER))
					{
						enderman.waterPos = pos;
						break;
					}
			}
			if (++distance > 32 || level == 0)
			{
				if (!enderman.teleportAboveWithBlock(enderman.waterPos))
				{
					if (enderman.getHeldBlockState() == null || enderman.placeBlock())
					{
						BlockPos pos = enderman.waterPos.up();
						for (int i = 0; i < 20; i++)
						{
							if (enderman.attemptTeleport(pos.getX(), pos.getY(), pos.getZ()))
							{
								pos = pos.down();
								state = enderman.world.getBlockState(pos);
								this.enderman.setHeldBlockState(state);
								enderman.world.setBlockToAir(pos);
								break;
							}
							pos = pos.up();
						}
					}
				}
				enderman.preWaterPos = enderman.waterPos;
				enderman.waterPos = null;
				enderman.nearWater = true;
			}
		}
	}

	private boolean placeBlock()
	{
		Random random = this.getRNG();
		World world = this.world;
		BlockPos pos = new BlockPos(MathHelper.floor(this.posX - 1.0D + random.nextDouble() * 2.0D),
				MathHelper.floor(this.posY + random.nextDouble() * 2.0D),
				MathHelper.floor(this.posZ - 1.0D + random.nextDouble() * 2.0D));
		IBlockState stateTarget = world.getBlockState(pos);
		IBlockState stateDown = world.getBlockState(pos.down());
		IBlockState held = this.getHeldBlockState();
		if (this.canPlaceBlock(world, pos, held.getBlock(), stateTarget, stateDown))
		{
			world.setBlockState(pos, held, 3);
			this.setHeldBlockState(null);
			return true;
		}
		return false;
	}

	private boolean canPlaceBlock(World world, BlockPos pos, Block block, IBlockState target, IBlockState targetDown)
	{
		if (!block.canPlaceBlockAt(world, pos))
			return false;
		if (target.getMaterial() != Material.AIR)
			return false;
		if (targetDown.getMaterial() == Material.AIR)
			return false;
		return targetDown.isFullCube();
	}

	static class FindWater extends EntityAIBase
	{
		private final EntityEnderGuardian enderman;

		protected FindWater(EntityEnderGuardian enderman)
		{
			this.enderman = enderman;
		}

		@Override
		public boolean shouldExecute()
		{
			return enderman.holdingEndstone() && enderman.waterPos == null && enderman.rand.nextInt(5) == 0;
		}

		@Override
		public void startExecuting()
		{
			BlockPos pos = enderman.preWaterPos == null ? enderman.getPosition() : enderman.preWaterPos;
			if (enderman.preWaterPos != null)
				enderman.preWaterPos = null;
			for (int i = -3; i < 4; i++)
				for (int j = -3; j < 4; j++)
					for (int k = -3; k < 4; k++)
						if (enderman.world.getBlockState(pos.add(i, j, k)).getMaterial().equals(Material.WATER))
						{
							enderman.waterPos = pos.add(i, j, k);
							return;
						}
			for (int i = 0; i < 10; i++)
			{
				pos = enderman.getPosition().add(enderman.rand.nextInt(33) - 16, 32, enderman.rand.nextInt(33) - 16);
				for (int j = 0; enderman.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && j < 16; j++)
					pos = pos.down();
				if (enderman.world.getBlockState(pos).getMaterial().equals(Material.WATER))
				{
					enderman.waterPos = pos;
					return;
				}
			}
		}

	}

	static class AITakeBlock extends EntityAIBase
	{
		private final EntityEnderGuardian enderman;

		public AITakeBlock(EntityEnderGuardian enderman)
		{
			this.enderman = enderman;
		}

		public boolean shouldExecute()
		{
			return !enderman.holdingEndstone() && !enderman.nearWater && enderman.rand.nextInt(3) == 0;
		}

		public void updateTask()
		{
			if (enderman.getHeldBlockState() != null)
				if (!enderman.placeBlock())
					return;

			Random random = this.enderman.getRNG();
			World world = this.enderman.world;
			int i = MathHelper.floor(this.enderman.posX - 2.0D + random.nextDouble() * 4.0D);
			int j = MathHelper.floor(this.enderman.posY - 1 + random.nextInt(2));
			int k = MathHelper.floor(this.enderman.posZ - 2.0D + random.nextDouble() * 4.0D);
			RayTraceResult raytraceresult = world.rayTraceBlocks(
					new Vec3d((MathHelper.floor(this.enderman.posX) + 0.5F), (j + 0.5F),
							(MathHelper.floor(this.enderman.posZ) + 0.5F)),
					new Vec3d((i + 0.5F), (j + 0.5F), (k + 0.5F)), false, true, false);
			if (raytraceresult != null && raytraceresult.getBlockPos() != null)
			{
				BlockPos blockpos = raytraceresult.getBlockPos();
				IBlockState state = world.getBlockState(blockpos);
				if (state.getBlock().equals(Blocks.END_STONE))
				{
					this.enderman.setHeldBlockState(state);
					world.setBlockToAir(blockpos);
				}
			}
		}
	}

}
