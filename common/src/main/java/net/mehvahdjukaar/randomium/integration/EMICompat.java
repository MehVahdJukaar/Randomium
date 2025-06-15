package net.mehvahdjukaar.randomium.integration;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeDecorator;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.mehvahdjukaar.randomium.common.items.AnyItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.ShapelessRecipe;

@EmiEntrypoint
public class EMICompat implements EmiPlugin {


    @Override
    public void register(EmiRegistry emiRegistry) {
        var recHolder = AnyItem.createDuplicateRecipe();
        var rec = recHolder.value();
        var emiRec = new EmiCraftingRecipe(
                rec.getIngredients().stream().map(EmiIngredient::of).toList(),
                EmiStack.of(rec.getResultItem(null)),
                recHolder.id(),
                rec instanceof ShapelessRecipe);

        emiRegistry.addRecipe(emiRec);
        emiRegistry.addRecipeDecorator((emiRecipe, widgetHolder) -> {
            if (emiRecipe == emiRec) {
                widgetHolder.addText(Component.translatable("randomium.jei.duplicate"),
                        60, 46, 5592405, false);
            }
        });
    }

}
