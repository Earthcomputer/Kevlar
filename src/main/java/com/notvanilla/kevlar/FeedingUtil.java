package com.notvanilla.kevlar;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.notvanilla.kevlar.item.KevlarItems;
import com.notvanilla.kevlar.mixin.VillagerProfessionAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;

import java.util.Map;

public class FeedingUtil {
    public static  Map<EntityType<? extends AnimalEntity>, Item> animalToFastBreedingPlant = ImmutableMap.of(
            EntityType.COW, KevlarItems.ALFALFA,
            EntityType.RABBIT, KevlarItems.ALFALFA,
            EntityType.PIG, KevlarItems.ALFALFA,
            EntityType.MOOSHROOM, KevlarItems.ALFALFA,
            EntityType.SHEEP, KevlarItems.ALFALFA
    );

    public static void initialize() {
        VillagerProfessionAccessor farmerProfession = (VillagerProfessionAccessor) VillagerProfession.FARMER;
        farmerProfession.setGatherableItems(ImmutableSet.<Item>builder()
                .addAll(farmerProfession.getGatherableItems())
                .add(KevlarItems.ALFALFA_SEEDS)
                .build());
    }
}
