package net.mehvahdjukaar.randomium.neoforge;

import net.mehvahdjukaar.randomium.common.items.RandomiumItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class RandomiumForgeClient {

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post tickEvent) {
        RandomiumItem.tickEffects();
    }
}
