package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.Kevlar;
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


public class KevlarBlocks {

    public static final PlanterBlock PLANTER = registerBlock(
            "planter",
            new PlanterBlock(FabricBlockSettings.copy(Blocks.DISPENSER).build()),
            ItemGroup.REDSTONE
    );

    public static final AlfalfaBlock ALFALFA = registerBlock(
            "alfalfa",
            new AlfalfaBlock(FabricBlockSettings.copy(Blocks.WHEAT).build()),
            null
    );

    public static final BlockBreakerBlock BLOCK_BREAKER = registerBlock(
            "block_breaker",
            new BlockBreakerBlock(FabricBlockSettings.copy(Blocks.DISPENSER).build()),
            ItemGroup.REDSTONE
    );

    public static final IronButtonBlock IRON_BUTTON = registerBlock(
            "iron_button",
            new IronButtonBlock(FabricBlockSettings.copy(Blocks.STONE_BUTTON).sounds(BlockSoundGroup.METAL).hardness(2.0f).build()),
            ItemGroup.REDSTONE
    );

    public static final GoldButtonBlock GOLD_BUTTON = registerBlock(
            "gold_button",
            new GoldButtonBlock(FabricBlockSettings.copy(Blocks.STONE_BUTTON).sounds(BlockSoundGroup.METAL).hardness(2.0f).build()),
            ItemGroup.REDSTONE
    );

    public static final WirelessTeleportBlock WIRELESS_TELEPORT_REPEATER = registerBlock(
            "wireless_teleport_repeater",
            new WirelessTeleportBlock(NodeType.REPEATER, FabricBlockSettings.of(Material.METAL)
                    .strength(3.5f, 3.5f)
                    .build()),
            ItemGroup.REDSTONE
    );

    public static final WirelessTeleportTransmitterBlock WIRELESS_TELEPORT_TRANSMITTER = registerBlock(
            "wireless_teleport_transmitter",
            new WirelessTeleportTransmitterBlock(FabricBlockSettings.of(Material.METAL)
                    .strength(3.5f, 3.5f)
                    .build()),
            ItemGroup.REDSTONE
    );

    public static final WirelessTeleportBlock WIRELESS_TELEPORT_RECEIVER = registerBlock(
            "wireless_teleport_receiver",
            new WirelessTeleportBlock(NodeType.RECEIVER, FabricBlockSettings.of(Material.METAL)
                    .strength(3.5f, 3.5f)
                    .build()),
            ItemGroup.REDSTONE
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
