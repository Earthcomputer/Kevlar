package com.notvanilla.kevlar.block;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.WorldView;

public class IronButtonBlock extends AbstractButtonBlock {


    protected IronButtonBlock(Settings settings) {
        super(false, settings);
    }

    @Override
    public int getTickRate(WorldView worldView) {
        return 10;
    }

    @Override
    protected SoundEvent getClickSound(boolean powered) {
        return powered ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
    }
}
