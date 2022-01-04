package me.adneths.advdragonfight.inventory;

import me.adneths.advdragonfight.AdvDragonFight;
import me.adneths.advdragonfight.item.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CustomCreativeTabs
{

	public static final CreativeTabs main = new CreativeTabs(AdvDragonFight.MODID+":main") {
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(ModItems.dragonScale);
		}};
	
}
