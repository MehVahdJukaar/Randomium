package net.mehvahdjukaar.randomium;

import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.randomium.client.DuplicateItemRenderer;
import net.mehvahdjukaar.randomium.client.MovingBlockEntityRenderer;

public class RandomiumClient {

    public static void init() {
        ClientHelper.addEntityRenderersRegistration(RandomiumClient::registerEntityRenderers);
        ClientHelper.addItemRenderersRegistration(RandomiumClient::registerItemRenderers);
    }

    private static void registerItemRenderers(ClientHelper.ItemRendererEvent event) {
        event.register(Randomium.DUPLICATE_ITEM.get(), new DuplicateItemRenderer());
    }


    public static void registerEntityRenderers(ClientHelper.EntityRendererEvent event) {
        event.register(Randomium.MOVING_BLOCK_ENTITY.get(), MovingBlockEntityRenderer::new);
    }


}
