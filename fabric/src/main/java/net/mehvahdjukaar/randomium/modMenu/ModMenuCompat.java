package net.mehvahdjukaar.randomium.modMenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.mehvahdjukaar.moonlight.core.client.config.MoonlightConfigSelectScreen;
import net.mehvahdjukaar.randomium.Randomium;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> MoonlightConfigSelectScreen.create(Randomium.MOD_ID, parent, Randomium.res(
                "textures/blocks/randomium_block.png"
        ));

    }
}