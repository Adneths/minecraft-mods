package me.adneths.burnout.blocks;

import me.adneths.burnout.init.ModTileEntity;
import me.adneths.burnout.tile.BurnoutableTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeBlock;

public interface BurnoutForgeBlock extends IForgeBlock {
	
	@Override
	default public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return ModTileEntity.BURNOUTABLE.create();
	}
	
	@Override
	default public boolean hasTileEntity(BlockState state)
	{
		return true;
	}
	
	default public ActionResultType universalOnBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		ItemStack stack = player.getHeldItem(handIn);
		if(this.isRefillItem(stack.getItem()))
		{
			BurnoutableTile tile = ((BurnoutableTile)worldIn.getTileEntity(pos));
			if(this.maxFuel()-tile.getFuel()>this.refillAmount()/2)
			{
				tile.addFuel(this.refillAmount(), this.maxFuel());
				stack.shrink(1);
				worldIn.notifyBlockUpdate(pos, state, state, 2);
				return ActionResultType.CONSUME;
			}
		}
		return ActionResultType.PASS;
	}
	
	public abstract int maxFuel();
	public abstract int refillAmount();
	public abstract boolean isRefillItem(Item item);
	
	public abstract Block setTranslationKey(String namespace, String key);
	
}
