package com.notvanilla.kevlar;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.notvanilla.kevlar.block.KevlarBlocks;
import com.notvanilla.kevlar.item.KevlarItems;
import com.notvanilla.kevlar.mixin.RuleStructureProcessorAccessor;
import com.notvanilla.kevlar.mixin.SinglePoolElementAccessor;
import com.notvanilla.kevlar.mixin.StructurePoolAccessor;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.block.Blocks;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.structure.PlainsVillageData;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.util.Identifier;

import java.util.function.Predicate;

public class KevlarWorldgenEnhancements {

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

    private static void initializeLootTables() {
        LootTableLoadingCallback.EVENT.register(((resourceManager, manager, id, supplier, setter) -> {
            if (ABANDONED_MINEHSAFT_ID.equals(id)) {
                abandonedMineshaft(supplier);
            }
        }));
    }

    private static void addStructureProcessorRule(
            String groupId,
            Predicate<Identifier> elementIdPredicate,
            StructureProcessorRule... rules
    ) {
        StructurePool pool = StructurePoolBasedGenerator.REGISTRY.get(new Identifier(groupId));
        for (Pair<StructurePoolElement, Integer> pair : ((StructurePoolAccessor) pool).getElementCounts()) {
            if (pair.getFirst() instanceof SinglePoolElement) {
                SinglePoolElementAccessor element = (SinglePoolElementAccessor) pair.getFirst();
                if (elementIdPredicate.test(element.getLocation())) {
                    ImmutableList<StructureProcessor> processors = element.getProcessors();
                    for (StructureProcessor processor : processors) {
                        if (processor instanceof RuleStructureProcessor) {
                            RuleStructureProcessorAccessor ruleProcessor = (RuleStructureProcessorAccessor) processor;
                            ruleProcessor.setRules(ImmutableList.<StructureProcessorRule>builder()
                                    .addAll(ruleProcessor.getRules())
                                    .add(rules)
                                    .build());
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void initializeVillageData() {
        PlainsVillageData.initialize();

        addStructureProcessorRule("village/plains/houses", element -> element.getPath().contains("farm"), new StructureProcessorRule(
                new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1f),
                AlwaysTrueRuleTest.INSTANCE,
                KevlarBlocks.ALFALFA.getDefaultState()
        ));
        addStructureProcessorRule("village/plains/zombie/houses", element -> true, new StructureProcessorRule(
                new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1f),
                AlwaysTrueRuleTest.INSTANCE,
                KevlarBlocks.ALFALFA.getDefaultState()
        ));
    }

    public static void initialize() {
        initializeLootTables();
        initializeVillageData();
    }

}
