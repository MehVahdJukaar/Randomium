package net.mehvahdjukaar.randomium.common;

import net.mehvahdjukaar.moonlight.api.entity.IExtraClientSpawnData;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.randomium.Randomium;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MovingBlockEntity extends FallingBlockEntity implements IExtraClientSpawnData {

    protected Direction gravityDirection = Direction.DOWN;
    protected BlockState state;

    public MovingBlockEntity(Level level) {
        super(Randomium.MOVING_BLOCK_ENTITY.get(), level);
    }

    public MovingBlockEntity(Level level, double x, double y, double z, BlockState state, Direction gravityDirection) {
        super(Randomium.MOVING_BLOCK_ENTITY.get(), level);
        this.blocksBuilding = true;
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
        this.gravityDirection = gravityDirection;
        this.state = state;
    }

    public MovingBlockEntity(EntityType<MovingBlockEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) PlatHelper.getEntitySpawnPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(Block.getId(this.getBlockState()));
        buffer.writeEnum(this.gravityDirection);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        this.state = Block.stateById(additionalData.readInt());
        this.gravityDirection = additionalData.readEnum(Direction.class);
    }

    @Override
    public EntityType<?> getType() {
        return Randomium.MOVING_BLOCK_ENTITY.get();
    }

    @Override
    public BlockState getBlockState() {
        return state;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) { //this.entityData.get(DATA_FALL_DIRECTION)
        super.addAdditionalSaveData(tag);
        tag.putByte("GravityDirection", (byte) this.gravityDirection.get3DDataValue());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.gravityDirection = Direction.from3DDataValue(tag.getByte("GravityDirection"));
        this.state = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK),tag.getCompound("BlockState"));
    }

    @Override
    public void tick() {
        if (this.state.isAir()) {
            this.discard();
        } else {
            Block block = this.state.getBlock();
            Level level = this.level();
            if (this.time++ == 0) {
                BlockPos blockpos = this.blockPosition();
                if (level.getBlockState(blockpos).is(block)) {
                    level.removeBlock(blockpos, false);
                } else if (!level.isClientSide) {
                    this.discard();
                    return;
                }
            }

            Vec3i n = this.gravityDirection.getNormal();
            if (!this.isNoGravity()) {

                this.setDeltaMovement(this.getDeltaMovement().add(n.getX() * 0.04D, n.getY() * 0.04D, n.getZ() * 0.04D));
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
            if (!level.isClientSide) {
                BlockPos blockpos1 = this.blockPosition();

                boolean collided = this.verticalCollision || this.horizontalCollision;

                boolean isVertical = this.gravityDirection.getAxis() == Direction.Axis.Y;


                if (collided ||
                        !isVertical && this.random.nextBoolean() && this.position().distanceToSqr(Vec3.atBottomCenterOf(this.blockPosition())) <=
                                Mth.square((float) this.getDeltaMovement().multiply(n.getX(), n.getY(), n.getZ()).length()) / 2f) {

                    BlockState blockstate = level.getBlockState(blockpos1);

                    if (!blockstate.is(Blocks.MOVING_PISTON)) {
                        boolean flag2 = blockstate.canBeReplaced(new DirectionalPlaceContext(level, blockpos1, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                        //boolean hasAirBelow = FallingBlock.isFree(this.level.getBlockState(blockpos1.below()));
                        boolean flag4 = this.state.canSurvive(level, blockpos1);
                        if (flag2 && flag4) {

                            if (level.setBlock(blockpos1, this.state, 3)) {
                                ((ServerLevel) level).getChunkSource().chunkMap.broadcast(this, new ClientboundBlockUpdatePacket(blockpos1, level.getBlockState(blockpos1)));
                                this.discard();
                                if (block instanceof Fallable fallable) {
                                    fallable.onLand(level, blockpos1, this.state, blockstate, this);
                                }

                            } else if (this.dropItem && level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                this.discard();
                                this.callOnBrokenAfterFall(block, blockpos1);
                                this.spawnAtLocation(block);
                            }
                        } else {
                            this.discard();
                            if (this.dropItem && level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                this.callOnBrokenAfterFall(block, blockpos1);
                                this.spawnAtLocation(block);
                            }
                        }
                    }
                } else {
                    if (!level.isClientSide && (this.time > 100 && (blockpos1.getY() <= level.getMinBuildHeight() || blockpos1.getY() > level.getMaxBuildHeight()) || this.time > 600)) {

                        if (this.gravityDirection == Direction.UP) {
                            this.gravityDirection = Direction.DOWN;
                            this.time = 1;
                        } else {
                            if (level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                this.spawnAtLocation(block);
                            }

                            this.discard();
                        }
                    }
                }
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }
    }


}