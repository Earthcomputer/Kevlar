package com.notvanilla.kevlar.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RuleStructureProcessor.class)
public interface RuleStructureProcessorAccessor {
    @Accessor
    ImmutableList<StructureProcessorRule> getRules();

    @Mutable
    @Accessor
    void setRules(ImmutableList<StructureProcessorRule> rules);
}
