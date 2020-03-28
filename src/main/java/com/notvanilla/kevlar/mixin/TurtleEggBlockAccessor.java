package com.notvanilla.kevlar.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TurtleEggBlock.class)
public interface TurtleEggBlockAccessor {
    @Invoker
    void callBreakEgg(World world, BlockPos pos, BlockState state);
}
