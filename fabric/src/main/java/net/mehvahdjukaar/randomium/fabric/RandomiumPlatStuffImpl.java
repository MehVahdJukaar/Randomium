package net.mehvahdjukaar.randomium.fabric;

import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.world.item.ItemStack;

public class RandomiumPlatStuffImpl {
    public static String getModId(ItemStack s) {
        return Utils.getID(s).getNamespace();
    }

    public static boolean hasCapability(ItemStack stack) {
        return false;
    }

}
