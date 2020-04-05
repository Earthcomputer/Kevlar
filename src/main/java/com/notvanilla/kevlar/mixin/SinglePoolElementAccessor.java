package com.notvanilla.kevlar.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SinglePoolElement.class)
public interface SinglePoolElementAccessor {
    @Accessor
    Identifier getLocation();

    @Accessor
    ImmutableList<StructureProcessor> getProcessors();
}
