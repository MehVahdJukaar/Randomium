package net.mehvahdjukaar.randomium.neoforge;

import net.mehvahdjukaar.randomium.common.items.RandomiumItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = RandomiumForge.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class RandomiumForgeClient {

    @SubscribeEvent
    public static void test(ClientTickEvent.Post tickEvent) {
            RandomiumItem.tickEffects();
    }
}
