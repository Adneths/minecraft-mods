package me.adneths.advdragonfight.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDragonFire extends BlockFire
{
	@Override
	public boolean isBurning(IBlockAccess world, BlockPos pos)
	{
		return true;
	}
	
	private static final DamageSource DRAGON_FIRE = new DamageSource("dragon_fire");
	
	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
	{
		if(entityIn instanceof EntityPlayer && entityIn.ticksExisted % 5 == 0)
			entityIn.attackEntityFrom(DRAGON_FIRE, 2);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if (worldIn.getGameRules().getBoolean("doFireTick"))
		{
			if (!worldIn.isAreaLoaded(pos, 2))
				return;
			if (!canPlaceBlockAt(worldIn, pos))
				worldIn.setBlockToAir(pos);
			Block block = worldIn.getBlockState(pos.down()).getBlock();
			boolean flag = block.isFireSource(worldIn, pos.down(), EnumFacing.UP);
			int i = ((Integer) state.getValue(AGE)).intValue();
			if (!flag && worldIn.isRaining() && canDie(worldIn, pos) && rand.nextFloat() < 0.2F + i * 0.03F)
			{
				worldIn.setBlockToAir(pos);
			}
			else
			{
				if (i < 15)
				{
					state = state.withProperty(AGE, Integer.valueOf(i + rand.nextInt(3) / 2));
					worldIn.setBlockState(pos, state, 4);
				}
				worldIn.scheduleUpdate(pos, this, tickRate(worldIn) + rand.nextInt(10));
				if (!flag)
				{
					if (!canNeighborCatchFire(worldIn, pos))
					{
						if (!worldIn.getBlockState(pos.down()).isSideSolid((IBlockAccess) worldIn, pos.down(),
								EnumFacing.UP) || i > 3)
							worldIn.setBlockToAir(pos);
						return;
					}
					if (!canCatchFire((IBlockAccess) worldIn, pos.down(), EnumFacing.UP) && i == 15
							&& rand.nextInt(30) == 0)
					{
						worldIn.setBlockToAir(pos);
						return;
					}
				}
			}
		}
	}

	private boolean canNeighborCatchFire(World worldIn, BlockPos pos)
	{
		for (EnumFacing enumfacing : EnumFacing.values())
		{
			if (canCatchFire((IBlockAccess) worldIn, pos.offset(enumfacing), enumfacing.getOpposite()))
				return true;
		}
		return false;
	}
}
