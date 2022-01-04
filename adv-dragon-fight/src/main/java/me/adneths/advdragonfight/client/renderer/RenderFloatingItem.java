package me.adneths.advdragonfight.client.renderer;

import java.util.Random;

import me.adneths.advdragonfight.entity.EntityFloatingItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFloatingItem extends Render<EntityFloatingItem>
{
	private final RenderItem itemRenderer;

	private final Random random = new Random();

	public RenderFloatingItem(RenderManager renderManagerIn, RenderItem p_i46167_2_)
	{
		super(renderManagerIn);
		this.itemRenderer = p_i46167_2_;
		this.shadowSize = 0.15F;
		this.shadowOpaque = 0.75F;
	}

	@SuppressWarnings("deprecation")
	private int transformModelCount(EntityFloatingItem itemIn, double p_177077_2_, double p_177077_4_,
			double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_)
	{
		ItemStack itemstack = itemIn.getItem();
		Item item = itemstack.getItem();
		if (item == null)
			return 0;
		boolean flag = p_177077_9_.isGui3d();
		int i = getModelCount(itemstack);
		float f2 = (p_177077_9_.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND)).scale.y;
		GlStateManager.translate(p_177077_2_, p_177077_4_ + 0.25F * f2, p_177077_6_);
		if (flag || this.renderManager.options != null)
		{
			float f3 = ((itemIn.getAge() + p_177077_8_) / 20.0F + itemIn.hoverStart) * 57.295776F;
			GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		return i;
	}

	protected int getModelCount(ItemStack stack)
	{
		int i = 1;
		if (stack.getCount() > 48)
		{
			i = 5;
		}
		else if (stack.getCount() > 32)
		{
			i = 4;
		}
		else if (stack.getCount() > 16)
		{
			i = 3;
		}
		else if (stack.getCount() > 1)
		{
			i = 2;
		}
		return i;
	}

	public void doRender(EntityFloatingItem entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		ItemStack itemstack = entity.getItem();
		int i = itemstack.isEmpty() ? 187 : (Item.getIdFromItem(itemstack.getItem()) + itemstack.getMetadata());
		this.random.setSeed(i);
		boolean flag = false;
		if (bindEntityTexture(entity))
		{
			this.renderManager.renderEngine.getTexture(getEntityTexture(entity)).setBlurMipmap(false, false);
			flag = true;
		}
		GlStateManager.enableRescaleNormal();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.pushMatrix();
		IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, entity.world,
				(EntityLivingBase) null);
		
		int j = transformModelCount(entity, x, y, z, partialTicks, ibakedmodel);
		boolean flag1 = ibakedmodel.isGui3d();
		if (!flag1)
		{
			float f3 = -0.0F * (j - 1) * 0.5F;
			float f4 = -0.0F * (j - 1) * 0.5F;
			float f5 = -0.09375F * (j - 1) * 0.5F;
			GlStateManager.translate(f3, f4, f5);
		}
		if (this.renderOutlines)
		{
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(getTeamColor(entity));
		}
		for (int k = 0; k < j; k++)
		{
			if (flag1)
			{
				GlStateManager.pushMatrix();
				if (k > 0)
				{
					float f7 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float f9 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float f6 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					GlStateManager.translate(f7, f9, f6);
				}
				IBakedModel transformedModel = ForgeHooksClient.handleCameraTransforms(ibakedmodel,
						ItemCameraTransforms.TransformType.GROUND, false);
				GlStateManager.scale(3, 3, 3);
				this.itemRenderer.renderItem(itemstack, transformedModel);
				GlStateManager.popMatrix();
			}
			else
			{
				GlStateManager.pushMatrix();
				if (k > 0)
				{
					float f8 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					GlStateManager.translate(f8, f10, 0.0F);
				}
				IBakedModel transformedModel = ForgeHooksClient.handleCameraTransforms(ibakedmodel,
						ItemCameraTransforms.TransformType.GROUND, false);
				GlStateManager.scale(3, 3, 3);
				this.itemRenderer.renderItem(itemstack, transformedModel);
				GlStateManager.popMatrix();
				GlStateManager.translate(0.0F, 0.0F, 0.09375F);
			}
		}
		if (this.renderOutlines)
		{
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}
		
		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		bindEntityTexture(entity);
		if (flag)
			this.renderManager.renderEngine.getTexture(getEntityTexture(entity)).restoreLastBlurMipmap();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	protected ResourceLocation getEntityTexture(EntityFloatingItem entity)
	{
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
}
