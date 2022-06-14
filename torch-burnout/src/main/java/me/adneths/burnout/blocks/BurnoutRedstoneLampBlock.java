package me.adneths.burnout.blocks;

import me.adneths.burnout.util.FuelDurations;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class BurnoutRedstoneLampBlock extends RedstoneLampBlock implements BurnoutForgeBlock {
	
	private String translationKey;

	public BurnoutRedstoneLampBlock(Properties properties) {
		super(properties);
		this.setDefaultState(this.getDefaultState().with(BlockStateProperties.LIT, false));
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
	}
	
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		ActionResultType ret = this.universalOnBlockActivated(state, worldIn, pos, player, handIn, hit);
		worldIn.setBlockState(pos, state.getBlockState().with(LIT, false));
		return ret;
	}
	
	@Override
	public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
	}
	
	@Override
	public int maxFuel() {
		return FuelDurations.Max.GLOWSTONE;
	}

	@Override
	public int refillAmount() {
		return FuelDurations.Refill.GLOWSTONE;
	}

	@Override
	public boolean isRefillItem(Item item) {
		return item.equals(Items.GLOWSTONE_DUST);
	}
	
	@Override
	public String getTranslationKey()
	{
		return this.translationKey;
	}

	@Override
	public Block setTranslationKey(String namespace, String key) {
		this.translationKey = namespace+":"+key;
		return this;
	}
	
}
