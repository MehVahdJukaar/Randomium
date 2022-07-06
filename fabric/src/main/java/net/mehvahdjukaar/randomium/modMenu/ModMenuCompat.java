package net.mehvahdjukaar.randomium.modMenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.mehvahdjukaar.moonlight.integration.ClothConfigCompat;
import net.mehvahdjukaar.moonlight.platform.configs.fabric.ConfigSpec;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.configs.CommonConfigs;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if( CommonConfigs.SPEC instanceof ConfigSpec spec){
            return parent-> ClothConfigCompat.makeScreen(parent, spec, Randomium.res(
                    "textures/blocks/randomium_block.png"
            ));
        }
        return parent -> null;
    }
}