package com.notvanilla.kevlar.mixin;


import com.notvanilla.kevlar.ducks.IAnimalEntity;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.passive.AnimalEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalMateGoal.class)
public class AnimalMateGoalMixin {

    @Shadow @Final protected AnimalEntity animal;

    @Shadow protected AnimalEntity mate;

    @Inject(method = "breed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/AnimalEntity;setBreedingAge(I)V", ordinal = 1, shift = At.Shift.AFTER))
    private void setQuickBreedingRegen(CallbackInfo ci) {
        if(((IAnimalEntity) animal).kevlarIsFastBreeding()) {
            animal.setBreedingAge(1200);//1 minute so 5 times faster
        }

        if(((IAnimalEntity) mate).kevlarIsFastBreeding()) {
            mate.setBreedingAge(1200);
        }
    }
}
