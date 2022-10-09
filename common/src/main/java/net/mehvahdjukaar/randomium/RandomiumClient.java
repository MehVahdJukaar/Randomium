package net.mehvahdjukaar.randomium;

import net.mehvahdjukaar.moonlight.api.platform.ClientPlatformHelper;
import net.mehvahdjukaar.randomium.client.MovingBlockEntityRenderer;

public class RandomiumClient {

    public static void init() {
        ClientPlatformHelper.addEntityRenderersRegistration(RandomiumClient::registerEntityRenderers);
    }


    public static void registerEntityRenderers(ClientPlatformHelper.EntityRendererEvent event) {
        event.register(Randomium.MOVING_BLOCK_ENTITY.get(), MovingBlockEntityRenderer::new);
    }


}
