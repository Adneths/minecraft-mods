package me.adneths.bottle_bee.inventory;

import me.adneths.bottle_bee.BottleOfBee;
import me.adneths.bottle_bee.init.ModContents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemGroups {

	public static final ItemGroup MAIN = new ItemGroup(BottleOfBee.MODID+":main") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModContents.Items.BOTTLE_OF_BEE);
		}};
	
}
