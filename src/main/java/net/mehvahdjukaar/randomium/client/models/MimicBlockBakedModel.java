package net.mehvahdjukaar.randomium.client.models;

import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.block.RandomiumBlockTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MimicBlockBakedModel implements IDynamicBakedModel {
    private final BlockModelShapes blockModelShaper;

    public MimicBlockBakedModel() {
        this.blockModelShaper = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {

        try {
            BlockState mimic = extraData.getData(RandomiumBlockTile.MIMIC);

            //RenderType layer = MinecraftForgeClient.getRenderLayer();
            //            if (mimic != null && !mimic.isAir() && (layer == null || (framed || RenderTypeLookup.canRenderInLayer(mimic, layer)))) {
            //always solid.

            if (mimic != null && !mimic.isAir()) {

                IModelData data;
                data = EmptyModelData.INSTANCE;
                IBakedModel model = blockModelShaper.getBlockModel(mimic);

                return model.getQuads(mimic, side, rand, data);
            }
        }
        catch (Exception ignored){
        }
        return Collections.emptyList();
    }


    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    private static final ResourceLocation PARTICLE = Randomium.res("blocks/randomium_block");

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(PARTICLE);
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@NotNull IModelData data) {
        BlockState mimic = data.getData(RandomiumBlockTile.MIMIC);
        if (mimic != null && !mimic.isAir()) {

            IBakedModel model = blockModelShaper.getBlockModel(mimic);
            try {
                return model.getParticleIcon();
            } catch (Exception ignored) {}

        }
        return getParticleIcon();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }

    @Override
    public ItemCameraTransforms getTransforms() {
        return ItemCameraTransforms.NO_TRANSFORMS;
    }
}
