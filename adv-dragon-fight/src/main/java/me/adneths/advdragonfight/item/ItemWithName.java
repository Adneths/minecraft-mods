package me.adneths.advdragonfight.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemWithName extends Item
{
	public ItemWithName(String modId, String name)
	{
		this.setRegistryName(new ResourceLocation(modId, name));
		this.setUnlocalizedName(this.getRegistryName().toString());
	}
}
