package net.mehvahdjukaar.randomium.forge;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class RandomiumPlatStuffImpl {

    public static String getModId(ItemStack s) {
        return s.getItem().getCreatorModId(s);
    }

    public static boolean hasCapability(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent();
    }
}
