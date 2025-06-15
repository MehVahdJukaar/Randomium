package net.mehvahdjukaar.randomium.common;

import net.mehvahdjukaar.moonlight.api.block.MimicBlockTile;
import net.mehvahdjukaar.randomium.Randomium;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TrolliumBlockEntity extends MimicBlockTile {
    public TrolliumBlockEntity(BlockPos pos, BlockState blockState) {
        super(Randomium.TROLLIUM_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
    }
}
