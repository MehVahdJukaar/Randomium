package net.mehvahdjukaar.randomium.recipes;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.randomium.Randomium;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;


public class RandomiumDuplicateRecipe extends CustomRecipe {

    public RandomiumDuplicateRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    private boolean isRandomium(ItemStack stack) {
        return stack.getItem() == Randomium.RANDOMIUM_ITEM.get();
    }

    private boolean canBeDuplicated(ItemStack stack) {

        CompoundTag tag = stack.getTag();
        if (tag != null) {
            if (hasCapability(stack)) return false;
            String s = tag.toString();
            //can never be too careful
            if (s.contains("Items:[") || s.contains("BlockEntityTag") ||
                    s.contains("Inventory:[") ||
                    s.contains("Drawers:[") ||
                    s.contains("randomium:randomium") ||
                    s.contains("randomium:randomium_ore") ||
                    s.contains("randomium:randomium_ore_deepslate") ||
                    s.contains("randomium:randomium_ore_end")) {
                return false;
            }
        }
        return !stack.is(Randomium.BLACKLIST);
    }

    @ExpectPlatform
    public static boolean hasCapability(ItemStack stack) {
        throw new AssertionError();
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        ItemStack toDuplicate = null;
        ItemStack randomium = null;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
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
    public ItemStack assemble(CraftingContainer inv) {
        for (int i = 0; i < inv.getContainerSize(); ++i) {
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
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

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

