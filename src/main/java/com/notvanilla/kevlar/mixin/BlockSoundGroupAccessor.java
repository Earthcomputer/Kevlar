package com.notvanilla.kevlar.mixin;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockSoundGroup.class)
public interface BlockSoundGroupAccessor {
    @Accessor("hitSound")
    SoundEvent getHitSound_common();
}
