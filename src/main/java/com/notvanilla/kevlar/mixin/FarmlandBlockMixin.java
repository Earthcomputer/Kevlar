package com.notvanilla.kevlar.mixin;

import com.notvanilla.kevlar.conservation.KevlarEnchantments;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin {

    @Redirect(method = "onLandedUpon", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z"))
    private boolean shouldProtectDirt(World world, World world2, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity) {
            int conservationLevel = EnchantmentHelper.getEquipmentLevel(KevlarEnchantments.CONSERVATION, (LivingEntity) entity);
            //noinspection RedundantIfStatement
            if (conservationLevel > 0) {
                return true;
            }
        }
        return false;
    }

}
