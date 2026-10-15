package net.mehvahdjukaar.randomium;

import net.mehvahdjukaar.candlelight.api.PlatformImpl;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

public class RandomiumPlatStuff {

    @PlatformImpl
    public static String getModId(HolderLookup.Provider registries, ItemStack s) {
        throw new AssertionError();
    }

    @PlatformImpl
    public static boolean hasCapability(ItemStack stack) {
        throw new AssertionError();
    }
}
