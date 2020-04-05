package com.notvanilla.kevlar;

import com.notvanilla.kevlar.block.KevlarBlocks;
import com.notvanilla.kevlar.block.KevlarPointOfInterestTypes;
import com.notvanilla.kevlar.block.entity.KevlarBlockEntities;
import com.notvanilla.kevlar.container.KevlarContainers;
import com.notvanilla.kevlar.enchantment.KevlarEnchantments;
import com.notvanilla.kevlar.item.KevlarItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class Kevlar implements ModInitializer {

    public static final String MOD_ID = "kevlar";

    public static final ChunkTicketType<BlockPos> REDSTONE_3TICK_TICKET = ChunkTicketType.create("kevlar:redstone3", Vec3i::compareTo, 3);
    public static final ChunkTicketType<BlockPos> REDSTONE_11TICK_TICKET = ChunkTicketType.create("kevlar:redstone11", Vec3i::compareTo, 11);

    @Override
    public void onInitialize() {

        //order important here

        KevlarBlocks.register();
        KevlarBlockEntities.register();
        KevlarContainers.register();
        KevlarItems.register();
        KevlarEnchantments.register();
        KevlarPointOfInterestTypes.register();

        FeedingUtil.initialize();
        KevlarWorldgenEnhancements.initialize();
    }

}
