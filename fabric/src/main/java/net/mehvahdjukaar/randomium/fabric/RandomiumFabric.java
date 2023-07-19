package net.mehvahdjukaar.randomium.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
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

        PlatHelper.addCommonSetup(RandomiumFabric::commonSetup);
        ServerWorldEvents.LOAD.register((s, l) -> Randomium.populateLoot(l));
    }

    private static void commonSetup() {
        BiomeModifications.addFeature(context -> context.hasTag(BiomeTags.IS_OVERWORLD) || context.hasTag(BiomeTags.IS_END),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ResourceKey.create(Registries.PLACED_FEATURE, Randomium.res("ore_randomium")));
    }


}
