package me.adneths.redstone_repair.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class BlockVarientItem extends BlockItem {

	private IPropertyGetter getter;
	
	public BlockVarientItem(Block blockIn, Properties builder, IPropertyGetter getter) {
		super(blockIn, builder);
		this.getter = getter;
	}
	
	@Override
	public String getTranslationKey(ItemStack stack)
	{
		return String.format("%s.%s", this.getTranslationKey(), getter.getProperty(stack));
	}

}
