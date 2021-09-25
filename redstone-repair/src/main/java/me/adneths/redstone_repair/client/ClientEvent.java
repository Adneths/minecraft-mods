package me.adneths.redstone_repair.client;

import me.adneths.redstone_repair.block.RepairableBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
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
public class ClientEvent {

	private static ItemStack stack = null;
	
	@SubscribeEvent
	public static void onRenderHUD(RenderGameOverlayEvent.Pre event)
	{
		if(stack!=null)
			try {
				MainWindow window = Minecraft.getInstance().getMainWindow();
				int width = window.getScaledWidth();
				int height = window.getScaledHeight();
				double factor = window.getGuiScaleFactor();
				width /= 2;
				width -= 3*factor;
				height /= 2;
				height += 3*factor;
				ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
				itemRenderer.renderItemIntoGUI(stack, width, height);
				itemRenderer.renderItemOverlays(Minecraft.getInstance().fontRenderer, stack, width, height);
			}
			catch (NullPointerException e)
			{	}
	}
	
	@SubscribeEvent
	public static void onLookBroken(DrawHighlightEvent.HighlightBlock event)
	{
		if(Minecraft.getInstance().player.isSneaking())
		{
			World world = Minecraft.getInstance().world;
			BlockPos pos = event.getTarget().getPos();
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() instanceof RepairableBlock)
			{
				RepairableBlock block = (RepairableBlock)state.getBlock();
				if(stack == null || stack.getItem() != block.getRepairMaterial(state) || stack.getCount() != block.repairCost(state))
					stack = new ItemStack(block.getRepairMaterial(state), block.repairCost(state));
				return;
			}
		}
		stack = null;
	}
	
}
