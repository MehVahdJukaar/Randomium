package net.mehvahdjukaar.randomium.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.mehvahdjukaar.randomium.entity.MovingBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

import java.util.Random;

public class MovingBlockEntityRenderer extends EntityRenderer<MovingBlockEntity> {

    public MovingBlockEntityRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public void render(MovingBlockEntity entity, float val, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light) {
        BlockState blockstate = entity.getBlockState();
        if (blockstate.getRenderShape() == BlockRenderType.MODEL) {
            World world = entity.level;
            if (blockstate != world.getBlockState(entity.blockPosition()) && blockstate.getRenderShape() != BlockRenderType.INVISIBLE) {
                matrixStack.pushPose();
                BlockPos blockpos = new BlockPos(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
                matrixStack.translate(-0.5D, 0.0D, -0.5D);
                BlockRendererDispatcher modelRenderer = Minecraft.getInstance().getBlockRenderer();
                for (net.minecraft.client.renderer.RenderType type : net.minecraft.client.renderer.RenderType.chunkBufferLayers()) {
                    if (RenderTypeLookup.canRenderInLayer(blockstate, type)) {
                        ForgeHooksClient.setRenderLayer(type);
                        modelRenderer.getModelRenderer().tesselateBlock(world, modelRenderer.getBlockModel(blockstate), blockstate, blockpos, matrixStack,
                                buffer.getBuffer(type), false, new Random(), blockstate.getSeed(entity.getStartPos()), OverlayTexture.NO_OVERLAY);
                    }
                }
                ForgeHooksClient.setRenderLayer(null);
                matrixStack.popPose();
                super.render(entity, val, partialTicks, matrixStack, buffer, light);
            }
        }
    }

    public ResourceLocation getTextureLocation(MovingBlockEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS;
    }

}
