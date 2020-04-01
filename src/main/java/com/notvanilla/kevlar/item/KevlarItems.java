package com.notvanilla.kevlar.item;

import com.notvanilla.kevlar.Kevlar;
import com.notvanilla.kevlar.block.KevlarBlocks;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class KevlarItems {


    public static final Item ALFALFA_SEEDS = registerItem(
            "alfalfa_seeds",
            new AliasedBlockItem(KevlarBlocks.ALFALFA, new Item.Settings().group(KevlarItemGroup.INSTANCE)));


    public static final Item ALFALFA = registerItem(
            "alfalfa",
            new Item(new Item.Settings().group(KevlarItemGroup.INSTANCE))
    );



    private static <T extends Item> T registerItem(String name, T item) {

        Registry.register(Registry.ITEM, new Identifier(Kevlar.MOD_ID, name), item);
        return item;
    }


    public static void register() {}

}
