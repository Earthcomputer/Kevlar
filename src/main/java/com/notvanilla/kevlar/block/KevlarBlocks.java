package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.Kevlar;
import com.notvanilla.kevlar.item.KevlarItemGroup;
import com.notvanilla.kevlar.wireless.NodeType;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

public class KevlarBlocks {

    public static final PlanterBlock PLANTER = registerBlock(
            "planter",
            new PlanterBlock(FabricBlockSettings.copy(Blocks.DISPENSER).build()),
            KevlarItemGroup.INSTANCE
    );

    public static final AlfalfaBlock ALFALFA = registerBlock(
            "alfalfa",
            new AlfalfaBlock(FabricBlockSettings.copy(Blocks.WHEAT).build()),
            null
    );

    public static final BlockBreakerBlock BLOCK_BREAKER = registerBlock(
            "block_breaker",
            new BlockBreakerBlock(FabricBlockSettings.copy(Blocks.DISPENSER).build()),
            KevlarItemGroup.INSTANCE
    );

    public static final IronButtonBlock IRON_BUTTON = registerBlock(
            "iron_button",
            new IronButtonBlock(FabricBlockSettings.copy(Blocks.STONE_BUTTON).sounds(BlockSoundGroup.METAL).hardness(2.0f).build()),
            KevlarItemGroup.INSTANCE
    );

    public static final GoldButtonBlock GOLD_BUTTON = registerBlock(
            "gold_button",
            new GoldButtonBlock(FabricBlockSettings.copy(Blocks.STONE_BUTTON).sounds(BlockSoundGroup.METAL).hardness(2.0f).build()),
            KevlarItemGroup.INSTANCE
    );

    public static final IronRepeaterBlock IRON_REPEATER = registerBlock(
            "iron_repeater",
            new IronRepeaterBlock(FabricBlockSettings.copy(Blocks.REPEATER).sounds(BlockSoundGroup.METAL).build()),
            KevlarItemGroup.INSTANCE
    );

    public static final GoldRepeaterBlock GOLD_REPEATER = registerBlock(
            "gold_repeater",
            new GoldRepeaterBlock(FabricBlockSettings.copy(Blocks.REPEATER).sounds(BlockSoundGroup.METAL).build()),
            KevlarItemGroup.INSTANCE
    );

    public static final DimensionReceiverBlock NETHER_RECEIVER = registerBlock(
            "nether_receiver",
            new DimensionReceiverBlock(FabricBlockSettings.of(Material.METAL).build(), DimensionType.THE_NETHER),
            KevlarItemGroup.INSTANCE
    );

    public static final DimensionReceiverBlock END_RECEIVER = registerBlock(
            "end_receiver",
            new DimensionReceiverBlock(FabricBlockSettings.of(Material.METAL).build(), DimensionType.THE_END),
            KevlarItemGroup.INSTANCE
    );

    public static final DimensionReceiverBlock OVERWORLD_RECEIVER = registerBlock(
            "overworld_receiver",
            new DimensionReceiverBlock(FabricBlockSettings.of(Material.METAL).build(), DimensionType.OVERWORLD),
            KevlarItemGroup.INSTANCE
    );


    public static final DimensionTransmitterBlock NETHER_TRANSMITTER = registerBlock(
            "nether_transmitter",
            new DimensionTransmitterBlock(FabricBlockSettings.of(Material.METAL).build(), DimensionType.THE_NETHER),
            KevlarItemGroup.INSTANCE
    );

    public static final DimensionTransmitterBlock END_TRANSMITTER = registerBlock(
            "end_transmitter",
            new DimensionTransmitterBlock(FabricBlockSettings.of(Material.METAL).build(), DimensionType.THE_END),
            KevlarItemGroup.INSTANCE
    );

    public static final DimensionTransmitterBlock OVERWORLD_TRANSMITTER = registerBlock(
            "overworld_transmitter",
            new DimensionTransmitterBlock(FabricBlockSettings.of(Material.METAL).build(), DimensionType.OVERWORLD),
            KevlarItemGroup.INSTANCE
    );


    public static final WirelessTeleportBlock WIRELESS_TELEPORT_REPEATER = registerBlock(
            "wireless_teleport_repeater",
            new WirelessTeleportBlock(NodeType.REPEATER, FabricBlockSettings.of(Material.METAL)
                    .strength(3.5f, 3.5f)
                    .build()),
            KevlarItemGroup.INSTANCE
    );

    public static final WirelessTeleportTransmitterBlock WIRELESS_TELEPORT_TRANSMITTER = registerBlock(
            "wireless_teleport_transmitter",
            new WirelessTeleportTransmitterBlock(FabricBlockSettings.of(Material.METAL)
                    .strength(3.5f, 3.5f)
                    .build()),
            KevlarItemGroup.INSTANCE
    );

    public static final WirelessTeleportBlock WIRELESS_TELEPORT_RECEIVER = registerBlock(
            "wireless_teleport_receiver",
            new WirelessTeleportReceiverBlock(FabricBlockSettings.of(Material.METAL)
                    .strength(3.5f, 3.5f)
                    .build()),
            KevlarItemGroup.INSTANCE
    );

    public static final WirelessRedstoneBlock WIRELESS_REDSTONE_REPEATER = registerBlock(
            "wireless_redstone_repeater",
            new WirelessRedstoneBlock(NodeType.REPEATER, FabricBlockSettings.of(Material.METAL)
                    .strength(1.5f, 6f)
                    .nonOpaque()
                    .build()),
            KevlarItemGroup.INSTANCE
    );

    public static final WirelessRedstoneTransmitterBlock WIRELESS_REDSTONE_TRANSMITTER = registerBlock(
            "wireless_redstone_transmitter",
            new WirelessRedstoneTransmitterBlock(FabricBlockSettings.of(Material.METAL)
                    .strength(1.5f, 6f)
                    .nonOpaque()
                    .build()),
            KevlarItemGroup.INSTANCE
    );

    public static final WirelessRedstoneReceiverBlock WIRELESS_REDSTONE_RECEIVER = registerBlock(
            "wireless_redstone_receiver",
            new WirelessRedstoneReceiverBlock(FabricBlockSettings.of(Material.METAL)
                    .strength(1.5f, 6f)
                    .nonOpaque()
                    .build()),
            KevlarItemGroup.INSTANCE
    );

    public static final FeederBlock FEEDER = registerBlock(
            "feeder",
            new FeederBlock(FabricBlockSettings.of(Material.WOOD).build()),
            KevlarItemGroup.INSTANCE
    );

    private static <T extends Block> T registerBlock(String name, T block, ItemGroup itemGroup) {

        Registry.register(Registry.BLOCK, new Identifier(Kevlar.MOD_ID, name), block);

        if (itemGroup != null) {
            Registry.register(
                    Registry.ITEM,
                    new Identifier(Kevlar.MOD_ID, name),
                    new BlockItem(block, new Item.Settings().group(itemGroup))
            );
        }


        return block;
    }

    public static void register() {
        // load the class
    }

}
