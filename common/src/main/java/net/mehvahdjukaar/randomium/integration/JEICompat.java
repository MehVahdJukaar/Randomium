package net.mehvahdjukaar.randomium.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.common.RandomiumDuplicateRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

@JeiPlugin
public class JEICompat implements IModPlugin {

    private static final Identifier ID = Randomium.res("jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(RandomiumDuplicateRecipe.class, new DuplicateRecipeExtension());
    }

    private static class DuplicateRecipeExtension implements ICraftingCategoryExtension<RandomiumDuplicateRecipe> {

        @Override
        public List<SlotDisplay> getIngredients(RecipeHolder<RandomiumDuplicateRecipe> recipeHolder) {
            return ((ShapelessCraftingRecipeDisplay) recipeHolder.value().display().getFirst()).ingredients();
        }

        @Override
        public void drawInfo(RecipeHolder<RandomiumDuplicateRecipe> recipe, int recipeWidth, int recipeHeight, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
            guiGraphics.text(Minecraft.getInstance().font, Component.translatable("randomium.jei.duplicate"), 60, 46, 0xFF555555, true);
        }
    }
}
