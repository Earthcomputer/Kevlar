package com.notvanilla.kevlar.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.HayBlock;
import net.minecraft.util.math.Direction;

public class AlfalfaBaleBlock extends HayBlock {
    public AlfalfaBaleBlock(Settings settings) {
        super(settings);

        this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.Y));
    }


}
