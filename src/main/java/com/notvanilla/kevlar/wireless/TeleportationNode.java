package com.notvanilla.kevlar.wireless;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public class TeleportationNode extends KevlarNode {

    private DyeColor color;

    public TeleportationNode() {
    }

    public TeleportationNode(BlockPos pos, NodeType type, DyeColor color) {
        super(pos, type);
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        color = DyeColor.byId(tag.getByte("Color"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putByte("Color", (byte) color.getId());
        return tag;
    }
}
