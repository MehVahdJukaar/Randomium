package net.mehvahdjukaar.randomium.entity;

import com.google.common.collect.Lists;
import net.mehvahdjukaar.randomium.Randomium;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;

public class MovingBlockEntity extends Entity implements IEntityAdditionalSpawnData {

    private Direction gravityDirection = Direction.DOWN;
    private BlockState blockState = Blocks.SAND.defaultBlockState();
    public int time;
    private boolean cancelDrop;

    protected static final DataParameter<BlockPos> DATA_START_POS = EntityDataManager.defineId(net.minecraft.entity.item.FallingBlockEntity.class, DataSerializers.BLOCK_POS);

    public MovingBlockEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
    }

    public MovingBlockEntity(World world, double x, double y, double z, BlockState state) {
        this(Randomium.MOVING_BLOCK_ENTITY.get(), world);
        this.blockState = state;
        this.blocksBuilding = true;
        this.setPos(x, y + (double) ((1.0F - this.getBbHeight()) / 2.0F), z);
        this.setDeltaMovement(Vector3d.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
    }

    public void setGravityDirection(Direction gravityDirection) {
        this.gravityDirection = gravityDirection;
    }

    public MovingBlockEntity(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(Randomium.MOVING_BLOCK_ENTITY.get(), world);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeInt(Block.getId(this.blockState));
        buffer.writeEnum(this.gravityDirection);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        this.blockState = Block.stateById(additionalData.readInt());
        this.gravityDirection = additionalData.readEnum(Direction.class);
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT tag) { //this.entityData.get(DATA_FALL_DIRECTION)
        tag.put("BlockState", NBTUtil.writeBlockState(this.blockState));
        tag.putByte("GravityDirection", (byte) this.gravityDirection.get3DDataValue());
        tag.putInt("Time", this.time);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT tag) {
        this.blockState = NBTUtil.readBlockState(tag.getCompound("BlockState"));
        this.gravityDirection = Direction.from3DDataValue(tag.getByte("GravityDirection"));
        //this.entityData.set(DATA_FALL_DIRECTION, Direction.from3DDataValue(tag.getByte("GravityDirection")));
        this.time = tag.getInt("Time");

        if (this.blockState.isAir()) {
            this.blockState = Blocks.RED_SAND.defaultBlockState();
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_START_POS, BlockPos.ZERO);
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    public void setStartPos(BlockPos pos) {
        this.entityData.set(DATA_START_POS, pos);
    }

    public BlockPos getStartPos() {
        return this.entityData.get(DATA_START_POS);
    }

    @Override
    protected boolean isMovementNoisy() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return this.isAlive();
    }

    @Override
    public void tick() {
        if (this.blockState.isAir()) {
            this.remove();
        } else {
            Block block = this.blockState.getBlock();
            if (this.time++ == 0) {
                BlockPos blockpos = this.blockPosition();
                if (this.level.getBlockState(blockpos).is(block)) {
                    this.level.removeBlock(blockpos, false);
                } else if (!this.level.isClientSide) {
                    this.remove();
                    return;
                }
            }

            Vector3i n = this.gravityDirection.getNormal();
            if (!this.isNoGravity()) {

                this.setDeltaMovement(this.getDeltaMovement().add(n.getX() * 0.04D, n.getY() * 0.04D, n.getZ() * 0.04D));
            }

            this.moveSelf(this.getDeltaMovement());
            if (!this.level.isClientSide) {
                BlockPos blockpos1 = this.blockPosition();

                boolean collided = this.verticalCollision || this.horizontalCollision;

                boolean isVertical = this.gravityDirection.getAxis() == Direction.Axis.Y;

                if (collided || !isVertical && this.random.nextBoolean() && this.position().distanceToSqr(Vector3d.atBottomCenterOf(this.blockPosition())) <=
                        MathHelper.square((float) this.getDeltaMovement().multiply(n.getX(), n.getY(), n.getZ()).length()) / 2f) {
                    BlockState blockstate = this.level.getBlockState(blockpos1);
                    //this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
                    if (!blockstate.is(Blocks.MOVING_PISTON)) {
                        this.remove();
                        if (!this.cancelDrop) {
                            boolean flag2 = blockstate.canBeReplaced(new DirectionalPlaceContext(this.level, blockpos1, this.gravityDirection, ItemStack.EMPTY, Direction.UP));
                            //boolean flag3 = FallingBlock.isFree(this.level.getBlockState(blockpos1.relative(this.gravityDirection)));
                            boolean flag4 = this.blockState.canSurvive(this.level, blockpos1);
                            if (flag2 && flag4 && this.level.setBlock(blockpos1, this.blockState, 3)) {

                            } else if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                this.spawnAtLocation(block);
                            }
                        }
                    }
                } else {
                    if (this.time > 100 && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || this.time > 600) {

                        if (this.gravityDirection == Direction.UP) {
                            this.gravityDirection = Direction.DOWN;
                            this.time = 1;
                        } else {
                            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                this.spawnAtLocation(block);
                            }

                            this.remove();
                        }
                    }
                }
            }
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }
    }


    public void moveSelf(Vector3d movement) {
        if (this.noPhysics) {
            this.setBoundingBox(this.getBoundingBox().move(movement));
            this.setLocationFromBoundingbox();
        } else {

            this.level.getProfiler().push("move");
            if (this.stuckSpeedMultiplier.lengthSqr() > 1.0E-7D) {
                movement = movement.multiply(this.stuckSpeedMultiplier);
                this.stuckSpeedMultiplier = Vector3d.ZERO;
                this.setDeltaMovement(Vector3d.ZERO);
            }

            Vector3d vector3d = this.collide(movement);
            if (vector3d.lengthSqr() > 1.0E-7D) {
                this.setBoundingBox(this.getBoundingBox().move(vector3d));
                this.setLocationFromBoundingbox();
            }

            this.level.getProfiler().pop();

            this.level.getProfiler().push("rest");
            this.horizontalCollision = !MathHelper.equal(movement.x, vector3d.x) || !MathHelper.equal(movement.z, vector3d.z);
            this.verticalCollision = movement.y != vector3d.y;
            this.onGround = this.verticalCollision && movement.y < 0.0D;
            BlockPos blockpos = this.getOnPos();
            BlockState blockstate = this.level.getBlockState(blockpos);

            this.checkFallDamage(vector3d.y, this.onGround, blockstate, blockpos);

            Vector3d vector3d1 = this.getDeltaMovement();
            if (movement.x != vector3d.x) {
                this.setDeltaMovement(0.0D, vector3d1.y, vector3d1.z);
            }

            if (movement.z != vector3d.z) {
                this.setDeltaMovement(vector3d1.x, vector3d1.y, 0.0D);
            }

            Block block = blockstate.getBlock();
            if (movement.y != vector3d.y) {
                block.updateEntityAfterFallOn(this.level, this);
            }

            try {
                this.checkInsideBlocks();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Checking entity block collision");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being checked for collision");
                this.fillCrashReportCategory(crashreportcategory);
                throw new ReportedException(crashreport);
            }

            float f2 = this.getBlockSpeedFactor();
            this.setDeltaMovement(this.getDeltaMovement().multiply(f2, 1.0D, f2));

            this.level.getProfiler().pop();
        }
    }

    @Override
    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
        int i = MathHelper.ceil(p_225503_1_ - 1.0F);
        if (i > 0) {
            List<Entity> list = Lists.newArrayList(this.level.getEntities(this, this.getBoundingBox()));
            boolean flag = this.blockState.is(BlockTags.ANVIL);
            DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;

            for (Entity entity : list) {
                float fallDamageAmount = 2.0F;
                int fallDamageMax = 40;
                entity.hurt(damagesource, (float) Math.min(MathHelper.floor((float) i * fallDamageAmount), fallDamageMax));
            }

            if (flag && (double) this.random.nextFloat() < (double) 0.05F + (double) i * 0.05D) {
                BlockState blockstate = AnvilBlock.damage(this.blockState);
                if (blockstate == null) {
                    this.cancelDrop = true;
                } else {
                    this.blockState = blockstate;
                }
            }
        }

        return false;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public BlockState getBlockState() {
        return blockState;
    }
}