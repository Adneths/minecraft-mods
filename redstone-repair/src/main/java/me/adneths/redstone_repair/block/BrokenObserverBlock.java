package me.adneths.redstone_repair.block;

import me.adneths.redstone_repair.RedstoneRepair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BrokenObserverBlock extends RepairableBlock {

	public static final EnumProperty<ObserverPart> BROKEN_PART = EnumProperty.create("broken_part", ObserverPart.class);
	public static final DirectionProperty FACING = DirectionalBlock.FACING;
	
	public BrokenObserverBlock(Properties properties) {
		super(properties);
	}

	@Override
	public int repairCost(BlockState current) {
		return current.get(BROKEN_PART) == ObserverPart.FRAME ? 4 : 1;
	}

	@Override
	public Item getRepairMaterial(BlockState current) {
		return current.get(BROKEN_PART) == ObserverPart.FRAME ? Items.COBBLESTONE : Items.QUARTZ;
	}

	@Override
	public BlockState repairedState(BlockState current) {
		return Blocks.OBSERVER.getDefaultState().with(FACING, current.get(FACING));
	}
	
	@Override
	public BlockState randomBreak(BlockState pre) {
		return this.getDefaultState().with(BROKEN_PART, RepairableBlock.getRandom(ObserverPart.values())).with(FACING, pre.get(FACING));
	}

	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		ItemStack stack = new ItemStack(this);
		CompoundNBT tag = new CompoundNBT();
		tag.putString(OBSERVER_PART, state.get(BROKEN_PART).getString());
		stack.setTag(tag);
		return stack;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
		if(context.getItem().getTag()!=null)
		{
			ObserverPart part = ObserverPart.getPartByName(context.getItem().getTag().getString(OBSERVER_PART));
			if(part != null)
				return state.with(BROKEN_PART, part);
		}
		return state;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, BROKEN_PART);
	}
	
	public static final String OBSERVER_PART = RedstoneRepair.MODID+":observer_part";
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		for(ObserverPart part : ObserverPart.values())
		{
			ItemStack stack = new ItemStack(this);
			CompoundNBT nbt = stack.getTag();
			if(nbt==null)
				nbt = new CompoundNBT();
			nbt.putString(OBSERVER_PART, part.getString());
			stack.setTag(nbt);
			items.add(stack);
		}
	}
	
	public enum ObserverPart implements IStringSerializable {
		FRAME("frame",0), SENSOR("sensor",1);

		private final String name;
		private final int id;

		ObserverPart(String name, int id) {
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
		
		public static ObserverPart getPartByName(String name)
		{
			switch(name)
			{
			case "frame":
				return FRAME;
			case "sensor":
				return SENSOR;
			}
			return null;
		}
		
		public static ObserverPart getPartById(int id)
		{
			switch(id)
			{
			case 0:
				return FRAME;
			case 1:
				return SENSOR;
			}
			return null;
		}
	}

}
