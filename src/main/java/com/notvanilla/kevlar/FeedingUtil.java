package com.notvanilla.kevlar;

import com.google.common.collect.ImmutableMap;
import com.notvanilla.kevlar.item.KevlarItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.Item;

import java.util.Map;

public class FeedingUtil {
    public static  Map<EntityType<? extends AnimalEntity>, Item> animalToFastBreedingPlant = ImmutableMap.of(
            EntityType.COW, KevlarItems.ALFALFA
    );
}
