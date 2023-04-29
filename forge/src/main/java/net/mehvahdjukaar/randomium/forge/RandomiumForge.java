package net.mehvahdjukaar.randomium.forge;

import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.RandomiumClient;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(RandomiumForge::init);

        Randomium.commonInit();
        if (PlatHelper.getPhysicalSide().isClient()) {
            RandomiumClient.init();
        }

    }

    //TODO: REI RECIPE


    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(Randomium::commonSetup);
    }

}
