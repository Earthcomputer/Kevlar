package com.notvanilla.kevlar.item;

import com.notvanilla.kevlar.Kevlar;
import com.notvanilla.kevlar.block.KevlarBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class KevlarItems {


    public static final Item ALFALFA_SEEDS = registerItem(
            "alfalfa_seeds",
            new AliasedBlockItem(KevlarBlocks.ALFALFA, new Item.Settings().group(ItemGroup.MATERIALS)));


    public static final Item ALFALFA = registerItem(
            "alfalfa",
            new Item(new Item.Settings().group(ItemGroup.MATERIALS))
    );



    private static <T extends Item> T registerItem(String name, T item) {

        Registry.register(Registry.ITEM, new Identifier(Kevlar.MOD_ID, name), item);
        return item;
    }


    public static void register() {}

}
