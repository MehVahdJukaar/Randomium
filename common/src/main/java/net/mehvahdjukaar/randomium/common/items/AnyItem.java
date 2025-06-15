package net.mehvahdjukaar.randomium.common.items;

import dev.architectury.injectables.annotations.PlatformOnly;
import net.mehvahdjukaar.moonlight.api.client.ItemStackRenderer;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.RandomiumPlatStuff;
import net.mehvahdjukaar.randomium.client.DuplicateItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class AnyItem extends Item {
    public AnyItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getDescriptionId() {
        return Randomium.getAnyItem().getDescriptionId();
    }

    @Nullable
    @PlatformOnly(PlatformOnly.FORGE)
    //@Override
    public String getCreatorModId(ItemStack itemStack) {
        ItemStack s = Randomium.getAnyItem();
        return RandomiumPlatStuff.getModId(s);
    }

    public static CraftingRecipe createDuplicateRecipe() {
        String group = "randomium.duplicate";

        Ingredient randomium = Ingredient.of(Randomium.RANDOMIUM_ITEM.get().getDefaultInstance());
        Ingredient in = Ingredient.of(new ItemStack(Randomium.DUPLICATE_ITEM.get()));
        NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, randomium, in);
        ResourceLocation id = Randomium.res("duplicate");
        return new ShapelessRecipe(id, group, CraftingBookCategory.MISC, new ItemStack(Randomium.DUPLICATE_ITEM.get()), inputs);
    }
}
