package com.notvanilla.kevlar.client;

import com.notvanilla.kevlar.block.KevlarBlocks;
import com.notvanilla.kevlar.client.gui.Generic1x1ContainerScreen;
import com.notvanilla.kevlar.container.KevlarContainers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.container.Container;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

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

        registerScreenProvider(KevlarContainers.GENERIC_1X1, Generic1x1ContainerScreen::new);
        registerScreenProvider(KevlarContainers.WIRELESS_TELEPORT_RECEIVER, Generic1x1ContainerScreen::new);
    }

    @SuppressWarnings("unchecked")
    private static <C extends Container> void registerScreenProvider(Identifier id, ContainerScreenFactory2<C> factory) {
        ScreenProviderRegistry.INSTANCE.registerFactory(id, (syncId, id2, player, buf) -> {
            BlockPos pos = buf.readBlockPos();
            Text name = buf.readText();
            NameableContainerFactory containerFactory = player.world.getBlockState(pos).createContainerFactory(player.world, pos);
            if (containerFactory == null)
                return null;
            Container container = containerFactory.createMenu(syncId, player.inventory, player);
            return factory.create((C) container, player.inventory, name);
        });
    }

    @FunctionalInterface
    private interface ContainerScreenFactory2<C extends Container> {
        ContainerScreen<C> create(C container, PlayerInventory inv, Text title);
    }
}
