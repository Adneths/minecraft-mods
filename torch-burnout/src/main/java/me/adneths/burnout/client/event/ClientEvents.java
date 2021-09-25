package me.adneths.burnout.client.event;

import me.adneths.burnout.blocks.BurnoutForgeBlock;
import me.adneths.burnout.tile.BurnoutableTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber
public class ClientEvents {

	private static String display = null;
	
	@SubscribeEvent
	public static void onRenderHUD(RenderGameOverlayEvent.Post event)
	{
		if(display!=null)
			try {
				int width = Minecraft.getInstance().getMainWindow().getScaledWidth();
				int height = Minecraft.getInstance().getMainWindow().getScaledHeight();
				int length = Minecraft.getInstance().fontRenderer.getStringWidth(display);
				AbstractGui.drawString(event.getMatrixStack(), Minecraft.getInstance().fontRenderer, display, (width-length)/2, height-70, 0xffffff);
				Minecraft.getInstance().getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
			}
			catch (NullPointerException e)
			{	}
	}
	
	@SubscribeEvent
	public static void onLookBurnout(DrawHighlightEvent.HighlightBlock event)
	{
		World world = Minecraft.getInstance().world;
		BlockPos pos = event.getTarget().getPos();
		BlockState state = world.getBlockState(pos);
		if(state.hasTileEntity())
		{
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof BurnoutableTile)
			{
				int ticks = ((BurnoutableTile) tile).getFuel();
				if(Minecraft.getInstance().player.isSneaking())
				{
					BurnoutForgeBlock block = (BurnoutForgeBlock) state.getBlock();
					display = String.format("%04.2f%%", 100d*ticks/block.maxFuel());
				}
				else
				{
					int hours = ticks/(20*60*60);
					ticks %= 20*60*60;
					int minutes = ticks/(20*60);
					ticks %= 20*60;
					int seconds = ticks/20;
					display = String.format("%02d:%02d:%02d", hours, minutes, seconds);
				}
				return;
			}
		}
		display = null;
	}
	
}
