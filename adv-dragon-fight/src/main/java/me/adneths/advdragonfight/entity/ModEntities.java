package me.adneths.advdragonfight.entity;

import me.adneths.advdragonfight.AdvDragonFight;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(AdvDragonFight.MODID)
@Mod.EventBusSubscriber(modid = AdvDragonFight.MODID)
public class ModEntities
{
	
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		int id = 0;
		event.getRegistry().register(EntityEntryBuilder.create()
				.entity(EntityHealthedEnderCrystal.class)
				.id(new ResourceLocation(AdvDragonFight.MODID, "healthed_ender_crystal"), id++)
				.name("advdragonfight.HealthedEnderCrystal")
				.tracker(256, Integer.MAX_VALUE, false).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.entity(EntityHardenedEndermite.class)
				.id(new ResourceLocation(AdvDragonFight.MODID, "hardened_endermite"), id++)
				.name("advdragonfight.HardenedEndermite")
				.tracker(80, 3, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.entity(EntityDivingBabyDragon.class)
				.id(new ResourceLocation(AdvDragonFight.MODID, "diving_baby_dragon"), id++)
				.name("advdragonfight.DivingBabyDragon")
				.tracker(80, 2, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.entity(EntityHealingBabyDragon.class)
				.id(new ResourceLocation(AdvDragonFight.MODID, "healing_baby_dragon"), id++)
				.name("advdragonfight.HealingBabyDragon")
				.tracker(80, 2, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.entity(EntityFireBabyDragon.class)
				.id(new ResourceLocation(AdvDragonFight.MODID, "fire_baby_dragon"), id++)
				.name("advdragonfight.FireBabyDragon")
				.tracker(80, 2, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.entity(EntityScale.class)
				.id(new ResourceLocation(AdvDragonFight.MODID, "scale_arrow"), id++)
				.name("advdragonfight.ScaleArrow")
				.tracker(64, 20, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.entity(EntitySmallDragonFireball.class)
				.id(new ResourceLocation(AdvDragonFight.MODID, "small_dragon_fireball"), id++)
				.name("advdragonfight.SmallDragonFireball")
				.tracker(64, 10, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.entity(EntityDragonEffectBall.class)
				.id(new ResourceLocation(AdvDragonFight.MODID, "dragon_effect_ball"), id++)
				.name("advdragonfight.DragonEffectBall")
				.tracker(64, 10, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.entity(EntityScaledDragon.class)
				.id(new ResourceLocation(AdvDragonFight.MODID, "scaled_dragon"), id++)
				.name("advdragonfight.ScaledDragon")
				.tracker(160, 2, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.entity(EntityDelaySpawn.class)
				.id(new ResourceLocation(AdvDragonFight.MODID, "delay_spawn"), id++)
				.name("advdragonfight.DelaySpawn")
				.tracker(64, 5, false).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.entity(EntityFloatingItem.class)
				.id(new ResourceLocation(AdvDragonFight.MODID, "floating_item"), id++)
				.name("advdragonfight.FloatingItem")
				.tracker(64, 20, true).build());
		

		event.getRegistry().register(EntityEntryBuilder.create()
				.entity(EntityEnderGuardian.class)
				.id(new ResourceLocation(AdvDragonFight.MODID, "ender_guardian"), id++)
				.name("advdragonfight.EnderGuardian")
				.tracker(64, 3, true).build());
	}
}
