package com.notvanilla.kevlar.wireless;

import net.minecraft.nbt.CompoundTag;

public class RedstoneNetwork extends KevlarNetwork<RedstoneNode> {

    public RedstoneNetwork() {
        super(64);
    }

    @Override
    protected RedstoneNode createNode() {
        return new RedstoneNode();
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);

        recalculateDistances();
    }

    public void recalculateDistances() {
        getAllNodes()
                .filter(node -> node.getType() == NodeType.TRANSMITTER)
                .forEach(transmitter -> {
                    getPathLengthsFrom(transmitter, node -> node.getColor() == transmitter.getColor()).forEach((pos, distance) -> {
                        RedstoneNode targetNode = getNode(pos);
                        if (targetNode != null) {
                            RedstoneNode.PowerSourceRef ref = targetNode.getPowerSourceRef(transmitter.getPos());
                            if (ref == null) {
                                targetNode.setPowerSourceRef(transmitter.getPos(), new RedstoneNode.PowerSourceRef(0, distance));
                            } else {
                                ref.setDistance(distance);
                            }
                        }
                    });
                });
    }
}
