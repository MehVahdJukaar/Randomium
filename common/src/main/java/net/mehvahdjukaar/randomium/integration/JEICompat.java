package net.mehvahdjukaar.randomium.integration;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.common.items.AnyItem;
import net.mehvahdjukaar.randomium.common.RandomiumDuplicateRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

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
        registration.getCraftingCategory().addCategoryExtension(RandomiumDuplicateRecipe.class, DuplicateRecipeExtension::new);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        registry.addRecipes(RecipeTypes.CRAFTING, List.of(AnyItem.createDuplicateRecipe()));
    }


    private record DuplicateRecipeExtension(RandomiumDuplicateRecipe recipe) implements ICraftingCategoryExtension {

        @Override
            public void drawInfo(int recipeWidth, int recipeHeight, PoseStack poseStack, double mouseX, double mouseY) {
                Minecraft.getInstance().font.draw(poseStack, I18n.get("randomium.jei.duplicate"), 60.0F, 46.0F, 5592405);
            }

            @Override
            public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, ICraftingGridHelper iCraftingGridHelper, IFocusGroup iFocusGroup) {

            }

            @Override
            public ResourceLocation getRegistryName() {
                return this.recipe.getId();
            }
        }
}