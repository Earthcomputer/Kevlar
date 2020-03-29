package com.notvanilla.kevlar;

import com.notvanilla.kevlar.block.KevlarBlocks;
import com.notvanilla.kevlar.block.entity.KevlarBlockEntities;
import com.notvanilla.kevlar.item.KevlarItems;
import net.fabricmc.api.ModInitializer;

public class Kevlar implements ModInitializer {

    public static final String MOD_ID = "kevlar";

    @Override
    public void onInitialize() {

        //order important here

        KevlarBlocks.register();
        KevlarBlockEntities.register();
        KevlarItems.register();
    }

}
