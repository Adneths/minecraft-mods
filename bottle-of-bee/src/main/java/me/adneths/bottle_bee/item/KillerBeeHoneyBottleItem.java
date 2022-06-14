package me.adneths.bottle_bee.item;

import me.adneths.bottle_bee.inventory.ItemGroups;
import net.minecraft.item.Food;
import net.minecraft.item.HoneyBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class KillerBeeHoneyBottleItem extends HoneyBottleItem {

	public static final Food KILLER_BEE_HONEY = new Food.Builder().hunger(12).saturation(0.2f).effect(() -> new EffectInstance(Effects.REGENERATION, 400, 1), 1f).effect(() -> new EffectInstance(Effects.SPEED, 400, 3), 1f).build();
	
	public KillerBeeHoneyBottleItem() {
		super(new Item.Properties().containerItem(Items.GLASS_BOTTLE).food(KILLER_BEE_HONEY).maxStackSize(16).group(ItemGroups.MAIN));
	}

}
