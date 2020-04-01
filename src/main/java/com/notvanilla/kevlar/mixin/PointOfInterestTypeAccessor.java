package com.notvanilla.kevlar.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.world.poi.PointOfInterestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(PointOfInterestType.class)
public interface PointOfInterestTypeAccessor {
    @Invoker
    static PointOfInterestType callRegister(String id, Set<BlockState> workStationStates, int ticketCount, int searchDistance) {
        throw new UnsupportedOperationException();
    }
}
