package com.notvanilla.kevlar;

import com.notvanilla.kevlar.block.KevlarBlocks;
import com.notvanilla.kevlar.block.entity.KevlarBlockEntities;
import net.fabricmc.api.ModInitializer;

public class Kevlar implements ModInitializer {

    public static final String MOD_ID = "kevlar";
    @Override
    public void onInitialize() {
        KevlarBlocks.register();
        KevlarBlockEntities.register();
    }

}
