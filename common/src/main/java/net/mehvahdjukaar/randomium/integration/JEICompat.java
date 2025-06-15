package net.mehvahdjukaar.randomium.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.common.RandomiumDuplicateRecipe;
import net.mehvahdjukaar.randomium.common.items.AnyItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

@JeiPlugin
public class JEICompat implements IModPlugin {

    private static final ResourceLocation ID = Randomium.res("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(RandomiumDuplicateRecipe.class, DuplicateRecipeExtension::new);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        if (!PlatHelper.isModLoaded("roughly_enough_items")) {
            registry.addRecipes(RecipeTypes.CRAFTING, List.of(AnyItem.createDuplicateRecipe()));
        }
    }


    private record DuplicateRecipeExtension(
            RandomiumDuplicateRecipe recipe) implements ICraftingCategoryExtension<CraftingRecipe> {

        @Override
        public void drawInfo(RecipeHolder<CraftingRecipe> recipe, int recipeWidth, int recipeHeight, GuiGraphics guiGraphics, double mouseX, double mouseY) {
            guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("randomium.jei.duplicate"), 60, 46, 5592405);
        }

        @Override
        public void setRecipe(RecipeHolder<CraftingRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        }
    }
}