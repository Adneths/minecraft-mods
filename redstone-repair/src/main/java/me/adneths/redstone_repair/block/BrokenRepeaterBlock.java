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
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BrokenRepeaterBlock extends UpdateRepairableBlock {
	
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final IntegerProperty DELAY = BlockStateProperties.DELAY_1_4;
	public static final EnumProperty<RepeaterPart> BROKEN_PART = EnumProperty.create("broken_part", RepeaterPart.class);
	
	public BrokenRepeaterBlock(Properties properties) {
		super(properties);
	}

	@Override
	public int repairCost(BlockState current) {
		return current.get(BROKEN_PART)==RepeaterPart.TORCHES ? 2 : 1;
	}

	@Override
	public Item getRepairMaterial(BlockState current) {
		return current.get(BROKEN_PART)==RepeaterPart.BASE ? Items.STONE : current.get(BROKEN_PART)==RepeaterPart.WIRE ? Items.REDSTONE : Items.REDSTONE_TORCH;
	}

	@Override
	public BlockState repairedState(BlockState current) {
		return Blocks.REPEATER.getDefaultState().with(HORIZONTAL_FACING, current.get(HORIZONTAL_FACING)).with(DELAY, current.get(DELAY));
	}
	
	@Override
	public BlockState randomBreak(BlockState pre) {
		return this.getDefaultState().with(BROKEN_PART, RepairableBlock.getRandom(RepeaterPart.values())).with(HORIZONTAL_FACING, pre.get(HORIZONTAL_FACING)).with(DELAY, pre.get(DELAY));
	}
	
	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		ItemStack stack = new ItemStack(this);
		CompoundNBT tag = new CompoundNBT();
		tag.putString(REPEATER_PART, state.get(BROKEN_PART).getString());
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
			RepeaterPart part = RepeaterPart.getPartByName(context.getItem().getTag().getString(REPEATER_PART));
			if(part != null)
				return state.with(BROKEN_PART, part);
		}
		return state;
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if(!player.abilities.allowEdit)
			return ActionResultType.PASS;
		ActionResultType ret = super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
		if(ret == ActionResultType.PASS)
		{
			worldIn.setBlockState(pos, state.func_235896_a_(DELAY), 3);
			return ActionResultType.func_233537_a_(worldIn.isRemote);
		}
		return ret;
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side)
	{
		return side!=null && state.get(HORIZONTAL_FACING).getAxis()==side.getAxis();
	}

	public static final String REPEATER_PART = RedstoneRepair.MODID+":repeater_part";
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		for(RepeaterPart part : RepeaterPart.values())
		{
			ItemStack stack = new ItemStack(this);
			CompoundNBT nbt = stack.getTag();
			if(nbt==null)
				nbt = new CompoundNBT();
			nbt.putString(REPEATER_PART, part.getString());
			stack.setTag(nbt);
			items.add(stack);
		}
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING, DELAY, BROKEN_PART);
	}
	
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return hasSolidSideOnTop((IBlockReader)worldIn, pos.down());
	}

	public enum RepeaterPart implements IStringSerializable {
		BASE("base",0), WIRE("wire",1), TORCH_FRONT("torch_front",2), TORCH_BACK("torch_back",3), TORCHES("torches",4);

		private final String name;
		private final int id;

		RepeaterPart(String name, int id) {
			this.name = name;
			this.id = id;
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
		
		public static boolean isBurn(RepeaterPart part)
		{
			return part.id > 0;
		}
		
		public static RepeaterPart getPartByName(String name)
		{
			switch(name)
			{
			case "base":
				return BASE;
			case "wire":
				return WIRE;
			case "torch_front":
				return TORCH_FRONT;
			case "torch_back":
				return TORCH_BACK;
			case "torches":
				return TORCHES;
			}
			return null;
		}
		
		public static RepeaterPart getPartById(int id)
		{
			switch(id)
			{
			case 0:
				return BASE;
			case 1:
				return WIRE;
			case 2:
				return TORCH_FRONT;
			case 3:
				return TORCH_BACK;
			case 4:
				return TORCHES;
			}
			return null;
		}
	}
	
}
