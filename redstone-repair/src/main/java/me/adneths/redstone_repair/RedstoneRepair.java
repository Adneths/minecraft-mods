package me.adneths.redstone_repair;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.adneths.redstone_repair.block.BrokenComparatorBlock;
import me.adneths.redstone_repair.block.BrokenDispenserBlock;
import me.adneths.redstone_repair.block.BrokenDropperBlock;
import me.adneths.redstone_repair.block.BrokenObserverBlock;
import me.adneths.redstone_repair.block.BrokenPistonBlock;
import me.adneths.redstone_repair.block.BrokenRedstoneTorchBlock;
import me.adneths.redstone_repair.block.BrokenRedstoneWallTorchBlock;
import me.adneths.redstone_repair.block.BrokenRedstoneWireBlock;
import me.adneths.redstone_repair.block.BrokenRepeaterBlock;
import me.adneths.redstone_repair.block.UpdateRepairableBlock;
import me.adneths.redstone_repair.event.BreakingEvent;
import me.adneths.redstone_repair.init.ItemGroups;
import me.adneths.redstone_repair.init.ModBlocks;
import me.adneths.redstone_repair.init.ModItems;
import me.adneths.redstone_repair.item.BlockVarientItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.Items;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(RedstoneRepair.MODID)
public class RedstoneRepair
{
	public static final String MODID = "redstone_repair";
	
    private static final Logger LOGGER = LogManager.getLogger();

