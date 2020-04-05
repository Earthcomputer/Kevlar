package com.notvanilla.kevlar.mixin;

import com.notvanilla.kevlar.item.KevlarItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends AbstractTraderEntity {

    public VillagerEntityMixin(EntityType<? extends AbstractTraderEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "hasSeedToPlant", at = @At("RETURN"), cancellable = true)
    private void onHasSeedToPlant(CallbackInfoReturnable<Boolean> ci) {
        if (!ci.getReturnValueZ()) {
            if (getInventory().containsAnyInInv(Collections.singleton(KevlarItems.ALFALFA_SEEDS))) {
                ci.setReturnValue(true);
            }
        }
    }

}
