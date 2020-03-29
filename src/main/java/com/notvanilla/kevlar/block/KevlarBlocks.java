package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.Kevlar;
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
