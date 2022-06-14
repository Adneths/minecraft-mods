package me.adneths.burnout.blocks;

import me.adneths.burnout.util.FuelDurations;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CarvedPumpkinBlock;
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

public class BurnoutCarvedPumpkinBlock extends CarvedPumpkinBlock implements BurnoutForgeBlock {
	
	private String translationKey;
	
	public BurnoutCarvedPumpkinBlock(Properties properties) {
		super(properties);
		this.setDefaultState(this.getDefaultState().with(BlockStateProperties.LIT, false));
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		return this.universalOnBlockActivated(state, worldIn, pos, player, handIn, hit);
	}
	
	@Override
	public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(BlockStateProperties.LIT);
	}

	@Override
	public int maxFuel() {
		return FuelDurations.Max.TORCH;
	}

	@Override
	public int refillAmount() {
		return FuelDurations.Refill.TORCH;
	}

	@Override
	public boolean isRefillItem(Item item) {
		return item.equals(Items.COAL)||item.equals(Items.CHARCOAL);
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
