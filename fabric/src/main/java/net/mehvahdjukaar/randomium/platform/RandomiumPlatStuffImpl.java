package net.mehvahdjukaar.randomium.platform;

import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

public class RandomiumPlatStuffImpl {
    public static String getModId(HolderLookup.Provider registries, ItemStack s) {
        return Utils.getID(s).getNamespace();
    }

    public static boolean hasCapability(ItemStack stack) {
        return false;
    }

}
