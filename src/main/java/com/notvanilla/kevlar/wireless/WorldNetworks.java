package com.notvanilla.kevlar.wireless;

import net.minecraft.nbt.CompoundTag;

public class WorldNetworks {

    public final TeleportationNetwork teleportation = new TeleportationNetwork();

    public void fromTag(CompoundTag tag) {
        teleportation.fromTag(tag.getCompound("Teleportation"));
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.put("Teleportation", teleportation.toTag(new CompoundTag()));
        return tag;
    }

}
