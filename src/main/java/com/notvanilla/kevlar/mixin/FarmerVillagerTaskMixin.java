package com.notvanilla.kevlar.mixin;

import com.notvanilla.kevlar.block.KevlarBlocks;
import com.notvanilla.kevlar.item.KevlarItems;
import net.minecraft.entity.ai.brain.task.FarmerVillagerTask;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FarmerVillagerTask.class)
public class FarmerVillagerTaskMixin {

    @Shadow private BlockPos currentTarget;

    @Unique
    private ItemStack invStack;

    @ModifyVariable(method = "keepRunning", ordinal = 0, at = @At(value = "STORE", ordinal = 0))
    private ItemStack captureInvStack(ItemStack invStack) {
        return this.invStack = invStack;
    }

    @ModifyVariable(method = "keepRunning", ordinal = 0, at = @At(value = "FIELD", target = "Lnet/minecraft/item/Items;WHEAT_SEEDS:Lnet/minecraft/item/Item;"))
    private boolean onPlantItem(boolean planted, ServerWorld world) {
        if (invStack.getItem() == KevlarItems.ALFALFA_SEEDS) {
            world.setBlockState(currentTarget, KevlarBlocks.ALFALFA.getDefaultState());
            planted = true;
        }
        invStack = null;
        return planted;
    }

}
