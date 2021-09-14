package net.mehvahdjukaar.randomium.recipes;

import net.mehvahdjukaar.randomium.Randomium;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

public class RandomiumRecipe extends SpecialRecipe {

    public RandomiumRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    private boolean isRandomium(ItemStack stack) {
        return stack.getItem() == Randomium.RANDOMIUM_ITEM.get();
    }

    private boolean isValid(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null) {
            if (stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) return false;
            String s = tag.toString();
            //can never be too careful
            if (s.contains("Items:[") || s.contains(Randomium.RANDOMIUM_ITEM.get().getRegistryName().toString()))
                return false;
        }
        return !stack.getItem().is(Randomium.BLACKLIST);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {

        ItemStack itemstack = null;
        ItemStack itemstack1 = null;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {

                if (isRandomium(stack)) {
                    if (itemstack1 != null) {
                        return false;
                    }
                    itemstack1 = stack;
                } else if (isValid(stack)) {

                    if (itemstack != null) {
                        return false;
                    }
                    itemstack = stack;
                }
            }

        }
        return itemstack != null && itemstack1 != null;
    }

    @Override
    public ItemStack assemble(CraftingInventory inv) {
        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty() && !isRandomium(stack)) {
                ItemStack s = stack.copy();
                s.setCount(1);
                return s;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
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
    public IRecipeSerializer<?> getSerializer() {
        return Randomium.RANDOMIUM_CLONE_RECIPE.get();
    }
}

