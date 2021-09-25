package me.adneths.redstone_repair.block;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import me.adneths.redstone_repair.init.ModBlockTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BrokenRedstoneWireBlock extends UpdateRepairableBlock {

	public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.REDSTONE_NORTH;
	public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.REDSTONE_EAST;
	public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.REDSTONE_SOUTH;
	public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.REDSTONE_WEST;
	public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
	public static final Map<Direction, EnumProperty<RedstoneSide>> FACING_PROPERTY_MAP = Maps.newEnumMap(ImmutableMap
			.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));

	private static final VoxelShape BASE_SHAPE = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D);
	private static final Map<Direction, VoxelShape> SIDE_TO_SHAPE = Maps
			.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D),
					Direction.SOUTH, Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Direction.EAST,
					Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Direction.WEST,
					Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D)));
	private static final Map<Direction, VoxelShape> SIDE_TO_ASCENDING_SHAPE = Maps.newEnumMap(ImmutableMap.of(
			Direction.NORTH,
			VoxelShapes.or(
					SIDE_TO_SHAPE.get(Direction.NORTH), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 16.0D, 1.0D)),
			Direction.SOUTH,
			VoxelShapes.or(
					SIDE_TO_SHAPE.get(Direction.SOUTH), Block.makeCuboidShape(3.0D, 0.0D, 15.0D, 13.0D, 16.0D, 16.0D)),
			Direction.EAST,
			VoxelShapes.or(SIDE_TO_SHAPE.get(Direction.EAST),
					Block.makeCuboidShape(15.0D, 0.0D, 3.0D, 16.0D, 16.0D, 13.0D)),
			Direction.WEST, VoxelShapes.or(SIDE_TO_SHAPE.get(Direction.WEST),
					Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 1.0D, 16.0D, 13.0D))));

	private final Map<BlockState, VoxelShape> stateToShapeMap = Maps.newHashMap();
	private final BlockState sideBaseState;

	public BrokenRedstoneWireBlock(AbstractBlock.Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, RedstoneSide.NONE)
				.with(EAST, RedstoneSide.NONE).with(SOUTH, RedstoneSide.NONE).with(WEST, RedstoneSide.NONE));
		this.sideBaseState = this.getDefaultState().with(NORTH, RedstoneSide.SIDE).with(EAST, RedstoneSide.SIDE)
				.with(SOUTH, RedstoneSide.SIDE).with(WEST, RedstoneSide.SIDE);
		for (BlockState blockstate : this.getStateContainer().getValidStates())
			this.stateToShapeMap.put(blockstate, this.getShapeForState(blockstate));
	}

	@Override
	public int repairCost(BlockState current) {
		return 1;
	}

	@Override
	public Item getRepairMaterial(BlockState current) {
		return Items.REDSTONE;
	}

	@Override
	public BlockState repairedState(BlockState current) {
		return Blocks.REDSTONE_WIRE.getDefaultState().with(NORTH, current.get(NORTH)).with(SOUTH, current.get(SOUTH))
				.with(EAST, current.get(EAST)).with(WEST, current.get(WEST));
	}

	@Override
	public BlockState randomBreak(BlockState pre) {
		return this.getDefaultState();
	}

	private VoxelShape getShapeForState(BlockState state) {
		VoxelShape voxelshape = BASE_SHAPE;

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			RedstoneSide redstoneside = state.get(FACING_PROPERTY_MAP.get(direction));
			if (redstoneside == RedstoneSide.SIDE) {
				voxelshape = VoxelShapes.or(voxelshape, SIDE_TO_SHAPE.get(direction));
			} else if (redstoneside == RedstoneSide.UP) {
				voxelshape = VoxelShapes.or(voxelshape, SIDE_TO_ASCENDING_SHAPE.get(direction));
			}
		}

		return voxelshape;
	}

	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return this.stateToShapeMap.get(state);
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos blockpos = pos.down();
		BlockState blockstate = worldIn.getBlockState(blockpos);
		return canPlaceOnTopOf((IBlockReader) worldIn, blockpos, blockstate);
	}

	private boolean canPlaceOnTopOf(IBlockReader reader, BlockPos pos, BlockState state) {
		return (state.isSolidSide(reader, pos, Direction.UP) || state.isIn(Blocks.HOPPER));
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getUpdatedState(context.getWorld(), this.getDefaultState(), context.getPos());
	}

	private BlockState getUpdatedState(World world, BlockState state, BlockPos pos) {
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			state = state.with(FACING_PROPERTY_MAP.get(dir), this.getConnectionForDirection(world, pos, dir,
					world.getBlockState(pos.up()).isNormalCube(world, pos)));
		}
		state = this.fixInvalid(state);
		return state;
	}

	private BlockState fixInvalid(BlockState state) {
		if (areAllSidesInvalid(state))
			return this.sideBaseState;
		Direction side = null;
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			if (state.get(FACING_PROPERTY_MAP.get(dir)) != RedstoneSide.NONE)
				if (side == null)
					side = dir;
				else
					return state;
		}
		return state.with(FACING_PROPERTY_MAP.get(side.getOpposite()), RedstoneSide.SIDE);
	}

	private RedstoneSide getConnectionForDirection(World world, BlockPos pos, Direction dir, boolean topBlock) {
		BlockPos offPos = pos.offset(dir);
		if (canConnectTo(world.getBlockState(offPos), world, offPos, dir))
			return RedstoneSide.SIDE;
		if (world.getBlockState(offPos).isNormalCube(world, offPos)) {
			if (!topBlock && world.getBlockState(offPos.up()).isIn(ModBlockTags.REDSTONE_WIRE))
				return RedstoneSide.UP;
		} else {
			if (world.getBlockState(offPos.down()).isIn(ModBlockTags.REDSTONE_WIRE))
				return RedstoneSide.SIDE;
		}
		return RedstoneSide.NONE;
	}

	private static boolean areAllSidesInvalid(BlockState state) {
		return !state.get(NORTH).func_235921_b_() && !state.get(SOUTH).func_235921_b_()
				&& !state.get(EAST).func_235921_b_() && !state.get(WEST).func_235921_b_();
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving) {
		if (!worldIn.isRemote) {
			if (state.isValidPosition(worldIn, pos)) {
				Direction facing = Direction.getFacingFromVector(fromPos.getX() - pos.getX(),
						fromPos.getY() - pos.getY(), fromPos.getZ() - pos.getZ());
				if (facing != Direction.DOWN)
					worldIn.setBlockState(pos, getUpdatedState(worldIn, state, pos), 2);
			} else {
				spawnDrops(state, worldIn, pos);
				worldIn.removeBlock(pos, false);
			}
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!oldState.isIn(state.getBlock()) && !worldIn.isRemote) {
			for (Direction direction : Direction.Plane.VERTICAL)
				worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
			updateNeighboursStateChange(worldIn, pos);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!isMoving && !state.isIn(newState.getBlock())) {
			super.onReplaced(state, worldIn, pos, newState, isMoving);
			if (!worldIn.isRemote) {
				for (Direction direction : Direction.values())
					worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
				updateNeighboursStateChange(worldIn, pos);
			}
		}
	}

	private void updateNeighboursStateChange(World world, BlockPos pos) {
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			this.notifyWireNeighborsOfStateChange(world, pos.offset(direction));
		}

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos blockpos = pos.offset(direction);
			if (world.getBlockState(blockpos).isNormalCube(world, blockpos)) {
				this.notifyWireNeighborsOfStateChange(world, blockpos.up());
			} else {
				this.notifyWireNeighborsOfStateChange(world, blockpos.down());
			}
		}
	}

	private void notifyWireNeighborsOfStateChange(World worldIn, BlockPos pos) {
		if (worldIn.getBlockState(pos).isIn(ModBlockTags.REDSTONE_WIRE)) {
			worldIn.notifyNeighborsOfStateChange(pos, this);
		}
	}

	protected static boolean canConnectTo(BlockState blockState, IBlockReader world, BlockPos pos,
			@Nullable Direction side) {
		if (blockState.isIn(ModBlockTags.REDSTONE_WIRE)) {
			return true;
		} else if (blockState.isIn(Blocks.REPEATER)) {
			Direction direction = blockState.get(RepeaterBlock.HORIZONTAL_FACING);
			return direction == side || direction.getOpposite() == side;
		} else if (blockState.isIn(Blocks.OBSERVER)) {
			return side == blockState.get(ObserverBlock.FACING);
		} else {
			return blockState.canConnectRedstone(world, pos, side) && side != null;
		}
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		switch (rot) {
		case CLOCKWISE_180:
			return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH))
					.with(WEST, state.get(EAST));
		case COUNTERCLOCKWISE_90:
			return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST))
					.with(WEST, state.get(NORTH));
		case CLOCKWISE_90:
			return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST))
					.with(WEST, state.get(SOUTH));
		default:
			return state;
		}
	}

	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		switch (mirrorIn) {
		case LEFT_RIGHT:
			return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
		case FRONT_BACK:
			return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
		default:
			return super.mirror(state, mirrorIn);
		}
	}

	@Override
	public boolean matchesBlock(Block block) {
		return (this == block) || block == Blocks.REDSTONE_WIRE;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, POWER);
	}

}