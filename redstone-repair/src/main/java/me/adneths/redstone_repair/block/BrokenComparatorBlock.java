package me.adneths.redstone_repair.block;

import javax.annotation.Nullable;

import me.adneths.redstone_repair.RedstoneRepair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ComparatorMode;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BrokenComparatorBlock extends UpdateRepairableBlock {

	protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<ComparatorMode> MODE = BlockStateProperties.COMPARATOR_MODE;
	public static final EnumProperty<ComparatorPart> BROKEN_PART = EnumProperty.create("broken_part", ComparatorPart.class);
	
	public BrokenComparatorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public int repairCost(BlockState current) {
		return current.get(BROKEN_PART).getCost();
	}

	@Override
	public Item getRepairMaterial(BlockState current) {
		ComparatorPart part = current.get(BROKEN_PART);
		switch(part)
		{
		case BASE:
			return Items.STONE;
		case CRYSTAL:
			return Items.QUARTZ;
		case WIRE:
			return Items.REDSTONE;
		default:
			return Items.REDSTONE_TORCH;
		}
	}

	@Override
	public BlockState repairedState(BlockState current) {
		return Blocks.COMPARATOR.getDefaultState().with(HORIZONTAL_FACING, current.get(HORIZONTAL_FACING)).with(MODE, current.get(MODE));
	}
	
	@Override
	public BlockState randomBreak(BlockState pre) {
		return this.getDefaultState().with(BROKEN_PART, RepairableBlock.getRandom(ComparatorPart.values())).with(HORIZONTAL_FACING, pre.get(HORIZONTAL_FACING)).with(MODE, pre.get(MODE));
	}
	
	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		ItemStack stack = new ItemStack(this);
		CompoundNBT tag = new CompoundNBT();
		tag.putString(COMPARATOR_PART, state.get(BROKEN_PART).getString());
		stack.setTag(tag);
		return stack;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
		if(context.getItem().getTag()!=null)
		{
			ComparatorPart part = ComparatorPart.getPartByName(context.getItem().getTag().getString(COMPARATOR_PART));
			if(part != null)
				return state.with(BROKEN_PART, part);
		}
		return state;
	}
	
	public static final String COMPARATOR_PART = RedstoneRepair.MODID+":comparator_part";
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		for(ComparatorPart part : ComparatorPart.values())
		{
			ItemStack stack = new ItemStack(this);
			CompoundNBT nbt = stack.getTag();
			if(nbt==null)
				nbt = new CompoundNBT();
			nbt.putString(COMPARATOR_PART, part.getString());
			stack.setTag(nbt);
			items.add(stack);
		}
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
	    if (!player.abilities.allowEdit)
	      return ActionResultType.PASS;
		ActionResultType ret = super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
		if(ret == ActionResultType.PASS)
		{
		    state = state.func_235896_a_(MODE);
		    float f = (state.get(MODE) == ComparatorMode.SUBTRACT) ? 0.55F : 0.5F;
		    worldIn.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, f);
		    worldIn.setBlockState(pos, state, 2);
		    return ActionResultType.func_233537_a_(worldIn.isRemote);
		}
		return ret;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING, MODE, BROKEN_PART);
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side)
	{
		return true;
	}
	
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return hasSolidSideOnTop((IBlockReader)worldIn, pos.down());
	}
	
	public enum ComparatorPart implements IStringSerializable {
		ALL("all",0,3), FRONT_LEFT("front_left",1,2), FRONT_RIGHT("front_right",2,2), LEFT_RIGHT("left_right",3,2),
		FRONT("front",4,1), LEFT("left",5,1), RIGHT("right",6,1), CRYSTAL("crystal",7,1), BASE("base",8,1), WIRE("wire",9,1);
		
		private final String name;
		private final int id;
		private final int cost;

		ComparatorPart(String name, int id, int cost) {
			this.name = name;
			this.id = id;
			this.cost = cost;
		}

		public String toString() {
			return getString();
		}

		public String getString() {
			return this.name;
		}
		
		public int getId()
		{
			return this.id;
		}
		
		public int getCost()
		{
			return this.cost;
		}
		
		public static boolean isBurn(ComparatorPart part)
		{
			return part.id < 7 || part.id == 9;
		}
		
		public static ComparatorPart getPartByName(String name)
		{
			switch(name)
			{
			case "all":
				return ALL;
			case "front_left":
				return FRONT_LEFT;
			case "front_right":
				return FRONT_RIGHT;
			case "left_right":
				return LEFT_RIGHT;
			case "front":
				return FRONT;
			case "left":
				return LEFT;
			case "right":
				return RIGHT;
			case "crystal":
				return CRYSTAL;
			case "base":
				return BASE;
			case "wire":
				return WIRE;
			}
			return null;
		}
		
		public static ComparatorPart getPartById(int id)
		{
			switch(id)
			{
			case 0:
				return ALL;
			case 1:
				return FRONT_LEFT;
			case 2:
				return FRONT_RIGHT;
			case 3:
				return LEFT_RIGHT;
			case 4:
				return FRONT;
			case 5:
				return LEFT;
			case 6:
				return RIGHT;
			case 7:
				return CRYSTAL;
			case 8:
				return BASE;
			case 9:
				return WIRE;
			}
			return null;
		}
	}

}
