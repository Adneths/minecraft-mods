package me.adneths.redstone_repair.init;

import me.adneths.redstone_repair.RedstoneRepair;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;

public class ModBlockTags {

	public static final ITag.INamedTag<Block> REDSTONE_WIRE = BlockTags.makeWrapperTag(RedstoneRepair.MODID+":redstone_wire");
	
}
