package me.adneths.advdragonfight.client.renderer;

import me.adneths.advdragonfight.client.model.ModelBabyDragon;
import me.adneths.advdragonfight.entity.EntityBabyDragonBase;
import me.adneths.advdragonfight.entity.EntityHealingBabyDragon;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBabyDragon extends RenderLiving<EntityBabyDragonBase>
{

	public static final ResourceLocation ENDERCRYSTAL_BEAM_TEXTURES = new ResourceLocation(
			"textures/entity/endercrystal/endercrystal_beam.png");

	private static final ResourceLocation DRAGON_TEXTURES = new ResourceLocation(
			"textures/entity/enderdragon/dragon.png");

	public RenderBabyDragon(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelBabyDragon(0.0F), 0.5F);
	}

	protected void applyRotations(EntityBabyDragonBase entityLiving, float p_77043_2_, float rotationYaw,
			float partialTicks)
	{
		float f = (float) entityLiving.getMovementOffsets(7, partialTicks)[0];
		float f1 = (float) (entityLiving.getMovementOffsets(5, partialTicks)[1]
				- entityLiving.getMovementOffsets(10, partialTicks)[1]);
		GlStateManager.rotate(-f, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(f1 * 10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.translate(0.0F, 0.0F, 1.0F);
		if (entityLiving.deathTime > 0)
		{
			float f2 = (entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
			f2 = MathHelper.sqrt(f2);
			if (f2 > 1.0F)
				f2 = 1.0F;
			GlStateManager.rotate(f2 * getDeathMaxRotation(entityLiving), 0.0F, 0.0F, 1.0F);
		}
	}

	@Override
	protected void renderModel(EntityBabyDragonBase entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor)
	{
		bindEntityTexture(entitylivingbaseIn);
		this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw,
				headPitch, scaleFactor);
		if (entitylivingbaseIn.hurtTime > 0)
		{
			GlStateManager.depthFunc(514);
			GlStateManager.disableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 0.0F, 0.0F, 0.5F);
			this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw,
					headPitch, scaleFactor);
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
			GlStateManager.depthFunc(515);
		}
	}

	@Override
	public void doRender(EntityBabyDragonBase entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		
		if(entity instanceof EntityHealingBabyDragon && entity.getAttackTarget() != null && entity.getAttackTarget().isEntityAlive())
		{
			bindTexture(ENDERCRYSTAL_BEAM_TEXTURES);
			float f = MathHelper.sin((entity.ticksExisted + partialTicks) * 0.2F) / 2.0F + 0.5F;
			f = (f * f + f) * 0.2F;
			renderCrystalBeams(x, y, z, partialTicks,
					entity.posX + (entity.prevPosX - entity.posX) * (1.0F - partialTicks),
					entity.posY - 1 + (entity.prevPosY - entity.posY) * (1.0F - partialTicks),
					entity.posZ + (entity.prevPosZ - entity.posZ) * (1.0F - partialTicks), entity.ticksExisted,
					entity.getAttackTarget().posX, f + entity.getAttackTarget().posY, entity.getAttackTarget().posZ);
		}
	}

	public static void renderCrystalBeams(double p_188325_0_, double p_188325_2_, double p_188325_4_, float p_188325_6_,
			double p_188325_7_, double p_188325_9_, double p_188325_11_, int p_188325_13_, double p_188325_14_,
			double p_188325_16_, double p_188325_18_)
	{
		float f = (float) (p_188325_14_ - p_188325_7_);
		float f1 = (float) (p_188325_16_ - 1.0D - p_188325_9_);
		float f2 = (float) (p_188325_18_ - p_188325_11_);
		float f3 = MathHelper.sqrt(f * f + f2 * f2);
		float f4 = MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) p_188325_0_, (float) p_188325_2_ + 2.0F, (float) p_188325_4_);
		GlStateManager.rotate((float) -Math.atan2(f2, f) * 57.295776F - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float) -Math.atan2(f3, f1) * 57.295776F - 90.0F, 1.0F, 0.0F, 0.0F);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableCull();
		GlStateManager.shadeModel(7425);
		float f5 = 0.0F - (p_188325_13_ + p_188325_6_) * 0.01F;
		float f6 = MathHelper.sqrt(f * f + f1 * f1 + f2 * f2) / 32.0F - (p_188325_13_ + p_188325_6_) * 0.01F;
		bufferbuilder.begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);
		for (int j = 0; j <= 8; j++)
		{
			float f7 = MathHelper.sin((j % 8) * 6.2831855F / 8.0F) * 0.75F;
			float f8 = MathHelper.cos((j % 8) * 6.2831855F / 8.0F) * 0.75F;
			float f9 = (j % 8) / 8.0F;
			bufferbuilder.pos((f7 * 0.2F), (f8 * 0.2F), 0.0D).tex(f9, f5).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(f7, f8, f4).tex(f9, f6).color(255, 255, 255, 255).endVertex();
		}
		tessellator.draw();
		GlStateManager.enableCull();
		GlStateManager.shadeModel(7424);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBabyDragonBase entity)
	{
		return DRAGON_TEXTURES;
	}

}
