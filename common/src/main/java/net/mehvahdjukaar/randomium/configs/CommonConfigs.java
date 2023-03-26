package net.mehvahdjukaar.randomium.configs;

import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigSpec;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.randomium.Randomium;

import java.util.List;
import java.util.function.Supplier;

public class CommonConfigs {

    public static final ConfigSpec SPEC;

    public static final Supplier<Integer> SPAWN_PER_CHUNK;

    public static final Supplier<Integer> EXCITE_ON_ATTACK_CHANCE;
    public static final Supplier<Integer> EXCITE_ON_BLOCK_UPDATE_CHANCE;
    public static final Supplier<Integer> MOVE_CHANCE;
    public static final Supplier<Integer> FALL_CHANCE;
    public static final Supplier<Integer> FLY_CHANCE;
    public static final Supplier<Integer> TELEPORT_CHANCE;
    public static final Supplier<Double> SILK_TOUCH_MULTIPLIER;

    public static final Supplier<Double> BASE_DROP_CHANCE;
    public static final Supplier<Double> LUCK_MULTIPLIER;
    public static final Supplier<Double> FORTUNE_MULTIPLIER;
    public static final Supplier<Boolean> ALLOW_SILK_TOUCH;
    public static final Supplier<List<String>> MOD_BLACKLIST;

    public static final Supplier<Randomium.ListMode> LOOT_MODE;

    static {
        ConfigBuilder builder = ConfigBuilder.create(Randomium.res("common"), ConfigType.COMMON);
        builder.push("spawns");
        SPAWN_PER_CHUNK = builder.comment("Spawn attempts per chunk")
                .define("spawn_attempts_per_chunk", 12, 0, 200);
        builder.pop();
        builder.push("interactions");
        EXCITE_ON_ATTACK_CHANCE = builder.comment("Chance for the block to try to move when it's attacked, picking one of the following actions")
                .define("excite_chance_on_attack", 70, 0, 100);
        EXCITE_ON_BLOCK_UPDATE_CHANCE = builder.comment("Chance for the block to try to move when it receives a block update, picking one of the following actions. You might want to disable this as it could break flying machines that run into it since it might teleport into them when they touch it")
                .define("excite_chance_on_block_update", 25, 0, 100);
        FALL_CHANCE = builder.comment("Chance for fall action to be picked")
                .define("fall_chance", 30, 0, 100);
        MOVE_CHANCE = builder.comment("Chance for horizontal move action to be picked")
                .define("move_chance", 40, 0, 100);
        FLY_CHANCE = builder.comment("Chance for fly up action to be picked")
                .define("fly_chance", 2, 0, 100);
        TELEPORT_CHANCE = builder.comment("Chance for teleport action to be picked")
                .define("teleport_chance", 8, 0, 100);
        SILK_TOUCH_MULTIPLIER = builder.comment("Excitement multiplier if silk touch is used on the block. The lower the value the less likely it will move")
                .define("silk_touch_multiplier", 0.5, 0, 1);
        builder.pop();
        builder.push("drops");
        MOD_BLACKLIST = builder.comment("A way to blacklist entire mods from the loot pool. Enter a list of mod ids")
                .define("mod_blacklist", List.of("chisel"), o -> o instanceof String);
        LOOT_MODE = builder.comment("Loot mode: decides if it can drop everything except blacklist or only stuff on the whitelist")
                .define("loot_mode", Randomium.ListMode.BLACKLIST);
        BASE_DROP_CHANCE = builder.comment("Base randomium drop chance (in percentage, so out of 100). " +
                        "Final chance will be [base_chance + luck*luck_multiplier + fortune*fortune_multiplier]")
                .define("base_drop_chance", 0.5, 0d, 100d);
        LUCK_MULTIPLIER = builder.comment("Multiplier applied to each luck level the player has")
                .define("luck_multiplier", 1, 0d, 20d);
        FORTUNE_MULTIPLIER = builder.comment("Multiplier applied to each fortune level the player has")
                .define("fortune_multiplier", 0.2, 0d, 20d);
        ALLOW_SILK_TOUCH = builder.comment("Allow the block to be silk touched")
                .define("allow_silk_touch", true);
        builder.pop();

        SPEC = builder.buildAndRegister();
        SPEC.loadFromFile();
    }

    public static void init() {

    }
}
