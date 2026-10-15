package net.mehvahdjukaar.randomium.common;

import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.RandomiumPlatStuff;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import java.util.List;


public class RandomiumDuplicateRecipe extends CustomRecipe {


    private boolean isRandomium(ItemStack stack) {
        return stack.getItem() == Randomium.RANDOMIUM_ITEM.get();
    }

    private boolean canBeDuplicated(ItemStack stack) {

        if (RandomiumPlatStuff.hasCapability(stack)) return false;
        if(stack.is(Randomium.BLACKLIST)) return false;
        return stack.getComponents().stream()
                .map(c -> BuiltInRegistries.DATA_COMPONENT_TYPE.wrapAsHolder(c.type()))
                .noneMatch(h -> h.is(Randomium.COMPONENT_BLACKLIST));
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
    public ItemStack assemble(CraftingInput inv) {
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty() && !isRandomium(stack) && canBeDuplicated(stack)) {
                return stack.copyWithCount(1);
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
    public List<RecipeDisplay> display() {
        SlotDisplay anyItem = new SlotDisplay.ItemSlotDisplay(Randomium.DUPLICATE_ITEM.get());
        return List.of(new ShapelessCraftingRecipeDisplay(
                List.of(new SlotDisplay.ItemSlotDisplay(Randomium.RANDOMIUM_ITEM.get()), anyItem),
                anyItem, new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
    }

    @Override
    public RecipeSerializer<RandomiumDuplicateRecipe> getSerializer() {
        return Randomium.RANDOMIUM_CLONE_RECIPE.get();
    }
}

