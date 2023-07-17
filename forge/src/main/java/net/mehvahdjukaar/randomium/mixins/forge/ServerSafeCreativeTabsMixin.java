package net.mehvahdjukaar.randomium.mixins.forge;

import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.forge.RandomiumForge;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.ModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.Random;

@Mixin(CreativeModeTab.class)
public class ServerSafeCreativeTabsMixin {

    @Redirect(method = "buildContents", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;onCreativeModeTabBuildContents(Lnet/minecraft/world/item/CreativeModeTab;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/item/CreativeModeTab$DisplayItemsGenerator;Lnet/minecraft/world/item/CreativeModeTab$ItemDisplayParameters;Lnet/minecraft/world/item/CreativeModeTab$Output;)V"))
    public void serverSafeEvent(CreativeModeTab tab, ResourceKey<CreativeModeTab> tabKey, CreativeModeTab.DisplayItemsGenerator originalGenerator, CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = new MutableHashedLinkedMap<>(ItemStackLinkedSet.TYPE_AND_TAG,
                (key, left, right) -> CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        originalGenerator.accept(params, (stack, vis) -> {
            if (stack.getCount() != 1) {
                throw new IllegalArgumentException("The stack count must be 1");
            } else {
                entries.put(stack, vis);
            }
        });
        try {
            ModLoader.get().postEvent(new BuildCreativeModeTabContentsEvent(tab, tabKey, params, entries));
        }catch (Exception e){
            Randomium.LOGGER.error("Failed to run creative mode tabs on server side. Randomium drops wont be correct", e);
        }
        for (Map.Entry<ItemStack, CreativeModeTab.TabVisibility> itemStackTabVisibilityEntry : entries) {
            Map.Entry<ItemStack, CreativeModeTab.TabVisibility> e = itemStackTabVisibilityEntry;
            output.accept(e.getKey(), e.getValue());
        }

    }
}
