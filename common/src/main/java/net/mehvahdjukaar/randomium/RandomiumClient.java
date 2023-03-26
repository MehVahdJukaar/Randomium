package net.mehvahdjukaar.randomium;

import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.randomium.client.MovingBlockEntityRenderer;

public class RandomiumClient {

    public static void init() {
        ClientHelper.addEntityRenderersRegistration(RandomiumClient::registerEntityRenderers);
    }


    public static void registerEntityRenderers(ClientHelper.EntityRendererEvent event) {
        event.register(Randomium.MOVING_BLOCK_ENTITY.get(), MovingBlockEntityRenderer::new);
    }


}
