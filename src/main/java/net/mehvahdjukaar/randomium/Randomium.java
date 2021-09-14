package net.mehvahdjukaar.randomium;

import net.mehvahdjukaar.randomium.block.RandomiumOreBlock;
import net.mehvahdjukaar.randomium.client.MovingBlockEntityRenderer;
import net.mehvahdjukaar.randomium.entity.MovingBlockEntity;
import net.mehvahdjukaar.randomium.recipes.RandomiumRecipe;
import net.mehvahdjukaar.randomium.world.FeatureRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;


@Mod(Randomium.MOD_ID)
public class Randomium {
    public static final String MOD_ID = "randomium";

    public static ResourceLocation res(String name){
        return new ResourceLocation(MOD_ID, name);
    }

    private static final Logger LOGGER = LogManager.getLogger();
    //yes
    private static Random RAND = new Random();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MOD_ID);
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);

    public static final RegistryObject<Block> RANDOMIUM_ORE = BLOCKS.register("randomium_ore", () ->
            new RandomiumOreBlock(AbstractBlock.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 3.0F)
                    .harvestTool(ToolType.PICKAXE)
                    .harvestLevel(2)));

    public static final RegistryObject<Item> RANDOMIUM_ORE_ITEM = ITEMS.register("randomium_ore", () ->
            new BlockItem(RANDOMIUM_ORE.get(), new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));

    public static final RegistryObject<Block> RANDOMIUM_END_ORE = BLOCKS.register("randomium_ore_end", () ->
            new RandomiumOreBlock(AbstractBlock.Properties.copy(Blocks.END_STONE)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 3.0F)
                    .harvestTool(ToolType.PICKAXE)
                    .harvestLevel(2)));

    public static final RegistryObject<Item> RANDOMIUM_END_ORE_ITEM = ITEMS.register("randomium_ore_end", () ->
            new BlockItem(RANDOMIUM_END_ORE.get(), new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));

    public static final RegistryObject<Item> RANDOMIUM_ITEM = ITEMS.register("randomium", () ->
            new Item(new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS).rarity(Rarity.EPIC)) {

                @Override
                public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag tooltipFlag) {
                    text.add((new TranslationTextComponent("message.randomium.description")).withStyle(TextFormatting.DARK_PURPLE));
                    super.appendHoverText(stack, world, text, tooltipFlag);
                }
            });

    public static final RegistryObject<EntityType<MovingBlockEntity>> MOVING_BLOCK_ENTITY = ENTITIES.register("moving_block", () ->
            EntityType.Builder.<MovingBlockEntity>of(MovingBlockEntity::new,
                    EntityClassification.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .setCustomClientFactory(MovingBlockEntity::new)
                    .build("moving_block"));

    public static final RegistryObject<IRecipeSerializer<?>> RANDOMIUM_CLONE_RECIPE = RECIPES.register("randomium_clone", ()->
            new SpecialRecipeSerializer<>(RandomiumRecipe::new));



    public static ForgeConfigSpec.IntValue SPAWN_PER_CHUNK;
    public static ForgeConfigSpec.IntValue SPAWN_PER_CHUNK_END;

    public Randomium() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        RECIPES.register(bus);

        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        SPAWN_PER_CHUNK = builder.comment("Overworld spawn chance")
                .defineInRange("spawn_attempts_per_chunk", 4, 0, 50);
        SPAWN_PER_CHUNK_END = builder.comment("End spawn chance")
                .defineInRange("end_spawn_attempts_per_chunk", 3, 0, 50);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, builder.build());

        // Register the setup method for modloading

        bus.addListener(EventPriority.LOWEST, this::setup);
        bus.addListener(this::doClientStuff);


        // Register ourselves for server and other game events we are interested in
        //MinecraftForge.EVENT_BUS.addListener(FeatureRegistry::addFeatureToBiomes);
        MinecraftForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void addFeatureToBiomes(BiomeLoadingEvent event) {
        FeatureRegistry.addFeatureToBiomes(event);
    }

    public static final List<List<ItemStack>> LOOT = new ArrayList<>();
    public static final List<SoundType> SOUNDS = new ArrayList<>();

    public static Tags.IOptionalNamedTag<Item> BLACKLIST = ItemTags.createOptional(res("blacklist"));

    @SubscribeEvent
    public void onTagLoad(TagsUpdatedEvent event) {
        if (LOOT.isEmpty()) {
            ForgeRegistries.ITEMS.getValues().stream().filter(i -> !i.is(BLACKLIST) && !i.getRegistryName().getPath().contains("creative")).forEach(i -> {
                NonNullList<ItemStack> temp = NonNullList.create();
                try {
                    Arrays.stream(ItemGroup.TABS).forEach(t -> i.fillItemCategory(t, temp));
                    if (!temp.isEmpty()) LOOT.add(temp);
                } catch (Exception ignored) {
                }
            });
            //lucky
            LOOT.add(Collections.singletonList(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LUCK)));
        }
    }

    private void setup(final FMLCommonSetupEvent event) {

        FeatureRegistry.init();

        //yay for one liners
        ForgeRegistries.BLOCKS.getValues().stream().map(b -> b.getSoundType(b.defaultBlockState()))
                .filter(s -> !SOUNDS.contains(s)).forEach(SOUNDS::add);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.trace(RAND.nextInt(1000) + "!");
        // do something when the server starts
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        RenderingRegistry.registerEntityRenderingHandler(MOVING_BLOCK_ENTITY.get(), MovingBlockEntityRenderer::new);
    }


}
