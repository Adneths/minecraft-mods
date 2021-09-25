package me.adneths.bottle_bee.client.renderer.entity;

import me.adneths.bottle_bee.BottleOfBee;
import me.adneths.bottle_bee.client.renderer.entity.model.KillerBeeModel;
import me.adneths.bottle_bee.entity.KillerBeeEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KillerBeeRenderer extends MobRenderer<KillerBeeEntity, KillerBeeModel<KillerBeeEntity>> {
	
	private static final ResourceLocation field_229040_a_ = new ResourceLocation(BottleOfBee.MODID, "textures/entity/killer_bee/killer_bee_angry.png");
	private static final ResourceLocation field_229041_g_ = new ResourceLocation(BottleOfBee.MODID, "textures/entity/killer_bee/killer_bee_angry_nectar.png");
	private static final ResourceLocation field_229042_h_ = new ResourceLocation(BottleOfBee.MODID, "textures/entity/killer_bee/killer_bee.png");
	private static final ResourceLocation field_229043_i_ = new ResourceLocation(BottleOfBee.MODID, "textures/entity/killer_bee/killer_bee_nectar.png");
	
	public KillerBeeRenderer(EntityRendererManager p_i226033_1_) {
		super(p_i226033_1_, new KillerBeeModel<>(), 0.4F);
	}

	@Override
	public ResourceLocation getEntityTexture(KillerBeeEntity entity) {
	    if (entity.func_233678_J__())
	      return entity.hasNectar() ? field_229041_g_ : field_229040_a_; 
	    return entity.hasNectar() ? field_229043_i_ : field_229042_h_;
	}
	
}
