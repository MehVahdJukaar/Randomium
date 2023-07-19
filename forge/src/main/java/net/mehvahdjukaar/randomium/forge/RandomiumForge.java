package net.mehvahdjukaar.randomium.forge;

import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.RandomiumClient;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.function.Supplier;

/**
 * Author: MehVahdJukaar
 */
@Mod(Randomium.MOD_ID)
public class RandomiumForge {
    public static final String MOD_ID = Randomium.MOD_ID;

    public RandomiumForge() {

        Randomium.commonInit();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event){
        Randomium.populateLoot((Level) event.getLevel());
    }

    //TODO: REI RECIPE


}
