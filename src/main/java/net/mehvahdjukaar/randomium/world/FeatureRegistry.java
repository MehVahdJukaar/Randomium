package net.mehvahdjukaar.randomium.world;

import com.mojang.serialization.Codec;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.configs.CommonConfigs;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.Random;

public class FeatureRegistry {


    public static final ConfiguredFeature<?, ?> RANDOMIUM_ORE_CONFIGURED_FEATURE = Randomium.RANDOMIUM_ORE_FEATURE.get().configured(
            new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, Randomium.RANDOMIUM_ORE.get().defaultBlockState(), 1))
            .range(128).squared().count(CommonConfigs.SPAWN_PER_CHUNK.get());

    public static final ConfiguredFeature<?, ?> RANDOMIUM_ORE_END_CONFIGURED_FEATURE = Randomium.RANDOMIUM_ORE_FEATURE.get().configured(
            new OreFeatureConfig(new BlockMatchRuleTest(Blocks.END_STONE), Randomium.RANDOMIUM_END_ORE.get().defaultBlockState(), 1))
            .range(128).squared().count(CommonConfigs.SPAWN_PER_CHUNK_END.get());

    public static void init(){

        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Randomium.MOD_ID, "randomium_ore"),
                RANDOMIUM_ORE_CONFIGURED_FEATURE);

        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Randomium.MOD_ID, "randomium_ore_end"),
                RANDOMIUM_ORE_END_CONFIGURED_FEATURE);

    }

    //@SubscribeEvent
    public static void addFeatureToBiomes(BiomeLoadingEvent event) {
        Biome.Category c = event.getCategory();
        if(c == Biome.Category.THEEND){
            event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).add(() -> RANDOMIUM_ORE_END_CONFIGURED_FEATURE);
        }
        else if (c != Biome.Category.NETHER) {
            event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).add(() -> RANDOMIUM_ORE_CONFIGURED_FEATURE);
        }
    }


    public static class RandomiumFeature extends Feature<OreFeatureConfig>{
        public RandomiumFeature(Codec<OreFeatureConfig> codec) {
            super(codec);
        }

        @Override
        public boolean place(ISeedReader reader, ChunkGenerator generator, Random random, BlockPos pos, OreFeatureConfig config) {
            if (reader.getLevel().dimensionType().natural() || reader.getBiome(pos).getBiomeCategory() == Biome.Category.THEEND) {
                if (config.target.test(reader.getBlockState(pos), random)) {
                    reader.setBlock(pos, config.state, 2);
                    return true;
                }
            }
            return false;
        }
    }
}
