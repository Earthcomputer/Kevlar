package com.notvanilla.kevlar.wireless;

import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public class TeleportationNode extends KevlarNode {

    public TeleportationNode() {
    }

    public TeleportationNode(BlockPos pos, NodeType type, DyeColor color) {
        super(pos, type, color);
    }
}
