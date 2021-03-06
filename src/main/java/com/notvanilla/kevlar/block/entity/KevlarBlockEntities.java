package com.notvanilla.kevlar.block.entity;

import com.notvanilla.kevlar.Kevlar;
import com.notvanilla.kevlar.block.KevlarBlocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class KevlarBlockEntities {

    public static final BlockEntityType<PlanterBlockEntity> PLANTER = registerBlockEntity(
            "planter",
            BlockEntityType.Builder.create(PlanterBlockEntity::new, KevlarBlocks.PLANTER)
    );
    public static final BlockEntityType<BlockBreakerBlockEntity> BLOCK_BREAKER = registerBlockEntity(
            "block_breaker",
            BlockEntityType.Builder.create(BlockBreakerBlockEntity::new, KevlarBlocks.BLOCK_BREAKER)
    );
    public static final BlockEntityType<WirelessTeleportTransmitterBlockEntity> WIRELESS_TELEPORT_TRANSMITTER = registerBlockEntity(
            "wireless_teleport_transmitter",
            BlockEntityType.Builder.create(WirelessTeleportTransmitterBlockEntity::new, KevlarBlocks.WIRELESS_TELEPORT_TRANSMITTER)
    );
    public static final BlockEntityType<WirelessTeleportReceiverBlockEntity> WIRELESS_TELEPORT_RECEIVER = registerBlockEntity(
            "wireless_teleport_receiver",
            BlockEntityType.Builder.create(WirelessTeleportReceiverBlockEntity::new, KevlarBlocks.WIRELESS_TELEPORT_RECEIVER)
    );

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, BlockEntityType.Builder<T> builder) {
        BlockEntityType<T> blockEntityType = builder.build(null);
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Kevlar.MOD_ID, name), blockEntityType);
    }

    public static void register() {
        // load the class
    }

}
