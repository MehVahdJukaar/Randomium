package net.mehvahdjukaar.randomium.recipes.forge;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.CapabilityItemHandler;

public class RandomiumDuplicateRecipeImpl {
    public static boolean hasCapability(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent();
    }
}
