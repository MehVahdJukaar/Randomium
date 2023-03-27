package net.mehvahdjukaar.randomium.integration;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.mehvahdjukaar.randomium.common.items.AnyItem;

@REIPluginClient
public class REICompat implements REIClientPlugin {


    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.add(DefaultCraftingDisplay.of(AnyItem.createDuplicateRecipe()));

    }
}
