package net.mehvahdjukaar.randomium.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.randomium.Randomium;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;

public class DuplicateItemRenderer extends BlockEntityWithoutLevelRenderer {

    public DuplicateItemRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType,
                             PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        ItemStack item = Randomium.getAnyItem();

        matrixStack.pushPose();
        matrixStack.translate(0.5D, 0.5D, 0.5D);
        Minecraft.getInstance().getItemRenderer().renderStatic(item, transformType, combinedLight, combinedOverlay, matrixStack, buffer, 0);
        matrixStack.popPose();

    }
}
