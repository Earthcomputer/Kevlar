package com.notvanilla.kevlar.client;

import com.notvanilla.kevlar.block.KevlarBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class KevlarClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(
                RenderLayer.getCutout(),
                KevlarBlocks.IRON_REPEATER,
                KevlarBlocks.GOLD_REPEATER,
                KevlarBlocks.ALFALFA
        );
    }
}
