package com.notvanilla.kevlar.wireless;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public class KevlarNode {
    private BlockPos pos;
    private NodeType type;
    private DyeColor color;

    protected KevlarNode() {}

    public KevlarNode(BlockPos pos, NodeType type, DyeColor color) {
        this.pos = pos;
        this.type = type;
        this.color = color;
    }

    public BlockPos getPos() {
        return pos;
    }

    public NodeType getType() {
        return type;
    }

    public DyeColor getColor() {
        return color;
    }

    public void fromTag(CompoundTag tag) {
        pos = NbtHelper.toBlockPos(tag.getCompound("Pos"));
        type = NodeType.byId(tag.getByte("NodeType"));
        color = DyeColor.byId(tag.getByte("Color"));
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.put("Pos", NbtHelper.fromBlockPos(pos));
        tag.putByte("NodeType", (byte) type.getId());
        tag.putByte("Color", (byte) color.getId());
        return tag;
    }

}
