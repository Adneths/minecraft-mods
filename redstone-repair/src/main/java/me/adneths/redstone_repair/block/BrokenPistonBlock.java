package me.adneths.redstone_repair.block;

import me.adneths.redstone_repair.RedstoneRepair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class BrokenPistonBlock extends UpdateRepairableBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final EnumProperty<PistonPart> BROKEN_PART = EnumProperty.create("broken_part", PistonPart.class);

	private boolean isSticky;

	public BrokenPistonBlock(Properties properties, boolean isSticky) {
		super(properties);
		this.isSticky = isSticky;
	}

	@Override
	public int repairCost(BlockState current) {
		switch (current.get(BROKEN_PART)) {
		case FRAME:
			return 2;
		case ROD:
			return 1;
		case HEAD:
			return 2;
		case SLIME:
			return 1;
		}
		return 0;
	}

	@Override
	public Item getRepairMaterial(BlockState current) {
		switch (current.get(BROKEN_PART)) {
		case FRAME:
			return Items.COBBLESTONE;
		case ROD:
			return Items.IRON_INGOT;
		case HEAD:
			return Items.OAK_PLANKS;
		case SLIME:
			return Items.SLIME_BALL;
		}
		return null;
	}

	@Override
	public ITag<Item> getRepairMaterialTag(BlockState current) {
		return current.get(BROKEN_PART) == PistonPart.HEAD ? ItemTags.PLANKS : null;
	}

	@Override
	public BlockState repairedState(BlockState current) {
		return (isSticky ? Blocks.STICKY_PISTON : Blocks.PISTON).getDefaultState().with(FACING, current.get(FACING));
	}
	
	@Override
	public BlockState randomBreak(BlockState pre) {
		PistonPart part = PistonPart.values()[(int) ((PistonPart.values().length-(isSticky?0:1))*Math.random())];
		return this.getDefaultState().with(BROKEN_PART, part).with(FACING, pre.get(FACING));
	}
	
	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		ItemStack stack = new ItemStack(this);
		CompoundNBT tag = new CompoundNBT();
		tag.putString(PISTON_PART, state.get(BROKEN_PART).getString());
		stack.setTag(tag);
		return stack;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
		if(context.getItem().getTag()!=null)
		{
			PistonPart part = PistonPart.getPartByName(context.getItem().getTag().getString(PISTON_PART));
			if(part != null)
				return state.with(BROKEN_PART, part);
		}
		return state;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, BROKEN_PART);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
		return state;
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	public static final String PISTON_PART = RedstoneRepair.MODID+":piston_part";
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		for(PistonPart part : PistonPart.values())
		{
			if(part==PistonPart.SLIME && !isSticky)
				continue;
			ItemStack stack = new ItemStack(this);
			CompoundNBT nbt = stack.getTag();
			if(nbt==null)
				nbt = new CompoundNBT();
			nbt.putString(PISTON_PART, part.getString());
			stack.setTag(nbt);
			items.add(stack);
		}
	}
	
	public enum PistonPart implements IStringSerializable {
		FRAME("frame",0), ROD("rod",1), HEAD("head",2), SLIME("slime",3);

		private final String name;
		private final int id;

		PistonPart(String name, int id) {
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
		
		public static PistonPart getPartByName(String name)
		{
			switch(name)
			{
			case "frame":
				return FRAME;
			case "rod":
				return ROD;
			case "head":
				return HEAD;
			case "slime":
				return SLIME;
			}
			return null;
		}
		
		public static PistonPart getPartById(int id)
		{
			switch(id)
			{
			case 0:
				return FRAME;
			case 1:
				return ROD;
			case 2:
				return HEAD;
			case 3:
				return SLIME;
			}
			return null;
		}
	}

}
