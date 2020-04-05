package com.notvanilla.kevlar.mixin;

import com.notvanilla.kevlar.FeederGoal;
import com.notvanilla.kevlar.ducks.IAnimalEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepEntity.class)
public abstract class SheepEntityMixin extends PassiveEntity {
    public SheepEntityMixin(EntityType<? extends CowEntity> entityType, World world) {
        super(entityType, world);
    }


    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addFeederGoal(CallbackInfo ci) {
        this.goalSelector.add(4, new FeederGoal((AnimalEntity) (Object) this, 1.25));
    }


}
