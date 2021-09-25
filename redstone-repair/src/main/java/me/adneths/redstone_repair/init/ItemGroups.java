package me.adneths.redstone_repair.init;

import me.adneths.redstone_repair.RedstoneRepair;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemGroups {

	public static final ItemGroup MAIN = new ItemGroup(RedstoneRepair.MODID+".main") {
		
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.BROKEN_REDSTONE_TORCH);
		}};
	
}
