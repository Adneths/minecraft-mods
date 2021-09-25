package me.adneths.bottle_bee.client.renderer.entity;

import me.adneths.bottle_bee.entity.BottleBeeEntity;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;

public class BottleBeeRenderer extends SpriteRenderer<BottleBeeEntity> {

	public BottleBeeRenderer(EntityRendererManager renderManagerIn, ItemRenderer itemRendererIn) {
		super(renderManagerIn, itemRendererIn);
	}

}
