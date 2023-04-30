package net.mehvahdjukaar.randomium;

import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.randomium.block.RandomiumOreBlock;
import net.mehvahdjukaar.randomium.configs.CommonConfigs;
import net.mehvahdjukaar.randomium.entity.MovingBlockEntity;
import net.mehvahdjukaar.randomium.items.RandomiumItem;
import net.mehvahdjukaar.randomium.recipes.RandomiumDuplicateRecipe;
import net.mehvahdjukaar.randomium.world.ModFeatures;
import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.material.Material;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;


public class Randomium {
    public static final String MOD_ID = "randomium";

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    private static final Logger LOGGER = LogManager.getLogger();
    //yes
    private static Random RAND = new Random();

    public static final Supplier<Block> RANDOMIUM_ORE = RegHelper.registerBlock(res("randomium_ore"), () ->
            new RandomiumOreBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 3.0F)));

    public static final Supplier<Block> RANDOMIUM_ORE_DEEP = RegHelper.registerBlock(res("randomium_ore_deepslate"), () ->
            new RandomiumOreBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(5.25F, 3.0F)));

    public static final Supplier<Block> RANDOMIUM_ORE_END = RegHelper.registerBlock(res("randomium_ore_end"), () ->
            new RandomiumOreBlock(BlockBehaviour.Properties.copy(Blocks.END_STONE)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 3.0F)));


    public static final Supplier<Item> RANDOMIUM_ORE_ITEM = RegHelper.registerItem(res("randomium_ore"), () ->
            new BlockItem(RANDOMIUM_ORE.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public static final Supplier<Item> RANDOMIUM_ORE_DEEP_ITEM = RegHelper.registerItem(res("randomium_ore_deepslate"), () ->
            new BlockItem(RANDOMIUM_ORE_DEEP.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public static final Supplier<Item> RANDOMIUM_END_ORE_ITEM = RegHelper.registerItem(res("randomium_ore_end"), () ->
            new BlockItem(RANDOMIUM_ORE_END.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public static final Supplier<Item> RANDOMIUM_ITEM = RegHelper.registerItem(res("randomium"), () ->
            new RandomiumItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).rarity(Rarity.EPIC)));


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
                    new SimpleRecipeSerializer<>(RandomiumDuplicateRecipe::new));


    public enum ListMode {BLACKLIST, WHITELIST}

    public static void commonInit() {
        CommonConfigs.init();
        ModFeatures.init();
    }

    public static void commonSetup() {

        //yay for oneliners
        for (var block : Registry.BLOCK) {
            var sound = block.getSoundType(block.defaultBlockState());
            if (!SOUNDS.contains(sound)) SOUNDS.add(sound);
        }

        if (LOOT.isEmpty()) {
            if (CommonConfigs.LOOT_MODE.get() == ListMode.BLACKLIST) {
                Registry.ITEM.stream()
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
        SHUFFLED_ANY_ITEM.addAll(LOOT.stream().map(l -> l.get(0)).toList());
        Collections.shuffle(SHUFFLED_ANY_ITEM);

    }

    public static ItemStack getRandomItem(RandomSource random) {
        var list = Randomium.LOOT.get(random.nextInt(Randomium.LOOT.size()));
        ItemStack stack = list.get(random.nextInt(list.size())).copy();
        stack.setCount(1);
        return stack;
    }

    public static ItemStack getAnyItem() {
        int size = Randomium.SHUFFLED_ANY_ITEM.size();
        if (size == 0) return RANDOMIUM_ITEM.get().getDefaultInstance();
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

    public static TagKey<Item> BLACKLIST = TagKey.create(Registry.ITEM_REGISTRY, res("blacklist"));
    public static TagKey<Item> WHITELIST = TagKey.create(Registry.ITEM_REGISTRY, res("whitelist"));

    private static final Predicate<Item> VALID_DROP = (i) -> {
        if (i == Items.AIR) return false;
        if (i.builtInRegistryHolder().is(BLACKLIST)) return false;
        if (i instanceof SpawnEggItem) return false;
        ResourceLocation reg = Utils.getID(i);
        if (CommonConfigs.MOD_BLACKLIST.get().contains(reg.getNamespace())) return false;
        String name = reg.getPath();
        return !name.contains("creative") && !name.contains("debug")
                && !name.contains("developer") && !name.contains("dev_") && !name.contains("_dev");
    };


}
