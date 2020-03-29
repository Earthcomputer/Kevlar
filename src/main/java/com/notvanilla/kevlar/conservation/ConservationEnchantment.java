package com.notvanilla.kevlar.conservation;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class ConservationEnchantment extends Enchantment {
    protected ConservationEnchantment(Weight weight, EnchantmentTarget type, EquipmentSlot... slotTypes) {
        super(weight, type, slotTypes);
    }
}
