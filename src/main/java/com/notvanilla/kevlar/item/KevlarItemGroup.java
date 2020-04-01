package com.notvanilla.kevlar.item;

import com.notvanilla.kevlar.Kevlar;
import com.notvanilla.kevlar.block.KevlarBlocks;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class KevlarItemGroup {
    public static final ItemGroup INSTANCE = FabricItemGroupBuilder.build(new Identifier(Kevlar.MOD_ID, "kevlar"), () -> new ItemStack(KevlarBlocks.GOLD_REPEATER));
}
