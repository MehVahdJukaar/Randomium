package net.mehvahdjukaar.randomium.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.mehvahdjukaar.moonlight.fabric.MLFabricSetupCallbacks;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.RandomiumClient;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;

public class RandomiumFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        Randomium.commonInit();
        MLFabricSetupCallbacks.COMMON_SETUP.add(RandomiumFabric::commonSetup);
        MLFabricSetupCallbacks.CLIENT_SETUP.add(RandomiumClient::init);
    }

    private static void commonSetup() {
        Randomium.commonSetup();
        BiomeModifications.addFeature(context -> context.hasTag(BiomeTags.IS_OVERWORLD) || context.hasTag(BiomeTags.IS_END),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ResourceKey.create(Registries.PLACED_FEATURE, Randomium.res("ore_randomium")));
    }


}
