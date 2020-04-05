package com.notvanilla.kevlar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OptionalDynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import com.notvanilla.kevlar.block.AlfalfaBaleBlock;
import com.notvanilla.kevlar.block.KevlarBlocks;
import com.notvanilla.kevlar.item.KevlarItems;
import com.notvanilla.kevlar.mixin.*;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HayBlock;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.structure.DesertVillageData;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

import java.util.Random;
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
                    boolean foundRuleProcessor = false;
                    for (StructureProcessor processor : processors) {
                        if (processor instanceof RuleStructureProcessor) {
                            RuleStructureProcessorAccessor ruleProcessor = (RuleStructureProcessorAccessor) processor;
                            ruleProcessor.setRules(ImmutableList.<StructureProcessorRule>builder()
                                    .addAll(ruleProcessor.getRules())
                                    .add(rules)
                                    .build());
                            foundRuleProcessor = true;
                            break;
                        }
                    }
                    if (!foundRuleProcessor) {
                        element.setProcessors(ImmutableList.<StructureProcessor>builder()
                                .addAll(processors)
                                .add(new RuleStructureProcessor(ImmutableList.copyOf(rules)))
                                .build());
                    }
                }
            }
        }
    }

    private static void initializeVillageData() {
        PlainsVillageData.initialize();
        DesertVillageData.initialize();

        StructureProcessorRule wheatToAlfalfaRule = new StructureProcessorRule(
                new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1f),
                AlwaysTrueRuleTest.INSTANCE,
                KevlarBlocks.ALFALFA.getDefaultState()
        );
        addStructureProcessorRule("village/plains/houses", element -> element.getPath().contains("farm"), wheatToAlfalfaRule);
        addStructureProcessorRule("village/plains/zombie/houses", element -> true, wheatToAlfalfaRule);

        StructureProcessorRule hayBaleToAlfalfaBaleRule = new StructureProcessorRule(
                new RandomBlockMatchRuleTest(Blocks.HAY_BLOCK, 0.3f),
                AlwaysTrueRuleTest.INSTANCE,
                KevlarBlocks.ALFALFA_BALE.getDefaultState()
        );
        addStructureProcessorRule(
                "village/plains/houses",
                element -> element.getPath().contains("stable") || element.getPath().contains("animal_pen") || element.getPath().contains("farm"),
                hayBaleToAlfalfaBaleRule
        );
        addStructureProcessorRule("village/desert/town_centers", element -> element.getPath().contains("meeting_point"), hayBaleToAlfalfaBaleRule);
        addStructureProcessorRule("village/desert/houses", element -> true, hayBaleToAlfalfaBaleRule);
        ((BlockPileFeatureConfigAccessor) DefaultBiomeFeatures.HAY_PILE_CONFIG).setStateProvider(
                new AlfalfaBaleBlockStateProvider(DefaultBiomeFeatures.HAY_PILE_CONFIG.stateProvider)
        );
    }

    public static void initialize() {
        initializeLootTables();
        initializeVillageData();
    }

    private static class AlfalfaBaleBlockStateProvider extends BlockStateProvider {

        private static final BlockStateProviderType<AlfalfaBaleBlockStateProvider> TYPE = BlockStateProviderTypeAccessor.callRegister(
                "kevlar:alfalfa_bale",
                AlfalfaBaleBlockStateProvider::new
        );

        private final BlockStateProvider delegate;

        public AlfalfaBaleBlockStateProvider(Dynamic<?> dynamic) {
            super(TYPE);
            OptionalDynamic<?> delegateDyn = dynamic.get("delegate");
            BlockStateProviderType<?> delegateType = Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier(delegateDyn.get("type").asString()
                    .orElseThrow(RuntimeException::new)));
            assert delegateType != null;
            this.delegate = delegateType.deserialize(delegateDyn.orElseEmptyMap());
        }

        public AlfalfaBaleBlockStateProvider(BlockStateProvider delegate) {
            super(TYPE);
            this.delegate = delegate;
        }

        @Override
        public BlockState getBlockState(Random random, BlockPos pos) {
            BlockState state = delegate.getBlockState(random, pos);
            if (state.getBlock() == Blocks.HAY_BLOCK && random.nextFloat() < 0.3f) {
                state = KevlarBlocks.ALFALFA_BALE.getDefaultState().with(AlfalfaBaleBlock.AXIS, state.get(HayBlock.AXIS));
            }
            return state;
        }

        @Override
        public <T> T serialize(DynamicOps<T> ops) {
            ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
            builder.put(ops.createString("delegate"), this.delegate.serialize(ops));
            return ops.createMap(builder.build());
        }
    }

}
