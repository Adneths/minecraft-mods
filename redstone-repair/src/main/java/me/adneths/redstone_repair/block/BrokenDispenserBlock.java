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

public class BrokenDispenserBlock extends RepairableBlock {

	public static final EnumProperty<DispenserPart> BROKEN_PART = EnumProperty.create("broken_part", DispenserPart.class);
	public static final DirectionProperty FACING = DirectionalBlock.FACING;
	
	public BrokenDispenserBlock(Properties properties) {
		super(properties);
	}

	@Override
	public int repairCost(BlockState current) {
		return current.get(BROKEN_PART) == DispenserPart.FRAME ? 4 : 2;
	}

	@Override
	public Item getRepairMaterial(BlockState current) {
		return current.get(BROKEN_PART) == DispenserPart.FRAME ? Items.COBBLESTONE : Items.STRING;
	}

	@Override
	public BlockState repairedState(BlockState current) {
		return Blocks.DISPENSER.getDefaultState().with(FACING, current.get(FACING));
	}
	
	@Override
	public BlockState randomBreak(BlockState pre) {
		return this.getDefaultState().with(BROKEN_PART, RepairableBlock.getRandom(DispenserPart.values())).with(FACING, pre.get(FACING));
	}
	
	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		ItemStack stack = new ItemStack(this);
		CompoundNBT tag = new CompoundNBT();
		tag.putString(DISPENSER_PART, state.get(BROKEN_PART).getString());
		stack.setTag(tag);
		return stack;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
		if(context.getItem().getTag()!=null)
		{
			DispenserPart part = DispenserPart.getPartByName(context.getItem().getTag().getString(DISPENSER_PART));
			if(part != null)
				return state.with(BROKEN_PART, part);
		}
		return state;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, BROKEN_PART);
	}
	
	public static final String DISPENSER_PART = RedstoneRepair.MODID+":dispenser_part";
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		for(DispenserPart part : DispenserPart.values())
		{
			ItemStack stack = new ItemStack(this);
			CompoundNBT nbt = stack.getTag();
			if(nbt==null)
				nbt = new CompoundNBT();
			nbt.putString(DISPENSER_PART, part.getString());
			stack.setTag(nbt);
			items.add(stack);
		}
	}
	
	public enum DispenserPart implements IStringSerializable {
		FRAME("frame",0), LAUNCHER("launcher",1);

		private final String name;
		private final int id;

		DispenserPart(String name, int id) {
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
		
		public static DispenserPart getPartByName(String name)
		{
			switch(name)
			{
			case "frame":
				return FRAME;
			case "launcher":
				return LAUNCHER;
			}
			return null;
		}
		
		public static DispenserPart getPartById(int id)
		{
			switch(id)
			{
			case 0:
				return FRAME;
			case 1:
				return LAUNCHER;
			}
			return null;
		}
	}

}
