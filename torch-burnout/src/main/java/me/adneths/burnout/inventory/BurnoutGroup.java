package me.adneths.burnout.inventory;

import me.adneths.burnout.Burnout;
import me.adneths.burnout.init.ModContents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class BurnoutGroup {

	public static ItemGroup burnout = new ItemGroup(Burnout.MODID) {

		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModContents.Items.torch);
		}
	};

}
