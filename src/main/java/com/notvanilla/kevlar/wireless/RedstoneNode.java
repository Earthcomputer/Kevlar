package com.notvanilla.kevlar.wireless;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RedstoneNode extends KevlarNode {

    private Map<BlockPos, PowerSourceRef> powerSources = new HashMap<>();

    public RedstoneNode() {}

    public RedstoneNode(BlockPos pos, NodeType type, DyeColor color) {
        super(pos, type, color);
    }

    public Set<BlockPos> getPowerSources() {
        return powerSources.keySet();
    }

    public PowerSourceRef getPowerSourceRef(BlockPos powerSource) {
        return powerSources.get(powerSource);
    }

    public void setPowerSourceRef(BlockPos powerSource, PowerSourceRef ref) {
        if (ref == null)
            powerSources.remove(powerSource);
        else
            powerSources.put(powerSource, ref);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        ListTag powerSourcesList = tag.getList("PowerSources", NbtType.COMPOUND);
        for (int i = 0; i < powerSourcesList.size(); i++) {
            CompoundTag powerSourceTag = powerSourcesList.getCompound(i);
            BlockPos pos = NbtHelper.toBlockPos(powerSourceTag);
            int power = MathHelper.clamp(powerSourceTag.getByte("Power"), 0, 15);
            powerSources.put(pos, new PowerSourceRef(power, 0));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        ListTag powerSourcesList = new ListTag();
        for (Map.Entry<BlockPos, PowerSourceRef> powerSource : powerSources.entrySet()) {
            CompoundTag powerSourceTag = NbtHelper.fromBlockPos(powerSource.getKey());
            powerSourceTag.putByte("Power", (byte) powerSource.getValue().getPower());
        }
        tag.put("PowerSources", powerSourcesList);
        return tag;
    }

    public static class PowerSourceRef {
        private int power;
        private double distance;

        public PowerSourceRef(int power, double distance) {
            this.power = power;
            this.distance = distance;
        }

        public int getPower() {
            return power;
        }

        public void setPower(int power) {
            this.power = power;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }
    }
}
