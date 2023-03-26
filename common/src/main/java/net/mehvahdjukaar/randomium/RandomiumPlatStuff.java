package net.mehvahdjukaar.randomium;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.ItemStack;

public class RandomiumPlatStuff {

    @ExpectPlatform
    public static String getModId(ItemStack s) {
        throw new AssertionError();
    }
}
