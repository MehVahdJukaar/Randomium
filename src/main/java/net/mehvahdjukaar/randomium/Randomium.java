package net.mehvahdjukaar.randomium;

import net.mehvahdjukaar.randomium.block.RandomiumOreBlock;
import net.mehvahdjukaar.randomium.client.MovingBlockEntityRenderer;
import net.mehvahdjukaar.randomium.configs.CommonConfigs;
import net.mehvahdjukaar.randomium.entity.MovingBlockEntity;
import net.mehvahdjukaar.randomium.items.AnyItem;
import net.mehvahdjukaar.randomium.items.RandomiumItem;
import net.mehvahdjukaar.randomium.recipes.RandomiumDuplicateRecipe;
import net.mehvahdjukaar.randomium.world.FeatureRegistry;
import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mod(Randomium.MOD_ID)
public class Randomium {
    public static final String MOD_ID = "randomium";

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    private static final Logger LOGGER = LogManager.getLogger();
    //yes
    private static Random RAND = new Random();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MOD_ID);


    public static final RegistryObject<Block> RANDOMIUM_ORE = BLOCKS.register("randomium_ore", () ->
            new RandomiumOreBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 3.0F)));

    public static final RegistryObject<Block> RANDOMIUM_ORE_DEEP = BLOCKS.register("randomium_ore_deepslate", () ->
            new RandomiumOreBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(5.25F, 3.0F)));

    public static final RegistryObject<Item> RANDOMIUM_ORE_DEEP_ITEM = ITEMS.register("randomium_ore_deepslate", () ->
            new BlockItem(RANDOMIUM_ORE_DEEP.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public static final RegistryObject<Item> RANDOMIUM_ORE_ITEM = ITEMS.register("randomium_ore", () ->
            new BlockItem(RANDOMIUM_ORE.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public static final RegistryObject<Block> RANDOMIUM_ORE_END = BLOCKS.register("randomium_ore_end", () ->
            new RandomiumOreBlock(BlockBehaviour.Properties.copy(Blocks.END_STONE)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 3.0F)));

    public static final RegistryObject<Item> RANDOMIUM_END_ORE_ITEM = ITEMS.register("randomium_ore_end", () ->
            new BlockItem(RANDOMIUM_ORE_END.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public static final RegistryObject<Item> RANDOMIUM_ITEM = ITEMS.register("randomium", () ->
            new RandomiumItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> DUPLICATE_ITEM = ITEMS.register("any_item", () ->
            new AnyItem(new Item.Properties().tab(null)));

    public static final RegistryObject<EntityType<MovingBlockEntity>> MOVING_BLOCK_ENTITY = ENTITIES.register("moving_block", () ->
            EntityType.Builder.<MovingBlockEntity>of(MovingBlockEntity::new,
                            MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .setCustomClientFactory(MovingBlockEntity::new)
                    .build("moving_block"));

    public static final RegistryObject<RecipeSerializer<?>> RANDOMIUM_CLONE_RECIPE = RECIPES.register("randomium_clone", () ->
            new SimpleRecipeSerializer<>(RandomiumDuplicateRecipe::new));


    public enum ListMode {BLACKLIST, WHITELIST}

    public Randomium() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        RECIPES.register(bus);
        FEATURES.register(bus);

        CommonConfigs.registerSpec();

        // Register the setup method for modloading
        bus.addListener(EventPriority.LOWEST, this::setup);
        bus.addListener(this::entityRenderers);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ItemStack getRandomItem(Random random) {
        var list = Randomium.LOOT.get(random.nextInt(Randomium.LOOT.size()));
        ItemStack stack = list.get(random.nextInt(list.size())).copy();
        stack.setCount(1);
        return stack;
    }

    public static ItemStack getAnyItem() {
        int size = Randomium.SHUFFLED_ANY_ITEM.size();
        int time = (int) (Util.getMillis() / 500L);
        return Randomium.SHUFFLED_ANY_ITEM.get(time % size);
    }

    public static SoundType getRandomSound(Random random){
        return SOUNDS.get(random.nextInt(Randomium.SOUNDS.size()));
    }


    //these are meant to be copied of course. Needed cause they hold tags
    private static final List<List<ItemStack>> LOOT = new ArrayList<>();
    private static final List<ItemStack> SHUFFLED_ANY_ITEM = new ArrayList<>();
    private static final List<SoundType> SOUNDS = new ArrayList<>();

    public static TagKey<Item> BLACKLIST = ItemTags.create(res("blacklist"));
    public static TagKey<Item> WHITELIST = ItemTags.create(res("whitelist"));

    private static final Predicate<Item> VALID_DROP = (i) -> {
        if (i == Items.AIR) return false;
        if (i.builtInRegistryHolder().is(BLACKLIST)) return false;
        if (i instanceof SpawnEggItem) return false;
        ResourceLocation reg = i.getRegistryName();
        if(CommonConfigs.MOD_BLACKLIST.get().contains(reg.getNamespace())) return false;
        String name = reg.getPath();
        return !name.contains("creative") && !name.contains("debug")
                && !name.contains("developer") && !name.contains("dev_") && !name.contains("_dev");
    };


    @SubscribeEvent
    public void addFeatureToBiomes(BiomeLoadingEvent event) {
        FeatureRegistry.addFeatureToBiomes(event);
    }

    @SubscribeEvent
    public void onTagLoad(TagsUpdatedEvent event) {
        if (LOOT.isEmpty()) {
            if (CommonConfigs.LOOT_MODE.get() == ListMode.BLACKLIST) {
                ForgeRegistries.ITEMS.getValues().stream()
                        .filter(VALID_DROP)
                        .forEach(i -> {
                            NonNullList<ItemStack> temp = NonNullList.create();
                            try {
                                Arrays.stream(CreativeModeTab.TABS).forEach(t -> i.fillItemCategory(t, temp));
                                if (!temp.isEmpty()) LOOT.add(temp);
                            } catch (Exception ignored) {
                            }
                        });
                //lucky
                LOOT.add(Collections.singletonList(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LUCK)));
            } else {
                Registry.ITEM.getTagOrEmpty(WHITELIST).forEach(i -> LOOT.add(Collections.singletonList(i.value().getDefaultInstance())));
            }
        }
        SHUFFLED_ANY_ITEM.clear();
        SHUFFLED_ANY_ITEM.addAll(LOOT.stream().map(l -> l.get(0)).collect(Collectors.toList()));
        Collections.shuffle(SHUFFLED_ANY_ITEM);
    }

    private void setup(final FMLCommonSetupEvent event) {
        FeatureRegistry.init();

        //yay for one liners
        ForgeRegistries.BLOCKS.getValues().stream().map(b -> b.getSoundType(b.defaultBlockState()))
                .filter(s -> !SOUNDS.contains(s)).forEach(SOUNDS::add);
    }

    public void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // do something that can only be done on the client
        event.registerEntityRenderer(MOVING_BLOCK_ENTITY.get(), MovingBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        //loaders
        //ModelLoaderRegistry.registerLoader(res("mimic_block_loader"), new MimicBlockLoader());
    }
}
