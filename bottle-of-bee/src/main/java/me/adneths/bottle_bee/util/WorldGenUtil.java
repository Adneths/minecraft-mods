package me.adneths.bottle_bee.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.Lists;

import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@SuppressWarnings("unchecked")
public class WorldGenUtil {

	public static void printFeaturesOfBiome(Biome biome)
    {
    	int i = 0;
		for(List<Supplier<ConfiguredFeature<?, ?>>> list : biome.getGenerationSettings().getFeatures())
		{
			System.out.println(GenerationStage.Decoration.values()[i++]);
			for(Supplier<ConfiguredFeature<?, ?>> item : list)
			{
				System.out.printf("-%s%n", WorldGenRegistries.CONFIGURED_FEATURE.getKey(item.get()));
			}
		}
    }

    @SuppressWarnings("rawtypes")
	public static void overrideFeatureInBiome(Biome biome, GenerationStage.Decoration decoration, ConfiguredFeature<?, ?>... configuredFeatures)
    {
		List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = new ArrayList(
            biome.getGenerationSettings().getFeatures() /* List of Configured Features */
        );

        while (biomeFeatures.size() <= decoration.ordinal()) {
            biomeFeatures.add(Lists.newArrayList());
        }
        
        List<Supplier<ConfiguredFeature<?, ?>>> features = new ArrayList<>(biomeFeatures.get(decoration.ordinal()));
        for(int j = 0; j < configuredFeatures.length; j+=2)
        	for(int i = 0; i < features.size(); i++)
        		if(features.get(i).get()==configuredFeatures[j])
        		{
        			int replacement = j + 1;
        			features.set(i, () -> configuredFeatures[replacement]);
        		}
        biomeFeatures.set(decoration.ordinal(), features);

        /* Change field_242484_f that contains the Configured Features of the Biome*/
        ObfuscationReflectionHelper.setPrivateValue(BiomeGenerationSettings.class, biome.getGenerationSettings(), biomeFeatures, "field_242484_f");
    }
    
    @SuppressWarnings("rawtypes")
	public static void overrideFeatureInBiome(Biome biome, ConfiguredFeature<?, ?>... configuredFeatures)
    {
		List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = new ArrayList(
            biome.getGenerationSettings().getFeatures() /* List of Configured Features */
        );
		
        for(int n = 0; n < biomeFeatures.size(); n++)
        {
	        List<Supplier<ConfiguredFeature<?, ?>>> features = new ArrayList<>(biomeFeatures.get(n));
	        for(int j = 0; j < configuredFeatures.length; j+=2)
	        	for(int i = 0; i < features.size(); i++)
	        		if(features.get(i).get()==configuredFeatures[j])
	        		{
	        			int replacement = j + 1;
	        			features.set(i, () -> configuredFeatures[replacement]);
	        		}
	        biomeFeatures.set(n, features);
        }

        /* Change field_242484_f that contains the Configured Features of the Biome*/
        ObfuscationReflectionHelper.setPrivateValue(BiomeGenerationSettings.class, biome.getGenerationSettings(), biomeFeatures, "field_242484_f");
    }

    @SuppressWarnings("rawtypes")
    public static void addFeatureToBiome(Biome biome, GenerationStage.Decoration decoration, ConfiguredFeature<?, ?>... configuredFeature) {
        List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = new ArrayList(
            biome.getGenerationSettings().getFeatures() /* List of Configured Features */
        );

        while (biomeFeatures.size() <= decoration.ordinal()) {
            biomeFeatures.add(Lists.newArrayList());
        }
        
        List<Supplier<ConfiguredFeature<?, ?>>> features = new ArrayList<>(biomeFeatures.get(decoration.ordinal()));
        for(ConfiguredFeature<?, ?> feature : configuredFeature)
        	features.add(() -> feature);
        biomeFeatures.set(decoration.ordinal(), features);

        /* Change field_242484_f that contains the Configured Features of the Biome*/
        ObfuscationReflectionHelper.setPrivateValue(BiomeGenerationSettings.class, biome.getGenerationSettings(), biomeFeatures, "field_242484_f");
    }
    
    @SuppressWarnings("rawtypes")
	public static void removeFeatureFromBiome(Biome biome, GenerationStage.Decoration decoration, ConfiguredFeature<?, ?>... configuredFeature) {
        List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = new ArrayList(
            biome.getGenerationSettings().getFeatures() /* List of Configured Features */
        );

        while (biomeFeatures.size() <= decoration.ordinal()) {
            biomeFeatures.add(Lists.newArrayList());
        }
        
        
        List<Supplier<ConfiguredFeature<?, ?>>> features = new ArrayList<>(biomeFeatures.get(decoration.ordinal()));
        for(ConfiguredFeature<?, ?> feature : configuredFeature)
        	for(int i = 0; i < features.size(); i++)
        		if(features.get(i).get()==feature)
        			features.remove(i--);
        biomeFeatures.set(decoration.ordinal(), features);

        /* Change field_242484_f that contains the Configured Features of the Biome*/
        ObfuscationReflectionHelper.setPrivateValue(BiomeGenerationSettings.class, biome.getGenerationSettings(), biomeFeatures, "field_242484_f");
    }
    
    @SuppressWarnings("rawtypes")
	public static void removeFeatureFromBiome(Biome biome, ConfiguredFeature<?, ?>... configuredFeature) {
        List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = new ArrayList(
            biome.getGenerationSettings().getFeatures() /* List of Configured Features */
        );
        
        for(int n = 0; n < biomeFeatures.size(); n++)
        {
	        List<Supplier<ConfiguredFeature<?, ?>>> features = new ArrayList<>(biomeFeatures.get(n));
	        for(ConfiguredFeature<?, ?> feature : configuredFeature)
	        	for(int i = 0; i < features.size(); i++)
	        		if(features.get(i).get()==feature)
	        			features.remove(i--);
	        biomeFeatures.set(n, features);
        }

        /* Change field_242484_f that contains the Configured Features of the Biome*/
        ObfuscationReflectionHelper.setPrivateValue(BiomeGenerationSettings.class, biome.getGenerationSettings(), biomeFeatures, "field_242484_f");
    }
	
}
