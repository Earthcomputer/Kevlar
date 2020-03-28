package com.notvanilla.kevlar.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class BlockBreakerBlock extends Block implements BlockEntityProvider {

    public BlockBreakerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return null;
    }
}
