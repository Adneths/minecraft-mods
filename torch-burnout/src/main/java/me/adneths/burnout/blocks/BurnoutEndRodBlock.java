package me.adneths.burnout.blocks;

import java.util.Random;

import me.adneths.burnout.util.FuelDurations;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.EndRodBlock;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BurnoutEndRodBlock extends EndRodBlock implements BurnoutForgeBlock {
	
	private String translationKey;

	public BurnoutEndRodBlock(Properties builder) {
		super(builder);
		this.setDefaultState(this.getDefaultState().with(BlockStateProperties.LIT, false));
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(stateIn.get(BlockStateProperties.LIT))
			super.animateTick(stateIn, worldIn, pos, rand);
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
		return FuelDurations.Max.ENDROD;
	}

	@Override
	public int refillAmount() {
		return FuelDurations.Refill.ENDROD;
	}

	@Override
	public boolean isRefillItem(Item item) {
		return item.equals(Items.BLAZE_ROD);
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
