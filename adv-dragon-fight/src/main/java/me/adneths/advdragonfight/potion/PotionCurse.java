package me.adneths.advdragonfight.potion;

import me.adneths.advdragonfight.AdvDragonFight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionCurse extends Potion
{
	private final ResourceLocation ICON = new ResourceLocation(AdvDragonFight.MODID, "textures/potion/curse.png");
	private final Minecraft mc = Minecraft.getMinecraft();

	protected PotionCurse()
	{
		super(true, 0xaa00aa);
		this.setPotionName("Dragon's Curse");
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
