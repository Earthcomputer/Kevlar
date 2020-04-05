package com.notvanilla.kevlar.mixin;

import com.google.common.collect.ImmutableSet;
import net.minecraft.item.Item;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VillagerProfession.class)
public interface VillagerProfessionAccessor {
    @Accessor
    ImmutableSet<Item> getGatherableItems();

    @Mutable
    @Accessor
    void setGatherableItems(ImmutableSet<Item> gatherableItems);
}
