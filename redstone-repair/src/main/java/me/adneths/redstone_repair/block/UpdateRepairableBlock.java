package me.adneths.redstone_repair.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public abstract class UpdateRepairableBlock extends RepairableBlock {

	public UpdateRepairableBlock(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		ActionResultType ret = super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
		if(ret == ActionResultType.CONSUME)
			worldIn.neighborChanged(pos, null, pos);
		return ret;
	}
	
}
