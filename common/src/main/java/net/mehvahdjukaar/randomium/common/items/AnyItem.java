package net.mehvahdjukaar.randomium.common.items;

import net.mehvahdjukaar.moonlight.api.misc.ForgeOverride;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.RandomiumPlatStuff;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;

public class AnyItem extends Item {
    public AnyItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getDescriptionId() {
        return Randomium.getAnyItem().getDescriptionId();
    }

    @Nullable
    @ForgeOverride
    //@Override
    public String getCreatorModId(ItemStack itemStack) {
        ItemStack s = Randomium.getAnyItem();
        return RandomiumPlatStuff.getModId(s);
    }

    public static RecipeHolder<CraftingRecipe> createDuplicateRecipe() {
        String group = "randomium.duplicate";

        Ingredient randomium = Ingredient.of(Randomium.RANDOMIUM_ITEM.get().getDefaultInstance());
        Ingredient in = Ingredient.of(new ItemStack(Randomium.DUPLICATE_ITEM.get()));
        NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, randomium, in);
        ResourceLocation id = Randomium.res("duplicate");
        var rec = new ShapelessRecipe(group, CraftingBookCategory.MISC, new ItemStack(Randomium.DUPLICATE_ITEM.get()), inputs);
        return new RecipeHolder<>(id, rec);
    }
}
