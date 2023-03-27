package net.mehvahdjukaar.randomium;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.ItemStack;

public class RandomiumPlatStuff {

    @ExpectPlatform
    public static String getModId(ItemStack s) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean hasCapability(ItemStack stack) {
        throw new AssertionError();
    }
}
