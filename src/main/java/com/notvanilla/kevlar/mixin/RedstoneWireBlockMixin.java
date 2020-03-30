package com.notvanilla.kevlar.mixin;

import com.notvanilla.kevlar.block.KevlarBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin  {

    @Inject(method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z", at = @At("HEAD"), cancellable = true)
    private static void stopCustomRepeaterConnections(BlockState state, Direction dir, CallbackInfoReturnable<Boolean> ci) {
        if (state.getBlock() == KevlarBlocks.IRON_REPEATER || state.getBlock() == KevlarBlocks.GOLD_REPEATER) {
            Direction direction = state.get(RepeaterBlock.FACING);
            ci.setReturnValue(direction == dir || direction.getOpposite() == dir);
        }
    }
}
