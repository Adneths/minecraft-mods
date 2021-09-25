package me.adneths.bottle_bee.init;

import com.google.common.collect.ImmutableList;

import me.adneths.bottle_bee.world.gen.KillerBeehiveTreeDecorator;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.MultipleRandomFeatureConfig;
import net.minecraft.world.gen.feature.TwoLayerFeature;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;

public class ModFeatures {

		public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> SUPER_BIRCH_KILLER_BEES_0002 = register("super_birch_killer_bees_0002", Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.BIRCH_LOG.getDefaultState()), new SimpleBlockStateProvider(Blocks.BIRCH_LEAVES.getDefaultState()), new BlobFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0), 3), new StraightTrunkPlacer(5, 2, 6), new TwoLayerFeature(1, 0, 1))).setIgnoreVines().setDecorators(ImmutableList.of(Features.Placements.BEES_0002_PLACEMENT)).build()));
		public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> OAK_KILLER_BEES_0002 = register("oak_killer_bees_0002", Feature.TREE.withConfiguration(Features.OAK.getConfig().func_236685_a_(ImmutableList.of(ModFeatures.Placements.BEES_0002_PLACEMENT))));
		public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> OAK_KILLER_BEES_002 = register("oak_killer_bees_002", Feature.TREE.withConfiguration(Features.OAK.getConfig().func_236685_a_(ImmutableList.of(ModFeatures.Placements.BEES_002_PLACEMENT))));
		public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> OAK_KILLER_BEES_005 = register("oak_killer_bees_005", Feature.TREE.withConfiguration(Features.OAK.getConfig().func_236685_a_(ImmutableList.of(ModFeatures.Placements.BEES_005_PLACEMENT))));
		public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> BIRCH_KILLER_BEES_0002 = register("birch_killer_bees_0002", Feature.TREE.withConfiguration(Features.BIRCH.getConfig().func_236685_a_(ImmutableList.of(ModFeatures.Placements.BEES_0002_PLACEMENT))));
		public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> BIRCH_KILLER_BEES_002 = register("birch_killer_bees_002", Feature.TREE.withConfiguration(Features.BIRCH.getConfig().func_236685_a_(ImmutableList.of(ModFeatures.Placements.BEES_002_PLACEMENT))));
		public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> BIRCH_KILLER_BEES_005 = register("birch_killer_bees_005", Feature.TREE.withConfiguration(Features.BIRCH.getConfig().func_236685_a_(ImmutableList.of(ModFeatures.Placements.BEES_005_PLACEMENT))));
		public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> FANCY_OAK_KILLER_BEES_0002 = register("fancy_oak_killer_bees_0002", Feature.TREE.withConfiguration(Features.FANCY_OAK.getConfig().func_236685_a_(ImmutableList.of(ModFeatures.Placements.BEES_0002_PLACEMENT))));
		public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> FANCY_OAK_KILLER_BEES_002 = register("fancy_oak_killer_bees_002", Feature.TREE.withConfiguration(Features.FANCY_OAK.getConfig().func_236685_a_(ImmutableList.of(ModFeatures.Placements.BEES_002_PLACEMENT))));
		public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> FANCY_OAK_KILLER_BEES_005 = register("fancy_oak_killer_bees_005", Feature.TREE.withConfiguration(Features.FANCY_OAK.getConfig().func_236685_a_(ImmutableList.of(ModFeatures.Placements.BEES_005_PLACEMENT))));
		
		
		public static final ConfiguredFeature<?, ?> FOREST_FLOWER_TREES = register("forest_flower_trees", Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(BIRCH_KILLER_BEES_002.withChance(0.2F), FANCY_OAK_KILLER_BEES_002.withChance(0.1F)), OAK_KILLER_BEES_002)).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(6, 0.1F, 1))));
		public static final ConfiguredFeature<?, ?> BIRCH_TALL = register("birch_tall", Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(SUPER_BIRCH_KILLER_BEES_0002.withChance(0.5F)), BIRCH_KILLER_BEES_0002)).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
		public static final ConfiguredFeature<?, ?> TREES_BIRCH = register("trees_birch", BIRCH_KILLER_BEES_0002.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
		public static final ConfiguredFeature<?, ?> BIRCH_OTHER = register("birch_other", Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(BIRCH_KILLER_BEES_0002.withChance(0.2F), FANCY_OAK_KILLER_BEES_0002.withChance(0.1F)), OAK_KILLER_BEES_0002)).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
		public static final ConfiguredFeature<?, ?> PLAIN_VEGETATION = register("plain_vegetation", Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(FANCY_OAK_KILLER_BEES_005.withChance(0.33333334F)), OAK_KILLER_BEES_005)).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(0, 0.05F, 1))));			   
		
		
		private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> register(String key, ConfiguredFeature<FC, ?> configuredFeature) {
			return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, key, configuredFeature);
		}
		
		public static final class Placements {
			public static final KillerBeehiveTreeDecorator BEES_0002_PLACEMENT = new KillerBeehiveTreeDecorator(0);//0.002F);
			public static final KillerBeehiveTreeDecorator BEES_002_PLACEMENT = new KillerBeehiveTreeDecorator(0);//0.02F);
			public static final KillerBeehiveTreeDecorator BEES_005_PLACEMENT = new KillerBeehiveTreeDecorator(0);//0.05F);
		}
	
}
