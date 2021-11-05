package net.mehvahdjukaar.randomium.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.mehvahdjukaar.randomium.Randomium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;

public class DuplicateItemRenderer extends ItemStackTileEntityRenderer {

    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transformType,
                             MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

        int size = Randomium.SHUFFLED_ANY_ITEM.size();
        int time = (int) (Util.getMillis() / 500L);
        ItemStack item = Randomium.SHUFFLED_ANY_ITEM.get(time % size);

        matrixStack.pushPose();
        matrixStack.translate(0.5D, 0.5D, 0.5D);
        Minecraft.getInstance().getItemRenderer().renderStatic(item, transformType, combinedLight, combinedOverlay, matrixStack, buffer);
        matrixStack.popPose();

    }
}
