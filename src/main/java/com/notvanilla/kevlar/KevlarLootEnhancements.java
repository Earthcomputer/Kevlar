package com.notvanilla.kevlar;

import com.notvanilla.kevlar.item.KevlarItems;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.util.Identifier;

public class KevlarLootEnhancements {

    private static final Identifier ABANDONED_MINEHSAFT_ID = new Identifier("chests/abandoned_mineshaft");

    private static void abandonedMineshaft(FabricLootSupplierBuilder supplier) {
        supplier.withPool(
                FabricLootPoolBuilder.builder()
                    .withRolls(ConstantLootTableRange.create(1))
                .withEntry(
                        ItemEntry.builder(KevlarItems.ALFALFA_SEEDS)
                            .withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2, 4)))
                            .withCondition(RandomChanceLootCondition.builder(0.3f))
                )
        );
    }

    public static void initialize() {
        LootTableLoadingCallback.EVENT.register(((resourceManager, manager, id, supplier, setter) -> {
            if (ABANDONED_MINEHSAFT_ID.equals(id)) {
                abandonedMineshaft(supplier);
            }
        }));
    }

}
