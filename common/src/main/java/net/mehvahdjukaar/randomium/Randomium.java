package net.mehvahdjukaar.randomium;

import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.randomium.common.CommonConfigs;
import net.mehvahdjukaar.randomium.common.MovingBlockEntity;
import net.mehvahdjukaar.randomium.common.RandomiumDuplicateRecipe;
import net.mehvahdjukaar.randomium.common.RandomiumOreBlock;
import net.mehvahdjukaar.randomium.common.items.AnyItem;
import net.mehvahdjukaar.randomium.common.items.RandomiumItem;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;


public class Randomium {
    public static final String MOD_ID = "randomium";
    public static final Logger LOGGER = LogManager.getLogger();

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public static final Supplier<Block> RANDOMIUM_ORE = RegHelper.registerBlockWithItem(res("randomium_ore"), () ->
            new RandomiumOreBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 3.0F)));

    public static final Supplier<Block> RANDOMIUM_ORE_DEEP = RegHelper.registerBlockWithItem(res("randomium_ore_deepslate"), () ->
            new RandomiumOreBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(5.25F, 3.0F)));

    public static final Supplier<Block> RANDOMIUM_ORE_END = RegHelper.registerBlockWithItem(res("randomium_ore_end"), () ->
            new RandomiumOreBlock(BlockBehaviour.Properties.copy(Blocks.END_STONE)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 3.0F)));

    public static final Supplier<Item> RANDOMIUM_ITEM = RegHelper.registerItem(res("randomium"), () ->
            new RandomiumItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final Supplier<Item> DUPLICATE_ITEM = RegHelper.registerItem(Randomium.res("any_item"), () ->
            new AnyItem(new Item.Properties()));


    public static final Supplier<EntityType<MovingBlockEntity>> MOVING_BLOCK_ENTITY = RegHelper.registerEntityType(
            res("moving_block"), () ->
                    EntityType.Builder.<MovingBlockEntity>of(MovingBlockEntity::new,
                                    MobCategory.MISC)
                            .sized(0.98F, 0.98F)
                            .clientTrackingRange(10)
                            .updateInterval(20)
                            .build("moving_block"));

    public static final Supplier<RecipeSerializer<?>> RANDOMIUM_CLONE_RECIPE = RegHelper.registerRecipeSerializer(
            res("randomium_clone"), () ->
                    new SimpleCraftingRecipeSerializer<>(RandomiumDuplicateRecipe::new));


    public enum ListMode {BLACKLIST, WHITELIST}

    public static void commonInit() {
        CommonConfigs.init();

        PlatHelper.addCommonSetup(Randomium::commonSetup);
        if(PlatHelper.getPhysicalSide().isClient()) {
            RandomiumClient.init();
        }
        RegHelper.addItemsToTabsRegistration(Randomium::addItemsToTab);
    }

    private static void addItemsToTab(RegHelper.ItemToTabEvent event) {
        event.addAfter(CreativeModeTabs.INGREDIENTS, i -> i.is(Items.AMETHYST_SHARD), RANDOMIUM_ITEM.get());
        event.addAfter(CreativeModeTabs.NATURAL_BLOCKS, i -> i.is(Items.DEEPSLATE_DIAMOND_ORE),
                RANDOMIUM_ORE.get(), RANDOMIUM_ORE_DEEP.get(), RANDOMIUM_ORE_END.get());
    }

    public static void commonSetup() {
        //yay for oneliners
        for (var block : BuiltInRegistries.BLOCK) {
            var sound = block.getSoundType(block.defaultBlockState());
            if (!SOUNDS.contains(sound)) SOUNDS.add(sound);
        }

        SHUFFLED_ANY_ITEM.clear();
        SHUFFLED_ANY_ITEM.addAll(LOOT.stream().map(l -> l.get(0)).toList());
        Collections.shuffle(SHUFFLED_ANY_ITEM);

    }

    //tabs arent even ready in mod setup...
    public static void populateLoot(Level level) {
        if(!CreativeModeTabs.getDefaultTab().hasAnyItems()) {
            CreativeModeTabs.tryRebuildTabContents(level.enabledFeatures(), false, level.registryAccess());
        }
        Map<Item, List<ItemStack>> temp = new HashMap<>();
        if (CommonConfigs.LOOT_MODE.get() == ListMode.BLACKLIST) {
            for (var t : CreativeModeTabs.tabs()) {
                t.getDisplayItems().stream()
                        .filter(VALID_DROP)
                        .forEach(i -> {
                            var set = temp.computeIfAbsent(i.getItem(), j -> new ArrayList<>());
                            for (ItemStack s : set) {
                                if (s.equals(i)) return;
                            }
                            set.add(i);
                        });
            }
            LOOT.addAll(temp.values());
            //lucky
            LOOT.add(Collections.singletonList(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LUCK)));
        } else {
            BuiltInRegistries.ITEM.getTagOrEmpty(WHITELIST).forEach(i -> LOOT.add(Collections.singletonList(i.value().getDefaultInstance())));
        }
    }


    public static ItemStack getRandomItem(Level level, RandomSource random) {
        if (LOOT.isEmpty()) populateLoot(level);
        var list = Randomium.LOOT.get(random.nextInt(Randomium.LOOT.size()));
        ItemStack stack = list.get(random.nextInt(list.size())).copy();
        stack.setCount(1);
        return stack;
    }

    public static ItemStack getAnyItem() {
        int size = Randomium.SHUFFLED_ANY_ITEM.size();
        if (size == 0) return Items.DIAMOND.getDefaultInstance();
        int time = (int) (Util.getMillis() / 500L);
        return Randomium.SHUFFLED_ANY_ITEM.get(time % size);
    }

    public static SoundType getRandomSound(RandomSource random) {
        return SOUNDS.get(random.nextInt(Randomium.SOUNDS.size()));
    }

    //these are meant to be copied of course. Needed cause they hold tags
    private static final List<List<ItemStack>> LOOT = new ArrayList<>();
    private static final List<ItemStack> SHUFFLED_ANY_ITEM = new ArrayList<>();
    private static final List<SoundType> SOUNDS = new ArrayList<>();

    public static final TagKey<Item> BLACKLIST = TagKey.create(Registries.ITEM, res("blacklist"));
    public static final TagKey<Item> WHITELIST = TagKey.create(Registries.ITEM, res("whitelist"));

    private static final Predicate<ItemStack> VALID_DROP = (i) -> {
        if (i.getItem() == Items.AIR) return false;
        if (i.is(BLACKLIST)) return false;
        if (i.getItem() instanceof SpawnEggItem) return false;
        if (BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.OP_BLOCKS).contains(i)) return false;
        //should be covered by subsequent blacklist but better be sure
        ResourceLocation reg = Utils.getID(i.getItem());
        if (CommonConfigs.MOD_BLACKLIST.get().contains(reg.getNamespace())) return false;
        String name = reg.getPath();
        return !name.contains("creative") && !name.contains("debug")
                && !name.contains("developer") && !name.contains("dev_") && !name.contains("_dev");
    };


}
