package net.mehvahdjukaar.randomium;

import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.randomium.client.AnyItemModel;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.item.ItemModels;

public class RandomiumClient {

    public static void init() {
        ClientHelper.addEntityRenderersRegistration(RandomiumClient::registerEntityRenderers);
        ItemModels.ID_MAPPER.put(Randomium.res("any_item"), AnyItemModel.Unbaked.MAP_CODEC);
    }


    public static void registerEntityRenderers(ClientHelper.EntityRendererEvent event) {
        event.register(Randomium.MOVING_BLOCK_ENTITY.get(), FallingBlockRenderer::new);
    }


}
