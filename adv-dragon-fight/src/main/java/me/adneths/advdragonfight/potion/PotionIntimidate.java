package me.adneths.advdragonfight.potion;

import java.util.ArrayList;
import java.util.List;

import me.adneths.advdragonfight.AdvDragonFight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.AttributeModifierOperation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionIntimidate extends Potion
{

	private final ResourceLocation ICON = new ResourceLocation(AdvDragonFight.MODID, "textures/potion/intimidate.png");
	private final Minecraft mc = Minecraft.getMinecraft();
	
	protected PotionIntimidate()
	{
		super(true, 0);
		this.setPotionName("Intimidation");
		this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED,
				"17b51bc6-fbe7-4dfb-8702-11261314906f", -0.2, AttributeModifierOperation.ADD_MULTIPLE);
		this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE,
				"6c3ef35f-2c0d-4563-837e-40ec1a03a397", -0.25, AttributeModifierOperation.ADD_MULTIPLE);
		this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED,
				"867c3be8-da3e-4cca-b609-9b57bf99c8df", -.5, AttributeModifierOperation.ADD_MULTIPLE);
	}

	@Override
	public boolean hasStatusIcon()
	{
		return false;
	}
	
	@Override
	public List<ItemStack> getCurativeItems()
	{
		return new ArrayList<>();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z)
	{
		mc.renderEngine.bindTexture(ICON);
		Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha)
	{
		mc.renderEngine.bindTexture(ICON);
		Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
	}

}
