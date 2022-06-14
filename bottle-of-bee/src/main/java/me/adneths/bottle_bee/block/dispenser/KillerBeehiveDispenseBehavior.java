package me.adneths.bottle_bee.block.dispenser;

import me.adneths.bottle_bee.block.KillerBeehiveBlock;
import me.adneths.bottle_bee.init.ModContents;
import me.adneths.bottle_bee.tile.KillerBeehiveTile;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.BeehiveDispenseBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.IShearable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

@SuppressWarnings("deprecation")
public class KillerBeehiveDispenseBehavior extends BeehiveDispenseBehavior {

	@Override
	protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
		ServerWorld serverWorld = source.getWorld();
		if (!serverWorld.isRemote()) {
			BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
			setSuccessful((shearComb(serverWorld, blockpos) || shear(serverWorld, blockpos)));
			if (isSuccessful() && stack.attemptDamageItem(1, serverWorld.getRandom(), (ServerPlayerEntity) null))
				stack.setCount(0);
		}
		return stack;
	}

	private static boolean shearComb(ServerWorld world, BlockPos pos) {
		BlockState blockstate = world.getBlockState(pos);
		if (blockstate.isIn(BlockTags.BEEHIVES)) {
			int i = blockstate.get(BeehiveBlock.HONEY_LEVEL).intValue();
			if (i >= 5) {
				world.playSound((PlayerEntity) null, pos, SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1.0F,
						1.0F);
				BeehiveBlock.dropHoneyComb((World) world, pos);
				((BeehiveBlock) blockstate.getBlock()).takeHoney(world, blockstate, pos, (PlayerEntity) null,
						BeehiveTileEntity.State.BEE_RELEASED);
				return true;
			}
		}
		else if(blockstate.getBlock().equals(ModContents.Blocks.KILLER_BEE_NEST))
		{
			int i = blockstate.get(KillerBeehiveBlock.HONEY_LEVEL).intValue();
			if (i >= 5) {
				world.playSound((PlayerEntity) null, pos, SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1.0F,
						1.0F);
				KillerBeehiveBlock.dropHoneyComb((World) world, pos);
				((KillerBeehiveBlock) blockstate.getBlock()).takeHoney(world, blockstate, pos, (PlayerEntity) null,
						KillerBeehiveTile.State.BEE_RELEASED);
				return true;
			}
		}
		return false;
	}
	
	private static boolean shear(ServerWorld world, BlockPos pos) {
		for (LivingEntity livingentity : world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos),
				EntityPredicates.NOT_SPECTATING)) {
			if (livingentity instanceof IShearable) {
				IShearable ishearable = (IShearable) livingentity;
				if (ishearable.isShearable()) {
					ishearable.shear(SoundCategory.BLOCKS);
					return true;
				}
			}
		}
		return false;
	}

}
