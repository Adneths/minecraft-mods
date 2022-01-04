package me.adneths.advdragonfight.potion;

import me.adneths.advdragonfight.AdvDragonFight;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(AdvDragonFight.MODID)
@Mod.EventBusSubscriber(modid = AdvDragonFight.MODID)
public class ModPotions
{
	
	@ObjectHolder("advdragonfight:intimidate")
	public static final Potion intimidate = null;
	@ObjectHolder("advdragonfight:curse")
	public static final Potion curse = null;

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<Potion> event) {
		event.getRegistry().register(new PotionIntimidate().setRegistryName(AdvDragonFight.MODID, "intimidate"));
		event.getRegistry().register(new PotionCurse().setRegistryName(AdvDragonFight.MODID, "curse"));
	}
}
