package me.adneths.advdragonfight.capability;

import me.adneths.advdragonfight.AdvDragonFight;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = AdvDragonFight.MODID)
public class CapabilityHandler
{
	public static final ResourceLocation dragonDifficulty = new ResourceLocation(AdvDragonFight.MODID, "dragonDifficulty");

	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event)
	{
		if (!(event.getObject() instanceof EntityPlayer))
			return;
		event.addCapability(dragonDifficulty, new DragonDifficultyProvider());
	}
}
