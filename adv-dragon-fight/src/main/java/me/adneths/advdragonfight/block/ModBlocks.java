package me.adneths.advdragonfight.block;

import me.adneths.advdragonfight.AdvDragonFight;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(AdvDragonFight.MODID)
@Mod.EventBusSubscriber(modid = AdvDragonFight.MODID)
public class ModBlocks
{
	@ObjectHolder(AdvDragonFight.MODID+":dragon_fire")
	public static final Block dragonFire = null;
	
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<Block> event) {
		event.getRegistry().register(new BlockDragonFire().setRegistryName(AdvDragonFight.MODID, "dragon_fire"));
	}
}
