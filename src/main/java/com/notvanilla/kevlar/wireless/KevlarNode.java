package com.notvanilla.kevlar.wireless;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;

public class KevlarNode {
    private BlockPos pos;
    private NodeType type;

    protected KevlarNode() {}

    public KevlarNode(BlockPos pos, NodeType type) {
        this.pos = pos;
        this.type = type;
    }

    public BlockPos getPos() {
        return pos;
    }

    public NodeType getType() {
        return type;
    }

    public void fromTag(CompoundTag tag) {
        pos = NbtHelper.toBlockPos(tag.getCompound("Pos"));
        type = NodeType.byId(tag.getByte("NodeType"));
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.put("Pos", NbtHelper.fromBlockPos(pos));
        tag.putByte("NodeType", (byte) type.getId());
        return tag;
    }

}
