package com.notvanilla.kevlar.container;

import com.notvanilla.kevlar.Kevlar;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class KevlarContainers {

    public static final Identifier GENERIC_1X1 = registerContainer("generic_1x1");

    private static Identifier registerContainer(String name) {
        Identifier id = new Identifier(Kevlar.MOD_ID, name);
        ContainerProviderRegistry.INSTANCE.registerFactory(
                id,
                (syncId, identifier, player, buf) -> {
                    BlockPos pos = buf.readBlockPos();
                    buf.readText(); // name
                    NameableContainerFactory containerFactory = player.world.getBlockState(pos).createContainerFactory(player.world, pos);
                    return containerFactory == null ? null : containerFactory.createMenu(syncId, player.inventory, player);
                }
        );
        return id;
    }

    public static void open(Identifier id, PlayerEntity player, BlockPos pos, Text name) {
        ContainerProviderRegistry.INSTANCE.openContainer(id, player, buf -> {
            buf.writeBlockPos(pos);
            buf.writeText(name);
        });
    }

    public static void register() {
        // load the class
    }

}
