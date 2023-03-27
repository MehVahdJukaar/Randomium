package net.mehvahdjukaar.randomium.modMenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.common.CommonConfigs;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> CommonConfigs.SPEC.makeScreen(parent, Randomium.res(
                "textures/blocks/randomium_block.png"
        ));

    }
}