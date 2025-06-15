package net.mehvahdjukaar.randomium.integration;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class EMICompat implements EmiPlugin {


    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.addRecipe();
    }
}
