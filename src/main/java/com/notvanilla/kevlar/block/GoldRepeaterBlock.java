package com.notvanilla.kevlar.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.RepeaterBlock;

public class GoldRepeaterBlock extends RepeaterBlock {
    protected GoldRepeaterBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return state.get(DELAY) * 32;
    }
}
