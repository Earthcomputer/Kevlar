package com.notvanilla.kevlar.mixin;

import net.minecraft.world.gen.feature.BlockPileFeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockPileFeatureConfig.class)
public interface BlockPileFeatureConfigAccessor {

    @Mutable
    @Accessor
    void setStateProvider(BlockStateProvider stateProvider);

}
