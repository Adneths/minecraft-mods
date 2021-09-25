package me.adneths.burnout.blocks;

import java.util.Random;

import me.adneths.burnout.util.FuelDurations;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BurnoutWallTorchBlock extends WallTorchBlock implements BurnoutForgeBlock {
	
	private String translationKey;
	
	public BurnoutWallTorchBlock(Properties properties, IParticleData particleData) {
		super(properties, particleData);
		this.setDefaultState(this.getDefaultState().with(BlockStateProperties.LIT, false));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(stateIn.get(BlockStateProperties.LIT))
			super.animateTick(stateIn, worldIn, pos, rand);
	}
	  
	public BlockState rotate(BlockState state, Rotation rot) {
		return (BlockState)state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
	}
	
	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)));
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
