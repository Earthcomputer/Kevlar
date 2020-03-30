package com.notvanilla.kevlar.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.RepeaterBlock;

public class IronRepeaterBlock extends RepeaterBlock {
    protected IronRepeaterBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return state.get(DELAY) * 8;
    }
}