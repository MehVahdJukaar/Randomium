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
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
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
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MOD_ID);


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

    public static final RegistryObject<Feature<OreFeatureConfig>> RANDOMIUM_ORE_FEATURE = Randomium.FEATURES.register("randomium_ore",
            ()-> new FeatureRegistry.RandomiumFeature(OreFeatureConfig.CODEC));

    public static ForgeConfigSpec.IntValue SPAWN_PER_CHUNK;
    public static ForgeConfigSpec.IntValue SPAWN_PER_CHUNK_END;

    public static ForgeConfigSpec.IntValue EXCITE_ON_ATTACK_CHANCE;
    public static ForgeConfigSpec.IntValue EXCITE_ON_BLOCK_UPDATE_CHANCE;
    public static ForgeConfigSpec.IntValue MOVE_CHANCE;
    public static ForgeConfigSpec.IntValue FALL_CHANCE;
    public static ForgeConfigSpec.IntValue FLY_CHANCE;
    public static ForgeConfigSpec.IntValue TELEPORT_CHANCE;
    public static ForgeConfigSpec.DoubleValue SILK_TOUCH_MULTIPLIER;

    public static ForgeConfigSpec.DoubleValue BASE_DROP_CHANCE;
    public static ForgeConfigSpec.DoubleValue LUCK_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue FORTUNE_MULTIPLIER;
    public static ForgeConfigSpec.BooleanValue ALLOW_SILK_TOUCH;

    public static ForgeConfigSpec.EnumValue<ListMode> LOOT_MODE;
    public enum ListMode{BLACKLIST, WHITELIST}

    public Randomium() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        RECIPES.register(bus);
        FEATURES.register(bus);

        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("spawns");
        SPAWN_PER_CHUNK = builder.comment("Overworld spawn chance")
                .defineInRange("spawn_attempts_per_chunk", 4, 0, 50);
        SPAWN_PER_CHUNK_END = builder.comment("End spawn chance")
                .defineInRange("end_spawn_attempts_per_chunk", 5, 0, 50);
        builder.pop();
        builder.push("interactions");
        EXCITE_ON_ATTACK_CHANCE = builder.comment("Chance for the block to try to move when it's attacked, picking one of the following actions")
                .defineInRange("excite_chance_on_attack", 70, 0, 100);
        EXCITE_ON_BLOCK_UPDATE_CHANCE = builder.comment("Chance for the block to try to move when it receives a block update, picking one of the following actions")
                .defineInRange("excite_chance_on_block_update", 25, 0, 100);
        FALL_CHANCE = builder.comment("Chance for fall action to be picked")
                .defineInRange("fall_chance", 30, 0, 100);
        MOVE_CHANCE = builder.comment("Chance for horizontal move action to be picked")
                .defineInRange("move_chance", 40, 0, 100);
        FLY_CHANCE = builder.comment("Chance for fly up action to be picked")
                .defineInRange("fly_chance", 2, 0, 100);
        TELEPORT_CHANCE = builder.comment("Chance for teleport action to be picked")
                .defineInRange("teleport_chance", 8, 0, 100);
        SILK_TOUCH_MULTIPLIER = builder.comment("Action multiplier if silk touch is used on the block. The lower the value the less likely it will be to more")
                .defineInRange("silk_touch_multiplier", 0.5, 0, 1);
        builder.pop();
        builder.push("drops");
        LOOT_MODE = builder.comment("Loot mode: decides if it can drop everything except blacklist or only stuff on the whitelist")
                .defineEnum("loot_mode", ListMode.BLACKLIST);
        BASE_DROP_CHANCE = builder.comment("Base randomium drop chance. " +
                "Final chance will be [base_chance + luck*luck_multiplier + fortune*fortune_multiplier]")
                .defineInRange("base_drop_chance", 0.5, 0d, 100d);
        LUCK_MULTIPLIER = builder.comment("Multiplier applied to each luck level the player has")
                .defineInRange("luck_multiplier", 1, 0d, 20d);
        FORTUNE_MULTIPLIER = builder.comment("Multiplier applied to each fortune level the player has")
                .defineInRange("fortune_multiplier", 0.2, 0d, 20d);
        ALLOW_SILK_TOUCH = builder.comment("Allow the block to be silk touched")
                .define("allow_silk_touch", true);
        builder.pop();
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
