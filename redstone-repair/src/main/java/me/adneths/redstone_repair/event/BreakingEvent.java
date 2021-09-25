package me.adneths.redstone_repair.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import me.adneths.redstone_repair.block.BrokenComparatorBlock;
import me.adneths.redstone_repair.block.BrokenObserverBlock;
import me.adneths.redstone_repair.block.BrokenPistonBlock;
import me.adneths.redstone_repair.block.BrokenRepeaterBlock;
import me.adneths.redstone_repair.block.RepairableBlock;
import me.adneths.redstone_repair.config.Configs;
import me.adneths.redstone_repair.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BreakingEvent {
	
	public static Map<Block,Pair<RepairableBlock,Integer>> breakMap;
	
	private static final Random rnd = new Random(System.currentTimeMillis()*(System.currentTimeMillis()&63));
	
	public static void init()
	{
		breakMap = new HashMap<Block,Pair<RepairableBlock,Integer>>();
		breakMap.put(Blocks.REDSTONE_LAMP, Pair.of(ModBlocks.BROKEN_REDSTONE_LAMP,Configs.REDSTONE_LAMP));
		breakMap.put(Blocks.REDSTONE_WIRE, Pair.of(ModBlocks.BROKEN_REDSTONE_WIRE,Configs.REDSTONE));
		breakMap.put(Blocks.REDSTONE_TORCH, Pair.of(ModBlocks.BROKEN_REDSTONE_TORCH,Configs.REDSTONE_TORCH));
		breakMap.put(Blocks.REDSTONE_WALL_TORCH, Pair.of(ModBlocks.BROKEN_REDSTONE_WALL_TORCH,Configs.REDSTONE_TORCH));
		breakMap.put(Blocks.PISTON, Pair.of(ModBlocks.BROKEN_PISTON,Configs.PISTON));
		breakMap.put(Blocks.STICKY_PISTON, Pair.of(ModBlocks.BROKEN_STICKY_PISTON,Configs.STICKY_PISTON));
		breakMap.put(Blocks.REPEATER, Pair.of(ModBlocks.BROKEN_REPEATER,Configs.REPEATER));
		breakMap.put(Blocks.COMPARATOR, Pair.of(ModBlocks.BROKEN_COMPARATOR,Configs.COMPARATOR));
		breakMap.put(Blocks.DROPPER, Pair.of(ModBlocks.BROKEN_DROPPER,Configs.DROPPER));
		breakMap.put(Blocks.DISPENSER, Pair.of(ModBlocks.BROKEN_DISPENSER,Configs.DISPENSER));
		breakMap.put(Blocks.OBSERVER, Pair.of(ModBlocks.BROKEN_OBSERVER,Configs.OBSERVER));
	}

	@SubscribeEvent
	public static void onRedstoneUpdate(BlockEvent.NeighborNotifyEvent event)
	{
		BlockState state = event.getState();
		Block block = event.getState().getBlock();
		if(breakMap.containsKey(block))
		{
			if(block == Blocks.REDSTONE_LAMP || block == Blocks.REDSTONE_TORCH || block == Blocks.REDSTONE_WALL_TORCH)
			{
				if(state.get(BlockStateProperties.LIT))
					return;
			}
			else if(block == Blocks.REDSTONE_WIRE)
			{
				if(state.get(BlockStateProperties.POWER_0_15) != 0)
					return;
			}
			else if(block == Blocks.PISTON || block == Blocks.STICKY_PISTON)
			{
				if(state.get(BlockStateProperties.EXTENDED))
					return;
			}
			else if(block == Blocks.REPEATER || block == Blocks.COMPARATOR || block == Blocks.OBSERVER)
			{
				if(state.get(BlockStateProperties.POWERED))
					return;
			}
			else if(block == Blocks.DISPENSER || block == Blocks.DROPPER)
			{
				if(!state.get(BlockStateProperties.TRIGGERED))
					return;
			}
		}
		else
			return;
		if(rnd.nextInt(breakMap.get(block).getRight()) == 0)
			breakBlock(event.getWorld(), block, state, event.getPos());
	}
	
	private static void breakBlock(IWorld world, Block block, BlockState state, BlockPos pos)
	{
		RepairableBlock brokenBlock = breakMap.get(block).getLeft();
		BlockState brokenState = brokenBlock.randomBreak(state);
		if(!world.isRemote())
			playSound(world, block, brokenState, pos);
		world.setBlockState(pos, brokenState, 3);
	}
	
	@SuppressWarnings("deprecation")
	private static void playBlock(IWorld world, Block block, BlockState state, BlockPos pos)
	{
		play(world, pos, block.getSoundType(state).getBreakSound());
	}
	
	private static void play(IWorld world, BlockPos pos, SoundEvent sound)
	{
		world.playSound(null, pos, sound, SoundCategory.BLOCKS, (float)(.9 + Math.random() * 0.2), (float)(.9 + Math.random() * 0.2));
	}
	
	private static void playSound(IWorld world, Block originalBlock, BlockState state, BlockPos pos)
	{
		if(originalBlock==Blocks.REDSTONE_TORCH || originalBlock==Blocks.REDSTONE_WALL_TORCH)
		{
			play(world, pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT);
			return;
		}
		else if(originalBlock==Blocks.REPEATER)
		{
			if(BrokenRepeaterBlock.RepeaterPart.isBurn(state.get(BrokenRepeaterBlock.BROKEN_PART)))
			{
				play(world, pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT);
				return;
			}
		}
		else if(originalBlock==Blocks.COMPARATOR)
		{
			BrokenComparatorBlock.ComparatorPart part = state.get(BrokenComparatorBlock.BROKEN_PART);
			if(BrokenComparatorBlock.ComparatorPart.isBurn(part))
			{
				play(world, pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT);
				return;
			}
			else if(part == BrokenComparatorBlock.ComparatorPart.CRYSTAL)
			{
				play(world, pos, SoundEvents.BLOCK_GLASS_BREAK);
				return;
			}
		}
		else if(originalBlock==Blocks.OBSERVER)
		{
			if(state.get(BrokenObserverBlock.BROKEN_PART) == BrokenObserverBlock.ObserverPart.SENSOR)
			{
				play(world, pos, SoundEvents.BLOCK_GLASS_BREAK);
				return;
			}
		}
		else if(originalBlock==Blocks.STICKY_PISTON)
		{
			if(state.get(BrokenPistonBlock.BROKEN_PART) == BrokenPistonBlock.PistonPart.SLIME)
			{
				play(world, pos, SoundEvents.BLOCK_SLIME_BLOCK_STEP);
				return;
			}
		}
		playBlock(world, originalBlock, state, pos);
	}
}
