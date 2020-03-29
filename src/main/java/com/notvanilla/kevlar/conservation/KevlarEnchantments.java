package com.notvanilla.kevlar.conservation;

import com.notvanilla.kevlar.Kevlar;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class KevlarEnchantments {

    public static Enchantment CONSERVATION = registerEnchantment("conservation", new ConservationEnchantment(Enchantment.Weight.UNCOMMON, EnchantmentTarget.ARMOR_FEET, EquipmentSlot.FEET));

    @SuppressWarnings("unchecked")
    private static <T extends Enchantment> T registerEnchantment(String name, Enchantment enchantment) {
        return (T) Registry.register(Registry.ENCHANTMENT, new Identifier(Kevlar.MOD_ID, name), enchantment);
    }

    public static void register() {
        // load class
    }

}
