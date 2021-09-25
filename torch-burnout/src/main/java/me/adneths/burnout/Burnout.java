package me.adneths.burnout;

import java.util.function.ToIntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.adneths.burnout.blocks.BasicBurnoutBlock;
import me.adneths.burnout.blocks.BurnoutCampfireBlock;
import me.adneths.burnout.blocks.BurnoutCarvedPumpkinBlock;
import me.adneths.burnout.blocks.BurnoutEndRodBlock;
import me.adneths.burnout.blocks.BurnoutLanternBlock;
import me.adneths.burnout.blocks.BurnoutRedstoneLampBlock;
import me.adneths.burnout.blocks.BurnoutTorchBlock;
import me.adneths.burnout.blocks.BurnoutWallTorchBlock;
import me.adneths.burnout.client.renderer.BurnoutCampfireTileRenderer;
import me.adneths.burnout.debug.DrainFuelItem;
import me.adneths.burnout.init.ModContents;
import me.adneths.burnout.init.ModTileEntity;
import me.adneths.burnout.inventory.BurnoutGroup;
import me.adneths.burnout.item.BurnoutItem;
import me.adneths.burnout.tile.BurnoutCampfireTile;
import me.adneths.burnout.tile.BurnoutableTile;
import me.adneths.burnout.util.FuelDurations;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractBlock.IExtendedPositionPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("burnout")
public class Burnout
{
	public static final String MODID = "burnout";
	
    private static final Logger LOGGER = LogManager.getLogger();

