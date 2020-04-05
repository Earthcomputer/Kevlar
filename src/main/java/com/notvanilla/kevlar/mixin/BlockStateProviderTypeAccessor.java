package com.notvanilla.kevlar.mixin;

import com.mojang.datafixers.Dynamic;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Function;

@Mixin(BlockStateProviderType.class)
public interface BlockStateProviderTypeAccessor {
    @Invoker
    static <P extends BlockStateProvider> BlockStateProviderType<P> callRegister(String id, Function<Dynamic<?>, P> configDeserializer) {
        throw new UnsupportedOperationException();
    }
}
