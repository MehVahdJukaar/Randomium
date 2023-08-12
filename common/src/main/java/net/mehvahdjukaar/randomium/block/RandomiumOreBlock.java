package net.mehvahdjukaar.randomium.block;

import com.google.common.base.Suppliers;
import dev.architectury.injectables.annotations.PlatformOnly;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.configs.CommonConfigs;
import net.mehvahdjukaar.randomium.entity.MovingBlockEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class RandomiumOreBlock extends Block {

    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    private static final RandomSource RANDOM = RandomSource.createNewThreadLocalInstance();

    public RandomiumOreBlock(Properties properties) {
        super(properties.lightLevel(s -> s.getValue(LIT) ? 4 : 0));
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return getRandomDrops(state, builder.getLevel(), builder.getOptionalParameter(LootContextParams.TOOL),
                builder.getOptionalParameter(LootContextParams.THIS_ENTITY));
    }

    public List<ItemStack> getRandomDrops(BlockState state, Level world, @Nullable ItemStack tool, @Nullable Entity entity) {

        ItemStack loot;
        double percentage = CommonConfigs.BASE_DROP_CHANCE.get();
        if (entity instanceof LivingEntity le) {
            if (le.hasEffect(MobEffects.LUCK)) {
                percentage += (le.getEffect(MobEffects.LUCK).getAmplifier()) * CommonConfigs.LUCK_MULTIPLIER.get();
            }
            if (le.hasEffect(MobEffects.UNLUCK)) {
                percentage -= (le.getEffect(MobEffects.UNLUCK).getAmplifier()) * CommonConfigs.LUCK_MULTIPLIER.get();
            }
            if (tool != null) {
                int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool);
                percentage += CommonConfigs.FORTUNE_MULTIPLIER.get() * fortune;
            }
        }

        //world rng is better
        if (tool != null && CommonConfigs.ALLOW_SILK_TOUCH.get() && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) != 0) {
            loot = new ItemStack(this.asItem());
        } else if (world.random.nextFloat() * 100 <= percentage) {
            loot = new ItemStack(Randomium.RANDOMIUM_ITEM.get());
        } else {
            loot = Randomium.getRandomItem(world.random);
        }
        return Collections.singletonList(loot);
    }

    @PlatformOnly(PlatformOnly.FORGE)
    public int getExpDrop(BlockState state, LevelReader world, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0 ? Mth.nextInt(world instanceof Level l ? l.getRandom() : RANDOM, 0, 6) : 0;
    }

    @Override
    public SoundType getSoundType(BlockState state) {
        return Randomium.getRandomSound(RANDOM);
    }

    @PlatformOnly(PlatformOnly.FORGE)
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
        if (world instanceof Level level) {
            return Randomium.getRandomSound(level.random);
        }
        return getSoundType(state);
    }

    @Override
    public float getExplosionResistance() {
        return (float) Math.max(0, (RANDOM.nextGaussian() * 6 + 8));
    }

    @PlatformOnly(PlatformOnly.FORGE)
    public float getExplosionResistance(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion) {
        if (world instanceof Level level) {
            return (float) Math.max(0, (level.random.nextGaussian() * 6 + 8));
        }
        return getExplosionResistance();
    }

    public Random getBlockRandom(BlockPos pos) {
        return new Random(this.getBlockSeed(pos));
    }

    public Long getBlockSeed(BlockPos pos) {
        return Mth.getSeed(pos);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return this.getBlockRandom(pos).nextInt(16);
    }

    public int getGravityType(BlockPos pos) {
        long seed = this.getBlockSeed(pos);
        if (seed % 3 == 0) return 1;
        //else if (seed % 3 == 0) return 2;
        return 0;

    }

    //TODO: change this on reload
    //this could be a weighted random list
    private double total = 0;
    private final Supplier<NavigableMap<Double, Direction>> map = Suppliers.memoize(() -> new TreeMap<>() {{
        put(total += CommonConfigs.FLY_CHANCE.get(), Direction.UP);
        put(total += CommonConfigs.FALL_CHANCE.get(), Direction.DOWN);
        put(total += CommonConfigs.MOVE_CHANCE.get() / 4d, Direction.NORTH);
        put(total += CommonConfigs.MOVE_CHANCE.get() / 4d, Direction.SOUTH);
        put(total += CommonConfigs.MOVE_CHANCE.get() / 4d, Direction.EAST);
        put(total += CommonConfigs.MOVE_CHANCE.get() / 4d, Direction.WEST);
        put(total += CommonConfigs.TELEPORT_CHANCE.get(), null);
    }});

    @Override
    public void attack(BlockState state, Level world, BlockPos pos, Player entity) {
        ItemStack tool = entity.getUseItem();
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool);
        double c = i != 0 ? CommonConfigs.SILK_TOUCH_MULTIPLIER.get() : 1;
        this.excite(state, world, pos, c * CommonConfigs.EXCITE_ON_ATTACK_CHANCE.get());
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        this.excite(state, world, pos, CommonConfigs.EXCITE_ON_BLOCK_UPDATE_CHANCE.get());
    }

    public void excite(BlockState state, Level world, BlockPos pos, double chance) {
        if (!world.isClientSide) {
            if (world.random.nextFloat() < chance / 100f) {

                Direction dir = map.get().higherEntry(world.random.nextDouble() * total).getValue();

                if (dir == null) {
                    this.teleport(state, (ServerLevel) world, pos);
                } else {
                    this.move(state, world, pos, dir);
                }
            } else if (world.random.nextInt(5) == 0) {
                this.lightUp(state, world, pos);
            }
        }
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos end, int eventID, int eventParam) {
        if (eventID == 0) {
            RandomSource random = world.random;
            //smort
            int dx = (eventParam & 255) - 64;
            int dy = (eventParam >> 8 & 255) - 64;
            int dz = (eventParam >> 16 & 255) - 64;
            BlockPos start = new BlockPos(end.getX() - dx, end.getY() - dy, end.getZ() - dz);
            for (int j = 0; j < 64; ++j) {
                double d0 = random.nextDouble();
                float f = (random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (random.nextFloat() - 0.5F) * 0.2F;
                double d1 = Mth.lerp(d0, end.getX(), start.getX()) + (random.nextDouble() - 0.5D) + 0.5D;
                double d2 = Mth.lerp(d0, end.getY(), start.getY()) + random.nextDouble() - 0.5D;
                double d3 = Mth.lerp(d0, end.getZ(), start.getZ()) + (random.nextDouble() - 0.5D) + 0.5D;
                world.addParticle(ParticleTypes.PORTAL, d1, d2, d3, f, f1, f2);
            }
            return true;
        }
        return super.triggerEvent(state, world, end, eventID, eventParam);
    }

    private void teleport(BlockState state, ServerLevel world, BlockPos pos) {
        final int range = 7;
        for (int i = 0; i < 1000; ++i) {
            BlockPos blockpos = pos.offset(world.random.nextInt(range) - world.random.nextInt(range), world.random.nextInt(range / 2) - world.random.nextInt(range / 2), world.random.nextInt(range) - world.random.nextInt(range));
            if (world.getBlockState(blockpos).isAir()) {

                int dx = (byte) (blockpos.getX() - pos.getX()) + 64;
                int dy = (byte) (blockpos.getY() - pos.getY()) + 64;
                int dz = (byte) (blockpos.getZ() - pos.getZ()) + 64;

                world.setBlock(blockpos, state, 2);
                world.removeBlock(pos, false);


                world.blockEvent(blockpos, this, 0, (dz & 255) << 16 | (dy & 255) << 8 | dx & 255);


                return;
            }
        }
    }

    public void move(BlockState state, Level world, BlockPos pos, Direction dir) {

        if (FallingBlock.isFree(world.getBlockState(pos.relative(dir)))) {
            MovingBlockEntity entity = new MovingBlockEntity(world, (double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D, state, dir);
            world.addFreshEntity(entity);
        }

    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        if (pLevel.random.nextInt(5) == 0)
            lightUp(pLevel.getBlockState(pPos), pLevel, pPos);
        super.stepOn(pLevel, pPos, pState, pEntity);
    }


    private void lightUp(BlockState state, Level world, BlockPos pos) {
        spawnParticles(world, pos);
        if (!state.getValue(LIT)) {
            world.setBlock(pos, state.setValue(LIT, true), 3);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(LIT);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            serverWorld.setBlock(pos, state.setValue(LIT, Boolean.FALSE), 2);
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            spawnParticles(world, pos);
        }
    }

    //todo: add custom ones
    //redstone ore stuff
    private static void spawnParticles(Level world, BlockPos pos) {
        double d0 = 0.5625D;
        RandomSource random = world.random;

        for (Direction direction : Direction.values()) {
            if (random.nextInt(5) == 0) {
                BlockPos blockpos = pos.relative(direction);
                if (!world.getBlockState(blockpos).isSolidRender(world, blockpos)) {
                    Direction.Axis direction$axis = direction.getAxis();
                    double d1 = direction$axis == Direction.Axis.X ? 0.5D + 0.5625D * (double) direction.getStepX() : (double) random.nextFloat();
                    double d2 = direction$axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double) direction.getStepY() : (double) random.nextFloat();
                    double d3 = direction$axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double) direction.getStepZ() : (double) random.nextFloat();

                    world.addParticle(ParticleTypes.WITCH, (double) pos.getX() + d1, (double) pos.getY() + d2, (double) pos.getZ() + d3, 0.0D, -0.1D, 0.0D);
                }
            }
        }
    }

    //@Override
    @PlatformOnly(PlatformOnly.FORGE)
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        if (state.getValue(LIT)) {
            return this.getBlockRandom(pos).nextInt(10) + 5;
        }
        return 0;
    }
}

