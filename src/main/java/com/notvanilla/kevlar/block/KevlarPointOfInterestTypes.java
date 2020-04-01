package com.notvanilla.kevlar.block;

import com.google.common.collect.ImmutableSet;
import com.notvanilla.kevlar.mixin.PointOfInterestTypeAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.Set;

public class KevlarPointOfInterestTypes {


    public static final PointOfInterestType DIMENSION_RECEIVER =register(
            "dimension_receiver",
            getAllStates(KevlarBlocks.NETHER_RECEIVER, KevlarBlocks.END_RECEIVER, KevlarBlocks.OVERWORLD_RECEIVER)
            );




    private static PointOfInterestType register(String id, Set<BlockState>blockStates ) {
        return PointOfInterestTypeAccessor.callRegister(
                "kevlar:" + id,
                blockStates,
                0,
                1
        );
    }

    public static Set<BlockState> getAllStates(Block... blocks) {


        ImmutableSet.Builder<BlockState> setBuilder = ImmutableSet.builder();

        for(Block b : blocks) {
            setBuilder.addAll(b.getStateManager().getStates());
        }

        return setBuilder.build();
    }


    public static void register() {
        //created just to load the class
    }
}
