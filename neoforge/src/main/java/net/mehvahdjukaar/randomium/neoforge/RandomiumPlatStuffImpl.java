package net.mehvahdjukaar.randomium.neoforge;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;

public class RandomiumPlatStuffImpl {

    public static String getModId(ItemStack s) {
        return s.getItem().getCreatorModId(s);
    }

    public static boolean hasCapability(ItemStack stack) {
        return stack.getCapability(Capabilities.ItemHandler.ITEM) != null;
    }
}
