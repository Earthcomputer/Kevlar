package com.notvanilla.kevlar.block;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.WorldView;

public class GoldButtonBlock extends AbstractButtonBlock {
    protected GoldButtonBlock(Settings settings) {
        super(false, settings);
    }

    @Override
    public int getTickRate(WorldView worldView) {
        return 2;
    }

    @Override
    protected SoundEvent getClickSound(boolean powered) {
        return powered ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;//TODO(Dan) Change this to my own sound
    }
}
