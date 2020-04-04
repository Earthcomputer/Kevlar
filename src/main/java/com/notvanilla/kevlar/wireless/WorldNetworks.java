package com.notvanilla.kevlar.wireless;

import net.minecraft.nbt.CompoundTag;

public class WorldNetworks {

    public final TeleportationNetwork teleportation = new TeleportationNetwork();
    public final RedstoneNetwork redstone = new RedstoneNetwork();

    public void fromTag(CompoundTag tag) {
        teleportation.fromTag(tag.getCompound("Teleportation"));
        redstone.fromTag(tag.getCompound("Redstone"));
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.put("Teleportation", teleportation.toTag(new CompoundTag()));
        tag.put("Redstone", redstone.toTag(new CompoundTag()));
        return tag;
    }

}
