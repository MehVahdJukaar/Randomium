package net.mehvahdjukaar.randomium.block;

import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.configs.CommonConfigs;
import net.mehvahdjukaar.randomium.entity.MovingBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;

public class RandomiumOreBlock extends Block {

    public RandomiumOreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return getRandomDrops(state, builder.getLevel(), builder.getOptionalParameter(LootParameters.TOOL),
                builder.getOptionalParameter(LootParameters.THIS_ENTITY));
    }

    public List<ItemStack> getRandomDrops(BlockState state, World world, @Nullable ItemStack tool, @Nullable Entity entity) {

        ItemStack loot;
        double percentage = CommonConfigs.BASE_DROP_CHANCE.get();
        if (entity instanceof LivingEntity) {
            LivingEntity le = ((LivingEntity) entity);
            if (le.hasEffect(Effects.LUCK)) {
                percentage += (le.getEffect(Effects.LUCK).getAmplifier()) * CommonConfigs.LUCK_MULTIPLIER.get();
            }
            if (le.hasEffect(Effects.UNLUCK)) {
                percentage -= (le.getEffect(Effects.UNLUCK).getAmplifier()) * CommonConfigs.LUCK_MULTIPLIER.get();
            }
            if (tool != null) {
                int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool);
                percentage += CommonConfigs.FORTUNE_MULTIPLIER.get() * fortune;
            }
        }

        //world rng is better
        if (world.random.nextFloat() * 100 <= percentage) {

            loot = new ItemStack(Randomium.RANDOMIUM_ITEM.get());
        } else if (tool != null && CommonConfigs.ALLOW_SILK_TOUCH.get() && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) != 0)
            loot = new ItemStack(this.asItem());
        else {
            List<ItemStack> l = Randomium.LOOT.get(world.random.nextInt(Randomium.LOOT.size()));
            loot = l.get(world.random.nextInt(l.size()));
        }
        return Collections.singletonList(loot);
    }

    @Override
    public int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0 ? MathHelper.nextInt(RANDOM, 0, 6) : 0;
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        return Randomium.SOUNDS.get(RANDOM.nextInt(Randomium.SOUNDS.size()));
    }

    @Override
    public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion) {
        if (world instanceof World) {
            return (float) Math.max(0, (((World) world).random.nextGaussian() * 6 + 8));
        }
        return 6;
    }

    public Random getBlockRandom(BlockPos pos) {
        return new Random(this.getBlockSeed(pos));
    }

    public Long getBlockSeed(BlockPos pos) {
        return MathHelper.getSeed(pos);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos) {
        return this.getBlockRandom(pos).nextInt(16);
    }

    public int getGravityType(BlockPos pos) {
        long seed = this.getBlockSeed(pos);
        if (seed % 3 == 0) return 1;
        //else if (seed % 3 == 0) return 2;
        return 0;
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
    }

    private double total = 0;
    private final NavigableMap<Double, Direction> map = new TreeMap<Double, Direction>() {{
        put(total += CommonConfigs.FLY_CHANCE.get(), Direction.UP);
        put(total += CommonConfigs.FALL_CHANCE.get(), Direction.DOWN);
        put(total += CommonConfigs.MOVE_CHANCE.get() / 4d, Direction.NORTH);
        put(total += CommonConfigs.MOVE_CHANCE.get() / 4d, Direction.SOUTH);
        put(total += CommonConfigs.MOVE_CHANCE.get() / 4d, Direction.EAST);
        put(total += CommonConfigs.MOVE_CHANCE.get() / 4d, Direction.WEST);
        put(total += CommonConfigs.TELEPORT_CHANCE.get(), null);
    }};

    @Override
    public void attack(BlockState state, World world, BlockPos pos, PlayerEntity entity) {
        ItemStack tool = entity.getUseItem();
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool);
        double c = i != 0 ? CommonConfigs.SILK_TOUCH_MULTIPLIER.get() : 1;
        this.excite(state, world, pos, c * CommonConfigs.EXCITE_ON_ATTACK_CHANCE.get());
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        this.excite(state, world, pos, CommonConfigs.EXCITE_ON_BLOCK_UPDATE_CHANCE.get());
    }

    public void excite(BlockState state, World world, BlockPos pos, double chance) {
        if (!world.isClientSide && world.random.nextFloat() < chance / 100f) {

            Direction dir = map.higherEntry(world.random.nextDouble() * total).getValue();

            if (dir == null) {
                this.teleport(state, (ServerWorld) world, pos);
            } else {
                this.move(state, world, pos, dir);
            }
        }
    }

    @Override
    public boolean triggerEvent(BlockState state, World world, BlockPos end, int eventID, int eventParam) {
        if (eventID == 0) {
            Random random = world.random;
            int dx = (eventParam & 255) - 64;
            int dy = (eventParam >> 8 & 255) - 64;
            int dz = (eventParam >> 16 & 255) - 64;
            BlockPos start = new BlockPos(end.getX() - dx, end.getY() - dy, end.getZ() - dz);
            for (int j = 0; j < 64; ++j) {
                double d0 = random.nextDouble();
                float f = (random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (random.nextFloat() - 0.5F) * 0.2F;
                double d1 = MathHelper.lerp(d0, end.getX(), start.getX()) + (random.nextDouble() - 0.5D) + 0.5D;
                double d2 = MathHelper.lerp(d0, end.getY(), start.getY()) + random.nextDouble() - 0.5D;
                double d3 = MathHelper.lerp(d0, end.getZ(), start.getZ()) + (random.nextDouble() - 0.5D) + 0.5D;
                world.addParticle(ParticleTypes.PORTAL, d1, d2, d3, f, f1, f2);
            }
            return true;
        }
        return super.triggerEvent(state, world, end, eventID, eventParam);
    }

    private void teleport(BlockState state, ServerWorld world, BlockPos pos) {
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

    public void move(BlockState state, World world, BlockPos pos, Direction dir) {

        if (FallingBlock.isFree(world.getBlockState(pos.relative(dir)))) {
            MovingBlockEntity entity = new MovingBlockEntity(world, (double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D, state);
            entity.setGravityDirection(dir);
            world.addFreshEntity(entity);
        }

    }
}

