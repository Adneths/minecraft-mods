package me.adneths.redstone_repair.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;

public class BrokenDropperBlock extends RepairableBlock {

	public static final DirectionProperty FACING = DirectionalBlock.FACING;

	public BrokenDropperBlock(Properties properties) {
		super(properties);
	}

	@Override
	public int repairCost(BlockState current) {
		return 4;
	}

	@Override
	public Item getRepairMaterial(BlockState current) {
		return Items.COBBLESTONE;
	}

	@Override
	public BlockState repairedState(BlockState current) {
		return Blocks.DROPPER.getDefaultState().with(FACING, current.get(FACING));
	}
	
	@Override
	public BlockState randomBreak(BlockState pre) {
		return this.getDefaultState().with(FACING, pre.get(FACING));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

}