    public RedstoneRepair() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
		LOGGER.info("Loading [Redstone Repair]");
		BreakingEvent.init();
    }

	private void doClientStuff(final FMLClientSetupEvent event)
    {
		RenderTypeLookup.setRenderLayer(ModBlocks.BROKEN_REDSTONE_TORCH, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.BROKEN_REDSTONE_WALL_TORCH, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.BROKEN_REDSTONE_WIRE, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.BROKEN_COMPARATOR, RenderType.getCutout());
		
		event.enqueueWork(new Runnable() {
			@Override
			public void run() {
				IItemPropertyGetter getter = (stack, world, entity) -> {
					return stack.getTag()==null ? -1 : BrokenPistonBlock.PistonPart.getPartByName(stack.getTag().getString(BrokenPistonBlock.PISTON_PART)).getId();
				};
				ItemModelsProperties.registerProperty(ModItems.BROKEN_PISTON, new ResourceLocation(RedstoneRepair.MODID, "piston_part"), getter);
				ItemModelsProperties.registerProperty(ModItems.BROKEN_STICKY_PISTON, new ResourceLocation(RedstoneRepair.MODID, "piston_part"), getter);
				
				ItemModelsProperties.registerProperty(ModItems.BROKEN_REPEATER, new ResourceLocation(RedstoneRepair.MODID, "repeater_part"), (stack, world, entity) -> {
					return stack.getTag()==null ? -1 : BrokenRepeaterBlock.RepeaterPart.getPartByName(stack.getTag().getString(BrokenRepeaterBlock.REPEATER_PART)).getId();
				});
				
				ItemModelsProperties.registerProperty(ModItems.BROKEN_COMPARATOR, new ResourceLocation(RedstoneRepair.MODID, "comparator_part"), (stack, world, entity) -> {
					return stack.getTag()==null ? -1 : BrokenComparatorBlock.ComparatorPart.getPartByName(stack.getTag().getString(BrokenComparatorBlock.COMPARATOR_PART)).getId();
				});
				
				ItemModelsProperties.registerProperty(ModItems.BROKEN_DISPENSER, new ResourceLocation(RedstoneRepair.MODID, "dispenser_part"), (stack, world, entity) -> {
					return stack.getTag()==null ? -1 : BrokenDispenserBlock.DispenserPart.getPartByName(stack.getTag().getString(BrokenDispenserBlock.DISPENSER_PART)).getId();
				});
				
				ItemModelsProperties.registerProperty(ModItems.BROKEN_OBSERVER, new ResourceLocation(RedstoneRepair.MODID, "observer_part"), (stack, world, entity) -> {
					return stack.getTag()==null ? -1 : BrokenObserverBlock.ObserverPart.getPartByName(stack.getTag().getString(BrokenObserverBlock.OBSERVER_PART)).getId();
				});
			}
		});
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) 
    {
    	
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemssRegistry(final RegistryEvent.Register<Item> event) {
        	IForgeRegistry<Item> registry = event.getRegistry();

        	registry.register(new WallOrFloorItem(ModBlocks.BROKEN_REDSTONE_TORCH, ModBlocks.BROKEN_REDSTONE_WALL_TORCH, (new Item.Properties()).group(ItemGroups.MAIN)).setRegistryName(RedstoneRepair.MODID, "broken_redstone_torch"));
        	
        	registry.register(new BlockVarientItem(ModBlocks.BROKEN_PISTON, new Item.Properties().group(ItemGroups.MAIN), (stack) -> {return stack.getTag()==null ? "" : stack.getTag().getString(BrokenPistonBlock.PISTON_PART);}).setRegistryName(ModBlocks.BROKEN_PISTON.getRegistryName()));
        	registry.register(new BlockVarientItem(ModBlocks.BROKEN_STICKY_PISTON, new Item.Properties().group(ItemGroups.MAIN), (stack) -> {return stack.getTag()==null ? "" : stack.getTag().getString(BrokenPistonBlock.PISTON_PART);}).setRegistryName(ModBlocks.BROKEN_STICKY_PISTON.getRegistryName()));
        	registry.register(new BlockVarientItem(ModBlocks.BROKEN_REPEATER, new Item.Properties().group(ItemGroups.MAIN), (stack) -> {return stack.getTag()==null ? "" : stack.getTag().getString(BrokenRepeaterBlock.REPEATER_PART);}).setRegistryName(ModBlocks.BROKEN_REPEATER.getRegistryName()));
        	registry.register(new BlockVarientItem(ModBlocks.BROKEN_COMPARATOR, new Item.Properties().group(ItemGroups.MAIN), (stack) -> {return stack.getTag()==null ? "" : stack.getTag().getString(BrokenComparatorBlock.COMPARATOR_PART);}).setRegistryName(ModBlocks.BROKEN_COMPARATOR.getRegistryName()));
        	registry.register(new BlockVarientItem(ModBlocks.BROKEN_OBSERVER, new Item.Properties().group(ItemGroups.MAIN), (stack) -> {return stack.getTag()==null ? "" : stack.getTag().getString(BrokenObserverBlock.OBSERVER_PART);}).setRegistryName(ModBlocks.BROKEN_OBSERVER.getRegistryName()));
        	registry.register(new BlockVarientItem(ModBlocks.BROKEN_DISPENSER, new Item.Properties().group(ItemGroups.MAIN), (stack) -> {return stack.getTag()==null ? "" : stack.getTag().getString(BrokenDispenserBlock.DISPENSER_PART);}).setRegistryName(ModBlocks.BROKEN_DISPENSER.getRegistryName()));

        	for(Block block : ModBlocks.blocksWithItem())
        		registry.register(new BlockItem(block, new Item.Properties().group(ItemGroups.MAIN)).setRegistryName(block.getRegistryName()));
        }
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
        	IForgeRegistry<Block> registry = event.getRegistry();
        	
        	registry.register(new UpdateRepairableBlock(AbstractBlock.Properties.create(Material.REDSTONE_LIGHT).hardnessAndResistance(0.3F).sound(SoundType.GLASS).setAllowsSpawn((state, reader, pos, type) -> {return true;})) {
				@Override public int repairCost(BlockState current) {
					return 1;
				}
				@Override public Item getRepairMaterial(BlockState current) {
					return Items.REDSTONE_TORCH;
				}
				@Override public BlockState repairedState(BlockState current) {
					return Blocks.REDSTONE_LAMP.getDefaultState();
				}
				@Override
				public BlockState randomBreak(BlockState pre) {
					return this.getDefaultState();
				}}.setRegistryName(RedstoneRepair.MODID, "broken_redstone_lamp"));
        	registry.register(new BrokenRedstoneTorchBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WOOD)).setRegistryName(RedstoneRepair.MODID, "broken_redstone_torch"));
        	registry.register(new BrokenRedstoneWallTorchBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WOOD)).setRegistryName(RedstoneRepair.MODID, "broken_redstone_wall_torch"));
        	registry.register(new BrokenRedstoneWireBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance()).setRegistryName(RedstoneRepair.MODID, "broken_redstone_wire"));
        	registry.register(new BrokenPistonBlock(AbstractBlock.Properties.create(Material.PISTON).hardnessAndResistance(1.5F).setOpaque((state, reader, pos) -> {return false;}), false).setRegistryName(RedstoneRepair.MODID, "broken_piston"));
        	registry.register(new BrokenPistonBlock(AbstractBlock.Properties.create(Material.PISTON).hardnessAndResistance(1.5F).setOpaque((state, reader, pos) -> {return false;}), true).setRegistryName(RedstoneRepair.MODID, "broken_sticky_piston"));
        	registry.register(new BrokenRepeaterBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS).zeroHardnessAndResistance().sound(SoundType.WOOD)).setRegistryName(RedstoneRepair.MODID, "broken_repeater"));
        	registry.register(new BrokenComparatorBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS).zeroHardnessAndResistance().sound(SoundType.WOOD)).setRegistryName(RedstoneRepair.MODID, "broken_comparator"));
        	registry.register(new BrokenDropperBlock(AbstractBlock.Properties.create(Material.ROCK).setRequiresTool().hardnessAndResistance(3.5F)).setRegistryName(RedstoneRepair.MODID, "broken_dropper"));
        	registry.register(new BrokenDispenserBlock(AbstractBlock.Properties.create(Material.ROCK).setRequiresTool().hardnessAndResistance(3.5F)).setRegistryName(RedstoneRepair.MODID, "broken_dispenser"));
        	registry.register(new BrokenObserverBlock(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3.0F).setRequiresTool().setOpaque((state, reader, pos) -> {return false;})).setRegistryName(RedstoneRepair.MODID, "broken_observer"));
        }
    }
}
