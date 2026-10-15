package net.mehvahdjukaar.randomium.platform;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;

public class RandomiumPlatStuffImpl {

    public static String getModId(HolderLookup.Provider registries, ItemStack s) {
        return s.getItem().getCreatorModId(registries, s);
    }

    public static boolean hasCapability(ItemStack stack) {
        return ItemAccess.forStack(stack).getCapability(Capabilities.Item.ITEM) != null;
    }
}
