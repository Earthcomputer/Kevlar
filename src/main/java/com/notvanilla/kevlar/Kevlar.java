package com.notvanilla.kevlar;

import com.notvanilla.kevlar.block.KevlarBlocks;
import com.notvanilla.kevlar.block.entity.KevlarBlockEntities;
import com.notvanilla.kevlar.conservation.KevlarEnchantments;
import com.notvanilla.kevlar.item.KevlarItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class Kevlar implements ModInitializer {

    public static final String MOD_ID = "kevlar";

    public static final ChunkTicketType<BlockPos> REDSTONE_TICKET = ChunkTicketType.create("kevlar:redstone", Vec3i::compareTo, 3);

    @Override
    public void onInitialize() {

        //order important here

        KevlarBlocks.register();
        KevlarBlockEntities.register();
        KevlarItems.register();
        KevlarEnchantments.register();
    }

}
