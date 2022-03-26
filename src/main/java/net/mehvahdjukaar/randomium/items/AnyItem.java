package net.mehvahdjukaar.randomium.items;

import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.client.DuplicateItemRenderer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.util.NonNullLazy;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class AnyItem extends Item {
    public AnyItem(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    public static void registerISTER(Consumer<IItemRenderProperties> consumer, BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> factory) {
        consumer.accept(new IItemRenderProperties() {
            final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(
                    () -> factory.apply(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                            Minecraft.getInstance().getEntityModels()));

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return renderer.get();
            }
        });
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        registerISTER(consumer, DuplicateItemRenderer::new);
    }

    @Override
    public String getDescriptionId() {
        return Randomium.getAnyItem().getDescriptionId();
    }

    @Nullable
    @Override
    public String getCreatorModId(ItemStack itemStack) {
        ItemStack s = Randomium.getAnyItem();
        return s.getItem().getCreatorModId(s);
    }

    @Override
    public Rarity getRarity(ItemStack p_77613_1_) {
        return Randomium.getAnyItem().getRarity();
    }
}
