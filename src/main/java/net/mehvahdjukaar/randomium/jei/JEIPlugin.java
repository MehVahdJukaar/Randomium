package net.mehvahdjukaar.randomium.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeRegistration;
import net.mehvahdjukaar.randomium.Randomium;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private static final ResourceLocation ID = Randomium.res("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        registry.addRecipes(createRandomiumDuplicateRecipe(), VanillaRecipeCategoryUid.CRAFTING);
    }

    public static List<IRecipe<?>> createRandomiumDuplicateRecipe() {
        List<IRecipe<?>> recipes = new ArrayList<>();
        String group = "randomium.jei.duplicate";

        Ingredient randomium = Ingredient.of(Randomium.RANDOMIUM_ITEM.get().getDefaultInstance());
        Ingredient in = Ingredient.of(new ItemStack(Randomium.DUPLICATE_ITEM.get()));
        NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, randomium, in);
        ResourceLocation id = Randomium.res("duplicate");
        ShapelessRecipe recipe = new ShapelessRecipe(id, group, new ItemStack(Randomium.DUPLICATE_ITEM.get()), inputs);
        recipes.add(recipe);

        return recipes;
    }


}