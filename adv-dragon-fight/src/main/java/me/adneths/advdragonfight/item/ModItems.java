package me.adneths.advdragonfight.item;

import me.adneths.advdragonfight.AdvDragonFight;
import me.adneths.advdragonfight.inventory.CustomCreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(AdvDragonFight.MODID)
@Mod.EventBusSubscriber(modid = AdvDragonFight.MODID)
public class ModItems
{
	@ObjectHolder(AdvDragonFight.MODID+":dragon_scale")
	public static final Item dragonScale = null;
	@ObjectHolder(AdvDragonFight.MODID+":dragon_skin")
	public static final Item dragonSkin = null;
	@ObjectHolder(AdvDragonFight.MODID+":scaled_skin")
	public static final Item scaledSkin = null;
	
	@ObjectHolder(AdvDragonFight.MODID+":diff_adjuster")
	public static final Item diffAdjuster = null;
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemWithName(AdvDragonFight.MODID, "dragon_scale").setCreativeTab(CustomCreativeTabs.main));
		event.getRegistry().register(new ItemWithName(AdvDragonFight.MODID, "dragon_skin").setCreativeTab(CustomCreativeTabs.main));
		event.getRegistry().register(new ItemWithName(AdvDragonFight.MODID, "scaled_skin").setCreativeTab(CustomCreativeTabs.main));
		
		event.getRegistry().register(new ItemDifficultyAdjuster());
	}
}
