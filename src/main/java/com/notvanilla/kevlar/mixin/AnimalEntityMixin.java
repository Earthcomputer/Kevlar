package com.notvanilla.kevlar.mixin;

import com.notvanilla.kevlar.FeedingUtil;
import com.notvanilla.kevlar.ducks.IAnimalEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity implements IAnimalEntity {


    protected AnimalEntityMixin(EntityType<? extends PassiveEntity> type, World world) {
        super(type, world);
    }

    @Shadow public abstract boolean canEat();

    @Shadow protected abstract void eat(PlayerEntity player, ItemStack stack);

    @Shadow public abstract void lovePlayer(PlayerEntity player);

    @Shadow private int loveTicks;
    @Unique
    protected boolean isFastBreeding = false;

    @Inject(method = "readCustomDataFromTag", at = @At("TAIL"))
    private void readIsFastBreeding(CompoundTag tag, CallbackInfo ci) {
        isFastBreeding = tag.getBoolean("Kevlar:FastBreeding");
    }

    @Inject(method = "writeCustomDataToTag", at = @At("TAIL"))
    private void writeIsFastBreeding(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("Kevlar:FastBreeding", isFastBreeding);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void checkIfFastBreeding(PlayerEntity player, Hand hand, CallbackInfoReturnable<Boolean> ci) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (FeedingUtil.animalToFastBreedingPlant.get(this.getType()) == itemStack.getItem()) {
            if (!this.world.isClient && this.getBreedingAge() == 0 && this.canEat()) {
                isFastBreeding = true;
                this.eat(player, itemStack);
                this.lovePlayer(player);
                player.swingHand(hand, true);
                ci.setReturnValue(true);
            }
        }
    }

    @Inject(method = "lovePlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/passive/AnimalEntity;loveTicks:I", shift = At.Shift.AFTER))
    private void setLongFertileTime(CallbackInfo ci) {
        if(isFastBreeding) {
            this.loveTicks = 1200;
        }
    }

    @Override
    public boolean kevlarIsFastBreeding() {
        return isFastBreeding;
    }



}
