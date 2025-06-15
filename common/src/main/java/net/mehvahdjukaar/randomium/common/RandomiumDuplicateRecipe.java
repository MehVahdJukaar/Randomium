package net.mehvahdjukaar.randomium.common;

import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.RandomiumPlatStuff;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;


public class RandomiumDuplicateRecipe extends CustomRecipe {


    public RandomiumDuplicateRecipe(CraftingBookCategory craftingBookCategory) {
        super(craftingBookCategory);
    }

    private boolean isRandomium(ItemStack stack) {
        return stack.getItem() == Randomium.RANDOMIUM_ITEM.get();
    }

    private boolean canBeDuplicated(ItemStack stack) {

        if (RandomiumPlatStuff.hasCapability(stack)) return false;
        if (stack.getComponents().stream()
                .map(c -> BuiltInRegistries.DATA_COMPONENT_TYPE.wrapAsHolder(c.type()))
                .anyMatch(h -> h.is(Randomium.COMPONENT_BLACKLIST))
        ) {
            return false;
        }
        return !stack.is(Randomium.BLACKLIST);
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        ItemStack toDuplicate = null;
        ItemStack randomium = null;

        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {

                if (isRandomium(stack)) {
                    if (randomium != null) {
                        return false;
                    }
                    randomium = stack;
                } else if (canBeDuplicated(stack)) {

                    if (toDuplicate != null) {
                        return false;
                    }
                    toDuplicate = stack;
                }
                //aaaa mojang why didnt you have this line in
                else return false;
            }
        }
        return toDuplicate != null && randomium != null;
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registries) {
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty() && !isRandomium(stack) && canBeDuplicated(stack)) {
                ItemStack s = stack.copy();
                s.setCount(1);
                return s;
            }
        }
        return ItemStack.EMPTY;
    }


    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.size(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inv.getItem(i).copy();
            if (!isRandomium(itemstack)) {
                itemstack.setCount(1);
                nonnulllist.set(i, itemstack);
            }

        }
        return nonnulllist;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Randomium.RANDOMIUM_CLONE_RECIPE.get();
    }
}

