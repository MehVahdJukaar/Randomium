package net.mehvahdjukaar.randomium.common;

import net.mehvahdjukaar.randomium.Randomium;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RandomiumBlockEntity extends BlockEntity {
    public RandomiumBlockEntity(BlockPos pos, BlockState state) {
        super(Randomium.RANDOMIUM_BLOCK_ENTITY.get(), pos, state);
    }
}
