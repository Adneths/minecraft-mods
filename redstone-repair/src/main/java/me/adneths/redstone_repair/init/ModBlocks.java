package me.adneths.redstone_repair.init;

import me.adneths.redstone_repair.RedstoneRepair;
import me.adneths.redstone_repair.block.RepairableBlock;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(RedstoneRepair.MODID)
public class ModBlocks {

	@ObjectHolder(RedstoneRepair.MODID+":broken_redstone_wire")
	public static final RepairableBlock BROKEN_REDSTONE_WIRE = null;
	
	@ObjectHolder(RedstoneRepair.MODID+":broken_redstone_lamp")
	public static final RepairableBlock BROKEN_REDSTONE_LAMP = null;

	@ObjectHolder(RedstoneRepair.MODID+":broken_redstone_torch")
	public static final RepairableBlock BROKEN_REDSTONE_TORCH = null;

	@ObjectHolder(RedstoneRepair.MODID+":broken_redstone_wall_torch")
	public static final RepairableBlock BROKEN_REDSTONE_WALL_TORCH = null;
	
	@ObjectHolder(RedstoneRepair.MODID+":broken_piston")
	public static final RepairableBlock BROKEN_PISTON = null;
	
	@ObjectHolder(RedstoneRepair.MODID+":broken_sticky_piston")
	public static final RepairableBlock BROKEN_STICKY_PISTON = null;
	
	@ObjectHolder(RedstoneRepair.MODID+":broken_repeater")
	public static final RepairableBlock BROKEN_REPEATER = null;

	@ObjectHolder(RedstoneRepair.MODID+":broken_comparator")
	public static final RepairableBlock BROKEN_COMPARATOR = null;

	@ObjectHolder(RedstoneRepair.MODID+":broken_dropper")
	public static final RepairableBlock BROKEN_DROPPER = null;

	@ObjectHolder(RedstoneRepair.MODID+":broken_dispenser")
	public static final RepairableBlock BROKEN_DISPENSER = null;

	@ObjectHolder(RedstoneRepair.MODID+":broken_observer")
	public static final RepairableBlock BROKEN_OBSERVER = null;
	
	public static RepairableBlock[] blocksWithItem()
	{
		return new RepairableBlock[] {BROKEN_REDSTONE_LAMP, BROKEN_DROPPER};
	}
}
