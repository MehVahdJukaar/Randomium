package net.mehvahdjukaar.randomium.forge;

import net.minecraft.world.item.ItemStack;

public class RandomiumPlatStuffImpl {

    public static String getModId(ItemStack s) {
        return s.getItem().getCreatorModId(s);
    }
}
