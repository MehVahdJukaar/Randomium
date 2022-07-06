package net.mehvahdjukaar.randomium.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.mehvahdjukaar.randomium.RandomiumClient;

public class RandomiumFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        RandomiumClient.init();
    }
}
