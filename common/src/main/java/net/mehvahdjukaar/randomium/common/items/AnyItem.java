package net.mehvahdjukaar.randomium.common.items;

import net.mehvahdjukaar.candlelight.api.VirtualOverride;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.RandomiumPlatStuff;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AnyItem extends Item {
    public AnyItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Randomium.getAnyItem().getItemName();
    }

    @Nullable
    @VirtualOverride("neoforge")
    public String getCreatorModId(HolderLookup.Provider registries, ItemStack itemStack) {
        ItemStack s = Randomium.getAnyItem();
        return RandomiumPlatStuff.getModId(registries, s);
    }
}
