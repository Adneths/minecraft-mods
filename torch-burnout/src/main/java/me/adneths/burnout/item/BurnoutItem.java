package me.adneths.burnout.item;

import net.minecraft.item.Item;

public class BurnoutItem extends Item {

	private String translationKey;
	
	public BurnoutItem(Properties properties) {
		super(properties);
	}
	
	public BurnoutItem setTranslationKey(String nameSpace, String name)
	{
		this.translationKey = String.format("%s:%s", nameSpace, name);
		return this;
	}
	
	@Override
	public String getTranslationKey()
	{
		return translationKey;
	}

}
