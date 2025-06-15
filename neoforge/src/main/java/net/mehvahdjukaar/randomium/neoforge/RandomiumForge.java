package net.mehvahdjukaar.randomium.neoforge;

import net.mehvahdjukaar.randomium.Randomium;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;

/**
 * Author: MehVahdJukaar
 */
@Mod(Randomium.MOD_ID)
public class RandomiumForge {
    public static final String MOD_ID = Randomium.MOD_ID;

    public RandomiumForge(IEventBus bus) {

        Randomium.commonInit();

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event){
        Randomium.populateLoot((Level) event.getLevel());
    }

    //TODO: REI RECIPE


}