    public Burnout() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    	LOGGER.info("Loading [Torch Burnout]");
    }
    
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
    	
    }
    
    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientStartupEvents {
    	
    	@SubscribeEvent
    	public static void onClientSetupEvent(FMLClientSetupEvent event) {
    		RenderTypeLookup.setRenderLayer(ModContents.Blocks.campfire, RenderType.getCutout());
    		RenderTypeLookup.setRenderLayer(ModContents.Blocks.soulCampfire, RenderType.getCutout());
    		
    		ClientRegistry.bindTileEntityRenderer(ModTileEntity.CAMPFIRE, dispatcher -> {return new BurnoutCampfireTileRenderer(dispatcher);});
    	}
    	
    }
    
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
    	
    	@SubscribeEvent
    	public static void onItemRegistery(final RegistryEvent.Register<Item> event)
    	{
    		for(Block block : ModContents.Blocks.allBlocks())
    			if(block.getRegistryName().toString().indexOf("wall")==-1)
    				event.getRegistry().register(new BlockItem(block, new Item.Properties().group(BurnoutGroup.burnout)).setRegistryName(block.getRegistryName()));
    		event.getRegistry().register(new DrainFuelItem());
    		event.getRegistry().register(new BurnoutItem(new Item.Properties().group(BurnoutGroup.burnout)).setTranslationKey(Burnout.MODID, "glowstone_dust").setRegistryName(Burnout.MODID, "glowstone_dust"));
    	}
    	
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
        	event.getRegistry().register(new BurnoutTorchBlock(AbstractBlock.Properties.create(
				Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance()
        			.setLightLevel(getLightValue(14))
        			.sound(SoundType.WOOD).lootFrom(Blocks.TORCH), (IParticleData)ParticleTypes.FLAME)
        			.setTranslationKey(Burnout.MODID, "torch").setRegistryName(Burnout.MODID, "torch"));
        	event.getRegistry().register(new BurnoutWallTorchBlock(AbstractBlock.Properties.create(
				Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance()
        			.setLightLevel(getLightValue(14))
        			.sound(SoundType.WOOD).lootFrom(Blocks.TORCH), (IParticleData)ParticleTypes.FLAME)
        			.setTranslationKey(Burnout.MODID, "wall_torch").setRegistryName(Burnout.MODID, "wall_torch"));
        	
    		event.getRegistry().register(new BurnoutTorchBlock(AbstractBlock.Properties.create(
				Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance()
    				.setLightLevel(getLightValue(10))
    				.sound(SoundType.WOOD).lootFrom(Blocks.SOUL_TORCH), (IParticleData)ParticleTypes.SOUL_FIRE_FLAME)
    				.setTranslationKey(Burnout.MODID, "soul_torch").setRegistryName(Burnout.MODID,"soul_torch"));
        	event.getRegistry().register(new BurnoutWallTorchBlock(AbstractBlock.Properties.create(
    			Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance()
        			.setLightLevel(getLightValue(10))
        			.sound(SoundType.WOOD).lootFrom(Blocks.SOUL_TORCH), (IParticleData)ParticleTypes.SOUL_FIRE_FLAME)
        			.setTranslationKey(Burnout.MODID, "soul_wall_torch").setRegistryName(Burnout.MODID,"soul_wall_torch"));
        	
        	event.getRegistry().register(new BasicBurnoutBlock(AbstractBlock.Properties.create(
    			Material.GLASS, MaterialColor.SAND).hardnessAndResistance(0.3F).sound(SoundType.GLASS)
        			.setLightLevel(getLightValue(15)),
        			FuelDurations.Max.GLOWSTONE, FuelDurations.Refill.GLOWSTONE, item -> item.equals(Items.GLOWSTONE_DUST))
        			.setTranslationKey(Burnout.MODID, "glowstone").setRegistryName(Burnout.MODID, "glowstone"));
        	
        	event.getRegistry().register(new BasicBurnoutBlock(AbstractBlock.Properties.create(
    			Material.GLASS, MaterialColor.QUARTZ).hardnessAndResistance(0.3F)
        			.sound(SoundType.GLASS).setLightLevel(getLightValue(15)),
        			FuelDurations.Max.SEALANTERN, FuelDurations.Refill.SEALANTERN, item -> item.equals(Items.PRISMARINE_CRYSTALS))
        			.setTranslationKey(Burnout.MODID, "sea_lantern").setRegistryName(Burnout.MODID, "sea_lantern"));
        	
        	event.getRegistry().register(new BasicBurnoutBlock(AbstractBlock.Properties.create(
    			Material.ORGANIC, MaterialColor.RED).hardnessAndResistance(1.0F)
        			.sound(SoundType.SHROOMLIGHT).setLightLevel(getLightValue(15)),
        			FuelDurations.Max.SHROOMLIGHT, FuelDurations.Refill.SHROOMLIGHT, item -> item.equals(Items.CRIMSON_FUNGUS)||item.equals(Items.WARPED_FUNGUS))
        			.setTranslationKey(Burnout.MODID, "shroomlight").setRegistryName(Burnout.MODID, "shroomlight"));
        	
        	event.getRegistry().register(new BurnoutLanternBlock(AbstractBlock.Properties.create(
    			Material.IRON).setRequiresTool().hardnessAndResistance(3.5F)
        			.sound(SoundType.LANTERN).setLightLevel(getLightValue(15))
        			.notSolid()).setTranslationKey(Burnout.MODID, "lantern").setRegistryName(Burnout.MODID, "lantern"));
        	
        	event.getRegistry().register(new BurnoutLanternBlock(AbstractBlock.Properties.create(
    			Material.IRON).setRequiresTool().hardnessAndResistance(3.5F)
        			.sound(SoundType.LANTERN).setLightLevel(getLightValue(10))
        			.notSolid()).setTranslationKey(Burnout.MODID, "soul_lantern").setRegistryName(Burnout.MODID, "soul_lantern"));
        	
        	event.getRegistry().register(new BurnoutEndRodBlock(AbstractBlock.Properties.create(
    			Material.MISCELLANEOUS).zeroHardnessAndResistance().setLightLevel(getLightValue(14))
        			.sound(SoundType.WOOD).notSolid()).setTranslationKey(Burnout.MODID, "end_rod").setRegistryName(Burnout.MODID, "end_rod"));
        	
        	event.getRegistry().register(new BurnoutCarvedPumpkinBlock(AbstractBlock.Properties.create(
        			Material.GOURD, MaterialColor.ADOBE).hardnessAndResistance(1.0F)
        			.sound(SoundType.WOOD).setLightLevel(getLightValue(15))
        			.setAllowsSpawn(new IExtendedPositionPredicate<EntityType<?>>() {
						@Override public boolean test(BlockState p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_, EntityType<?> p_test_4_) {
							return true;
						}
					})).setTranslationKey(Burnout.MODID, "jack_o_lantern").setRegistryName(Burnout.MODID, "jack_o_lantern"));
        	
        	event.getRegistry().register(new BurnoutRedstoneLampBlock(AbstractBlock.Properties.create(
        			Material.REDSTONE_LIGHT).setLightLevel(getLightValue(15))
        			.hardnessAndResistance(0.3F).sound(SoundType.GLASS).setAllowsSpawn(new IExtendedPositionPredicate<EntityType<?>>() {
						@Override public boolean test(BlockState p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_, EntityType<?> p_test_4_) {
							return true;
						}
					})).setTranslationKey(Burnout.MODID, "redstone_lamp").setRegistryName(Burnout.MODID, "redstone_lamp"));
        	
        	event.getRegistry().register(new BurnoutCampfireBlock(true, 1, AbstractBlock.Properties.create(
        			Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F).sound(SoundType.WOOD)
        			.setLightLevel(getLightValue(15)).notSolid()).setTranslationKey(Burnout.MODID, "campfire").setRegistryName(Burnout.MODID, "campfire"));
        	event.getRegistry().register(new BurnoutCampfireBlock(true, 1, AbstractBlock.Properties.create(
        			Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F).sound(SoundType.WOOD)
        			.setLightLevel(getLightValue(10)).notSolid()).setTranslationKey(Burnout.MODID, "soul_campfire").setRegistryName(Burnout.MODID, "soul_campfire"));
        }
        
        private static ToIntFunction<BlockState> getLightValue(int light)
        {
        	return state -> {return state.get(BlockStateProperties.LIT)?light:0;};
        }
        
        @SubscribeEvent
        public static void registerTE(RegistryEvent.Register<TileEntityType<?>> event) {
        	TileEntityType<?> type = TileEntityType.Builder.create(BurnoutableTile::new, ModContents.Blocks.allBlocksNoCampfire()).build(null);
        	type.setRegistryName(Burnout.MODID, "tile.burnoutable");
        	event.getRegistry().register(type);
        	type = TileEntityType.Builder.create(BurnoutCampfireTile::new, ModContents.Blocks.campfire, ModContents.Blocks.soulCampfire).build(null);
        	type.setRegistryName(Burnout.MODID, "tile.burnout_campfire");
        	event.getRegistry().register(type);
        }
    }
}
