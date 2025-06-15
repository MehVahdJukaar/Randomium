package net.mehvahdjukaar.randomium;

import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.randomium.client.MovingBlockEntityRenderer;
import net.mehvahdjukaar.randomium.client.TrolliumModel;

public class RandomiumClient {

    public static void init() {
        ClientHelper.addEntityRenderersRegistration(RandomiumClient::registerEntityRenderers);
        ClientHelper.addModelLoaderRegistration(RandomiumClient::registerModelLoaders);
    }

    private static void registerModelLoaders(ClientHelper.ModelLoaderEvent event) {
        event.register(Randomium.res("trollium"), TrolliumModel::new);
    }


    public static void registerEntityRenderers(ClientHelper.EntityRendererEvent event) {
        event.register(Randomium.MOVING_BLOCK_ENTITY.get(), MovingBlockEntityRenderer::new);
    }


}
