package net.mehvahdjukaar.randomium.world;

import com.google.common.collect.ImmutableList;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.configs.CommonConfigs;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

//load after registration
public class ModFeatures {

    private static List<OreConfiguration.TargetBlockState> getTargetList() {
        return ImmutableList.of(
                OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), Randomium.RANDOMIUM_ORE.get().defaultBlockState()),
                OreConfiguration.target(new BlockMatchTest(Blocks.END_STONE), Randomium.RANDOMIUM_ORE_END.get().defaultBlockState()),
                OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), Randomium.RANDOMIUM_ORE_DEEP.get().defaultBlockState()));
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier modifier, PlacementModifier modifier1) {
        return List.of(modifier, InSquarePlacement.spread(), modifier1, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int attempts, PlacementModifier distribution) {
        return orePlacement(CountPlacement.of(attempts), distribution);
    }


    public static final RegSupplier<ConfiguredFeature<OreConfiguration, Feature<OreConfiguration>>> RANDOMIUM_ORE_CONFIGURED =
            RegHelper.registerConfiguredFeature(Randomium.res("ore_randomium"),
                    () -> Feature.ORE, () -> new OreConfiguration(getTargetList(), 3));

    public static final RegSupplier<PlacedFeature> RANDOMIUM_ORE_PLACED =
            RegHelper.registerPlacedFeature(Randomium.res("ore_randomium"),
                    RANDOMIUM_ORE_CONFIGURED,
                    () -> commonOrePlacement(CommonConfigs.SPAWN_PER_CHUNK.get(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6),
                            VerticalAnchor.absolute(152))));

    public static void init() {
    }


}
