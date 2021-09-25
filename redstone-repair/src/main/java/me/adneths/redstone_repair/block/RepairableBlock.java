package me.adneths.redstone_repair.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public abstract class RepairableBlock extends Block {

	public RepairableBlock(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if(!player.abilities.allowEdit)
			return ActionResultType.PASS;
		ItemStack stack = player.getHeldItem(handIn);
		ITag<Item> tag = getRepairMaterialTag(state);
		if ((tag == null ? stack.getItem().equals(getRepairMaterial(state)) : tag.contains(stack.getItem()))
				&& stack.getCount() >= repairCost(state)) {
			if (!player.isCreative())
				stack.shrink(repairCost(state));
			worldIn.setBlockState(pos, repairedState(state));
			return ActionResultType.CONSUME;
		}
		return ActionResultType.PASS;
	}

	public ITag<Item> getRepairMaterialTag(BlockState current) {
		return null;
	}
	
	public abstract BlockState randomBreak(BlockState pre);
	
	public abstract int repairCost(BlockState current);

	public abstract Item getRepairMaterial(BlockState current);

	public abstract BlockState repairedState(BlockState current);
	
	public static <T> T getRandom(T[] arr)
	{
		return arr[(int) (arr.length*Math.random())];
	}

}
