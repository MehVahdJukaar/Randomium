package net.mehvahdjukaar.randomium.items;

import net.mehvahdjukaar.randomium.Randomium;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public class AnyItem extends Item {
    public AnyItem(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Override
    public String getDescriptionId() {
        int size = Randomium.SHUFFLED_ANY_ITEM.size();
        int time = (int) (Util.getMillis() / 500L);
        return Randomium.SHUFFLED_ANY_ITEM.get(time % size).getDescriptionId();
    }

    @Nullable
    @Override
    public String getCreatorModId(ItemStack itemStack) {
        int size = Randomium.SHUFFLED_ANY_ITEM.size();
        int time = (int) (Util.getMillis() / 500L);
        ItemStack s = Randomium.SHUFFLED_ANY_ITEM.get(time % size);
        return s.getItem().getCreatorModId(s);
    }

    @Override
    public Rarity getRarity(ItemStack p_77613_1_) {
        int size = Randomium.SHUFFLED_ANY_ITEM.size();
        int time = (int) (Util.getMillis() / 500L);
        return Randomium.SHUFFLED_ANY_ITEM.get(time % size).getRarity();
    }
}
