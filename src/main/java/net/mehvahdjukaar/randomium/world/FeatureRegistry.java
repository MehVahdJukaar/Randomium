package net.mehvahdjukaar.randomium.world;

import com.google.common.collect.ImmutableList;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.configs.CommonConfigs;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.List;

public class FeatureRegistry {

    public static final ImmutableList<OreConfiguration.TargetBlockState> RANDOMIUM_TARGET_LIST = ImmutableList.of(
            OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, Randomium.RANDOMIUM_ORE.get().defaultBlockState()),
            OreConfiguration.target(new BlockMatchTest(Blocks.END_STONE), Randomium.RANDOMIUM_ORE_END.get().defaultBlockState()),
            OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, Randomium.RANDOMIUM_ORE_DEEP.get().defaultBlockState()));

    public static final ConfiguredFeature<?, ?> RANDOMIUM_ORE_CONFIGURED = Feature.ORE.configured(
            new OreConfiguration(RANDOMIUM_TARGET_LIST, 3));

    public static final PlacedFeature RANDOMIUM_ORE_PLACED = RANDOMIUM_ORE_CONFIGURED.placed(
            commonOrePlacement(CommonConfigs.SPAWN_PER_CHUNK.get(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6),
                    VerticalAnchor.absolute(152))));

    private static List<PlacementModifier> orePlacement(PlacementModifier modifier, PlacementModifier modifier1) {
        return List.of(modifier, InSquarePlacement.spread(), modifier1, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int attempts, PlacementModifier distribution) {
        return orePlacement(CountPlacement.of(attempts), distribution);
    }


    public static void init() {

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Randomium.res("ore_randomium"), RANDOMIUM_ORE_CONFIGURED);
        Registry.register(BuiltinRegistries.PLACED_FEATURE, Randomium.res("ore_randomium"), RANDOMIUM_ORE_PLACED);
    }

    public static void addFeatureToBiomes(BiomeLoadingEvent event) {
        Biome.BiomeCategory c = event.getCategory();
        if (c != Biome.BiomeCategory.NETHER) {
            event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).add(() -> RANDOMIUM_ORE_PLACED);
        }
    }

}
