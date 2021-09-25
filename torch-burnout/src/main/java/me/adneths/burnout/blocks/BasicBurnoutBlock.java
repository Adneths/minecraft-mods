package me.adneths.burnout.blocks;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class BasicBurnoutBlock extends Block implements BurnoutForgeBlock {
	
	private int maxFuel;
	private int refillAmount;
	private Predicate<Item> validItem;
	
	private String translationKey;
	
	public BasicBurnoutBlock(Properties properties, int maxFuel, int refillAmount, Predicate<Item> validItem) {
		super(properties);
		this.maxFuel = maxFuel;
		this.refillAmount = refillAmount;
		this.validItem = validItem;
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
		return this.maxFuel;
	}

	@Override
	public int refillAmount() {
		return this.refillAmount;
	}

	@Override
	public boolean isRefillItem(Item item) {
		return validItem.apply(item);
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
